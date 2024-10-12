package fr.thesakyo.portfolioapi.payloads.requests.authentication;

import fr.thesakyo.portfolioapi.models.entities.Role;

import java.io.Serializable;
import java.util.Set;

public interface BaseRequest extends Serializable {

    /****************************************************************/
    /**************   ⬇️    GETTERS & SETTERS    ⬇️   **************/
    /***************************************************************/

    /**
     * Récupère le {@link String nom}.
     *
     * @return Le {@link String nom}.
     */
     default String getName() { return null; }

    /**
     * Récupère l'{@link String adresse e-mail}.
     *
     * @return L'{@link String adresse e-mail}.
     */
     default String getEmail() { return null; }

    /**
     * Récupère le {@link String mot de passe} (crypté).
     *
     * @return Le {@link String mot de passe} (crypté).
     */
     default String getPassword() { return null; }

    /**
     * Récupère la {@link Set liste} des {@link String nom}s des {@link Role rôle}s qui seront associés à l'utilisateur.
     *
     * @return  La {@link Set liste} des {@link String nom}s des {@link Role rôle}s qui seront associés à l'utilisateur.
     */
     default Set<String> getRoles() { return null; }

    /**
     * Vérifie si le compte de l'utilisateur authentifié est vérifié.
     *
     * @return Une {@link Boolean valeur booléenne}.
     */
    default boolean getVerificationEnabled() { return false; }

                    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
                    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
                    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

    /**
     * Définit le {@link String nom}.
     *
     */
    default void setName(String name) {}

    /**
     * Définit l'{@link String adresse e-mail}.
     *
     * @param email L'{@link String adresse e-mail}.
     */
    default void setEmail(String email) {}

    /**
     * Définit le {@link String mot de passe} (crypté).
     *
     * @param password Le {@link String mot de passe} (crypté).
     */
    default void setPassword(String password) {}

    /**
     * Définit la {@link Set liste} des {@link String nom}s des {@link Role rôle}s qui seront associés à l'utilisateur.
     *
     * @param roles La {@link Set liste} des {@link String nom}s des {@link Role rôle}s qui seront associés à l'utilisateur.
     */
    default void setRoles(Set<String> roles) {}

    /**
     * Définit si le compte de l'utilisateur authentifié est vérifié.
     *
     * @param isEnabled Le compte de l'utilisateur authentifié est-il vérifié ?
     */
    default void setVerificationEnabled(boolean isEnabled) {}
}
