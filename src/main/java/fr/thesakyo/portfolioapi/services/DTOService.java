package fr.thesakyo.portfolioapi.services;

import fr.thesakyo.portfolioapi.interfaces.IBaseEntity;
import fr.thesakyo.portfolioapi.interfaces.IEntityDAO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DTOService {

    /********************************************************************/
    /**************   ⬇️    MÉTHODES DE CONVERSION   ⬇️   **************/
    /*******************************************************************/

    public <E extends IBaseEntity, DTO extends IEntityDAO<E, DTO>> List<DTO> convertToDTOs(DTO dto, List<E> entities) {
        return entities.stream().map(entity -> convertToDTO(dto, entity)).collect(Collectors.toList());
    }

    public <E extends IBaseEntity, DTO extends IEntityDAO<E, DTO>> DTO convertToDTO(DTO dto, E entity) {

        return dto.convert(entity);
    }
}
