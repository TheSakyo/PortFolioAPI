package fr.thesakyo.portfolioapi.services.entities;

import fr.thesakyo.portfolioapi.repositories.ProjectRepository;
import fr.thesakyo.portfolioapi.services.DTOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    @Autowired
    private ProjectRepository projectRepository; // Référentiel faisant référence aux projets de la base de données.

    @Autowired
    private DTOService dtoService; // Récupère le service lié au DTO

    /***********************************************************/
    /**************   ⬇️    MÉTHODES CRUD   ⬇️   **************/
    /***********************************************************/
}
