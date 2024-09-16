package fr.thesakyo.portfolioapi.controllers;

import fr.thesakyo.portfolioapi.models.DTO.LanguageDTO;
import fr.thesakyo.portfolioapi.models.SerializableResponseEntity;
import fr.thesakyo.portfolioapi.models.entities.Language;
import fr.thesakyo.portfolioapi.models.entities.Project;
import fr.thesakyo.portfolioapi.payloads.requests.ProjectsEntityRequest;
import fr.thesakyo.portfolioapi.services.entities.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.management.InstanceNotFoundException;
import java.util.Set;

@Controller
@RequestMapping("/api/languages")
public class LanguageController {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/
    
    @Autowired
    private LanguageService languageService; // Service concordant à la table des langages dans la base de données.

    /***************************************************************/
    /**************   ⬇️    MÉTHODES DE REQUÊTE   ⬇️   ************/
    /***************************************************************/

    /**
     * Récupère tous les {@link Language langage}(s).
     *
     * @return Une {@link SerializableResponseEntity réponse sérialisé en http} incluant tous les {@link Language langage}(s) connecté(s).
     */
    /*@RequestMapping(value = "/all", method = RequestMethod.GET)
    public Set<LanguageDTO> getAll() { return languageService.getAllLanguages(); }*/

    /**
     * Récupère un {@link Language langage} par son {@link Long identifiant}.
     *
     * @param id L'{@link Long Identifiant} de l'{@link Language langage} a récupéré.
     *
     * @return Une {@link SerializableResponseEntity réponse sérialisé en http} récupérant l'{@link Language langage} en question.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public SerializableResponseEntity<?> get(@PathVariable("id") Long id) { return languageService.getLanguageById(id); }

    /*********************************************************/

    /**
     * Créer un nouveau {@link Language langage} pour un {@link Project projet}.
     *
     * @param languageRequest Une '{@link ProjectsEntityRequest requête de création}' pour l'ajout d'un {@link Language langage} à un {@link Project projet}.
     *
     * @return L'Objet '{@link LanguageDTO language}' créer.
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public LanguageDTO create(@RequestBody ProjectsEntityRequest<Language> languageRequest) { return languageService.createLanguage(languageRequest); }

    /**
     * Met à jour {@link Language langage} pour un {@link Project projet}.
     *
     * @param id L'{@link Long Identifiant} du {@link Language language}.
     * @param languageRequest Une '{@link ProjectsEntityRequest requête de modification}' pour la mise à jour d'un {@link Language langage} à un {@link Project projet}.
     *
     * @return L'Objet '{@link LanguageDTO language}' modifier.
     */
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PATCH)
    public LanguageDTO update(@PathVariable("id") Long id, @RequestBody ProjectsEntityRequest<Language> languageRequest) { return languageService.updateLanguage(id, languageRequest); }

    /*********************************************************/

    /**
     * Supprime un {@link Language langage} pour un {@link Project projet}.
     *
     * @param idLanguage L'{@link Long Identifiant} du {@link Language language}.
     * @param idProject L'{@link Long Identifiant} du {@link Project projet} associé.
     *
     * @return Une {@link SerializableResponseEntity réponse sérialisé en http} incluant une valeur booléenne pour le statut de la suppression pour le {@link Language langage}.
     *
     * @throws InstanceNotFoundException  Une exception est levée, si le langage est introuvable
     */
    @RequestMapping(value = "/delete/{idLanguage}/{idProject}", method = RequestMethod.DELETE)
    public SerializableResponseEntity<?> delete(@PathVariable("idLanguage") Long idLanguage, @PathVariable("idProject") Long idProject) throws InstanceNotFoundException { return languageService.deleteLanguage(idLanguage, idProject); }
}
