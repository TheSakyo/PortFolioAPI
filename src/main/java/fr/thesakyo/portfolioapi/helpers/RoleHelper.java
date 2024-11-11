package fr.thesakyo.portfolioapi.helpers;

import fr.thesakyo.portfolioapi.enums.ERole;
import fr.thesakyo.portfolioapi.models.entities.Role;
import fr.thesakyo.portfolioapi.models.entities.User;
import fr.thesakyo.portfolioapi.repositories.RoleRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class RoleHelper {

    /**********************************************************************************/
    /*************   ⬇️    MÉTHODES UTILES EN RAPPORT AVEC LES RÔLES  ⬇️   ***********/
    /*********************************************************************************/

    /**
     * Récupère la {@link Set liste} des {@link Role rôle}s pour un {@link User utilisateur} à partir d'une liste de plusieurs {@link String nom}s des {@link Role rôle}s donnés.
     *
     * @param strRoles La {@link Set liste} des {@link String nom}s des {@link Role rôle}s pour l'utilisateur.
     * @param roleRepository Interface référentielle pour concorder la table des rôles dans la base de données.
     *
     * @return La {@link Set liste} des {@link Role rôle}s pour un {@link User utilisateur} à partir d'une liste des {@link String nom}s des {@link Role rôle}s donnés.
     */
    public static Set<Role> checkRolesName(Set<String> strRoles, RoleRepository roleRepository) {

        Set<Role> roles = new HashSet<>(); // Lites des noms des rôles vides (utile pour la récupération les rôles)

        /***********************************************/
        /***********************************************/

        /**
         * On vérifie le premier nom de rôle récupéré, et ainsi, on ajoute à la liste les rôles dépendants en fonction du rôle en question
         * et de ce qu'il se trouve dans la liste.
         */
        switch(new ArrayList<>(strRoles).getFirst().toLowerCase()) {

            /**
             * Si on récupère le rôle `super-admin` : on l'ajoute à la liste avec d'autres rôles dépendants
             */
            case "superadmin", "super_admin", "role_superadmin" -> {

                Role ownerRole = checkedRole(ERole.ROLE_SUPERADMIN, roleRepository); // Vérifie le rôle `super-admin`

                /**************************************************/

                // On ajoute à la liste le rôle dépendant
                roles.addAll(checkRolesName(new HashSet<>(List.of("admin")), roleRepository));
                roles.add(ownerRole); // On ajoute à la liste le rôle `super-admin`
            }

            /**
             * Si on récupère le rôle `admin` : on l'ajoute à la liste avec d'autres rôles dépendants
             * (on lui ajoute également à la liste le rôle au-dessus s'il en contient un)
             */
            case "admin", "role_admin" -> {

                Set<String> rolesToAdd = new HashSet<>(List.of("unknown")); // Récupère une liste de rôle dépendant
                Role adminRole = checkedRole(ERole.ROLE_ADMIN, roleRepository); // Vérifie le rôle `admin`

                /**************************************************/

                // On vérifie si le rôle 'super_admin' existe bien dans la liste
                checkRolesNameExist(strRoles, rolesToAdd, ERole.ROLE_SUPERADMIN, "superadmin", "super_admin", "super_admin");

                /********************************/

                // On ajoute à la liste les rôles dépendants, si la liste n'est pas vide
                if(!rolesToAdd.isEmpty()) roles.addAll(checkRolesName(rolesToAdd, roleRepository));
                roles.add(adminRole); // On ajoute à la liste le rôle `admin`
            }

            /**
             * Par défaut : on ajoute le rôle 'inconnu(e)', s'il contient d'autre(s) rôle(s), on les ajoute(s) également, et en enlève le rôle 'inconnu(e)'
             */
            default -> {

                Set<String> rolesToAdd = new HashSet<>(); // Récupère une liste de rôle dépendant [Vide par défaut, car il s'agit du rôle le plus bas]
                Role unknownRole = checkedRole(ERole.ROLE_UNKNOWN, roleRepository); // Vérifie le rôle `employé(e)`

                /**************************************************/

                // On vérifie si le rôle 'super_admin' existe bien dans la liste
                checkRolesNameExist(strRoles, rolesToAdd, ERole.ROLE_SUPERADMIN, "superadmin", "super_admin", "super_admin");

                // On vérifie si le rôle 'super_admin' existe bien dans la liste
                checkRolesNameExist(strRoles, rolesToAdd, ERole.ROLE_ADMIN, "admin", "role_admin");

                /********************************/

                // On ajoute à la liste les rôles dépendants, si la liste n'est pas vide
                if(!rolesToAdd.isEmpty()) roles.addAll(checkRolesName(rolesToAdd, roleRepository));
                roles.add(unknownRole); // On ajoute à la liste le rôle `inconnu(e)`
            }
        }

        /***********************************************/
        /***********************************************/

        return roles; // Renvoie la liste des rôles en fonction des noms récupérés et des rôles dépendants
    }

    /**
     * Vérifie et récupère le {@link Role rôle} demandé.
     *
     * @param role Le {@link ERole nom du rôle} en question.
     * @param roleRepository Interface référentielle pour concorder la table des rôles dans la base de données.
     *
     * @return Le {@link Role rôle} demandé.
     *
     * @throws RuntimeException Une exception est envoyé en cas de rôle introuvable.
     */
    public static Role checkedRole(ERole role, RoleRepository roleRepository) { return roleRepository.findByName(role).orElseThrow(() -> new RuntimeException("Erreur : Le rôle est introuvable.")); }

    /******************************************************************************************************************/
    /******************************************************************************************************************/

    /**
     * Vérifie si le ou les {@link ERole nom(s) du/des rôle(s)} spécifié(s) existe(nt) dans la liste des {@link Role rôle}s cibles.
     * Si l'un du ou des {@link ERole nom(s) du/des rôle(s)} recherché(s) existe(nt), on ajoute le nom du rôle initial à la liste des {@link Role rôle}s à ajouter.
     *
     * @param targetRolesName   Ensemble du ou des {@link ERole nom(s) du/des rôle(s)} cibles à vérifier
     *                          (Utilisation d'une/des {@link String chaîne(s) de caractère}).
     * @param rolesNameToAdd    Ensemble du ou des {@link ERole nom(s) du/des rôle(s)} à ajouter si une correspondance est trouvée.
     * @param initialRoleName   {@link ERole Nom du rôle initial} à ajouter si une correspondance est trouvée.
     * @param searchNames       {@link String Nom}s des {@link Role rôle}s à rechercher dans la {@link Set liste} des {@link Role rôle}s cibles.
     */
    private static void checkRolesNameExist(Set<String> targetRolesName, Set<String> rolesNameToAdd, ERole initialRoleName, String ...searchNames) {

        if(initialRoleName == ERole.ROLE_UNKNOWN) return; // Si le rôle initial est `inconnu(e)` on ne fait rien

        // On ajoute à la liste les rôles dépendants, si la liste n'est pas vide
        if(StrHelper.containsIgnoreCase(targetRolesName, searchNames)) rolesNameToAdd.add(initialRoleName.name());
    }
}
