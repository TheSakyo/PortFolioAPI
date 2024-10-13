package fr.thesakyo.portfolioapi.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.thesakyo.portfolioapi.helpers.MapperHelper;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "projects")
public class Project extends BaseEntity implements Serializable {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    @NotBlank
    private String title; // Titre du projet.

    @NotBlank
    @Column(name = "detail", length = Integer.MAX_VALUE)
    private String detail; // Détail du projet.

    @NotBlank
    @URL(regexp = "^(http|https)://.*$", message = "Format d'URL Invalide")
    private String link; // Lien du projet.

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"projects"})
    @OrderBy("id DESC")
    private User user; // Relation de l'utilisateur associé au projet.

    @ManyToMany()
    @JoinTable(
            name = "project_languages",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id"))
    @JsonIgnoreProperties({"projects"})
    @OrderBy("id DESC")
    private Set<Language> languages; // Liste des langages associés au projet.

    //private Image image

    /***********************************************************/
    /**************   ⬇️    CONSTRUCTEUR    ⬇️   **************/
    /**********************************************************/

    /**
     * Construit un nouveau {@link Project projet}.
     */
    public Project() {}

    /**
     * Construit un nouveau {@link Project projet}.
     *
     * @param title Le {@link String titre} au {@link Project projet}.
     * @param detail La {@link String description} au {@link Project projet}.
     * @param link Le {@link String mot de passe} au {@link Project projet}.
     * @param user L'{@link User utilisateur} associé au {@link Project projet}.
     * @param languages La {@link Set liste} des {@link Language langage}s associés au{@link Project projet}.
     */
    public Project(String title, String detail, String link, User user, Set<Language> languages) {

        this.title = title;
        this.detail = detail;
        this.link = link;
        this.user = user;
        this.languages = languages;
    }

    /****************************************************************/
    /**************   ⬇️    GETTERS & SETTERS    ⬇️   **************/
    /***************************************************************/

    /**
     * Récupère le {@link String titre} du {@link Project projet}.
     *
     * @return Le {@link String titre} du {@link Project projet}.
     */
    @NotBlank
    public String getTitle() { return title; }

    /**
     * Récupère le {@link String détail} du {@link Project projet}.
     *
     * @return Le {@link String détail} du {@link Project projet}.
     */
    @NotBlank
    public String getDetail() { return detail; }

    /**
     * Récupère le {@link String lien} du {@link Project projet}.
     *
     * @return Le {@link String lien} du {@link Project projet}.
     */
    @NotBlank @Pattern(regexp = "^(http|https)://.*$", message = "Format d'URL Invalide")
    public String getLink() { return link; }

    /**
     * Récupère l'{@link User utilisateur} associé au {@link Project projet}.
     *
     * @return L'{@link User Utilisateur} associé au {@link Project projet}.
     */
    public User getUser() { return user; }

    /**
     * Récupère la {@link Set liste} des {@link Language langage}s associés au {@link Project projet}.
     *
     * @return La {@link Set liste} des {@link Language langage}s associés au {@link Project projet}.
     */
    public Set<Language> getLanguages() { return languages; }
                
                    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
                    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
                    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

    /**
     * Définit le {@link String titre} du {@link Project projet}.
     *
     * @param title Le {@link String titre} du {@link Project projet}.
     */
    public void setTitle(@NotBlank String title) { this.title = title; }

    /**
     * Définit le {@link String détail} du {@link Project projet}.
     *
     * @param detail Le {@link String détail} du {@link Project projet}.
     */
    public void setDetail(@NotBlank String detail) { this.detail = detail; }

    /**
     * Définit le {@link String lien} du {@link Project projet}.
     *
     * @param link Le {@link String lien} du {@link Project projet}.
     */
    public void setLink(@NotBlank @Pattern(regexp = "^(http|https)://.*$", message = "Format d'URL Invalide") String link) { this.link = link; }

    /**
     * Définit l'{@link User utilisateur} associé au {@link Project projet}.
     *
     * @param user L'{@link User Utilisateur} associé au {@link Project projet}.
     */
    public void setUser(User user) { this.user = user; }

    /**
     * Définit la {@link Set liste} des {@link Language langage}s associés au {@link Project projet}.
     *
     * @param languages La {@link Set liste} des {@link Language langage}s associés au {@link Project projet}.
     */
    public void setLanguages(Set<Language> languages) { this.languages = languages; }

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
