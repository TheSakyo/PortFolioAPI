package fr.thesakyo.portfolioapi.models.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.thesakyo.portfolioapi.enums.ERole;
import fr.thesakyo.portfolioapi.helpers.MapperHelper;
import fr.thesakyo.portfolioapi.models.entities.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.io.Serializable;


public class RoleDTO extends BaseEntityDTO<Role, RoleDTO> implements Serializable  {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    @Enumerated(EnumType.STRING)
    private ERole name; // Nom du rôle (utilise une énumération de type : chaîne de caractère).

    private Integer severity = 0; // Le niveau de gravité du rôle.

    private String description; // Description du rôle.

    /*****************************************************************/
    /*****************    ⬇️   CONSTRUCTEUR    ⬇️   *****************/
    /****************************************************************/

    /**
     * Construit un nouveau {@link RoleDTO rôle}.
     */
    public RoleDTO() {}

    /**
     * Construit un nouveau {@link RoleDTO rôle}.
     *
     * @param id {@link Long Identifiant} du {@link RoleDTO rôle}.
     * @param name Le {@link ERole nom} du {@link RoleDTO rôle}.
     * @param severity Le {@link Integer niveau de gravité} du {@link RoleDTO rôle}.
     * @param description Le {@link String détail} du {@link RoleDTO rôle}.
     */
    public RoleDTO(Long id, ERole name, Integer severity, String description) {

        super(id);

        this.name = name;
        this.severity = severity;
        this.description = description;
    }

    /******************************************************/
    /**************   ⬇️    GETTERS    ⬇️   **************/
    /*****************************************************/

    /**
     * Récupère le {@link ERole nom} du {@link RoleDTO rôle}.
     *
     * @return Le {@link ERole nom} du {@link RoleDTO rôle}.
     */
    public ERole getName() { return name; }

    /**
     * Récupère le {@link Integer niveau de gravité} du {@link RoleDTO rôle}.
     *
     * @return La {@link Integer niveau de gravité} du {@link RoleDTO rôle}.
     */
    public int getSeverity() { return severity; }

    /**
     * Récupère la {@link String description} du {@link RoleDTO rôle}.
     *
     * @return La {@link String description} du {@link RoleDTO rôle}.
     */
    public String getDescription() { return description; }

    /*******************************************************************************************************/
    /***************************            ⬇️    CONVERSION    ⬇️              ***************************/
    /******************************************************************************************************/

    @Override
    @JsonIgnore
    public RoleDTO convert(Role role) { return new RoleDTO(role.getId(), role.getName(), role.getSeverity(), role.getDescription()); }


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
