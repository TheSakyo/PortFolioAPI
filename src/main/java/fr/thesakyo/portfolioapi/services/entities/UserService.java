package fr.thesakyo.portfolioapi.services.entities;

import fr.thesakyo.portfolioapi.enums.ERole;
import fr.thesakyo.portfolioapi.exceptions.UnauthorizedException;
import fr.thesakyo.portfolioapi.helpers.StrHelper;
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

    @Autowired
    private DTOService dtoService; // Récupère le service lié au DTO
    @Autowired
    private RoleService roleService;

    /***********************************************************/
    /**************   ⬇️    MÉTHODES CRUD   ⬇️   **************/
    /***********************************************************/

    /**
     * Récupération de tous les {@link User utilisateur}s.
     *
     * @return Une {@link List liste} d'{@link UserDTO utilisateur}s.
     */
    public List<UserDTO> getAllUsers() {

        // Récupère la liste des utilisateurs
        List<User> users = userRepository.findAll();

        // On renvoie la liste d'utilisateur(s) et les convertis en leur 'DTO' respectif
        return dtoService.convertToDTOs(new UserDTO(), users);
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

            // Si l'utilisateur et le rôle ne sont pas 'null', on essaie d'ajouter son rôle, s'il ne l'a pas
            if(user != null && role != null && !user.getRoles().contains(role)) {

                Set<Role> roles = checkRolesName(new HashSet<>(List.of(role.getName().name()))); // Vérifie et récupère les rôles de l'utilisateur modifiés
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
                        user.setRoles(checkRolesName(rolesName));
                        break;

                    case ROLE_ADMIN:

                        rolesName = rolesName.stream().filter(element -> element.equalsIgnoreCase(ERole.ROLE_UNKNOWN.name())).collect(Collectors.toSet());
                        user.setRoles(checkRolesName(rolesName));
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
     * @param redirectURL Lien de redirection de l'API Spring.
     *
     * @return L'Objet '{@link UserDTO utilisateur}' mis à jour.
     */
    @Transactional(rollbackFor = { UnauthorizedException.class })
    public UserDTO updateUser(final Long id, User user, String redirectURL) {

        User existingUser = userRepository.findById(id).orElse(null); // Récupère l'utilisateur à modifier par son identifiant
        if(existingUser == null) return null; // Si l'utilisateur à modifier est 'null', 'null' est donc renvoyé


        /**********************************************************/

        Set<Role> roles = user.getRoles(); // Récupère les rôles modifiés de l'utilisateur
        Set<Project> projects = user.getProjects(); // Récupère les projets modifiés de l'utilisateur

        String name = user.getName(); // Récupère le nom modifié de l'utilisateur
        String password = user.getPassword(); // Récupère le mot de passe modifié de l'utilisateur
        String email = user.getEmail(); // Récupère l'adresse e-mail modifiée de l'utilisateur

        /**********************************************************/

        if(roles != null && !roles.isEmpty()) existingUser.setRoles(roles);
        if(projects != null && !projects.isEmpty()) existingUser.setProjects(projects);

        if(Strings.isNotBlank(name)) existingUser.setName(name); // Modifie le nom de l'utilisateur, si cela a été demandé
        if(Strings.isNotBlank(password)) existingUser.setPassword(password); // Modifie le mot de passe de l'utilisateur

        // ⬇️ Modifie l'adresse e-mail de l'utilisateur, si cela a été demandé, désactive l'utilisateur et envoie un compte d'activation sur le nouveau mail ⬇️ //
        if(Strings.isNotBlank(email) && !existingUser.getEmail().equalsIgnoreCase(email)) {

            existingUser.setEmail(email); // Ajoute le nouvel adresse e-mail à l'utilisateur
            existingUser = userRepository.save(existingUser); // Sauvegarde l'utilisateur en base de données
        }
        // ⬆️ Modifie l'adresse e-mail de l'utilisateur, si cela a été demandé, désactive l'utilisateur et envoie un compte d'activation sur le nouveau mail ⬆️ //

        // Renvoie le 'DTO' de l'utilisateur en sauvegardant l'utilisateur en base de donnée
        return dtoService.convertToDTO(new UserDTO(), userRepository.save(existingUser));
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

    /***********************************************************************************/
    /***********************************************************************************/

    /**
     * Récupère la {@link Set liste} des {@link Role rôle}s pour un {@link User utilisateur} à partir d'une liste de plusieurs {@link String nom}s des {@link Role rôle}s donnés.
     *
     * @param strRoles La {@link Set liste} des {@link String nom}s des {@link Role rôle}s pour l'utilisateur.
     *
     * @return La {@link Set liste} des {@link Role rôle}s pour un {@link User utilisateur} à partir d'une liste des {@link String nom}s des {@link Role rôle}s donnés.
     */
    public Set<Role> checkRolesName(Set<String> strRoles) {

        Set<Role> roles = new HashSet<>(); // Lites des noms des rôles vides (utile pour la récupération les rôles)

        /***********************************************/
        /***********************************************/

        /**
         * On vérifie le premier nom de rôle récupéré, et ainsi, on ajoute à la liste les rôles dépendants en fonction du rôle en question
         * et de ce qu'il se trouve dans la liste
         */
        switch(new ArrayList<>(strRoles).getFirst().toLowerCase()) {

            /**
             * Si on récupère le rôle `super-admin` : on l'ajoute à la liste avec d'autres rôles dépendants
             */
            case "superadmin", "super_admin", "role_superadmin" -> {

                Role ownerRole = roleService.checkedRole(ERole.ROLE_SUPERADMIN); // Vérifie le rôle `super-admin`

                /**************************************************/

                // On ajoute à la liste les rôles dépendants en effectuant les vérifications nécessaires
                roles.addAll(checkRolesName(new HashSet<>(List.of("admin", "unknown"))));
                roles.add(ownerRole); // On ajoute à la liste le rôle `super-admin`
            }

            /**
             * Si on récupère le rôle `admin` : on l'ajoute à la liste avec d'autres rôles dépendants
             * (on lui ajoute également à la liste le rôle au-dessus s'il en contient un)
             */
            case "admin", "role_admin" -> {

                Set<String> rolesToAdd = new HashSet<>(List.of("unknown")); // Récupère une liste de rôle dépendant
                Role adminRole = roleService.checkedRole(ERole.ROLE_ADMIN); // Vérifie le rôle `admin`

                /**************************************************/

                // Effectue une vérification du rôle de super administrateur
                checkRolesNameExist(strRoles, rolesToAdd, ERole.ROLE_SUPERADMIN, "superadmin", "super_admin", "super_admin");

                /********************************/

                if(!rolesToAdd.isEmpty()) roles.addAll(checkRolesName(rolesToAdd)); // On ajoute à la liste les rôles dépendants
                roles.add(adminRole); // On ajoute à la liste le rôle `admin`
            }

            /**
             * Par défaut : on ajoute le rôle d'Inconnue(e), (on lui ajoute également à la liste le rôle au-dessus s'il en contient un)
             */
            default -> {

                Set<String> rolesToAdd = new HashSet<>(); // Récupère une liste de rôle dépendant [Vide par défaut, car il s'agit du rôle le plus bas]
                Role employeeRole = roleService.checkedRole(ERole.ROLE_UNKNOWN); // Vérifie le rôle `Inconnue`

                /**************************************************/

                // Effectue une vérification du rôle de super administrateur
                checkRolesNameExist(strRoles, rolesToAdd, ERole.ROLE_SUPERADMIN, "superadmin", "super_admin", "super_admin");

                // Effectue une vérification du rôle d'administrateur
                checkRolesNameExist(strRoles, rolesToAdd, ERole.ROLE_ADMIN, "admin", "role_admin");

                /********************************/

                if(!rolesToAdd.isEmpty()) roles.addAll(checkRolesName(rolesToAdd));
                roles.add(employeeRole); // On ajoute à la liste le rôle `Inconnue(e)`
            }
        }

        /***********************************************/
        /***********************************************/

        return roles; // Renvoie la liste des rôles en fonction des noms récupérés et des rôles dépendants
    }

    /***********************************************************************************/

    private void checkRolesNameExist(Set<String> targetRolesName, Set<String> rolesNameToAdd, ERole initialRoleName, String ...searchNames) {

        if(StrHelper.containsIgnoreCase(targetRolesName, searchNames)) rolesNameToAdd.add(initialRoleName.name());
    }
}
