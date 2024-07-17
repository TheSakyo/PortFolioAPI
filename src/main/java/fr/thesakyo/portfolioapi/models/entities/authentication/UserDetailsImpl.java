package fr.thesakyo.portfolioapi.models.entities.authentication;

import fr.thesakyo.portfolioapi.models.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    @Serial
    private static final long serialVersionUID = 1L; // Valeur unique pour rendre un objet serializable.

    /*********************************************************/

    private final Long id; // L'Identifiant de l'utilisateur authentifié.

    private final String name; // Le nom de l'utilisateur authentifié.

    private final String email; // L'adresse e-mail de l'utilisateur authentifié.

    @JsonIgnore
    private String password; // Un mot de passe (crypté) de l'utilisateur authentifié.

    @JsonIgnore
    private boolean isEnabled; // L'utilisateur authentifié est-il bien enregistré.

    private final Collection<? extends GrantedAuthority> authorities; // Une collection d'autorisation pour l'utilisateur.

    /*****************************************************************/
    /*****************    ⬇️   CONSTRUCTEUR    ⬇️   *****************/
    /****************************************************************/

    /**
    * Construit une nouvelle connexion pour l'{@link User utilisateur}.
    *
    * @param id L'{@link Long Identification} de l'{@link User utilisateur}.
    * @param name Le {@link String nom} de l'{@link User utilisateur}.
    * @param email L'{@link String adresse e-mail} de l'{@link User utilisateur}.
    * @param password Le {@link String mot de passe} (crypté) de l'{@link User utilisateur}.
    * @param isEnabled Une valeur booléenne disant si l'{@link User utilisateur} est bien vérifié.
    * @param authorities Les {@link String autorisations} de l'{@link User utilisateur}.
    */
    public UserDetailsImpl(Long id, String name, String email, String password, boolean isEnabled, Collection<? extends GrantedAuthority> authorities) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.isEnabled = isEnabled;
        this.authorities = authorities;
    }

    /*****************************************************/
    /*****************************************************/

    /**
    * Construit une nouvelle connexion pour l'{@link User utilisateur} à partir d'un objet '{@link User utilisateur}'.
    *
    * @param user L'Objet '{@link User utilisateur}' dont il est question.
    *
    * @return Un objet de {@link UserDetailsImpl détails de l'{@link User utilisateur}} pour sa connexion à partir d'un objet '{@link User utilisateur}'.
    */
    public static UserDetailsImpl build(User user) {

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(user.getId(), user.getName(), user.getEmail(), user.getPassword(), user.getVerificationEnabled(), authorities);
    }

    /****************************************************************/
    /**************   ⬇️    GETTERS & SETTERS    ⬇️   **************/
    /***************************************************************/

    /**
    * Récupère une collection d'autorisation pour l'{@link User utilisateur} authentifié.
    *
    * @return Une collection d'autorisation pour l'{@link User utilisateur} authentifié.
    */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    /**
     * Récupère l'{@link Long identifiant} de l'{@link User utilisateur} authentifié.
     *
     * @return L'{@link Long Identifiant} de l'{@link User utilisateur} authentifié.
     */
    public Long getId() { return id; }

    /**
     * Récupère le {@link String nom} de l'{@link User utilisateur} authentifié.
     *
     * @return Le {@link String nom} de l'{@link User utilisateur} authentifié.
     */
    public String getName() { return name; }

    /**
     * Récupère le {@link String nom d'utilisateur} (adresse e-mail) de l'{@link User utilisateur} authentifié.
     *
     * @return Le {@link String nom d'utilisateur} (adresse e-mail) de l'{@link User utilisateur} authentifié.
     */
    @Override
    public String getUsername() { return email; }

    /**
     * Récupère le {@link String mot de passe} (crypté) de l'{@link User utilisateur} authentifié.
     *
     * @return Le {@link String mot de passe} (crypté) de l'{@link User utilisateur} authentifié.
     */
    @Override
    public String getPassword() { return password; }

    /**
     * Vérifie si le compte authentifié est vérifié.
     *
     * @return Une {@link Boolean valeur booléenne}.
     */
    @Override
    public boolean isEnabled() { return isEnabled; }

    /*******************************************************/
    /*******************************************************/

    /**
     * Vérifie si le compte authentifié n'est pas expiré.
     *
     * @return Une {@link Boolean valeur booléenne}.
     */
    @Override
    public boolean isAccountNonExpired() { return true; }

    /**
     * Vérifie si le compte authentifié n'est pas bloqué.
     *
     * @return Une {@link Boolean valeur booléenne}.
     */
    @Override
    public boolean isAccountNonLocked() { return true; }

    /**
     * Vérifie si le compte authentifié n'a pas son jeton d'authentification expiré.
     *
     * @return Une {@link Boolean valeur booléenne}.
     */
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    /****************************************************************************/

    /**
     * Vérifie si un {@link Object objet} donné peut être égale à l'{@link UserDetailsImpl objet actuel}.
     *
     * @param o L'{@link Object Objet} à vérifier l'égalité.
     *
     * @return Une {@link Boolean valeur booléenne}.
     */
    @Override
    public boolean equals(Object o) {

        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        UserDetailsImpl user = (UserDetailsImpl)o;
        return Objects.equals(id, user.id);
    }
}
