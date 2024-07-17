package fr.thesakyo.portfolioapi.services.entities;

import fr.thesakyo.portfolioapi.enums.ERole;
import fr.thesakyo.portfolioapi.models.DTO.RoleDTO;
import fr.thesakyo.portfolioapi.models.SerializableResponseEntity;
import fr.thesakyo.portfolioapi.models.entities.Role;
import fr.thesakyo.portfolioapi.repositories.RoleRepository;
import fr.thesakyo.portfolioapi.services.DTOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RoleService {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    @Autowired
    private RoleRepository roleRepository; // Référentiel faisant référence aux rôles de la base de données.

    @Autowired
    private DTOService dtoService; // Récupère le service lié au DTO

    /***********************************************************/
    /**************   ⬇️    MÉTHODES CRUD   ⬇️   **************/
    /***********************************************************/

    /**
     * Récupération de tous les {@link Role rôle}s.
     *
     * @return Une {@link List liste} de {@link RoleDTO rôle}s.
     */
    public List<RoleDTO> getAllRoles() {

        // On renvoie la liste de rôle(s) et les convertis en leur 'DTO' respectif
        return dtoService.convertToDTOs(new RoleDTO(), roleRepository.findAll());
    }

    /**
     * Récupération d'un seul {@link Role rôle}.
     *
     * @param id L'{@link Long Identifiant} du {@link Role rôle}.
     *
     * @return Une {@link ResponseEntity réponse http} permettant de récupérer un objet '{@link RoleDTO rôle}'.
     */
    public SerializableResponseEntity<?> getRoleById(final Long id) {

        Map<String, Object> responseMap = new HashMap<>(); // Dictionnaire 'map' pour récupérer une clé → valeur (utile pour le retour de la réponse http)

        /*******************************************************/

        // On récupère le rôle en tant qu'optionnel
        Optional<Role> roleOptional = roleRepository.findById(id);

        /**
         * Si le rôle optionnel, existe bel et bien,
         * on renvoie son 'DTO' respectif
         */
        if(roleOptional.isPresent()) {

            RoleDTO roleDTO = dtoService.convertToDTO(new RoleDTO(), roleOptional.orElse(null)); // Convertit le rôle en son 'DTO' respectif
            responseMap.putIfAbsent("entity", roleDTO); // Envoie dans le dictionnaire 'map' le rôle en question
        }

        /*******************************************************/

        responseMap.putIfAbsent("isAvailable", roleRepository.existsById(id)); // Envoie dans le dictionnaire 'map' une vérification si le rôle existe
        return new SerializableResponseEntity<>(responseMap, HttpStatus.OK); // Renvoie la réponse http avec le dictionnaire 'map'
    }

    /***********************************************************************************/
    /***********************************************************************************/

    /**
     * Vérifie et récupère le {@link Role rôle} demandé.
     *
     * @param role Le {@link ERole nom du rôle} en question.
     *
     * @return Le {@link Role rôle} demandé.
     *
     * @throws RuntimeException Une exception est envoyé en cas de rôle introuvable.
     */
    public Role checkedRole(ERole role) { return roleRepository.findByName(role).orElseThrow(() -> new RuntimeException("Erreur : Le rôle est introuvable.")); }
}
