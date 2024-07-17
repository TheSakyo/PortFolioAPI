package fr.thesakyo.portfolioapi.models.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.thesakyo.portfolioapi.helpers.MapperHelper;
import fr.thesakyo.portfolioapi.models.entities.Language;
import fr.thesakyo.portfolioapi.models.entities.Project;
import fr.thesakyo.portfolioapi.models.entities.User;
import jakarta.annotation.Nullable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ProjectDTO extends BaseEntityDTO<Project, ProjectDTO> implements Serializable {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    private String title; // Titre du projet.

    private String detail; // Détail du projet.

    private String link; // Lien du projet.

    @JsonIgnoreProperties({"projects"})
    private UserDTO user; // Utilisateur associé au projet.

    @JsonIgnoreProperties({"projects"})
    private Set<LanguageDTO> languages; // Liste des langages associés au projet.

    /*****************************************************************/
    /*****************    ⬇️   CONSTRUCTEUR    ⬇️   *****************/
    /****************************************************************/

    /**
     * Construit un nouvel {@link ProjectDTO projet}.
     */
    public ProjectDTO() {}

    /**
     * Construit un nouvel {@link User projet}.
     *
     * @param id {@link Long Identifiant} de l'{@link ProjectDTO projet}.
     * @param title Le {@link String titre} au {@link ProjectDTO projet}.
     * @param detail La {@link String description} au {@link ProjectDTO projet}.
     * @param link Le {@link String mot de passe} au {@link ProjectDTO projet}.
     * @param user L'{@link UserDTO utilisateur} associé au {@link ProjectDTO projet}.
     * @param languages La {@link Set liste} des {@link LanguageDTO langage}s associés au{@link ProjectDTO projet}.
     */
    public ProjectDTO(Long id, String title, String detail, String link, @Nullable UserDTO user, @Nullable Set<LanguageDTO> languages) {

        super(id);

        this.title = title;
        this.detail = detail;
        this.link = link;
        this.user = user;
        this.languages = languages;
    }

    /******************************************************/
    /**************   ⬇️    GETTERS    ⬇️   **************/
    /*****************************************************/

    /**
     * Récupère le {@link String titre} du {@link ProjectDTO projet}.
     *
     * @return Le {@link String titre} du {@link ProjectDTO projet}.
     */
    public String getTitle() { return title; }

    /**
     * Récupère le {@link String détail} du {@link ProjectDTO projet}.
     *
     * @return Le {@link String détail} du {@link ProjectDTO projet}.
     */
    public String getDetail() { return detail; }

    /**
     * Récupère le {@link String lien} du {@link ProjectDTO projet}.
     *
     * @return Le {@link String lien} du {@link ProjectDTO projet}.
     */
    public String getLink() { return link; }

    /**
     * Récupère l'{@link User utilisateur} associé au {@link ProjectDTO projet}.
     *
     * @return L'{@link User Utilisateur} associé au {@link ProjectDTO projet}.
     */
    public UserDTO getUser() { return user; }

    /**
     * Récupère la {@link Set liste} des {@link LanguageDTO langages}s associés au {@link ProjectDTO projet}.
     *
     * @return La {@link Set liste} des {@link LanguageDTO langages}s associés au {@link ProjectDTO projet}.
     */
    public Set<LanguageDTO> getLanguages() { return this.languages; }

    /*******************************************************************************************************/
    /***************************            ⬇️    CONVERSION    ⬇️              ***************************/
    /******************************************************************************************************/

    @Override
    @JsonIgnore
    public ProjectDTO convert(Project project) {

        Set<Language> languagesProject = project.getLanguages();

        Set<LanguageDTO> languagesDTO = new HashSet<>();
        UserDTO userDTO = null;

        /****************************************************************/


        if(project.getUser() != null) userDTO = new UserDTO().convert(project.getUser());
        if(languagesProject != null && !languagesProject.isEmpty()) {

            languagesDTO = languagesProject.stream().map(new LanguageDTO()::convert).collect(Collectors.toSet());
        }

        /****************************************************************/

        return new ProjectDTO(project.getId(), project.getTitle(), project.getDetail(), project.getLink(), userDTO, languagesDTO);
    }

    /************************************************************************************/
    /************************************************************************************/

    /**
     * Convertit l'{@link Object objet} de l'{@link ProjectDTO projet} en {@link String chaîne de caractère}.
     *
     * @return Une {@link String chaîne de caractère} de notre {@link ProjectDTO projet}.
     */
    @Override
    public String toString() { return MapperHelper.readJsonFromObjectAsString(this); }
}
