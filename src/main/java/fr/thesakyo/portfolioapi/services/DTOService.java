package fr.thesakyo.portfolioapi.services;

import fr.thesakyo.portfolioapi.interfaces.IBaseEntity;
import fr.thesakyo.portfolioapi.interfaces.IEntityDAO;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DTOService {

    /********************************************************************/
    /**************   ⬇️    MÉTHODES DE CONVERSION   ⬇️   **************/
    /*******************************************************************/

    /**
     * Converti plusieurs {@link IBaseEntity entité} en un '{@link IEntityDAO DTO}'.
     *
     * @param dto Une instance de {@link IEntityDAO} pour la conversion
     *            (Utiliser une instance correspondante aux entités à convertir).
     * @param entities une {@link Collection liste} d'{@link IBaseEntity entité}s à convertir.
     *
     * @param <E> Spécifie le type des {@link IBaseEntity entité}s.
     * @param <DTO> Spécifie le type du {@link IEntityDAO DTO}.
     *
     * @return Le '{@link IEntityDAO DTO}' de l'{@link IBaseEntity entité}.
     */
    public <E extends IBaseEntity, DTO extends IEntityDAO<E, DTO>> Collection<DTO> convertToDTOs(DTO dto, Collection<E> entities) {

        // On renvoie la liste de DTO(s) et les convertis en leur 'DTO' respectif
        return entities.stream().map(entity -> convertToDTO(dto, entity)).collect(Collectors.toList());
    }

    /**
     * Converti une {@link IBaseEntity entité} en un '{@link IEntityDAO DTO}'.
     *
     * @param dto Une instance de {@link IEntityDAO} pour la conversion.
     *            (Utiliser une instance correspondante à l'entité à convertir)
     * @param entity L'{@link IBaseEntity entité} à convertir.
     *
     * @param <E> Spécifie le type de l'{@link IBaseEntity entité}.
     * @param <DTO> Spécifie le type du {@link IEntityDAO DTO}.
     *
     * @return Le '{@link IEntityDAO DTO}' de l'{@link IBaseEntity entité}.
     */
    public <E extends IBaseEntity, DTO extends IEntityDAO<E, DTO>> DTO convertToDTO(DTO dto, E entity) {

        // On renvoie le DTO et le converti en son 'DTO' respectif
        return dto.convert(entity);
    }
}
