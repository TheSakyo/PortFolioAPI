package fr.thesakyo.portfolioapi.models.entities;

import fr.thesakyo.portfolioapi.helpers.MapperHelper;
import fr.thesakyo.portfolioapi.interfaces.IBaseEntity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.io.Serializable;

@MappedSuperclass
public abstract class BaseEntity implements IBaseEntity, Serializable {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identifiant de l'entité.

    /****************************************************************/
    /**************   ⬇️    GETTERS & SETTERS    ⬇️   **************/
    /***************************************************************/

    /**
     * Récupère l'{@link Long identifiant} de l'{@link BaseEntity entité}.
     *
     * @return L'{@link Long Identifiant} de l'{@link BaseEntity entité}.
     */
    @Override
    public Long getId() { return id; }

    /**
     * Définit l'{@link Long identifiant} de l'{@link BaseEntity entité}.
     *
     * @param id L'{@link Long Identifiant} de l'{@link BaseEntity entité}.
     */
    @Override
    public void setId(Long id) { this.id = id; }

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
