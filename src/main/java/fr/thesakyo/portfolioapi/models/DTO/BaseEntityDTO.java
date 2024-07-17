package fr.thesakyo.portfolioapi.models.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.thesakyo.portfolioapi.helpers.MapperHelper;
import fr.thesakyo.portfolioapi.interfaces.IEntityDAO;
import fr.thesakyo.portfolioapi.models.entities.BaseEntity;

import java.io.Serializable;

public abstract class BaseEntityDTO<E extends BaseEntity, DTO extends BaseEntityDTO<E, DTO>> implements IEntityDAO<E, DTO>, Serializable {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    private Long id; // Identifiant de l'entité

    /*****************************************************************/
    /*****************    ⬇️   CONSTRUCTEUR    ⬇️   *****************/
    /****************************************************************/

    /**
     * Construit une nouvelle {@link BaseEntityDTO entité}.
     */
    public BaseEntityDTO() {}

    /**
     * Construit une nouvelle {@link BaseEntityDTO entité}.
     *
     * @param id {@link Long Identifiant} de l'{@link BaseEntityDTO entité}.
     */
    public BaseEntityDTO(Long id) { this.id = id; }

    /******************************************************/
    /**************   ⬇️    GETTERS    ⬇️   **************/
    /*****************************************************/

    /**
     * Récupère l'{@link Long identifiant} de l'{@link BaseEntityDTO entité}.
     *
     * @return L'{@link Long Identifiant} de l'{@link BaseEntityDTO entité}.
     */
    @Override
    public Long getId() { return id; }

    /**************************************************************/
    /**************   ⬇️    AUTRES MÉTHODES    ⬇️   **************/
    /*************************************************************/

    @JsonIgnore
    public abstract DTO convert(E entity);

    /******************************************************************************************************************/
    /******************************************************************************************************************/
    /******************************************************************************************************************/

    /**
     * Convertit l'{@link Object objet} de l'{@link BaseEntity entité} en {@link String chaîne de caractère}.
     *
     * @return Une {@link String chaîne de caractère} de notre {@link BaseEntity entité}.
     */
    @Override
    public String toString() { return MapperHelper.readJsonFromObjectAsString(this); }
}
