package fr.thesakyo.portfolioapi.helpers;

import fr.thesakyo.portfolioapi.models.entities.Language;
import fr.thesakyo.portfolioapi.models.entities.Role;
import fr.thesakyo.portfolioapi.models.entities.User;
import fr.thesakyo.portfolioapi.models.entities.Project;

import fr.thesakyo.portfolioapi.enums.ERole;
import fr.thesakyo.portfolioapi.repositories.LanguageRepository;
import fr.thesakyo.portfolioapi.security.UserConnection;

public abstract class PermissionHelper {

    /****************************************************************************************/
    /*************   ⬇️    MÉTHODES UTILES EN RAPPORT AVEC LES PERMISSIONS  ⬇️   ***********/
    /***************************************************************************************/

    /**
     * Méssage d'erreur disant que l'utilisateur n'a pas l'autorisation de faire ceci.
     */
    public static String UNAUTHORIZED_MESSAGE = "Non autorisé : Vous n'avez pas l'autorisation de faire ceci.";

    /********************************************************************************************/

    /**
     * Vérifie si l'{@link User utilisateur} connecté à le {@link Role rôle} dont il est question.
     * Si l'utilisateur n'est pas vérifié, la fonction renvoie 'faux'.
     *
     * @param targetRole Le {@link Role rôle} à vérifier.
     *
     * @return Une valeur booléenne vérifiant si l'{@link User utilisateur} connecté à le {@link Role rôle} demandé.
     */
    public static boolean userHasRole(ERole targetRole) {

        if(UserConnection.getUserLogged() == null) return false;
        return UserConnection.getUserLogged().getVerificationEnabled() &&
                UserConnection.getUserLogged().getRoles().stream().anyMatch(role -> role.getName().equals(targetRole));
    }

    /**
     * Vérifie si l'{@link User utilisateur} connecté à le {@link Project projet} dont il est question.
     *
     * @param targetProject Le {@link Project projet} à vérifier.
     *
     * @return Une valeur booléenne vérifiant si l'{@link User utilisateur} connecté à le {@link Project projet} demandé.
     */
    public static boolean userHasProjectPermission(Project targetProject) {

        if(UserConnection.getUserLogged() == null) return false;
        return UserConnection.getUserLogged().getProjects().contains(targetProject) || userHasRole(ERole.ROLE_SUPERADMIN);
    }

    /**
     * Vérifie si l'{@link User utilisateur} connecté à le {@link Language langage} dont il est question dans ses {@link Project projet}s associés.
     *
     * @param targetLanguage Le {@link Language langage} à vérifier.
     * @param languageRepository Le {@link LanguageRepository référentiel du langage} pour rechercher en base de données.
     *
     * @return Une valeur booléenne vérifiant si l'{@link User utilisateur} connecté à le {@link Language langage} demandé dans ses {@link Project projet}s associés.
     */
    public static boolean userHasLanguageInProjetsPermission(Language targetLanguage, LanguageRepository languageRepository) {

        if(UserConnection.getUserLogged() == null) return false;
        return languageRepository.existsUserByIdAndLanguageIdInProjects(targetLanguage.getId(), UserConnection.getUserLogged().getId()) || userHasRole(ERole.ROLE_SUPERADMIN);
    }
}
