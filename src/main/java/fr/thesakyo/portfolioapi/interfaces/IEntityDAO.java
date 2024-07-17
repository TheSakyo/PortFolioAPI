package fr.thesakyo.portfolioapi.interfaces;

public interface IEntityDAO<E extends IBaseEntity, DTO extends IEntityDAO<E, DTO>> {

    /**
     * Récupère l'{@link Long identifiant} de l'{@link IBaseEntity entité}.
     *
     * @return L'{@link Long Identifiant} de l'{@link IBaseEntity entité}.
     */
    default Long getId() { return null; }

    /*******************************************************************/

    /**
     * Converti une {@link IBaseEntity entité} en son {@link IEntityDAO objet DAO} concerné.
     *
     * @param entity L'{@link IBaseEntity Entité} en question.
     *
     * @return Un {@link IEntityDAO objet DAO} converti en fonction de son {@link IBaseEntity entité} concerné.
     */
    DTO convert(E entity);
}
