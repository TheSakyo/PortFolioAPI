package fr.thesakyo.portfolioapi.services.authentication;

import fr.thesakyo.portfolioapi.repositories.UserRepository;
import fr.thesakyo.portfolioapi.services.DTOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    @Autowired
    private UserRepository userRepository; // Référentiel faisant référence aux utilisateurs de la base de données.

    @Autowired
    private DTOService dtoService; // Récupère le service lié au DTO

    /***********************************************************/
    /**************   ⬇️    MÉTHODES CRUD   ⬇️   **************/
    /***********************************************************/
}
