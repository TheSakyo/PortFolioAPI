package fr.thesakyo.portfolioapi.repositories;

import fr.thesakyo.portfolioapi.models.entities.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language, Long> {

    /**
     * Récupère un {@link Language langage} par son {@link String libellé}.
     *
     * @param label Le {@link String libellé} du {@link Language langage}.
     *
     * @return Une {@link Language langage} par son nom d'{@link String libellé}.
     */
    Optional<Language> findByLabel(String label);

    /**
     * Vérifie un {@link Language langage} par son {@link String libellé}.
     *
     * @param label Le {@link String libellé} du {@link Language langage} à vérifier.
     *
     * @return Une {@link Boolean valeur booléenne}.
     */
    Boolean existsByLabel(String label);
}
