package fr.thesakyo.portfolioapi.controllers;

import fr.thesakyo.portfolioapi.services.entities.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/languages")
public class LanguageController {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/
    
    @Autowired
    private LanguageService languageService; // Service concordant à la table des langages dans la base de données.

    /***************************************************************/
    /**************   ⬇️    MÉTHODES DE REQUÊTE   ⬇️   ************/
    /***************************************************************/
}
