package fr.thesakyo.portfolioapi.interfaces;

import fr.thesakyo.portfolioapi.models.entities.BaseEntity;

import java.io.Serializable;

public interface IBaseEntity extends Serializable {

    /**
     * Récupère l'{@link Long identifiant} de l'{@link BaseEntity entité}.
     *
     * @return L'{@link Long Identifiant} de l'{@link BaseEntity entité}.
     */
    default Long getId() { return null; }

    /**
     * Définit l'{@link Long identifiant} de l'{@link BaseEntity entité}.
     *
     * @param id L'{@link Long Identifiant} de l'{@link BaseEntity entité}.
     */
    default void setId(Long id) {}
}
