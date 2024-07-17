package fr.thesakyo.portfolioapi.repositories;

import fr.thesakyo.portfolioapi.models.entities.Role;
import fr.thesakyo.portfolioapi.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>  {

    /**
     * Récupère un {@link Role rôle} par son nom d'{@link ERole Énumération}.
     *
     * @param name Le nom d'{@link ERole Énumération} du {@link Role rôle}.
     *
     * @return Une {@link Role rôle} par son nom d'{@link ERole Énumération}.
     */
    Optional<Role> findByName(ERole name);

    /**
     * Vérifie un {@link Role rôle} par son nom d'{@link ERole Énumération}.
     *
     * @param name Le nom d'{@link ERole Énumération} du {@link Role rôle} à vérifier.
     *
     * @return Une {@link Boolean valeur booléenne}.
     */
    Boolean existsByName(ERole name);
}
