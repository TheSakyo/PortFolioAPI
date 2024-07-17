package fr.thesakyo.portfolioapi.repositories;

import fr.thesakyo.portfolioapi.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

}
