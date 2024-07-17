package fr.thesakyo.portfolioapi.services.entities;

import fr.thesakyo.portfolioapi.enums.ERole;
import fr.thesakyo.portfolioapi.exceptions.UnauthorizedException;
import fr.thesakyo.portfolioapi.helpers.PermissionHelper;
import fr.thesakyo.portfolioapi.models.DTO.ProjectDTO;
import fr.thesakyo.portfolioapi.models.SerializableResponseEntity;
import fr.thesakyo.portfolioapi.models.entities.BaseEntity;
import fr.thesakyo.portfolioapi.models.entities.Language;
import fr.thesakyo.portfolioapi.models.entities.Project;
import fr.thesakyo.portfolioapi.models.entities.User;
import fr.thesakyo.portfolioapi.repositories.LanguageRepository;
import fr.thesakyo.portfolioapi.repositories.ProjectRepository;
import fr.thesakyo.portfolioapi.services.DTOService;
import fr.thesakyo.portfolioapi.payloads.requests.LanguagesEntityRequest;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    @Autowired
    private ProjectRepository projectRepository; // Référentiel faisant référence aux projets de la base de données.

    @Autowired
    private LanguageRepository languageRepository; // Référentiel faisant référence aux langages de la base de données.

    /*******************************/

    @Autowired
    private DTOService dtoService; // Récupère le service lié au DTO

    /***********************************************************/
    /**************   ⬇️    MÉTHODES CRUD   ⬇️   **************/
    /***********************************************************/

    /**
     * Récupération de tous les {@link Project projet}s.
     *
     * @param entityClazz {@link Class} de l'{@link BaseEntity entité} associée aux {@link Project projet}s. (si nécessaire)
     * @param entityId {@link Long Identifiant} de l'{@link BaseEntity entité} associée aux {@link Project projet}s (si nécessaire)
     *
     * @return Une {@link Set liste} de {@link ProjectDTO projet}s.
     */
    public <C extends BaseEntity> Set<ProjectDTO> getAllProjects(Class<C> entityClazz, Long entityId) {

        // On récupère tous les projets en base de données
        Set<Project> projectsList = new HashSet<>(projectRepository.findAll());

        /************************************************/

        /**
         * Si on récupère un identifiant de l'entité et sa class,
         * on filtre les projets étant associée à l'entité respective
         */
        if(entityId != null && entityClazz != null) {

            switch(entityClazz.getSimpleName().toLowerCase()) {

                case "user":

                    projectsList = projectsList.stream().filter(project -> project.getUser()
                            .getId().equals(entityId)).collect(Collectors.toSet());
                    break;

                case "language":

                    projectsList = projectsList.stream().filter(project -> project.getLanguages()
                            .stream().anyMatch(category -> category.getId().equals(entityId)))
                            .collect(Collectors.toSet());
                    break;

                default: break;
            }
        }

        /************************************************/

        // On renvoie la liste de projet(s) et les convertis en leur 'DTO' respectif
        return new HashSet<>(dtoService.convertToDTOs(new ProjectDTO(), projectsList));
    }

    /**
     * Récupération d'un seul {@link Project projet}.
     *
     * @param id L'{@link Long Identifiant} du {@link Project projet}.
     *
     * @return Une {@link ResponseEntity réponse http} permettant de récupérer un objet '{@link ProjectDTO projet}'.
     */
    public SerializableResponseEntity<?> getProjectById(final Long id) {

        Project project = projectRepository.findById(id).orElse(null); // Récupère le projet en question par son identifiant
        Map<String, Object> responseMap = new HashMap<>(); // Dictionnaire 'map' pour récupérer une clé → valeur (utile pour le retour de la réponse http)

        /**************************************/

        // Si le projet récupéré est bel et bien existant, on recharge donc les informations nécessaires du client
        if(project != null) responseMap.putIfAbsent("isAvailable", true); // Envoie dans le dictionnaire 'map' une valeur booléenne disant que le projet existe

        // Sinon, on envoie dans le dictionnaire 'map' une valeur booléenne disant que le projet n'existe pas
        else responseMap.putIfAbsent("isAvailable", false);

        /**************************************/

        // On récupère le projet en tant qu'optionnel
        Optional<Project> projectOptional = projectRepository.findById(id);

        /**
         * Si le projet optionnel, existe bel et bien,
         * on renvoie son 'DTO' respectif
         */
        if(projectOptional.isPresent()) {

            ProjectDTO projectDTO = dtoService.convertToDTO(new ProjectDTO(), projectOptional.orElse(null)); // Convertit le projet en son 'DTO' respectif
            responseMap.putIfAbsent("entity", projectDTO); // Envoie dans le dictionnaire 'map' le projet en question
        }

        /*******************************************************/

        return new SerializableResponseEntity<>(responseMap, HttpStatus.OK); // Renvoie la réponse http avec le dictionnaire 'map'
    }

    /**
     * Création d'un nouveau {@link Project projet}.
     *
     * @param project Un nouvel objet '{@link Project projet}'.
     *
     * @return L'Objet '{@link ProjectDTO projet}' créer.
     */
    @Transactional(rollbackFor = { UnauthorizedException.class })
    public ProjectDTO createProject(Project project) {

        checkPermission(project); // Vérifie si l'utilisateur connecté a la permission de supprimer le projet

        // Renvoie le 'DTO' du projet en sauvegardant le projet en base de donnée
        return dtoService.convertToDTO(new ProjectDTO(), projectRepository.save(project));
    }

    /**
     * Mise à jour d'un {@link Project projet}.
     *
     * @param id L'{@link Long Identifiant} du {@link Project projet}.
     * @param project Un nouvel objet '{@link Project projet}'.
     *
     * @return L'Objet '{@link ProjectDTO projet}' mis à jour.
     */
    @Transactional(rollbackFor = { UnauthorizedException.class })
    public ProjectDTO updateProject(final Long id, Project project) {

        Project existingProject = projectRepository.findById(id).orElse(null); // Récupère le projet à modifier par son identifiant
        if(existingProject == null) return null; // Si le projet à modifier est 'null', 'null' est donc renvoyé
        checkPermission(project); // Vérifie si l'utilisateur connecté a la permission de supprimer le projet

        /**********************************************************/

        Set<Language> languages = project.getLanguages();
        User user = project.getUser();

        String title = project.getTitle();
        String detail = project.getDetail();
        String link = project.getLink();

        /**************/

        if(languages != null && !languages.isEmpty()) existingProject.setLanguages(languages);
        if(user != null) existingProject.setUser(user);

        if(Strings.isNotBlank(title)) existingProject.setTitle(title);
        if(Strings.isNotBlank(detail)) existingProject.setDetail(detail);
        if(Strings.isNotBlank(link)) existingProject.setLink(link);

        /******************************/

        // Renvoie le 'DTO' du projet en sauvegardant le projet en base de donnée
        return dtoService.convertToDTO(new ProjectDTO(), projectRepository.save(existingProject));
    }

    /**
     * Met à jour les  {@link Language langage}(s) d'un {@link Project projet}.
     *
     * @param languagesRequest Un '{@link LanguagesEntityRequest objet de requête}' pour la mise à jour des {@link Language langage}(s) du {@link Project projet} en question.
     *
     * @return Une {@link ResponseEntity réponse http} récupérant une valeur booléenne vérifiant si la modification
     *      des {@link Language langage}(s) du {@link Project projet} ont bien été effectuée ('true' ou 'false').
     */
    @Transactional(rollbackFor = { UnauthorizedException.class, EntityNotFoundException.class })
    public SerializableResponseEntity<?> updateLanguage(LanguagesEntityRequest<Project> languagesRequest) {

        Map<String, Object> responseMap = new HashMap<>(); // Dictionnaire 'map' pour récupérer une clé → valeur (utile pour le retour de la réponse http)

        /******************************/

        Project project = languagesRequest.getEntity() != null ? projectRepository.findById(languagesRequest.getEntity().getId()).orElse(null) : null;
        List<Language> languages = languageRepository.findAllById(languagesRequest.getLanguagesId());

        /**********************************************************/
        /**********************************************************/

        if(project == null) throw new EntityNotFoundException("Le projet n'est pas trouvé dans la base de données ou le projet demandé n'est pas défini.");
        checkPermission(project); // Vérifie si l'utilisateur connecté a la permission de supprimer le projet

        /**********************************************************/
        /**********************************************************/

        project.setLanguages(new HashSet<>(languages));  // Met à jour les catégories du projet

        /***********************************/

        // Récupère le projet mis à jour dans la base de données
        Project projectUpdated = projectRepository.save(project);

        // Envoie dans un dictionnaire une clé → valeur récupérant le 'DTO' du projet sauvegardé en base de donnée
        responseMap.putIfAbsent("entity", dtoService.convertToDTO(new ProjectDTO(), projectUpdated));
        return new SerializableResponseEntity<>(responseMap, HttpStatus.OK); // Renvoie la réponse http
    }

    /**
     * Suppression d'un {@link Project projet}.
     *
     * @param id L'{@link Long Identifiant} du {@link Project projet}.
     *
     * @return Une {@link ResponseEntity réponse http} récupérant une valeur booléenne vérifiant si le {@link Project projet} a bien été supprimé ('true' ou 'false').
     */
    @Transactional(rollbackFor = { UnauthorizedException.class })
    public SerializableResponseEntity<?> deleteProject(final Long id) {

        Map<String, Boolean> responseMap = new HashMap<>(); // Dictionnaire 'map' pour récupérer une clé → valeur (utile pour le retour de la réponse http)

        /******************************/

        Project existingProject = projectRepository.findById(id).orElse(null); // Récupère le projet à supprimer par son identifiant
        if(existingProject == null) return null; // Si le projet à supprimer est 'null', 'null' est donc renvoyé
        checkPermission(existingProject); // Vérifie si l'utilisateur connecté a la permission de supprimer le projet

        /**********************************************************/

        // Supprime les langages du projet en question //
        Set<Language> languages = existingProject.getLanguages();
        if(!languages.isEmpty()) languageRepository.deleteAll(existingProject.getLanguages());
        // Supprime les langages du projet en question //

        /****************************************************/
        /****************************************************/

        projectRepository.deleteById(id); // Supprime le projet
        responseMap.putIfAbsent("isDeleted", !projectRepository.existsById(id)); // Définit une clé → valeur : Le projet a-t-il était supprimé ?
        return new SerializableResponseEntity<>(responseMap, HttpStatus.OK); // Renvoie la réponse http
    }

    /******************************************************************************************************************/
    /******************************************************************************************************************/
    /******************************************************************************************************************/

    /**
     * Envoie un {@link UnauthorizedException exception} si l'utilisateur connecté n'a pas la permission adéquate.
     *
     * @param project Le {@link Project projet} à valider.
     *
     * @throws UnauthorizedException Si l'utilisateur connecté n'a pas la permission adéquate.
     */
    private void checkPermission(Project project) throws UnauthorizedException {

        /**
         * Si l'utilisateur connecté n'a pas la permission adéquate, on envoie donc une exception !
         */
        if(!PermissionHelper.userHasProjectPermission(project) || !PermissionHelper.userHasRole(ERole.ROLE_ADMIN))
            throw new UnauthorizedException(PermissionHelper.UNAUTHORIZED_MESSAGE);
    }
}
