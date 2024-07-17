package fr.thesakyo.portfolioapi.payloads.requests;

import fr.thesakyo.portfolioapi.helpers.MapperHelper;
import fr.thesakyo.portfolioapi.interfaces.IBaseEntity;
import fr.thesakyo.portfolioapi.models.entities.BaseEntity;
import fr.thesakyo.portfolioapi.models.entities.Language;

import java.io.Serializable;
import java.util.Set;

public class ProjectsEntityRequest<E extends IBaseEntity> implements Serializable {

    private E entity; // Entité cible associée à cette liste d'entreprise
    private Set<Long> projectsId; // Liste d'identifiant de catégorie(s)

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
    public Set<Long> getProjectsId() { return projectsId; }

    /**
     * Définit la {@link Set liste} d'{@link Long identifiant} de {@link Language catégorie}(s) depuis la requête.
     *
     * @param projectsId La {@link Set liste} d'{@link Long identifiant} de {@link Language catégorie}(s) depuis la requête.
     */
    public void setProjectsId(Set<Long> projectsId) { this.projectsId = projectsId; }

    /**********************************************************************/
    /**********************************************************************/
    /**********************************************************************/

    /**
     * Convertit l'{@link Object objet} de la {@link ProjectsEntityRequest requête} en {@link String chaîne de caractère}.
     *
     * @return Une {@link String chaîne de caractère} de notre {@link ProjectsEntityRequest objet de requête}.
     */
    @Override
    public String toString() { return MapperHelper.readJsonFromObjectAsString(this); }
}
