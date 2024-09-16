package fr.thesakyo.portfolioapi.services.entities;

import fr.thesakyo.portfolioapi.enums.ERole;
import fr.thesakyo.portfolioapi.enums.EStack;
import fr.thesakyo.portfolioapi.exceptions.UnauthorizedException;
import fr.thesakyo.portfolioapi.helpers.ObjectHelper;
import fr.thesakyo.portfolioapi.helpers.PermissionHelper;
import fr.thesakyo.portfolioapi.models.DTO.LanguageDTO;
import fr.thesakyo.portfolioapi.models.SerializableResponseEntity;
import fr.thesakyo.portfolioapi.models.entities.BaseEntity;
import fr.thesakyo.portfolioapi.models.entities.Language;
import fr.thesakyo.portfolioapi.models.entities.Project;
import fr.thesakyo.portfolioapi.models.entities.User;
import fr.thesakyo.portfolioapi.payloads.requests.ProjectsEntityRequest;
import fr.thesakyo.portfolioapi.repositories.LanguageRepository;
import fr.thesakyo.portfolioapi.repositories.ProjectRepository;
import fr.thesakyo.portfolioapi.repositories.UserRepository;
import fr.thesakyo.portfolioapi.services.DTOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LanguageService {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    @Autowired
    private LanguageRepository languageRepository; // Référentiel faisant référence aux langages de la base de données.

    @Autowired
    private ProjectRepository projectRepository; // Référentiel faisant reference aux projets de la base de données.

    @Autowired
    private UserRepository userRepository; // Référentiel faisant reference aux utilisateurs de la base de données.

    /*******************************/

    @Autowired
    private DTOService dtoService; // Récupère le service lié au DTO

    /***********************************************************/
    /**************   ⬇️    MÉTHODES CRUD   ⬇️   **************/
    /***********************************************************/

    /**
     * Récupération de toutes les {@link Language langage}s.
     *
     * @param entityClazz {@link Class} de l'{@link BaseEntity entité} associée aux {@link Language langage}s. (si nécessaire)
     * @param entityId {@link Long Identifiant} de l'{@link BaseEntity entité} associée aux {@link Language langage}s (si nécessaire)
     *
     * @return Une {@link Set liste} de {@link LanguageDTO langage}s.
     */
    public <C extends BaseEntity> Set<LanguageDTO> getAllLanguages(Class<C> entityClazz, Long entityId) {

        // Récupère la liste des langages
        Set<Language> languages = new HashSet<>(languageRepository.findAll());

        /************************************************/

        /**
         * Si on récupère un identifiant de l'entité et sa class,
         * on filtre les langages étant associée à l'entité respective
         */
        if(entityId != null && entityClazz != null) {

            switch(entityClazz.getSimpleName().toLowerCase()) {

                case "user":

                    languages = languages.stream().filter(language -> {

                        Set<User> filteredUsers = userRepository.findAllByLanguageIdInProjets(language.getId()).orElse(new HashSet<>());
                        return filteredUsers.stream().anyMatch(user -> user.getId().equals(entityId));

                    }).collect(Collectors.toSet());
                    break;

                case "project":

                    languages = languages.stream().filter(language -> language.getProjects()
                            .stream().anyMatch(project -> project.getId().equals(entityId)))
                            .collect(Collectors.toSet());
                    break;

                default: break;
            }
        }

        // On renvoie la liste de langage(s) et les convertis en leur 'DTO' respectif
        return new HashSet<>(dtoService.convertToDTOs(new LanguageDTO(), languages));
    }

    /**
     * Récupération d'une seule {@link Language langage}.
     *
     * @param id L'{@link Long Identifiant} de la {@link Language langage}.
     *
     * @return Une {@link ResponseEntity réponse http} permettant de récupérer un objet '{@link LanguageDTO language}'.
     */
    public SerializableResponseEntity<?> getLanguageById(final Long id) {

        Map<String, Object> responseMap = new HashMap<>(); // Dictionnaire 'map' pour récupérer une clé → valeur (utile pour le retour de la réponse http)

        /*******************************************************/

        // On récupère le langage en tant qu'optionnel
        Optional<Language> languageOptional = languageRepository.findById(id);

        /**
         * Si le langage optionnel, existe bel et bien,
         * on renvoie son 'DTO' respectif
         */
        if(languageOptional.isPresent()) {

            LanguageDTO languageDTO = dtoService.convertToDTO(new LanguageDTO(), languageOptional.orElse(null)); // Convertit le langage en son 'DTO' respectif
            responseMap.putIfAbsent("entity", languageDTO); // Envoie dans le dictionnaire 'map' le langage en question
        }

        /*******************************************************/

        responseMap.putIfAbsent("isAvailable", languageRepository.existsById(id)); // Envoie dans le dictionnaire 'map' une vérification si le langage existe
        return new SerializableResponseEntity<>(responseMap, HttpStatus.OK); // Renvoie la réponse http avec le dictionnaire 'map'
    }

    /**
     * Création d'une nouvelle {@link Language language}.
     *
     * @param languageRequest Un '{@link ProjectsEntityRequest objet de requête}' pour l'ajout d'une {@link Language language}
     *                        avec ses {@link Project entreprise}s associés.
     *
     * @return L'Objet '{@link LanguageDTO language}' créer.
     */
    public LanguageDTO createLanguage(ProjectsEntityRequest<Language> languageRequest) {

        // Envoie la méthode de requête effectuant toutes les vérifications nécessaires pour la création.
        return requestLanguage(null, languageRequest, false);
    }

    /**
     * Mise à jour d'une {@link Language language}.
     *
     * @param id L'{@link Long Identifiant} du {@link Language language}.
     * @param languageRequest Un '{@link ProjectsEntityRequest objet de requête}' pour la modification d'une {@link Language language}
     *                        avec ses {@link Project projet}s associés.
     *
     * @return L'Objet '{@link LanguageDTO langage}' mis à jour.
     */
    @Transactional(rollbackFor = { UnauthorizedException.class })
    public LanguageDTO updateLanguage(final Long id, ProjectsEntityRequest<Language> languageRequest) {

        // Envoie la méthode de requête effectuant toutes les vérifications nécessaires pour la mise à jour.
        return requestLanguage(id, languageRequest, true);
    }

    /**
     * Suppression d'un {@link Language langage}.
     *
     * @param languageId L'{@link Long Identifiant} du {@link Language langage}.
     * @param projectId L'{@link Long Identifiant} du {@link Project projet} associé.
     *
     * @return Une {@link ResponseEntity réponse http} récupérant une valeur booléenne vérifiant si le {@link Language langage} a bien été supprimé ('true' ou 'false').

     * @throws InstanceNotFoundException Une exception est levée, si le langage est introuvable
     */
    @Transactional(rollbackFor = { UnauthorizedException.class })
    public SerializableResponseEntity<?> deleteLanguage(final Long languageId, final Long projectId) throws InstanceNotFoundException {

        Map<String, Boolean> responseMap = new HashMap<>(); // Dictionnaire 'map' pour récupérer une clé → valeur (utile pour le retour de la réponse http)

        /*************************************/

        Language existingLanguage = languageRepository.findById(languageId).orElse(null); // Récupère le langage à supprimer par son identifiant
        if(existingLanguage == null) throw new InstanceNotFoundException("L'Entité n'éxiste pas !"); // Si le langage à supprimer est 'null', une exception est renvoyé

        /**********************************************************/

        Set<Project> projects = existingLanguage.getProjects();

        /**
         * Si le langage a des projets, on lui supprime les projets correspondants si l'utilisateur contient ses projets
         */
         if(projects != null && !projects.isEmpty()) {

           // Définit une clé → valeur : L'entreprise cible a-t-il bien été supprimé du langage ?
           responseMap.putIfAbsent("isUpdated", projects.removeIf(project -> project.getId().longValue() == projectId.longValue() && PermissionHelper.userHasProjectPermission(project)));

        // Sinon, si le langage n'a pas de projets, on vérifie quand même si l'utilisateur connecté a la permission, sinon on envoie une erreur.
        } else checkPermission(ERole.ROLE_ADMIN, existingLanguage, true);

        /******************************/

        // Supprime le langage, si sa liste de projet est vide
        if(projectId == null || projects.isEmpty()) languageRepository.deleteById(languageId);

        // Sinon, on met à jour le langage
        else languageRepository.save(existingLanguage);

        /*************************************/

        // Définit une clé → valeur : Le langage a-t-il était supprimée ?
        responseMap.putIfAbsent("isDeleted", !languageRepository.existsById(languageId));
        responseMap.putIfAbsent("isModified", PermissionHelper.userHasLanguageInProjetsPermission(existingLanguage, languageRepository));
        return new SerializableResponseEntity<>(responseMap, HttpStatus.OK); // Renvoie la réponse http
    }

    /******************************************************************************************************************/
    /******************************************************************************************************************/
    /******************************************************************************************************************/

    /**
     * Envoie la méthode de requête effectuant toutes les vérifications nécessaires pour une mise à jour ou une création.
     * Cette méthode permet notamment d'optimiser les enregistrements des langages dans la base de données, tout en gardant un langage propre pour chaques utilisateurs.
     *
     * @param id L'{@link Long Identifiant} du {@link Language language}.
     * @param languageRequest Un '{@link ProjectsEntityRequest objet de requête}' pour la modification d'une {@link Language language}.
     * @param isUpdate 'Vrai' si la requête est de mise à jour, 'faux' si la requête est de creation
     *
     * @return L'Objet '{@link LanguageDTO language}' mis à jour/créer.
     */
    private LanguageDTO requestLanguage(final Long id, ProjectsEntityRequest<Language> languageRequest, boolean isUpdate) {

        // Initialise le langage final qui sera traité, on récupère d'abord dans la requête de l'utilisateur le langage qu'on veut mettre à jour
        final Language[] finalLanguage = { languageRequest.getEntity() };

        // Initialise le dictionnaire 'map' pour le retour d'une réponse http
        Map<String, Object> responseMap = new HashMap<>();

        /**************************/

        // Récupère le langage existant correspondant au langage demandé par son identifiant ou son label
        final Language existingLanguage = languageRepository.findById(id).orElse(languageRepository.findByLabel(finalLanguage[0].getLabel()).orElse(null));

        // Récupère la liste des utilisateurs ayant des projets associés au langage demandé
        Set<User> users = userRepository.findAllByLanguageIdInProjets(id).orElse(userRepository.findAllByLanguageLabelInProjets(finalLanguage[0].getLabel()).orElse(new HashSet<>()));

        // Si le langage existant correspondant n'existe pas, on renvoie null
        if(isUpdate && existingLanguage == null) return null;

        /**************************/

        // Récupère la liste des projets du langage existant, s'il existe
        Set<Project> existingProjects = existingLanguage != null ? existingLanguage.getProjects() : new HashSet<>();

        // On récupère la liste des projets demandée par l'utilisateur en les retrouvant dans la base de données
        Set<Project> projectsRequest = new HashSet<>(projectRepository.findAllById(languageRequest.getProjectsId()));

        // On récupère une liste des projets demandée par l'utilisateur en récupérant uniquement les projets liés à l'utilisateur
        projectsRequest = projectsRequest.stream().filter(PermissionHelper::userHasProjectPermission).collect(Collectors.toSet());

        // Si la liste des projets demandée par l'utilisateur est vide, on renvoie une erreur de permission dans le cas d'une modification.
        if(isUpdate && projectsRequest.isEmpty()) throw new UnauthorizedException(PermissionHelper.UNAUTHORIZED_MESSAGE);

        /**
         * Si la liste des projets du langage existant n'est pas null et que la liste des projets demandée par l'utilisateur n'est pas vide,
         * on peut donc continuer la vérification à travers les utilisateurs et projets.
         */
        if(existingProjects != null && !existingProjects.isEmpty() && !projectsRequest.isEmpty()) {

            // Créer une copie du langage pour l'utilisateur actuel (utile si ce langage est dans plusieurs projets d'utilisateurs différents)
            final Language[] updatedLanguage = { new Language() };

            updatedLanguage[0].setLabel(finalLanguage[0].getLabel()); // Définit le libellé modifié/créée au langage cloné
            updatedLanguage[0].setStack(finalLanguage[0].getStack()); // Définit la stack modifié/créée au langage cloné

            /***************************/

            /**
             * Si le langage existant est identique au langage modifié/créée par l'utilisateur, on remplace la copie du langage
             * par l'éxistant qui sera donc le langage définitif à envoyer en base de données.
             */
            if(ObjectHelper.areObjectsIdentical(existingLanguage, finalLanguage[0], "id", "projects")) updatedLanguage[0] = existingLanguage;

            /***************************/

            // Récupère la liste des projets actuelle du langage à envoyer
            Set<Project> updatedProjects = updatedLanguage[0].getProjects();

            /**
             * Vérifie, s'il existe plusieurs utilisateurs ayant des projets associés au langage à envoyer,
             * alors, on opte à créer un nouveau langage cloné
             */
            if(users.size() > 1) {

                // Si le langage à envoyé, contient déjà un identifiant, on lui enlève (pour créer un nouveau langage à par entière).
                if(updatedLanguage[0].getId() != null) updatedLanguage[0].setId(null);

                /**
                 * Boucle pour parcourir les projets et pouvoir chacun leur mettre à jour leur association du langage à envoyer.
                 */
                projectsRequest.forEach(projectRequest ->{

                    updatedProjects.add(projectRequest); // Ajoute au langage à envoyer le projet correspondant
                    finalLanguage[0] = languageRepository.save(updatedLanguage[0]); // Sauvegarde le langage à envoyé en tant que langage final

                    projectRequest.getLanguages().remove(existingLanguage); // Supprime le langage existant au projet correspondant
                    projectRequest.getLanguages().add(finalLanguage[0]); // Ajoute le langage clôné au projet correspondant

                    projectRepository.save(projectRequest); // Sauvegarde le projet
                });

                // Sinon, on met à jour directement le langage a envoyé
            } else {

                // Met à jour le libellé, s'ils ne sont pas égaux dans les deux langages
                if(!Objects.equals(updatedLanguage[0].getLabel(), existingLanguage.getLabel())) existingLanguage.setLabel(updatedLanguage[0].getLabel());
                if(!Objects.equals(updatedLanguage[0].getStack(), existingLanguage.getStack())) existingLanguage.setStack(updatedLanguage[0].getStack());

                projectsRequest.forEach(existingLanguage.getProjects()::add); // Ajoute au langage à envoyer les projets correspondants
                finalLanguage[0] = languageRepository.save(existingLanguage); // Sauvegarde le langage existant en tant que langage final
            }

        // Sinon, si le langage n'a pas de projets, on vérifie quand même si l'utilisateur connecté a la permission, sinon on envoie une erreur.
        } else {

            // Vérifie si l'utilisateur connecté a la permission
            checkPermission(ERole.ROLE_ADMIN, finalLanguage[0], isUpdate);

            finalLanguage[0].setProjects(projectsRequest); // Ajoute au langage à envoyer les projets correspondants
            finalLanguage[0] = languageRepository.save(finalLanguage[0]); // Sauvegarde le langage final
        }

        /*****************************************************************************/

        return dtoService.convertToDTO(new LanguageDTO(), finalLanguage[0]); // Renvoie le 'DTO' du langage final et mis à jour
    }

    /********************************************************************************************************************/
    /********************************************************************************************************************/

    /**
     * Envoie un {@link UnauthorizedException exception} si l'utilisateur connecté n'a pas la permission adéquate.
     *
     * @param role Le {@link Project rôle} à valider.
     * @param language Le {@link Language langage} à valider.
     * @param isUpdate Doit-on vérifier pour une modification.
     *
     * @throws UnauthorizedException Si l'utilisateur connecté n'a pas la permission adéquate.
     */
    private void checkPermission(ERole role, Language language, boolean isUpdate) throws UnauthorizedException {

        boolean hasPermission = false; // Permettra de vérifier si l'utilisateur connecté a la permission
        if(role == ERole.ROLE_SUPERADMIN) return; // Si l'utilisateur connecté est un super-admin, on ne fait rien !

        /**************************************************/

        // Vérifie si l'utilisateur connecté a le rôle demandé, dans ce cas, il a la permission
        if(PermissionHelper.userHasRole(role)) hasPermission = true;

        /**
         * On vérifie si l'utilisateur à bien de langage associé à l'un de ses projets, sinon, il n'a pas la permission !
         */
        if(language == null || !PermissionHelper.userHasLanguageInProjetsPermission(language, languageRepository)) hasPermission = false;

        // S'il s'agit d'une création, il a finalement la permission
        if(!isUpdate) hasPermission = true;

        /**************************************************/

        // Vérifie si l'utilisateur connecté a la permission, dans le cas contraire, une exception est levée
        if(!hasPermission) throw new UnauthorizedException("Vous n'avez pas la permission !");
    }
}
