package fr.thesakyo.portfolioapi.controllers;

import fr.thesakyo.portfolioapi.models.SerializableResponseEntity;
import fr.thesakyo.portfolioapi.payloads.requests.authentication.user.LoginUserRequest;
import fr.thesakyo.portfolioapi.payloads.requests.authentication.user.RegisterUserRequest;
import fr.thesakyo.portfolioapi.payloads.requests.authentication.user.UpdateUserRequest;
import fr.thesakyo.portfolioapi.services.authentication.AuthService;
import fr.thesakyo.portfolioapi.models.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/
    
    @Autowired
    private AuthService authService; // Service gérant l'authentification des utilisateurs dans la base de données.

    /***************************************************************/
    /**************   ⬇️    MÉTHODES DE REQUÊTE   ⬇️   ************/
    /***************************************************************/

    /**
     * Connexion d'un {@link User utilisateur}.
     *
     * @param loginUserRequest Une '{@link LoginUserRequest requête de connexion}' pour connecter l'{@link User utilisateur} en question.
     *
     * @return Une {@link SerializableResponseEntity réponse sérialisé en http} incluant l'{@link User utilisateur} connecté.
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public SerializableResponseEntity<?> login(@RequestBody LoginUserRequest loginUserRequest) { return authService.authenticate(loginUserRequest); }

    /**
     * Inscription d'un {@link User utilisateur}.
     *
     * @param registerUserRequest Une '{@link RegisterUserRequest requête d'inscription}' pour inscrire l'{@link User utilisateur} en question.
     *
     * @return Une {@link SerializableResponseEntity réponse sérialisé en http} incluant l'{@link User utilisateur} inscrit.
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public SerializableResponseEntity<?> register(@RequestBody RegisterUserRequest registerUserRequest) { return authService.register(registerUserRequest); }

    /**
     * Déconnexion d'un {@link User utilisateur}.
     *
     * @return Une {@link SerializableResponseEntity réponse sérialisé en http} incluant une valeur booléenne pour le statut de la déconnexion.
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public SerializableResponseEntity<?> logout() { return authService.logoutUser(); }

    /*********************************************************/

    /**
     * Mis à jour d'un {@link User utilisateur}.
     *
     * @param id L'{@link Long identifiant} de l'{@link User utilisateur} en question.
     * @param updateUserRequest Une '{@link RegisterUserRequest requête de mise à jour}' pour modifier les informations de l'{@link User utilisateur} en question.
     *
     * @return Une {@link SerializableResponseEntity réponse sérialisé en http} incluant l'{@link User utilisateur} modifié.
     */
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PATCH)
    public SerializableResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody UpdateUserRequest updateUserRequest) { return authService.update(id, updateUserRequest); }
}
