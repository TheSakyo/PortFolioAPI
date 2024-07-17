package fr.thesakyo.portfolioapi.payloads.requests;

import fr.thesakyo.portfolioapi.helpers.MapperHelper;
import fr.thesakyo.portfolioapi.interfaces.IBaseEntity;
import fr.thesakyo.portfolioapi.models.entities.Language;
import fr.thesakyo.portfolioapi.models.entities.BaseEntity;

import java.io.Serializable;
import java.util.Set;

public class LanguagesEntityRequest<E extends IBaseEntity> implements Serializable {

    private E entity; // Entité cible associée à cette liste d'entreprise
    private Set<Long> languagesId; // Liste d'identifiant de catégorie(s)

    /*********************************************************************/
    /*********************************************************************/
    /*********************************************************************/

    /**
     * Récupère l'{@link BaseEntity entité} cible depuis la requête.
     *
     * @return L'{@link BaseEntity Entité} cible depuis la requête.
     */
    public E getEntity() { return entity; }

    /**
     * Définit l'{@link BaseEntity entité} cible depuis la requête.
     *
     * @param entity L'{@link BaseEntity Entité} cible depuis la requête.
     */
    public void setEntity(E entity) { this.entity = entity; }

    /**********************************************/

    /**
     * Récupère la {@link Set liste} d'{@link Long identifiant} de {@link Language catégorie}(s) depuis la requête.
     *
     * @return La {@link Set liste} d'{@link Long identifiant} de {@link Language catégorie}(s) depuis la requête.
     */
    public Set<Long> getLanguagesId() { return languagesId; }

    /**
     * Définit la {@link Set liste} d'{@link Long identifiant} de {@link Language catégorie}(s) depuis la requête.
     *
     * @param languagesId La {@link Set liste} d'{@link Long identifiant} de {@link Language catégorie}(s) depuis la requête.
     */
    public void setLanguagesId(Set<Long> languagesId) { this.languagesId = languagesId; }

    /**********************************************************************/
    /**********************************************************************/
    /**********************************************************************/

    /**
     * Convertit l'{@link Object objet} de la {@link LanguagesEntityRequest requête} en {@link String chaîne de caractère}.
     *
     * @return Une {@link String chaîne de caractère} de notre {@link LanguagesEntityRequest objet de requête}.
     */
    @Override
    public String toString() { return MapperHelper.readJsonFromObjectAsString(this); }
}
