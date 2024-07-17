package fr.thesakyo.portfolioapi.repositories;

import fr.thesakyo.portfolioapi.models.entities.Language;
import fr.thesakyo.portfolioapi.models.entities.Project;
import fr.thesakyo.portfolioapi.models.entities.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Récupère un {@link User utilisateur} par son {@link String adresse e-mail}.
     *
     * @param email L'{@link String adresse e-mail} de l'{@link User utilisateur}.
     *
     * @return Une {@link User utilisateur} par son {@link String adresse e-mail}.
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un {@link User utilisateur} existe par son {@link String adresse e-mail}.
     *
     * @param email L'{@link String adresse e-mail} de l'{@link User utilisateur} à vérifier.
     *
     * @return Une {@link Boolean valeur booléenne}.
     */
    Boolean existsByEmail(String email);

    /***************************************************/
    /***************************************************/

    /**
     * Récupère une {@link Set liste} des {@link User utilisateur}s ayants des {@link Project projet}s
     * contenant l'{@link Long identifiant} du {@link Language langage} demandé.
     *
     * @param languageId L'{@link Long identifiant} du {@link Language langage}.
     *
     * @return Une {@link Set liste} des {@link User utilisateur}s ayants des {@link Project projet}s
     *         contenant l'{@link Long identifiant} du {@link Language langage} demandé.
     */
    @Query("SELECT DISTINCT p.user FROM Language l JOIN l.projects p WHERE l.id = :languageId")
    Optional<Set<User>> findAllByLanguageIdInProjets(@Param("languageId") Long languageId);

    /**
     * Récupère une {@link Set liste} des {@link User utilisateur}s ayants des {@link Project projet}s
     * contenant le {@link String libellé} du {@link Language langage} demandé.
     *
     * @param languageLabel Le {@link String libellé} du {@link Language langage}.
     *
     * @return Une {@link Set liste} des {@link User utilisateur}s ayants des {@link Project projet}s
     *         contenant le {@link String libellé} du {@link Language langage} demandé.
     */
    @Query("SELECT DISTINCT p.user FROM Language l JOIN l.projects p WHERE l.label = :languageLabel")
    Optional<Set<User>> findAllByLanguageLabelInProjets(@Param("languageLabel") String languageLabel);

    /**
     * Vérifie si un {@link User utilisateur} spécifique a des {@link Project projet}s
     * contenant l'{@link Long identifiant} du {@link Language langage} demandé.
     *
     * @param languageId L'{@link Long identifiant} du {@link Language langage}.
     * @param userId L'{@link Long identifiant} du {@link User utilisateur}.
     *
     * @return {@code true} si l'{@link User utilisateur} a des {@link Project projet}s
     *         contenant l'{@link Long identifiant} du {@link Language langage} demandé, sinon {@code false}.
     */
    @Query("SELECT COUNT(p) > 0 FROM Language l JOIN l.projects p WHERE l.id = :languageId AND p.user.id = :userId")
    Boolean existsByIdAndLanguageIdInProjects(@Param("languageId") Long languageId, @Param("userId") Long userId);
}
