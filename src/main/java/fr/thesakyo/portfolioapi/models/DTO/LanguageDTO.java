package fr.thesakyo.portfolioapi.models.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.thesakyo.portfolioapi.enums.EStack;
import fr.thesakyo.portfolioapi.helpers.MapperHelper;
import fr.thesakyo.portfolioapi.models.entities.Language;
import fr.thesakyo.portfolioapi.models.entities.Project;
import jakarta.annotation.Nullable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LanguageDTO extends BaseEntityDTO<Language, LanguageDTO> implements Serializable {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    private String label; // Libellé du langage.

    @Enumerated(EnumType.STRING)
    private EStack stack; // Stack du langage (utilise une énumération de type : chaîne de caractère).

    @JsonIgnoreProperties({"user", "languages"})
    private Set<ProjectDTO> projects; // Liste des projet(s) associé(s) au langage.

    /*****************************************************************/
    /*****************    ⬇️   CONSTRUCTEUR    ⬇️   *****************/
    /****************************************************************/

    /**
     * Construit un nouveau {@link LanguageDTO langage}.
     */
    public LanguageDTO() {}

    /**
     * Construit un nouveau {@link LanguageDTO langage}.
     *
     * @param id {@link Long Identifiant} du {@link LanguageDTO langage}.
     * @param label Le {@link String libellé} du {@link LanguageDTO langage}.
     * @param stack La {@link EStack stack} du {@link LanguageDTO langage}.
     * @param projects La {@link Set liste} des {@link ProjectDTO projet}s associés à l'{@link UserDTO utilisateur}.
     */
    public LanguageDTO(Long id, String label, EStack stack, @Nullable Set<ProjectDTO> projects) {

        super(id);

        this.label = label;
        this.stack = stack;
        this.projects = projects;
    }

    /******************************************************/
    /**************   ⬇️    GETTERS    ⬇️   **************/
    /*****************************************************/

    /**
     * Récupère le {@link String libellé} du {@link LanguageDTO langage}.
     *
     * @return Le {@link String libellé} du {@link LanguageDTO langage}.
     */
    public String getLabel() { return label; }

    /**
     * Récupère le {@link EStack stack} du {@link LanguageDTO langage}.
     *
     * @return Le {@link EStack stack} du {@link LanguageDTO langage}.
     */
    public EStack getStack() { return stack; }

    /**
     * Récupère la {@link Set liste} des {@link ProjectDTO projet}s associés au {@link LanguageDTO langage}.
     *
     * @return La {@link Set liste} des {@link ProjectDTO projet}s associés au {@link LanguageDTO langage}.
     */
    public Set<ProjectDTO> getProjects() { return projects; }

    /*******************************************************************************************************/
    /***************************            ⬇️    CONVERSION    ⬇️              ***************************/
    /******************************************************************************************************/

    @Override
    @JsonIgnore
    public LanguageDTO convert(Language language) {

        Set<Project> projectsLanguage = language.getProjects();
        Set<ProjectDTO> projectsDTO = new HashSet<>();

        /****************************************************************/

        if(projectsLanguage != null && !projectsLanguage.isEmpty()) projectsDTO = projectsLanguage.stream().map(new ProjectDTO()::convert).collect(Collectors.toSet());

        /****************************************************************/

        return new LanguageDTO(language.getId(), language.getLabel(), language.getStack(), projectsDTO);
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
