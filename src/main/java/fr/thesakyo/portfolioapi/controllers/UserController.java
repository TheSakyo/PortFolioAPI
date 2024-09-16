package fr.thesakyo.portfolioapi.controllers;

import fr.thesakyo.portfolioapi.models.SerializableResponseEntity;
import fr.thesakyo.portfolioapi.payloads.requests.RoleRequest;
import fr.thesakyo.portfolioapi.services.entities.UserService;
import fr.thesakyo.portfolioapi.models.entities.User;
import fr.thesakyo.portfolioapi.models.entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Set;

@Controller
@RequestMapping("/api/users")
public class UserController {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/
    
    @Autowired
    private UserService userService; // Service concordant à la table des utilisateurs dans la base de données.

    /***************************************************************/
    /**************   ⬇️    MÉTHODES DE REQUÊTE   ⬇️   ************/
    /***************************************************************/

    /**
     * Récupère tous les {@link User utilisateur}(s).
     *
     * @return Une {@link SerializableResponseEntity réponse sérialisé en http} incluant tous les {@link User utilisateur}(s) connecté(s).
     */
    /*@RequestMapping(value = "/all", method = RequestMethod.GET)
    public Set<UserDTO> getAll() { return userService.getAllUsers(); }*/

    /**
     * Récupère un {@link User utilisateur} par son {@link Long identifiant}.
     *
     * @param id L'{@link Long Identifiant} de l'{@link User utilisateur} a récupéré.
     *
     * @return Une {@link SerializableResponseEntity réponse sérialisé en http} récupérant l'{@link User utilisateur} en question.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public SerializableResponseEntity<?> get(@PathVariable("id") Long id) { return userService.getUserById(id); }

    /*********************************************************/

    /**
     * Ajoute un {@link Role rôle} à un {@link Role utilisateur}.
     *
     * @param roleRequest Une '{@link RoleRequest requête de rôle}' pour ajouter un {@link Role rôle} à l'{@link User utilisateur} en question.
     *
     * @return Une {@link SerializableResponseEntity réponse sérialisé en http} incluant une valeur booléenne pour le statut du {@link Role rôle} ajouté ou supprimé pour l'{@link User utilisateur}.
     */
    @RequestMapping(value = "/addRole", method = RequestMethod.PATCH)
    public SerializableResponseEntity<?> addRole(@RequestBody RoleRequest roleRequest) { return userService.addOrRemoveRole(roleRequest, true); }

    /**
     * Supprime un {@link Role rôle} à un {@link Role utilisateur}.
     *
     * @param roleRequest Une '{@link RoleRequest requête de rôle}' pour supprimer un {@link Role rôle} à l'{@link User utilisateur} en question.
     *
     * @return Une {@link SerializableResponseEntity réponse sérialisé en http} incluant une valeur booléenne pour le statut du {@link Role rôle} ajouté ou supprimé pour l'{@link User utilisateur}.
     */
    @RequestMapping(value = "/removeRole", method = RequestMethod.PATCH)
    public SerializableResponseEntity<?> removeRole(@RequestBody RoleRequest roleRequest) { return userService.addOrRemoveRole(roleRequest, false); }

    /*********************************************************/

    /**
     * Active un {@link User utilisateur} en fonction de sa valeur de 'verificationEnabled'.
     *
     * @param id L'{@link Long identifiant} de l'{@link User utilisateur} à activer.
     *
     * @return Une {@link SerializableResponseEntity réponse sérialisé en http} incluant une valeur booléenne pour le statut de l'activation pour l'{@link User utilisateur}.
     */
    @RequestMapping(value = "/enable/{id}", method = RequestMethod.PATCH)
    public SerializableResponseEntity<?> enable(@PathVariable("id") Long id) { return userService.enableUser(id); }

    /**
     * Supprime ou désactive un {@link User utilisateur} en fonction de sa valeur de 'verificationEnabled'.
     *
     * @param id L'{@link Long identifiant} de l'{@link User utilisateur} à supprimer.
     *
     * @return Une {@link SerializableResponseEntity réponse sérialisé en http} incluant une valeur booléenne pour le statut de la suppression et de la désactivation pour l'{@link User utilisateur}.
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public SerializableResponseEntity<?> create(@PathVariable("id") Long id) { return userService.deleteUser(id); }
}
