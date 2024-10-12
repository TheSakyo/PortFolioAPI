package fr.thesakyo.portfolioapi.models.entities;

import fr.thesakyo.portfolioapi.enums.ERole;
import fr.thesakyo.portfolioapi.helpers.MapperHelper;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

@Entity
@Table(name = "roles", uniqueConstraints = { @UniqueConstraint(columnNames = "name") })
public class Role extends BaseEntity implements Serializable  {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    @NotBlank
    @Enumerated(EnumType.STRING)
    private ERole name; // Nom du rôle (utilise une énumération de type : chaîne de caractère).

    private Integer severity = 0; // Le niveau de gravité du rôle

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description; // Description du rôle

    /*****************************************************************/
    /*****************    ⬇️   CONSTRUCTEUR    ⬇️   *****************/
    /****************************************************************/

    /**
     * Construit un nouveau {@link Role rôle}.
     */
    public Role() {}

    /**
     * Construit un nouveau {@link Role rôle}.
     *
     * @param name Le {@link ERole nom} du {@link Role rôle}.
     * @param severity Le {@link Integer niveau de gravité} du {@link Role rôle}.
     * @param description Le {@link String détail} du {@link Role rôle}.
     */
    public Role(ERole name, Integer severity, String description) {

        this.name = name;
        this.severity = severity;
        this.description = description;
    }

    /****************************************************************/
    /**************   ⬇️    GETTERS & SETTERS    ⬇️   **************/
    /***************************************************************/

    /**
     * Récupère le {@link ERole nom} du {@link Role rôle}.
     *
     * @return Le {@link ERole nom} du {@link Role rôle}.
     */
    public ERole getName() { return name; }

    /**
     * Récupère le {@link Integer niveau de gravité} du {@link Role rôle}.
     *
     * @return La {@link Integer niveau de gravité} du {@link Role rôle}.
     */
    public int getSeverity() { return severity; }

    /**
     * Récupère la {@link String description} du {@link Role rôle}.
     *
     * @return La {@link String description} du {@link Role rôle}.
     */
    public String getDescription() { return description; }

                    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
                    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
                    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

    /**
     * Définit le {@link ERole nom} du {@link Role rôle}.
     *
     * @param name Le {@link ERole nom} du {@link Role rôle}.
     */
    public void setName(ERole name) { this.name = name; }

    /**
     * Définit le {@link Integer niveau de gravité} du {@link Role rôle}.
     *
     * @param severity La {@link Integer niveau de gravité} du {@link Role rôle}.
     */
    public void setSeverity(int severity) { this.severity = severity; }

    /**
     * Définit la {@link String description} du {@link Role rôle}.
     *
     * @param description La {@link String description} du {@link Role rôle}.
     */
    public void setDescription(String description) { this.description = description; }

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
