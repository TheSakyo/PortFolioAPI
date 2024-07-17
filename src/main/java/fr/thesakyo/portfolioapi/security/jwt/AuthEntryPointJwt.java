package fr.thesakyo.portfolioapi.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class); // Un enregistreur 'Logger' permettant d'effectuer des messages consoles.

    /**
     * Vérifie et envoie une erreur en cas d'une action d'authentification non autorisée.
     *
     * @param request La {@link HttpServletRequest requête http} cible.
     * @param response La {@link HttpServletResponse réponse de la requête http} cible.
     * @param authException l'{@link AuthenticationException Exception de l'authentication} dont il est question.
     *
     * @throws IOException Une exception est levée en cas d'erreur interne.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {

        /*logger.error("Erreur non autorisée : {}", authException.getCause().getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> body = new HashMap<String, Object>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getCause().getMessage());
        body.put("path", request.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);*/

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
