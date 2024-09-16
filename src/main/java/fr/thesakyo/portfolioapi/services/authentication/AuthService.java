package fr.thesakyo.portfolioapi.services.authentication;

import fr.thesakyo.portfolioapi.enums.ERole;
import fr.thesakyo.portfolioapi.exceptions.UnauthorizedException;
import fr.thesakyo.portfolioapi.helpers.PermissionHelper;
import fr.thesakyo.portfolioapi.helpers.RoleHelper;
import fr.thesakyo.portfolioapi.models.DTO.RoleDTO;
import fr.thesakyo.portfolioapi.models.DTO.UserDTO;
import fr.thesakyo.portfolioapi.models.SerializableResponseEntity;
import fr.thesakyo.portfolioapi.models.entities.Role;
import fr.thesakyo.portfolioapi.models.entities.authentication.UserDetailsImpl;
import fr.thesakyo.portfolioapi.payloads.requests.authentication.BaseRequest;
import fr.thesakyo.portfolioapi.payloads.requests.authentication.user.LoginUserRequest;
import fr.thesakyo.portfolioapi.payloads.requests.authentication.user.RegisterUserRequest;
import fr.thesakyo.portfolioapi.payloads.requests.authentication.user.UpdateUserRequest;
import fr.thesakyo.portfolioapi.payloads.responses.MessageResponse;
import fr.thesakyo.portfolioapi.payloads.responses.authentication.UserInfoResponse;
import fr.thesakyo.portfolioapi.repositories.RoleRepository;
import fr.thesakyo.portfolioapi.repositories.UserRepository;
import fr.thesakyo.portfolioapi.security.jwt.JwtUtils;
import fr.thesakyo.portfolioapi.services.DTOService;
import fr.thesakyo.portfolioapi.services.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import fr.thesakyo.portfolioapi.models.entities.User;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    @Autowired
    private UserRepository userRepository; // Référentiel faisant référence aux utilisateurs de la base de données.

    @Autowired
    private RoleRepository roleRepository; // Référentiel faisant référence aux rôles de la base de données.

    /*****************************/

    @Autowired
    private UserService userService;

    @Autowired
    private DTOService dtoService; // Récupère le service lié au DTO

    /*****************************/

    @Autowired
    private PasswordEncoder encoder; // Interface permettant de crypter un mot de passe.

    /*****************************/

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;

    /***********************************************************/
    /**************   ⬇️    MÉTHODES CRUD   ⬇️   **************/
    /***********************************************************/

    /**
     * Connexion d'un {@link User utilisateur} depuis sa base de données en récupérant ainsi ses rôles.
     *
     * @param loginUserRequest Un '{@link LoginUserRequest objet de requête}' pour la connexion de l'{@link User utilisateur} en question.
     *
     * @return Une {@link ResponseEntity réponse http} incluant l'utilisateur connecté si la requête fût un succès (sinon ça retourne une {@link ResponseEntity réponse http} d'erreur).
     */
    public SerializableResponseEntity<?> authenticate(LoginUserRequest loginUserRequest) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUserRequest.getEmail(), loginUserRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl)authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        /******************************************/

        List<String> rolesName = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        List<Role> roles = roleRepository.findAll().stream().filter(role -> rolesName.contains(role.getName().name())).toList();

        /******************************************/

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.putIfAbsent("response", new MessageResponse("Connexion de l'utilisateur établie !"));

        /*******************************/

        Long id = userDetails.getId();
        String name = userDetails.getName();
        String username = userDetails.getUsername();
        boolean isEnabled = userDetails.isEnabled();

        String cookie = jwtCookie.toString();

        /*******************************/

        responseMap.putIfAbsent("entity", new UserInfoResponse(id, name, username, new HashSet<>(dtoService.convertToDTOs(new RoleDTO(), roles))));
        responseMap.putIfAbsent("cookie", cookie);

        /*******************************/

        ResponseEntity<?> responseEntity = ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie).body(responseMap); // Génère la réponse http
        return new SerializableResponseEntity<>(responseEntity); // Renvoie la réponse http
    }

    /**
     * Création d'un {@link User utilisateur} dans la base de données, ainsi, on lui ajoute ses rôles respectifs.
     *
     * @param registerUserRequest Un '{@link RegisterUserRequest objet de requête}' pour la création de l'{@link User utilisateur} en question.
     *
     * @return Une {@link ResponseEntity réponse http} incluant l'utilisateur créé si la requête fût un succès (sinon ça retourne une {@link ResponseEntity réponse http} d'erreur).
     *
     * @throws UnauthorizedException Si l'utilisateur n'a pas la permission pour modifier l'entité cible
     */
    @Transactional(rollbackFor = { UnauthorizedException.class })
    public SerializableResponseEntity<?> register(RegisterUserRequest registerUserRequest) {

        // Récupère la réponse http de la requête pour la création de l'utilisateur
        SerializableResponseEntity<?> response = userRequest(registerUserRequest);

        // Si le status de la réponse http n'est pas ok, on retourne directement la réponse d'erreur
        if(response.getStatusCode() != HttpStatus.OK) return response;

        /******************************************/

        User user = (User)response.getBody(); // Récupère l'utilisateur pour effectuer sa création

        // Si l'utilisateur récupéré est 'null', on envoie une erreur
        if(user == null) return new SerializableResponseEntity<>("Erreur lors de la création de l'utilisateur", HttpStatus.INTERNAL_SERVER_ERROR);
        checkPermission(ERole.ROLE_SUPERADMIN); // S'il n'a pas la permission pour vérifier l'entité cible, on envoie une erreur

        /******************************************/

        // ⬇️ Si les rôles de l'utilisateur sont vides, on lui ajoure donc le rôle d'employée ⬇️ //
        if(user.getRoles().isEmpty()) {

            Set<Role> roles = new HashSet<>(); // Lites des noms des rôles vides (utile pour la récupération les rôles)

            /******************************************/

            // Récupère le rôle des employées, s'il existe, sinon, on envoie une erreur
            Role userRole = roleRepository.findByName(ERole.ROLE_UNKNOWN).orElseThrow(() -> new RuntimeException("Erreur : Le rôle est introuvable."));
            roles.add(userRole); // Ajour le roles à la liste des rôles de l'utilisateur

            /******************************************/

            user.setRoles(roles); // Ajoute la liste des rôles de l'utilisateur aux rôles de l'utilisateur
        }
        // ⬆️ Si les rôles de l'utilisateur sont vides, on lui ajoure donc le rôle d'employée ⬆️ //

        // Créer un dictionnaire, permettant de stocker des informations pour ensuite l'envoyé côté front
        Map<String, Object> responseEntity = new HashMap<>();

        responseEntity.putIfAbsent("response", new MessageResponse("Création de l'utilisateur établie !")); // Ajoute une réponse dans le dictionnaire
        responseEntity.putIfAbsent("entity", user); // Ajoute l'utilisateur dans le dictionnaire
        userRepository.save(user); // Envoie l'utilisateur enregistré en base de données

        return new SerializableResponseEntity<>(responseEntity, HttpStatus.OK); // Renvoie une réponse http incluant le dictionnaire stockant des informations utiles
    }

    /**
     * Modification d'un {@link User utilisateur} dans la base de données, ainsi, on lui ajoute ses rôles respectifs.
     *
     * @param id L'{@link Long Identifiant} de l'{@link User utilisateur}.
     * @param updateUserRequest Un '{@link UpdateUserRequest objet de requête}' pour la mise à jour de l'{@link User utilisateur} en question.
     *
     * @return Une {@link ResponseEntity réponse http} incluant l'utilisateur modifié si la requête fût un succès (sinon ça retourne une {@link ResponseEntity réponse http} d'erreur).
     *
     * @throws UnauthorizedException Si l'utilisateur n'a pas la permission pour modifier l'entité cible
     */
    @Transactional(rollbackFor = { UnauthorizedException.class })
    public SerializableResponseEntity<?> update(final Long id, UpdateUserRequest updateUserRequest) {

        // Récupère l'utilisateur en question par son identifiant
        User existingUser = userRepository.findById(id).orElse(null);

        // Si l'utilisateur récupéré est 'null', on envoie une erreur
        if(existingUser == null) return new SerializableResponseEntity<>("Erreur lors de la modification de l'utilisateur", HttpStatus.INTERNAL_SERVER_ERROR);

        /******************************************/

        // Récupère la réponse http de la requête pour la modification de l'utilisateur
        SerializableResponseEntity<?> response = userRequest(updateUserRequest);

        // Si le status de la réponse http n'est pas ok, on retourne directement la réponse d'erreur
        if(response.getStatusCode() != HttpStatus.OK) return response;

        /******************************************/

        // Récupère l'utilisateur avec les modifications en question envoyé à la requête
        UserDTO user = userService.updateUser(id, (User)response.getBody());

        /******************************************/

        // Créer un dictionnaire, permettant de stocker des informations pour ensuite l'envoyé côté front
        Map<String, Object> responseEntity = new HashMap<>();

        responseEntity.putIfAbsent("response", new MessageResponse("Modification de l'utilisateur établie !")); // Ajoute une réponse dans le dictionnaire
        responseEntity.putIfAbsent("entity", user); // Ajoute l'utilisateur dans le dictionnaire

        return new SerializableResponseEntity<>(responseEntity, HttpStatus.OK); //  Renvoie une réponse http incluant le dictionnaire stockant des informations utiles
    }

    /**
     * Gère la déconnexion de l'{@link User utilisateur}.
     *
     * @return Une {@link ResponseEntity réponse http} récupérant une valeur booléenne vérifiant si l'utilisateur à bien était déconnecté ('true' ou 'false').
     */
    public SerializableResponseEntity<?> logoutUser() {

        Map<String, Boolean> responseMap = new HashMap<>(); // Dictionnaire 'map' pour récupérer une clé → valeur (utile pour le retour de la réponse http)

        /******************************/

        ResponseCookie cookie = jwtUtils.getCleanJwtCookie(); // Vide les cookies
        responseMap.putIfAbsent("isLoggedOut", cookie.getMaxAge().isZero()); // Définit une clé → valeur : La catégorie a-t-elle était supprimée ?


        ResponseEntity<?> responseEntity = ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(responseMap); // Génère la réponse http
        return new SerializableResponseEntity<>(responseEntity); // Renvoie la réponse http
    }

    /***********************************************************************************/
    /***********************************************************************************/

    /**
     * Initialisation de la requête renvoyée, s'il s'agit d'une création ou d'une modification.
     *
     * @param baseRequest Un '{@link BaseRequest objet de requête}' pour la création/modification de l'{@link User utilisateur} en question.
     *
     * @return Une {@link ResponseEntity réponse http} incluant l'utilisateur modifié ou créer, si la requête fût un succès (sinon ça retourne une {@link ResponseEntity réponse http} d'erreur).
     */
    private SerializableResponseEntity<?> userRequest(BaseRequest baseRequest) {

        String email = baseRequest.getEmail() != null ? baseRequest.getEmail() : null; // Adresse e-mail de l'utilisateur a sauvegardé
        String name = baseRequest.getName() != null ? baseRequest.getName() : null; // Nom de l'utilisateur a sauvegardé
        String password = baseRequest.getPassword() != null ? encoder.encode(baseRequest.getPassword()) : null; // Mot de passe (crypté) de l'utilisateur a sauvegardé


        // ⬇️ Lite des noms des rôles de l'utilisateur a sauvegardé ⬇️ //
        Set<String> strRoles = baseRequest.getRoles();
        strRoles = strRoles != null && !strRoles.isEmpty() ? strRoles : new HashSet<>(List.of("unknown"));
        // ⬆️ Lite des noms des rôles de l'utilisateur a sauvegardé ⬆️ //

        /****************************************************************/
        /****************************************************************/

        // Si l'adresse e-mail est déjà existant dans la base de données des utilisateurs, on envoie donc de même une réponse d'erreur.
        if(userRepository.existsByEmail(email)) return new SerializableResponseEntity<>(new MessageResponse("Erreur : L'adresse e-mail est déjà utilisé !"), HttpStatus.SEE_OTHER);

        /****************************************************************/
        /****************************************************************/

        User user = new User(name, email, password, true, new HashSet<>(), new HashSet<>()); // Création d'un nouvel objet utilisateur
        user.setRoles(RoleHelper.checkRolesName(strRoles, roleRepository)); // Ajoute les rôles dont il est question à l'utilisateur
        return new SerializableResponseEntity<>(user, HttpStatus.OK); // Renvoie une réponse de l'utilisateur
    }

    /******************************************************************************************************************/
    /******************************************************************************************************************/
    /******************************************************************************************************************/

    /**
     * Envoie un {@link UnauthorizedException exception} si l'utilisateur connecté n'a pas la permission adéquate.
     *
     * @param role Le {@link ERole nom du rôle} à valider.
     *
     * @throws UnauthorizedException Si l'utilisateur connecté n'a pas la permission adéquate.
     */
    private void checkPermission(ERole role) throws UnauthorizedException {

        /**
         * Si l'utilisateur connecté n'a pas la permission adéquate, on envoie donc une exception !
         */
        if(!PermissionHelper.userHasRole(role)) throw new UnauthorizedException(PermissionHelper.UNAUTHORIZED_MESSAGE);
    }
}
