package fr.thesakyo.portfolioapi.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.thesakyo.portfolioapi.helpers.MapperHelper;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "email") })
public class User extends BaseEntity implements Serializable {

    /*********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /********************************************************/

    @NotBlank
    private String name; // Nom de l'utilisateur.

    @NotBlank
    @Email
    private String email; // Adresse e-mail de l'utilisateur.

    @NotBlank
    @Size(min = 5)
    @Size(max = 120)
    @JsonIgnore
    private String password; // Mot de passe (crypté) de l'utilisateur.

    @NotNull
    private boolean verificationEnabled; // Est-ce que l'utilisateur est vérifié ?

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties({"user"})
    @OrderBy("id DESC")
    private Set<Project> projects; // Liste des projets associés à l'utilisateur.

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @OrderBy("id ASC")
    @NotNull
    @NotEmpty
    private Set<Role> roles; // Liste des rôles associés à l'utilisateur.

    /***********************************************************/
    /**************   ⬇️    CONSTRUCTEUR    ⬇️   **************/
    /**********************************************************/

    /**
     * Construit un nouvel {@link User utilisateur}.
     */
    public User() {}

    /**
     * Construit un nouvel {@link User utilisateur}.
     *
     * @param name Le {@link String nom} de l'{@link User utilisateur}.
     * @param email L'{@link String Adresse e-mail} de l'{@link User utilisateur}.
     * @param password Le {@link String mot de passe} de l'{@link User utilisateur}.
     * @param verificationEnabled Une valeur booléenne disant si l'{@link User utilisateur} est bien vérifié.
     * @param projects La {@link Set liste} des {@link Project projet}s associés à l'{@link User utilisateur}.
     * @param roles La {@link Set liste} des {@link Role role}s associés à l'{@link User utilisateur}.
     */
    public User(String name, String email, String password, boolean verificationEnabled, Set<Project> projects, Set<Role> roles) {

        this.name = name;
        this.email = email;
        this.password = password;
        this.verificationEnabled = verificationEnabled;
        this.projects = projects;
        this.roles = roles;
    }

    /****************************************************************/
    /**************   ⬇️    GETTERS & SETTERS    ⬇️   **************/
    /***************************************************************/

    /**
     * Récupère le {@link String nom} de l'{@link User utilisateur}.
     *
     * @return Le {@link String nom} de l'{@link User utilisateur}.
     */
    @NotBlank
    public String getName() { return name; }

    /**
     * Récupère l'{@link String adresse e-mail} de l'{@link User utilisateur}.
     *
     * @return L'{@link String adresse e-mail} de l'{@link User utilisateur}.
     */
    @NotBlank @Email
    public String getEmail() { return email; }

    /**
     * Récupère le {@link String mot de passe} (crypté) de l'{@link User utilisateur}.
     *
     * @return Le {@link String mot de passe} (crypté) de l'{@link User utilisateur}.
     */
    @NotBlank @Size(min = 5) @Size(max = 120)
    public String getPassword() { return password; }

    /**
     * Vérifie si l'{@link User utilisateur} est vérifié.
     *
     * @return Une valeur booléenne indiquant si l'{@link User utilisateur} est vérifié.
     */
    @NotNull
    public boolean getVerificationEnabled() { return verificationEnabled; }

    /**
     * Récupère la {@link Set liste} des {@link Project projet}s associés à l'{@link User utilisateur}.
     *
     * @return La {@link Set liste} des {@link Project projet}s associés à l'{@link User utilisateur}.
     */
    public Set<Project> getProjects() { return projects; }

    /**
     * Récupère la {@link Set liste} des {@link Role rôle}s associés à l'{@link User utilisateur}.
     *
     * @return La {@link Set liste} des {@link Role rôle}s associés à l'{@link User utilisateur}.
     */
    public Set<Role> getRoles() { return roles; }

                    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
                    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
                    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

    /**
     * Définit le {@link String nom} de l'{@link User utilisateur}.
     *
     * @param name Le {@link String nom} de l'{@link User utilisateur}.
     */
    public void setName(@NotBlank String name) { this.name = name; }

    /**
     * Définit l'{@link String adresse e-mail} de l'{@link User utilisateur}.
     *
     * @param email L'{@link String adresse e-mail} de l'{@link User utilisateur}.
     */
    public void setEmail(@NotBlank @Email String email) { this.email = email; }

    /**
     * Définit le {@link String mot de passe} (crypté) de l'{@link User utilisateur}.
     *
     * @param password Le {@link String mot de passe} (crypté) de l'{@link User utilisateur}.
     */
    public void setPassword(@NotBlank @Size(min = 5) @Size(max = 120) String password) { this.password = password; }

    /**
     * Définit si l'{@link User utilisateur} est vérifié.
     *
     * @param verificationEnabled Une valeur booléenne indiquant si l'{@link User utilisateur} est vérifié.
     */
    @NotNull
    public void setVerificationEnabled(boolean verificationEnabled) { this.verificationEnabled = verificationEnabled; }

    /**
     * Définit la {@link Set liste} des {@link Project projet}s associés à l'{@link User utilisateur}.
     *
     * @param projects La {@link Set liste} des {@link Project projet}s associés à l'{@link User utilisateur}.
     */
    public void setProjects(Set<Project> projects) { this.projects = projects; }

    /**
     * Définit la {@link Set liste} des {@link Role rôle}s associés à l'{@link User utilisateur}.
     *
     * @param roles La {@link Set liste} des {@link Role rôle}s associés à l'{@link User utilisateur}.
     */
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    /******************************************************************************************************************/
    /******************************************************************************************************************/
    /******************************************************************************************************************/

    /**
     * Convertit l'{@link Object objet} de l'{@link Role entité} en {@link String chaîne de caractère}.
     *
     * @return Une {@link String chaîne de caractère} de notre {@link Role entité}.
     */
    @Override
    public String toString() { return MapperHelper.readJsonFromObjectAsString(this); }
}
