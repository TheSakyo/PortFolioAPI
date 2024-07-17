package fr.thesakyo.portfolioapi.payloads.requests;

import fr.thesakyo.portfolioapi.helpers.MapperHelper;
import fr.thesakyo.portfolioapi.models.entities.Role;
import fr.thesakyo.portfolioapi.models.entities.User;

import java.io.Serializable;

public class RoleRequest implements Serializable {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    private Long userId; // Identifiant de l'utilisateur

    private Long roleId; // Identifiant du rôle

    /****************************************************************/
    /**************   ⬇️    GETTERS & SETTERS    ⬇️   **************/
    /***************************************************************/

    /**
     * Récupère l'{@link Long identifiant} de l'{@link User utilisateur} depuis la requête.
     *
     * @return L'{@link Long Identifiant} de l'{@link User utilisateur} depuis la requête.
     */
    public Long getUserId() { return userId; }

    /**
     * Récupère l'{@link Long identifiant} du {@link Role rôle} depuis la requête.
     *
     * @return L'{@link Long Identifiant} du {@link Role rôle} depuis la requête.
     */
    public Long getRoleId() { return roleId; }

                    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
                    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
                    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

    /**
     * Définit l'{@link Long identifiant} de l'{@link User utilisateur} depuis la requête.
     *
     * @param userId L'{@link Long Identifiant} de l'{@link User utilisateur} depuis la requête.
     */
    public void setUserId(Long userId) { this.userId = userId; }
    /**
     * Définit l'{@link Long identifiant} du {@link Role rôle} depuis la requête.
     *
     * @param roleId L'{@link Long Identifiant} du {@link Role rôle} depuis la requête.
     */
    public void setRoleId(Long roleId) { this.roleId = roleId; }

    /******************************************************************************************************************/
    /******************************************************************************************************************/
    /******************************************************************************************************************/

    /**
     * Convertit l'{@link Object objet} de la {@link RoleRequest requête} en {@link String chaîne de caractère}.
     *
     * @return Une {@link String chaîne de caractère} de notre {@link RoleRequest objet de requête}.
     */
    @Override
    public String toString() { return MapperHelper.readJsonFromObjectAsString(this); }
}
