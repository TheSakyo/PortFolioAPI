package fr.thesakyo.portfolioapi.payloads.requests.authentication.user;

import fr.thesakyo.portfolioapi.models.entities.Role;
import fr.thesakyo.portfolioapi.helpers.MapperHelper;
import fr.thesakyo.portfolioapi.payloads.requests.authentication.BaseRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Set;

public class UpdateUserRequest implements BaseRequest, Serializable {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    private String name; // Nom depuis le formulaire de modification.

    @Email
    private String email; // Adresse e-mail depuis le formulaire de modification.

    @Size(min = 5)
    @Size(max = 120)
    private String password; // Mot de passe (crypté) depuis le formulaire de modification.

    private Set<String> roles; // Liste des rôles associés 0 l'utilisateur depuis le formulaire de modification.

    /****************************************************************/
    /**************   ⬇️    GETTERS & SETTERS    ⬇️   **************/
    /***************************************************************/

    /**
     * Récupère le {@link String nom} depuis le formulaire de modification.
     *
     * @return Le {@link String nom} depuis le formulaire de modification.
     */
    @Override
    public String getName() { return name; }

    /**
     * Récupère l'{@link String adresse e-mail} depuis le formulaire de modification.
     *
     * @return L'{@link String adresse e-mail} depuis le formulaire de modification.
     */
    @Override
    public String getEmail() { return email; }

    /**
     * Récupère le {@link String mot de passe} (crypté) depuis le formulaire de modification.
     *
     * @return Le {@link String mot de passe} (crypté) depuis le formulaire de modification.
     */
    @Override
    public String getPassword() { return password; }

    /**
     * Récupère la {@link Set liste} des {@link String nom}s des {@link Role rôle}s qui seront associés à l'utilisateur depuis le formulaire de modification.
     *
     * @return  La {@link Set liste} des {@link String nom}s des {@link Role rôle}s qui seront associés à l'utilisateur depuis le formulaire de modification.
     */
    @Override
    public Set<String> getRoles() { return roles; }

                    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
                    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
                    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

    /**
     * Définit le {@link String nom} depuis le formulaire de modification.
     *
     * @param name Le {@link String nom} depuis le formulaire de modification.
     */
    @Override
    public void setName(String name) { this.name = name; }

    /**
     * Définit l'{@link String adresse e-mail} depuis le formulaire de modification.
     *
     * @param email L'{@link String adresse e-mail} depuis le formulaire de modification.
     */
    @Override
    public void setEmail(String email) { this.email = email; }

    /**
     * Définit le {@link String mot de passe} (crypté) depuis le formulaire de modification.
     *
     * @param password Le {@link String mot de passe} (crypté) depuis le formulaire de modification.
     */
    @Override
    public void setPassword(String password) { this.password = password; }

    /**
     * Définit la {@link Set liste} des {@link String nom}s des {@link Role rôle}s qui seront associés à l'utilisateur depuis le formulaire de modification.
     *
     * @param roles La {@link Set liste} des {@link String nom}s des {@link Role rôle}s qui seront associés à l'utilisateur depuis le formulaire de modification.
     */
    @Override
    public void setRoles(Set<String> roles) { this.roles = roles; }

    /******************************************************************************************************************/
    /******************************************************************************************************************/
    /******************************************************************************************************************/

    /**
     * Convertit l'{@link Object objet} de la {@link UpdateUserRequest requête} en {@link String chaîne de caractère}.
     *
     * @return Une {@link String chaîne de caractère} de notre {@link UpdateUserRequest objet de requête}.
     */
    @Override
    public String toString() { return MapperHelper.readJsonFromObjectAsString(this); }
}
