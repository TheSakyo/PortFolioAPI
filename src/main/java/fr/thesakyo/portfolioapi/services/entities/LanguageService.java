package fr.thesakyo.portfolioapi.services.entities;

import fr.thesakyo.portfolioapi.repositories.LanguageRepository;
import fr.thesakyo.portfolioapi.services.DTOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LanguageService {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    @Autowired
    private LanguageRepository languageRepository; // Référentiel faisant référence aux langages de la base de données.

    @Autowired
    private DTOService dtoService; // Récupère le service lié au DTO

    /***********************************************************/
    /**************   ⬇️    MÉTHODES CRUD   ⬇️   **************/
    /***********************************************************/
}
