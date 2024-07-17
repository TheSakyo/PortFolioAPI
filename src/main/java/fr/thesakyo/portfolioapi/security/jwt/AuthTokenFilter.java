package fr.thesakyo.portfolioapi.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.thesakyo.portfolioapi.exceptions.UnauthorizedException;
import fr.thesakyo.portfolioapi.exceptions.throwables.UserNameNotMatchingCause;
import fr.thesakyo.portfolioapi.security.UserConnection;
import fr.thesakyo.portfolioapi.services.authentication.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class AuthTokenFilter extends OncePerRequestFilter {

      @Autowired
      private UserConnection userConnection; // Utilitaire de sécurité concordant à la connexion de l'utilisateur.

      @Autowired
      private UserDetailsServiceImpl userDetailsService; // Service concordant à la connexion/inscription de l'utilisateur.

      /*******************************************************************/
      /*******************************************************************/

    /**
     * Essaie de définir l'authentification d'un utilisateur à partir {@link HttpServletRequest requête http} récupéré
     * pour ensuite traiter la requête envoyée et sa réponse générée.
     *
     * @param request La {@link HttpServletRequest requête http} cible.
     * @param response La {@link HttpServletResponse réponse de la requête http} cible.
     *
     * @param filterChain  Une {@link FilterChain chaîne de filtrage} en question qui traitera la requête et sa réponse.
     *
     * @throws ServletException Une exception est levée en cas d'erreur interne avec une exception '{@link ServletException}'.
     * @throws IOException Une exception est levée en cas d'erreur interne avec une exception '{@link IOException}'.
     */
      @Override
      protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

          /**
           * On essaie de récupérer l'utilisateur actuellement authentifié pour ensuite traiter la requête envoyée et la réponse générée.
           */
          try {

              // On initialise la connexion de l'utilisateur connecté à partir de la requête http
              UserConnection userLogged = userConnection.initConnection(request);

              /******************************************/

              /**
               * Si la connexion de l'utilisateur connecté a bien été initialisé et bien valide,
               * on essaie de récupérer l'utilisateur par les informations de l'utilisateur connecté.
               */
              if(userLogged != null) {

               /**
                *  On essaie de recharger et récupéré l'utilisateur authentifié (en cas de soucis, cette méthode peut générer des exceptions, dans ce cas les exceptions seront gérés plus bas)
                */
                UserDetails userDetails = userDetailsService.loadCompletelyUser(userLogged.getId(), userLogged.getUsername());

                /**************************************/
                /**************************************/

                // On récupère l'authentification depuis l'utilisateur authentifié récupéré
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // On ajoute à l'authentification les détails de l'utilisateur depuis la requête récupérée
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                /**************************************/
                /**************************************/

                // On ajoute donc l'authentification récupérée depuis l'utilisateur authentifié dans le contexte de sécurité de l'application
                SecurityContextHolder.getContext().setAuthentication(authentication);
              }

              /***********************************************/
              /***********************************************/

              filterChain.doFilter(request, response); // On traite la requête http et sa réponse
              response.setHeader("X-Content-Type-Options", "nosniff");

        /**
         *  En cas d'exception du nom d'utilisateur non trouvé ou d'exception non autorisé, on gère l'exception récupérée
         *  (si la requête s'agit d'une déconnexion, on laisse l'application traiter la requête normalement)
         */
        } catch(UsernameNotFoundException | UnauthorizedException ex) {

            // Si la requête récupère la déconnexion de l'utilisateur, on laisse faire (traite la requête http et sa réponse)
            if(request.getServletPath().endsWith("auth/signout")) {

                filterChain.doFilter(request, response);
                response.setHeader("X-Content-Type-Options", "nosniff");
            }

            // ⬇️ Sinon, on gère l'exception ⬇️ //
            else {

                // ⬇️ Si l'exception est une exception de type 'Non Autorisé', on renvoie une erreur personnalisée à partir de l'exception côté 'front' ⬇️ //
                if(ex instanceof UnauthorizedException unauthorizedException) {

                    // On essaie de récupérer la cause initiale de type 'UserNameNotMatchingCause' (aucune correspondance avec le nom d'utilisateur)
                    UserNameNotMatchingCause initialCause = unauthorizedException.getInitialCause(UserNameNotMatchingCause.class);

                    /******************************************************/

                    // Si la cause initiale n'est pas 'null', on peut gérer la réponse d'erreur qui sera envoyée 'coté front'.
                    if(initialCause != null) {

                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // On définit le status de la réponse étant une réponse 401 'Non Autorisé'
                        response.setContentType("application/json/"); // On lui renseigne un type de réponse 'JSON'.

                        /**************************************************/

                        Map<String, Object> responseErrorMap = getUsernameErrorJSON(unauthorizedException, initialCause); // Génère le crops de la réponse d'erreur de type JSON a envoyé
                        response.getWriter().write(new ObjectMapper().writeValueAsString(responseErrorMap)); // On renvoie le dictionnaire 'map' dans l'erreur de la réponse http
                    }
                }
                // ⬆️ Si l'exception est une exception de type 'Non Autorisé', on renvoie une erreur personnalisée à partir de l'exception côté 'front' ⬆️ //
            }
            // ⬆️ Sinon, on gère l'exception ⬆️ //
        }
      }

    /*******************************************************************/
    /*******************************************************************/
    /*******************************************************************/

    /**
     * Créer un dictionnaire 'map' pour récupérer une clé → valeur (utile pour générer le corps de la réponse d'erreur http en cas de nom d'utilisateur changé).
     *
     * @param unauthorizedException {@link UnauthorizedException Exception} de type 'Non Autorisé' étant la cause de du nom d'utilisateur non correspondant.
     * @param initialCause La {@link UserNameNotMatchingCause cause initiale} du fait que le nom d'utilisateur ne correspond pas pour l'authentification.
     *
     * @return un dictionnaire 'map' pour récupérant une clé → valeur permettant de générer en {@link ResponseEntity réponse http} un JSON clé → valeur.
     */
    private static Map<String, Object> getUsernameErrorJSON(UnauthorizedException unauthorizedException, UserNameNotMatchingCause initialCause) {

        Map<String, Object> responseMap = new HashMap<>(); // Dictionnaire 'map' pour récupérer une clé → valeur (utile pour le retour de la réponse http)

        // Envoie dans le dictionnaire 'map' le message de l'exception
        responseMap.putIfAbsent("errorMessage", unauthorizedException.getMessage());

        // Envoie dans le dictionnaire 'map' le nom d'utilisateur cible qui a causé l'exception
        responseMap.putIfAbsent("invalidUsernameCause", initialCause.getTargetUsername());

        // Envoie dans le dictionnaire 'map' le nom d'utilisateur qui devrait normalement correspondre
        responseMap.putIfAbsent("validUsernameToPreventException", initialCause.getValidUsername());

        /*************************************/

        return responseMap;
    }

    /*******************************************************************/
    /*******************************************************************/


}