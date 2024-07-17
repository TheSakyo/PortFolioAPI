package fr.thesakyo.portfolioapi.models.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.thesakyo.portfolioapi.helpers.MapperHelper;
import fr.thesakyo.portfolioapi.models.entities.User;
import fr.thesakyo.portfolioapi.models.entities.Role;
import fr.thesakyo.portfolioapi.models.entities.Project;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDTO extends BaseEntityDTO<User, UserDTO> implements Serializable {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    private String name; // Nom de l'utilisateur.

    private String email; // Adresse e-mail de l'utilisateur.

    @JsonIgnoreProperties({"user", "languages"})
    private Set<ProjectDTO> projects; // Liste des projet(s) de l'utilisateur.

    private Set<RoleDTO> roles; // Liste des rôle(s) de l'utilisateur.

    /*****************************************************************/
    /*****************    ⬇️   CONSTRUCTEUR    ⬇️   *****************/
    /****************************************************************/

    /**
     * Construit un nouvel {@link UserDTO utilisateur}.
     */
    public UserDTO() {}

    /**
     * Construit un nouvel {@link User utilisateur}.
     *
     * @param id {@link Long Identifiant} de l'{@link UserDTO utilisateur}.
     * @param name Le {@link String nom} de l'{@link UserDTO utilisateur}.
     * @param email L'{@link String adresse e-mail} de l'{@link UserDTO utilisateur}.
     * @param projects La {@link Set liste} des {@link ProjectDTO projet}s associés à l'{@link UserDTO utilisateur}.
     * @param roles La {@link Set liste} des {@link RoleDTO rôle}s associés à l'{@link UserDTO utilisateur}.
     */
    public UserDTO(Long id, String name, String email, @Nullable Set<ProjectDTO> projects, @Nullable Set<RoleDTO> roles) {

        super(id);

        this.name = name;
        this.email = email;
        this.projects = projects;
        this.roles = roles;
    }

    /******************************************************/
    /**************   ⬇️    GETTERS    ⬇️   **************/
    /*****************************************************/

    /**
     * Récupère le {@link String nom} de l'{@link UserDTO utilisateur}.
     *
     * @return Le {@link String nom} de l'{@link UserDTO utilisateur}.
     */
    public String getName() { return name; }

    /**
     * Récupère l'{@link String adresse e-mail} de l'{@link UserDTO utilisateur}.
     *
     * @return L'{@link String adresse e-mail} de l'{@link UserDTO utilisateur}.
     */
    public String getEmail() { return email; }

    /**
     * Récupère la {@link Set liste} des {@link ProjectDTO projet}s associés à l'{@link UserDTO utilisateur}.
     *
     * @return La {@link Set liste} des {@link ProjectDTO projet}s associés à l'{@link UserDTO utilisateur}.
     */
    public Set<ProjectDTO> getProjects() { return projects; }

    /**
     * Récupère la {@link Set liste} des {@link RoleDTO rôle}s associés à l'{@link UserDTO utilisateur}.
     *
     * @return La {@link Set liste} des {@link RoleDTO rôle}s associés à l'{@link UserDTO utilisateur}.
     */
    public Set<RoleDTO> getRoles() { return roles; }

    /*******************************************************************************************************/
    /***************************            ⬇️    CONVERSION    ⬇️              ***************************/
    /******************************************************************************************************/

    @Override
    @JsonIgnore
    public UserDTO convert(User user) {

        Set<Role> rolesUser = user.getRoles();
        Set<Project> projectsUser = user.getProjects();

        Set<ProjectDTO> projectsDTO = new HashSet<>();
        Set<RoleDTO> rolesDTO = new HashSet<>();

        /****************************************************************/

        if(projectsUser != null && !projectsUser.isEmpty()) projectsDTO = projectsUser.stream().map(new ProjectDTO()::convert).collect(Collectors.toSet());
        if(rolesUser != null && !rolesUser.isEmpty()) rolesDTO = rolesUser.stream().map(new RoleDTO()::convert).collect(Collectors.toSet());

        /****************************************************************/

        return new UserDTO(user.getId(), user.getName(), user.getEmail(), projectsDTO, rolesDTO);
    }

    /************************************************************************************/
    /************************************************************************************/

    /**
     * Convertit l'{@link Object objet} de l'{@link UserDTO utilisateur} en {@link String chaîne de caractère}.
     *
     * @return Une {@link String chaîne de caractère} de notre {@link UserDTO utilisateur}.
     */
    @Override
    public String toString() { return MapperHelper.readJsonFromObjectAsString(this); }
}
