package fr.thesakyo.portfolioapi.controllers;

import fr.thesakyo.portfolioapi.services.entities.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
