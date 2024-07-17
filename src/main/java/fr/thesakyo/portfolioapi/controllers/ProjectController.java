package fr.thesakyo.portfolioapi.controllers;

import fr.thesakyo.portfolioapi.services.entities.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/projects")
public class ProjectController {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/
    
    @Autowired
    private ProjectService projectService; // Service concordant à la table des projets dans la base de données.

    /***************************************************************/
    /**************   ⬇️    MÉTHODES DE REQUÊTE   ⬇️   ************/
    /***************************************************************/
}
