package fr.thesakyo.portfolioapi.services.entities;

import fr.thesakyo.portfolioapi.enums.ERole;
import fr.thesakyo.portfolioapi.exceptions.UnauthorizedException;
import fr.thesakyo.portfolioapi.helpers.PermissionHelper;
import fr.thesakyo.portfolioapi.helpers.RoleHelper;
import fr.thesakyo.portfolioapi.models.DTO.UserDTO;
import fr.thesakyo.portfolioapi.models.SerializableResponseEntity;
import fr.thesakyo.portfolioapi.models.entities.*;
import fr.thesakyo.portfolioapi.payloads.requests.RoleRequest;
import fr.thesakyo.portfolioapi.repositories.RoleRepository;
import fr.thesakyo.portfolioapi.repositories.UserRepository;
import fr.thesakyo.portfolioapi.security.UserConnection;
import fr.thesakyo.portfolioapi.services.DTOService;
import jakarta.annotation.Nullable;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.StringTemplate.STR;

@Service
public class UserService {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    @Autowired
    private UserRepository userRepository; // Référentiel faisant référence aux utilisateurs de la base de données.

    @Autowired
    private RoleRepository roleRepository; // Référentiel faisant référence aux rôles de la base de données.

    /*******************************/

    @Autowired
    private DTOService dtoService; // Récupère le service lié au DTO

    /*************************/

    @Autowired
    private RoleService roleService;

    /***********************************************************/
    /**************   ⬇️    MÉTHODES CRUD   ⬇️   **************/
    /***********************************************************/

    /**
     * Récupération de tous les {@link User utilisateur}s.
     *
     * @param entityClazz {@link Class} de l'{@link BaseEntity entité} associée aux {@link User utilisateur}s. (si nécessaire)
     * @param entityId {@link Long Identifiant} de l'{@link BaseEntity entité} associée aux {@link User utilisateur}s (si nécessaire)
     *
     * @return Une {@link Set liste} d'{@link UserDTO utilisateur}s.
     */
    public <C extends BaseEntity> Set<UserDTO> getAllUsers(Class<C> entityClazz, Long entityId) {

        // Récupère la liste des utilisateurs
        Set<User> users = new HashSet<>(userRepository.findAll());

        /************************************************/

        /**
         * Si on récupère un identifiant de l'entité et sa class,
         * on filtre les utilisateurs étant associée à l'entité respective
         */
        if(entityId != null && entityClazz != null) {

            switch(entityClazz.getSimpleName().toLowerCase()) {

                case "language":

                    users = userRepository.findAllByLanguageIdInProjets(entityId).orElse(new HashSet<>());
                    break;

                case "project":

                    users = users.stream().filter(user -> user.getProjects()
                            .stream().anyMatch(project -> project.getId().equals(entityId)))
                            .collect(Collectors.toSet());
                    break;

                default: break;
            }
        }

        // On renvoie la liste d'utilisateur(s) et les convertis en leur 'DTO' respectif
        return new HashSet<>(dtoService.convertToDTOs(new UserDTO(), users));
    }

    /**
     * Récupération d'un seul {@link User utilisateur} par son identifiant.
     *
     * @param id L'{@link Long Identifiant} de l'{@link User utilisateur}.
     *
     * @return Une {@link ResponseEntity réponse http} permettant de récupérer un objet '{@link UserDTO utilisateur}'.
     */
    public SerializableResponseEntity<?> getUser(@Nullable Long id) {

        User user = userRepository.findById(id).orElse(null); // Récupère l'utilisateur par son identifiant
        Map<String, Object> responseMap = new HashMap<>(); // Dictionnaire 'map' pour récupérer une clé → valeur (utile pour le retour de la réponse http)

        /**************************************/

        // Si l'utilisateur récupéré est bel et bien existant, on envoie dans le dictionnaire 'map' une valeur booléenne disant que l'utilisateur existe
        if(user != null) responseMap.putIfAbsent("isAvailable", true);

        // Sinon, on envoie dans le dictionnaire 'map' une valeur booléenne disant que l'utilisateur n'existe pas
        else responseMap.putIfAbsent("isAvailable", false);

        /**************************************/

        // On récupère l'utilisateur en tant qu'optionnel
        Optional<User> userOptional = Optional.ofNullable(user);

        /**
         * Si l'utilisateur optionnel, existe bel et bien,
         * on renvoie son 'DTO' respectif
         */
        if(userOptional.isPresent()) {

            UserDTO userDTO = dtoService.convertToDTO(new UserDTO(), userOptional.orElse(null)); // Convertit l'utilisateur en son 'DTO' respectif
            responseMap.putIfAbsent("entity", userDTO); // Envoie dans le dictionnaire 'map' l'utilisateur en question
        }

        /*******************************************************/

        return new SerializableResponseEntity<>(responseMap, HttpStatus.OK); // On envoie la réponse http avec le dictionnaire 'map'
    }

    /**
     * Met à jour les rôles d'un {@link User utilisateur} (ajoute ou supprime un {@link Role rôle}).
     *
     * @param roleRequest Un '{@link RoleRequest objet de requête}' pour l'ajout/suppression d'un {@link Role rôle} à l'{@link User utilisateur} en question.
     * @param addRole Doit-on ajouter le {@link Role rôle} à l'{@link User utilisateur} ?
     *
     * @return Une {@link ResponseEntity réponse http} récupérant une valeur booléenne vérifiant si l'{@link User utilisateur}
     *      a bien eu son {@link Role rôle} modifié ('true' ou 'false').
     */
    public SerializableResponseEntity<?> addOrRemoveRole(RoleRequest roleRequest, boolean addRole) {

        Map<String, Object> responseMap = new HashMap<>(); // Dictionnaire 'map' pour récupérer une clé → valeur (utile pour le retour de la réponse http)
        responseMap.putIfAbsent("isAddedRole", false); // Définit une clé → valeur : Le rôle à t'il été ajouté à l'utilisateur ('faux' par défaut) ?
        responseMap.putIfAbsent("isRemovedRole", false); // Définit une clé → valeur : Le rôle à t'il été supprimé de l'utilisateur ('faux' par défaut) ?

        /******************************/

        User user = userRepository.findById(roleRequest.getUserId()).orElse(null);
        Role role = roleRepository.findById(roleRequest.getRoleId()).orElse(null);

        /***********************************/
        /***********************************/

        // On essaie de récupérer le modèle de l'utilisateur authentifié
        User userConnected = UserConnection.getUserLogged();

        /**
         * Si l'utilisateur authentifié n'est pas 'null', et que qu'il a belle et bien des rôles,
         * on vérifie s'il a les permissions nécessaires pour ajouter/supprimer le rôle demandé.
         */
        if(userConnected != null && !userConnected.getRoles().isEmpty()) {

            String status = addRole ? "ajouter" : "enlever"; // On met à jour le status dans le message d'exception, s'il s'agit d'un ajout ou d'une suppression de rôle

            /**********************/
            /**********************/

            /**
             * On récupère une liste de tous les rôles supérieure au rôle demandé, ainsi, si la liste est vide, c'est qu'il n'a pas les permissions.
             * Une exception est donc levée !
             */
            List<Role> rolesOfUser = userConnected.getRoles().stream().filter(targetRole -> (role == null || targetRole.getName() == ERole.ROLE_SUPERADMIN
                    || targetRole.getSeverity() > role.getSeverity())).toList();
            if(rolesOfUser.isEmpty()) throw new AccessDeniedException(STR."Vous n'avez pas la permission pour \{status} le rôle \{role != null ? role.getName() : "undefined"} à un utilisateur");
        }

        /***********************************/
        /***********************************/

        // Si on décide d'ajouter un rôle à l'utilisateur, on essaie de lui ajouter le rôle en question
        if(addRole) {

            // Si l'utilisateur et le rôle ne sont pas 'null', on essaie d'ajouter son rôle, s'il ne l'a pas.
            if(user != null && role != null && !user.getRoles().contains(role)) {

                // Vérifie et récupère les rôles de l'utilisateur modifiés
                Set<Role> roles = RoleHelper.checkRolesName(new HashSet<>(List.of(role.getName().name())), roleRepository);
                user.setRoles(roles); // Ajoute les rôles à l'utilisateur

                /*************************/

                responseMap.replace("isAddedRole", true); // Définit une clé → valeur : Le rôle à t'il été ajouté à l'utilisateur ?
            }

            // Sinon, on décide d'ajouter un rôle à l'utilisateur, on essaie de lui ajouter le rôle en question
        } else {

            // Si l'utilisateur et le rôle ne sont pas 'null', on essaie de supprimer son rôle, s'il ne l'a déjà
            if(user != null && role != null && user.getRoles().contains(role)) {

                user.getRoles().remove(role); // Supprime le rôle en question à l'utilisateur

                // Récupère la liste des noms des rôles de l'utilisateur
                Set<String> rolesName = user.getRoles().stream().map(element -> element.getName().name()).collect(Collectors.toSet());

                /***********************************/
                /***********************************/

                switch(role.getName()) {

                    case ROLE_SUPERADMIN:

                        rolesName = rolesName.stream().filter(element -> element.equalsIgnoreCase(ERole.ROLE_ADMIN.name())).collect(Collectors.toSet());
                        user.setRoles(RoleHelper.checkRolesName(rolesName, roleRepository));
                        break;

                    case ROLE_ADMIN:

                        rolesName = rolesName.stream().filter(element -> element.equalsIgnoreCase(ERole.ROLE_UNKNOWN.name())).collect(Collectors.toSet());
                        user.setRoles(RoleHelper.checkRolesName(rolesName, roleRepository));
                        break;

                    default:

                        Role unkownRole = roleRepository.findByName(ERole.ROLE_UNKNOWN).orElse(null);
                        if(unkownRole != null) {

                            roleRequest.setRoleId(unkownRole.getId());
                            return addOrRemoveRole(roleRequest, false);
                        }
                        break;
                }

                responseMap.replace("isRemovedRole", true); // Définit une clé → valeur : Le rôle à t'il été supprimé de l'utilisateur ?
            }
        }

        /***********************************/
        /***********************************/

        /**
         * Sauvegarde l'utilisateur en base de données, s'il existe
         */
        if(user != null) {

            user = userRepository.save(user); // Sauvegarde l'utilisateur en base de données

            // Envoie dans un dictionnaire clé → valeur, le 'DTO' de l'utilisateur en sauvegardé en base de donnée
            responseMap.putIfAbsent("entity", dtoService.convertToDTO(new UserDTO(), user));
        }
        return new SerializableResponseEntity<>(responseMap, HttpStatus.OK); // Renvoie la réponse http
    }

    /**
     * Mise à jour d'un {@link User utilisateur}.
     *
     * @param id L'{@link Long Identifiant} de l'{@link User utilisateur}.
     * @param user Un nouvel objet '{@link User utilisateur}'.
     *
     * @return L'Objet '{@link UserDTO utilisateur}' mis à jour.
     *
     * @throws UnauthorizedException Si l'utilisateur n'a pas la permission pour modifier l'entité cible
     */
    @Transactional(rollbackFor = { UnauthorizedException.class })
    public UserDTO updateUser(final Long id, User user) {

        User existingUser = userRepository.findById(id).orElse(null); // Récupère l'utilisateur à modifier par son identifiant
        if(existingUser == null) return null; // Si l'utilisateur à modifier est 'null', 'null' est donc renvoyé
        checkPermission(ERole.ROLE_ADMIN); // S'il n'a pas la permission pour modifier l'entité cible, on envoie une erreur

        /**********************************************************/

        Set<Role> roles = user.getRoles(); // Récupère les rôles modifiés de l'utilisateur
        Set<Project> projects = user.getProjects(); // Récupère les projets modifiés de l'utilisateur

        String name = user.getName(); // Récupère le nom modifié de l'utilisateur
        String email = user.getEmail(); // Récupère l'adresse e-mail modifiée de l'utilisateur
        String password = user.getPassword(); // Récupère le mot de passe modifié de l'utilisateur

        /**********************************************************/

        if(roles != null && !roles.isEmpty()) existingUser.setRoles(roles);
        if(projects != null && !projects.isEmpty()) existingUser.setProjects(projects);

        if(Strings.isNotBlank(name)) existingUser.setName(name); // Modifie le nom de l'utilisateur, si cela a été demandé
        if(Strings.isNotBlank(password)) existingUser.setPassword(password); // Modifie le mot de passe de l'utilisateur

        // ⬇️ Modifie l'adresse e-mail de l'utilisateur, si cela a été demandé, désactive l'utilisateur et envoie un compte d'activation sur le nouveau mail ⬇️ //
        if(Strings.isNotBlank(email) && !existingUser.getEmail().equalsIgnoreCase(email)) {

            /**
             * Si l'utilisateur connecté n'a pas la permission pour modifier
             * l'entité cible dans la base de donnés, on envoie donc une exception !
             */
            if(!PermissionHelper.userHasRole(ERole.ROLE_ADMIN)) throw new UnauthorizedException(PermissionHelper.UNAUTHORIZED_MESSAGE);
            existingUser.setEmail(email); // Ajoute le nouvel adresse e-mail à l'utilisateur
        }
        // ⬆️ Modifie l'adresse e-mail de l'utilisateur, si cela a été demandé, désactive l'utilisateur et envoie un compte d'activation sur le nouveau mail ⬆️ //

        // Renvoie le 'DTO' de l'utilisateur en sauvegardant l'utilisateur en base de donnée
        return dtoService.convertToDTO(new UserDTO(), userRepository.save(existingUser));
    }

    /**
     * Activation d'un {@link User utilisateur}.
     *
     * @param id L'{@link Long Identifiant} de l'{@link User utilisateur}.
     *
     * @return Une {@link ResponseEntity réponse http} récupérant une valeur booléenne vérifiant si l'{@link User utilisateur} a bien été activé ('true' ou 'false').
     */
    @Transactional(rollbackFor = { UnauthorizedException.class })
    public SerializableResponseEntity<?> enableUser(final Long id) {

        Map<String, Boolean> responseMap = new HashMap<>(); // Dictionnaire 'map' pour récupérer une clé → valeur (utile pour le retour de la réponse http)
        responseMap.putIfAbsent("isEnabled", false); // Redéfinit une clé → valeur : L'Utilisateur a-t-il était activé ?
        checkPermission(ERole.ROLE_SUPERADMIN); // S'il n'a pas la permission pour vérifier l'entité cible, on envoie une erreur

        /**********************************/

        userRepository.findById(id).ifPresent(user -> {

            user.setVerificationEnabled(true); // Désactive le compte de l'utilisateur
            userRepository.save(user); // Sauvegarde l'utilisateur en base de données

            /*******************/

            responseMap.replace("isEnabled", true); // Redéfinit une clé → valeur : L'Utilisateur a-t-il était activé ?
        });

        /******************************/

        return new SerializableResponseEntity<>(responseMap, HttpStatus.OK); // Renvoie la réponse http
    }

    /**
     * Suppression/Désactivation d'un {@link User utilisateur}.
     *
     * @param id L'{@link Long Identifiant} de l'{@link User utilisateur}.
     * @param disableUser Doit-on plutôt désactiver l'{@link User utilisateur}.
     *
     * @return Une {@link ResponseEntity réponse http} récupérant une valeur booléenne vérifiant si l'{@link User utilisateur} a bien été supprimé ('true' ou 'false').
     */
    @Transactional(rollbackFor = { UnauthorizedException.class })
    public SerializableResponseEntity<?> deleteUser(final Long id, boolean disableUser) {

        Map<String, Boolean> responseMap = new HashMap<>(); // Dictionnaire 'map' pour récupérer une clé → valeur (utile pour le retour de la réponse http)
        responseMap.putIfAbsent("isDeleted", false); // Redéfinit une clé → valeur : L'Utilisateur a-t-il était supprimé ?

        /******************************/

        User existingUser = userRepository.findById(id).orElse(null); // Récupère l'utilisateur à supprimer/désactiver par son identifiant
        if(existingUser == null) return null; // Si l'utilisateur à supprimer/désactiver est 'null', 'null' est donc renvoyé

        /**********************************************************/

        userRepository.deleteById(id); // Supprime l'utilisateur
        responseMap.replace("isDeleted", !userRepository.existsById(id)); // Redéfinit une clé → valeur : L'Utilisateur a-t-il était supprimé ?

        /******************************/

        return new SerializableResponseEntity<>(responseMap, HttpStatus.OK); // Renvoie la réponse http
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
