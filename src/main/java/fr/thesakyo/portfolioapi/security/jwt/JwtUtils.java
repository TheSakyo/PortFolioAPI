package fr.thesakyo.portfolioapi.security.jwt;

import fr.thesakyo.portfolioapi.models.entities.authentication.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class); // Un enregistreur 'Logger' permettant d'effectuer des messages consoles.

    @Value("${d2planapi.app.jwtSecret}")
    private String jwtSecret; // Clé secrète pour cet utilitaire de sécurité permettant de générer un jeton (token).
    @Value("${d2planapi.app.jwtExpirationMs}")
    private int jwtExpirationMs; // Expiration en millisecond pour cet utilitaire de sécurité permettant de générer un jeton (token).
    @Value("${d2planapi.app.jwtCookieName}")
    private String jwtCookie; // nom du 'cookie' qui sera utilisé pour cet utilitaire de sécurité permettant de générer un jeton (token).

    /*******************************************************************/
    /*******************************************************************/

    /**
     * Génère une {@link Key clé} à partir de la clé secrète de la propriété 'jwtSecret' donnée dans la configuration 'application.properties'.
     *
     * @return Une {@link Key clé} à partir de la clé secrète de la propriété 'jwtSecret' donnée dans la configuration 'application.properties'.
     */
    private Key key() { return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)); }

    /*******************************************************************/

    /**
     * Génère la {@link ResponseCookie réponse du 'cookie'} JSON Web Token (JWT) à partir des {@link UserDetailsImpl détails de l'utilisateur} récupérés à la connexion.
     *
     * @return Une {@link ResponseCookie réponse du 'cookie'} JSON Web Token (JWT) généré pour la connexion de l'utilisateur.
     */
    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {

      String jwt = generateTokenFromUsername(userPrincipal.getId(), userPrincipal.getUsername());
      return ResponseCookie.from(jwtCookie, jwt).path("/api").maxAge(24 * 60 * 60).httpOnly(true).build();
    }

    /**
     * Récupère la {@link String valeur du 'cookie'} JSON Web Token (JWT) à partir de la {@link HttpServletRequest requête http} donnée.
     *
     * @return La {@link String valeur du 'cookie'} JSON Web Token (JWT) à partir de la {@link HttpServletRequest requête http} donnée.
     */
    public String getJwtFromCookies(HttpServletRequest request) {

      Cookie cookie = WebUtils.getCookie(request, jwtCookie);

      if(cookie != null) {

          cookie.setAttribute("SameSite", "None");
          return cookie.getValue();

      } else return null;
    }

    /**
     * Récupère {@link ResponseCookie réponse du 'cookie'} vidée depuis JSON Web Token (JWT).
     *
     * @return Une {@link ResponseCookie réponse du 'cookie'} vide depuis JSON Web Token (JWT).
     */
    public ResponseCookie getCleanJwtCookie() { return ResponseCookie.from(jwtCookie, "").path("/api").maxAge(0).build(); }

    /*******************************************************************/

    /**
     * Génère un {@link String jeton 'token'} à partir d'un nom d'utilisateur.
     *
     * @param id L'{@link String Identifiant} de l'utilisateur dont il est question.
     * @param username Le {@link String nom d'utilisateur} dont il est question.
     *
     * @return Un {@link String jeton 'token'} pour le cookie de l'utilisateur à partir de son {@link String nom d'utilisateur}.
     */
    public String generateTokenFromUsername(Long id, String username) {

      return Jwts.builder().setId(String.valueOf(id)).setSubject(username).setIssuedAt(new Date())
                 .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                 .signWith(key(), SignatureAlgorithm.HS256).compact();
    }

    /**
     * Récupère un {@link String nom d'utilisateur} à partir d'un jeton 'token' du cookie donné depuis JSON Web Token (JWT).
     *
     * @param token Le {@link String jeton 'token'} du cookie dont il est question.
     *
     * @return Un {@link String nom d'utilisateur} à partir d'un {@link String jeton 'token'} du cookie donné depuis JSON Web Token (JWT).
     */
    public String getUserNameFromJwtToken(String token) { return getJwtToken(token).getSubject(); }

    /**
     * Récupère l'{@link String identifiant} à partir d'un jeton 'token' du cookie donné depuis JSON Web Token (JWT).
     *
     * @param token Le {@link String jeton 'token'} du cookie dont il est question.
     *
     * @return Un {@link String nom d'utilisateur} à partir d'un {@link String jeton 'token'} du cookie donné depuis JSON Web Token (JWT).
     */
    public String getIdFromJwtToken(String token) { return getJwtToken(token).getId(); }

    /*******************************************************************/
    /*******************************************************************/

    /**
     * Vérifie si un {@link String jeton 'token'} du cookie authentifié est bien valide.
     *
     * @param authToken Le {@link String jeton 'token'} du cookie authentifié dont il est question.
     *
     * @return Une {@link Boolean valeur booléenne}.
     */
    public boolean validateJwtToken(String authToken) {

      try {

        Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
        return true;

      } catch(MalformedJwtException e) { logger.error("Jeton JSON Web Token (JWT) invalide : {}", e.getMessage()); }
      catch(ExpiredJwtException e) { logger.error("Le jeton JSON Web Token (JWT) a expiré : {}", e.getMessage()); }
      catch(UnsupportedJwtException e) { logger.error("Le jeton JSON Web Token (JWT) n'est pas pris en charge : {}", e.getMessage()); }
      catch(IllegalArgumentException e) { logger.error("La chaîne de revendications JSON Web Token (JWT) est vide : {}", e.getMessage()); }

      return false;
    }

    /*******************************************************************/
    /*******************************************************************/

    /**
     * Récupère à partir d'un jeton 'token' du cookie donné le JSON Web Token (JWT).
     *
     * @param token Le {@link String jeton 'token'} du cookie dont il est question.
     *
     * @return le JSON Web Token (JWT) associée {@link String jeton 'token'} du cookie dont il est question.
     */
    private Claims getJwtToken(String token) { return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody(); }
}