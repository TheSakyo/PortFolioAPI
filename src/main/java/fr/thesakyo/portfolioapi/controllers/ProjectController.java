package fr.thesakyo.portfolioapi.controllers;

import fr.thesakyo.portfolioapi.models.DTO.ProjectDTO;
import fr.thesakyo.portfolioapi.models.SerializableResponseEntity;
import fr.thesakyo.portfolioapi.models.entities.Language;
import fr.thesakyo.portfolioapi.models.entities.Project;
import fr.thesakyo.portfolioapi.payloads.requests.LanguagesEntityRequest;
import fr.thesakyo.portfolioapi.payloads.requests.ProjectsEntityRequest;
import fr.thesakyo.portfolioapi.services.entities.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Set;

@Controller
@RequestMapping("/api/projects")
public class ProjectController {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/
    
    @Autowired
    private ProjectService projectService; // Service concordant à la table des projets dans la base de données.

    /***************************************************************/
    /**************   ⬇️    MÉTHODES DE REQUÊTE   ⬇️   ************/
    /***************************************************************/

    /**
     * Récupère tous les {@link Project projet}(s).
     *
     * @return Une {@link SerializableResponseEntity réponse sérialisé en http} incluant tous les {@link Project projet}(s) connecté(s).
     */
    /*@RequestMapping(value = "/all", method = RequestMethod.GET)
    public Set<ProjectDTO> getAll() { return projectService.getAllProjects(); }*/

    /**
     * Récupère un {@link Project projet} par son {@link Long identifiant}.
     *
     * @param id L'{@link Long Identifiant} de l'{@link Project projet} a récupéré.
     *
     * @return Une {@link SerializableResponseEntity réponse sérialisé en http} récupérant l'{@link Project projet} en question.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public SerializableResponseEntity<?> get(@PathVariable("id") Long id) { return projectService.getProjectById(id); }

    /*********************************************************/

    /**
     * Créer un nouveau {@link Project projet}.
     *
     * @param project Le {@link Project projet} à créer.
     *
     * @return L'Objet '{@link ProjectDTO projet}' créer.
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ProjectDTO create(@RequestBody Project project) { return projectService.createProject(project); }

    /**
     * Mise à jour d'un {@link Project projet}.
     *
     * @param project Le {@link Project projet} à modifier.
     *
     * @return L'Objet '{@link ProjectDTO projet}' modifier.
     */
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PATCH)
    public ProjectDTO update(@PathVariable("id") Long id, @RequestBody Project project) { return projectService.updateProject(id, project); }

    /**
     * Modifie les {@link Language langage}(s) pour un {@link Project projet}.
     *
     * @param languagesRequest Une '{@link ProjectsEntityRequest requête de mise à jour}' pour la modification des {@link Language langage}(s) pour le {@link Project projet} cible.
     *
     * @return Une {@link SerializableResponseEntity réponse sérialisé en http} incluant le {@link Project projet} avec ses {@link Language langage}(s) modifié(s).
     */
    @RequestMapping(value = "/update/languages", method = RequestMethod.PATCH)
    public SerializableResponseEntity<?> updateLanguage(LanguagesEntityRequest<Project> languagesRequest) { return projectService.updateLanguage(languagesRequest); }

    /*********************************************************/

    /**
     * Supprime un {@link Project projet}.
     *
     * @param id L'{@link Long Identifiant} du {@link Project projet} à supprimer.
     *
     * @return Une {@link SerializableResponseEntity réponse sérialisé en http} incluant une valeur booléenne du statut de la suppression.
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public SerializableResponseEntity<?> delete(@PathVariable("id") Long id) { return projectService.deleteProject(id); }
}
