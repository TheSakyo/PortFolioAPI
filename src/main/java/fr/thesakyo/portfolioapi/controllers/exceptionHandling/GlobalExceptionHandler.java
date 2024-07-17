package fr.thesakyo.portfolioapi.controllers.exceptionHandling;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**************************************************************************************************/
    /*************   ⬇️    DIFFÉRENTES MÉTHODES SPÉCIFIQUES CAPTURANT UNE EXCEPTION   ⬇️   ***********/
    /**************************************************************************************************/

    /**
     * Envoie une exception en cas d'une authentication non autorisé.
     *
     * @param request La {@link HttpServletRequest requête} de la {@link ResponseEntity réponse http} renvoyée.
     * @param ex L'{@link AuthenticationException Exception} en question au moment de l'authentification.
     *
     * @return Une exception {@link HashMap} englobant des détails sur l'erreur pour la partie 'front'.
     */
    @ExceptionHandler(AuthenticationException.class)
    public Map<String, Object> handleAuthenticationException(HttpServletRequest request, AuthenticationException ex) {

        return buildErrorResponse(request.getServletPath(), "Unauthorized Authentication", HttpStatus.UNAUTHORIZED, ex);
    }

    /**
     * Envoie une exception en cas d'une action non autorisé.
     *
     * @param request La {@link HttpServletRequest requête} de la {@link ResponseEntity réponse http} renvoyée.
     * @param ex L'{@link AccessDeniedException Exception} en question au moment d'une action non autorisé.
     *
     * @return Une exception {@link HashMap} englobant des détails sur l'erreur pour la partie 'front'.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Map<String, Object> handleAccessDeniedException(HttpServletRequest request, AuthenticationException ex) {

        return buildErrorResponse(request.getServletPath(), "Access Denied", HttpStatus.FORBIDDEN, ex);
    }

    /**
     * Envoie une exception en cas d'erreur de serveur interne.
     *
     * @param request La {@link HttpServletRequest requête} de la {@link ResponseEntity réponse http} renvoyée.
     * @param ex L'{@link Exception Exception} globale en question.
     *
     * @return Une exception {@link HashMap} englobant des détails sur l'erreur pour la partie 'front'.
     */
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleException(HttpServletRequest request, Exception ex) {

        return buildErrorResponse(request.getServletPath(), "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    /********************************************************************************************************************/
    /********************************************************************************************************************/

    /**
     * Permet de construire une exception avec des erreurs détaillées en {@link HashMap} pour la lire côté 'front'.
     *
     * @param path Le {@link String chemin} de l'api où l'erreur a été levée.
     * @param error Un {@link String titre d'erreur} en question.
     * @param status Un {@link HttpStatus status} de {@link ResponseEntity réponse http}.
     * @param ex L'{@link Exception Exception} qui a été levée.
     *
     * @return Une exception {@link HashMap} englobant des détails sur l'erreur ('status http', 'chemin api', 'titre', 'message, 'cause').
     */
    private static Map<String, Object> buildErrorResponse(String path, String error, HttpStatus status, Exception ex) {

        Map<String, Object> body = new HashMap<>(); // Dictionnaire 'map' pour récupérer une clé → valeur (utile pour le retour de la réponse http)

        body.put("path", path); // Ajoute une clé → valeur pour le chemin de l'api
        body.put("error", error); // Ajoute une clé → valeur pour le titre de l'erreur en question
        body.put("status", status.value()); // Ajoute une clé → valeur pour le status de la réponse 'http'
        body.put("message", ex.getMessage()); // Ajoute une clé → valeur pour la cause de l'erreur en question
        body.put("cause", ExceptionUtils.getRootCauseMessage(ex)); // Ajoute une clé → valeur pour le message de l'erreur en question

        ex.printStackTrace(System.err); // Imprime l'erreur dans la console 'coté back' (utile pour le débogage)
        return body; // On renvoie la réponse de l'erreur détaillée pour la partie 'front'
    }
}

