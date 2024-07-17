package fr.thesakyo.portfolioapi.security;

import fr.thesakyo.portfolioapi.models.entities.User;
import fr.thesakyo.portfolioapi.interfaces.IConnection;
import fr.thesakyo.portfolioapi.repositories.UserRepository;
import fr.thesakyo.portfolioapi.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserConnection implements IConnection {

    private static User userLoggedModel; // Permettra de récupérer le modèle entier de l'utilisateur connecté

    private Long id; // Propriété concordant à l'identifiant de l'utilisateur connectée
    private String username; // Propriété concordant au nom d'utilisateur de l'utilisateur connectée

    /***************************************************/

    @Autowired
    private UserRepository userRepository; // Interface référentielle pour concorder à la table des utilisateurs dans la base de données.

    /***************************************************/

    // Utilitaire de sécurité JSON Web Token (JWT) permettant de générer un jeton (token) pour l'utilisateur [Utile pour la création de cookie permettant la connexion de l'utilisateur].
    @Autowired
    private JwtUtils jwtUtils;

    /****************************************************************************/
    /****************************************************************************/

    /**
     * Récupère l'{@link Long identifiant} de l'utilisateur connecté.
     *
     * @return L'{@link Long Identifiant} de l'utilisateur actuellement connecté.
     */
    @Override
    public Long getId() { return id; }

    /**
     * Récupère le {@link String nom d'utilisateur} de l'utilisateur connecté.
     *
     * @return Le {@link String nom d'utilisateur de l'utilisateur actuellement connecté.
     */
    @Override
    public String getUsername() { return username; }

    /****************************************************************************/
    /****************************************************************************/

    /**
     * Initialise la connection de l'utilisateur actuellement authentifié à partir d'une {@link HttpServletRequest requête http} envoyée en récupérant son cookie
     *
     * @param request La {@link HttpServletRequest requête http} envoyée permettant de récupérer le cookie
     *
     * @return Une instance de l'utilitaire de sécurité concordant à la connexion de l'utilisateur. [{@link UserConnection}]
     */
    public UserConnection initConnection(HttpServletRequest request) {

        String jwt = parseJwt(request); // On récupère le cookie en chaîne de caractère depuis la requête récupérée.

        /***************************************/

        /**
         * Si le cookie récupère n'est pas 'null' et bien valide, on essaie de récupérer
         * l'utilisateur lié au cookie en récupérant son identifiant et son nom d'utilisateur
         */
        if(jwt != null && jwtUtils.validateJwtToken(jwt)) {

            this.id = Long.valueOf(jwtUtils.getIdFromJwtToken(jwt)); // Définit l'identifiant de l'utilisateur authentifié à partir du jeton du cookie
            this.username = jwtUtils.getUserNameFromJwtToken(jwt); // Définit le nom d'utilisateur de l'utilisateur authentifié à partir du jeton du cookie

            /**********************************/

            userRepository.findById(this.id).ifPresent(UserConnection::setUserLoggedModel); // On définit le modèle de l'utilisateur connecté en base de données

            /**********************************/

            return this; // On renvoie l'instance de la connexion de l'utilisateur.
        }

        /***************************************/

        this.id = null; // L'Identifiant n'a pû être trouvée ('null')
        this.username = null; // Le nom d'utilisateur n'a pû être trouvée ('null')

        return null; // On renvoie 'null'.
    }

    /************************************************************************/

    /**
     * Récupère l'instance de l'{@link User utilisateur} connecté depuis la base de données.
     *
     * @return L'Instance de l'{@link User utilisateur} connecté depuis la base de données.
     */
    public static User getUserLogged() { return userLoggedModel; }

    /************************************************************************/

    /**
     * Définit le modèle entier de l'{@link User utilisateur} connecté depuis la base de données.
     *
     * @param userLoggedModel Le modèle entier de l'{@link User utilisateur} connecté depuis la base de données.
     */
    private static void setUserLoggedModel(User userLoggedModel) { UserConnection.userLoggedModel = userLoggedModel; }

    /**
     * Récupère la {@link String valeur du 'cookie'} JSON Web Token (JWT) à partir d'une {@link HttpServletRequest requête http} donnée.
     *
     * @param request La {@link HttpServletRequest requête http} cible.
     *
     * @return la {@link String valeur du 'cookie'} JSON Web Token (JWT) à partir la {@link HttpServletRequest requête http} cible.
     */
    private String parseJwt(HttpServletRequest request) { return jwtUtils.getJwtFromCookies(request); }
}
