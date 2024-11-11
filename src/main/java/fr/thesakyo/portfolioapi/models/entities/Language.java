package fr.thesakyo.portfolioapi.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.thesakyo.portfolioapi.enums.EStack;
import fr.thesakyo.portfolioapi.helpers.MapperHelper;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "languages", uniqueConstraints = { @UniqueConstraint(columnNames = "label") })
public class Language extends BaseEntity implements Serializable {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    @NotBlank
    private String label; // Libellé du langage.

    @NotBlank
    @Enumerated(EnumType.STRING)
    private EStack stack; // Stack du langage (Front, Back, FullStack)

    @ManyToMany(mappedBy = "languages")
    @JsonIgnoreProperties({"languages"})
    @OrderBy("id DESC")
    private Set<Project> projects; // Liste des projets associés au langage.

    /***********************************************************/
    /**************   ⬇️    CONSTRUCTEUR    ⬇️   **************/
    /**********************************************************/

    /**
     * Construit un nouveau {@link Language langage}.
     */
    public Language() {}

    /**
     * Construit un nouveau {@link Language langage}.
     *
     * @param label Le {@link String libellé} du {@link Language langage}.
     * @param stack La {@link EStack stack} du {@link Language langage}.
     * @param projects La {@link Set liste} des {@link Project projet}s associés au {@link Language langage}.
     */
    public Language(String label, EStack stack, Set<Project> projects) {

        this.label = label;
        this.stack = stack;
        this.projects = projects;
    }

    /****************************************************************/
    /**************   ⬇️    GETTERS & SETTERS    ⬇️   **************/
    /***************************************************************/

    /**
     * Récupère le {@link String libellé} du {@link Language langage}.
     *
     * @return Le {@link String libellé} du {@link Language langage}.
     */
    public String getLabel() { return label; }

    /**
     * Récupère la {@link EStack stack} du {@link Language langage}.
     * La {@link EStack stack} peut uniquement être les valeurs suivantes : 'BACK_END', 'FRONT_END' ou 'FULL_STACK'.
     *
     * @return La {@link EStack stack} du {@link Language langage}.
     */
    public EStack getStack() { return stack; }

    /**
     * Récupère la {@link Set liste} des {@link Project projet}s associés au {@link Language langage}.
     *
     * @return La {@link Set liste} des {@link Project projet}s associés au {@link Language langage}.
     */
    public Set<Project> getProjects() { return projects; }

                    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
                    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
                    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

    /**
     * Définit le {@link String libellé} du {@link Language langage}.
     *
     * @param label Le {@link String libellé} du {@link Language langage}.
     */
    public void setLabel(@NotBlank String label) { this.label = label; }

    /**
     * Définit la {@link EStack stack} du {@link Language langage}.
     * La {@link EStack stack} peut prendre les valeurs suivantes : 'BACK_END', 'FRONT_END' ou 'FULL_STACK'.
     *
     * @param stack La {@link EStack stack} du {@link Language langage}.
     */
    public void setStack(@NotBlank EStack stack) { this.stack = stack; }

    /**
     * Définit la {@link Set liste} des {@link Project projet}s associés au {@link Language langage}.
     *
     * @param projects La {@link Set liste} des {@link Project projet}s associés au {@link Language langage}.
     */
    public void setProjects(Set<Project> projects) { this.projects = projects; }

    /******************************************************************************************************************/
    /******************************************************************************************************************/
    /******************************************************************************************************************/

    /**
     * Convertit l'{@link Object objet} de l'{@link Language entité} en {@link String chaîne de caractère}.
     *
     * @return Une {@link String chaîne de caractère} de notre {@link Language entité}.
     */
    @Override
    public String toString() { return MapperHelper.readJsonFromObjectAsString(this); }
}
