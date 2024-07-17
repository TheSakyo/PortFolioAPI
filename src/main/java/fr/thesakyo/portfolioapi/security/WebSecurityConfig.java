package fr.thesakyo.portfolioapi.security;

import fr.thesakyo.portfolioapi.security.jwt.AuthEntryPointJwt;
import fr.thesakyo.portfolioapi.security.jwt.AuthTokenFilter;
import fr.thesakyo.portfolioapi.services.authentication.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService; // Service concordant à la connexion/inscription de l'utilisateur.

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler; // Gestionnaire en cas d'accès non autorisé.

    /*******************************************************************/
    /*******************************************************************/

    /**
     * Enregistre un bean MultipartResolver avec des paramètres par défaut. Ce résolveur est
     * adapté pour une utilisation avec des conteneurs Servlet basés sur l'API de partie Servlet 3.0
     * (y compris GlassFish 3.0, Tomcat 7, Jetty 8, et équivalents).
     * <p>Prend en charge les téléversements de fichiers ainsi que l'extraction de messages multipartes avec
     * le type de contenu "multipart/form-data". Les fichiers sont écrits dans
     * le répertoire temporaire du conteneur Servlet par défaut.
     * <p><strong>Note :</strong> Ce résolveur est automatiquement enregistré par
     * {@link DispatcherServlet} s'il n'y a pas d'autre bean MultipartResolver enregistré.
     * Pour configurer le résolveur, vous pouvez définir un bean de type
     * {@code MultipartResolver} avec le même
     * nom de bean ("multipartResolver") dans votre contexte d'application.
     * <p><strong>Note :</strong> À partir de Spring 3.1, lorsqu'il est utilisé dans
     * des environnements Servlet 3.0+, ce résolveur prend en charge la localisation pour les téléversements de fichiers multipartes.
     *
     * @return le MultipartResolver configuré
     *
     * @see MultipartResolver
     * @see StandardServletMultipartResolver
     * @see DispatcherServlet#MULTIPART_RESOLVER_BEAN_NAME
     */
    @Bean
    public MultipartResolver multipartResolver() { return new StandardServletMultipartResolver(); }

    /*******************************************************************/
    /*******************************************************************/

    /**
     * Récupère le {@link AuthenticationManager gestionnaire d'authentification} à partir d'une {@link AuthenticationConfiguration configuration d'authentification}.
     *
     * @param authConfig Une Configuration d'authentification.
     *
     * @return Un {@link AuthenticationManager gestionnaire d'authentification}.
     * @throws Exception Lorsqu'une erreur survient, une exception est levée.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception { return authConfig.getAuthenticationManager(); }

    /**
     * Génère un nouveau {@link PasswordEncoder mot de passe crypté}.
     *
     * @return Un {@link PasswordEncoder mot de passe crypté}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    /*******************************************************************/
    /*******************************************************************/

    /**
     * Génère un nouveau {@link AuthTokenFilter jeton d'authentification filtré}.
     *
     * @return Un {@link AuthTokenFilter jeton d'authentification filtré}.
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() { return new AuthTokenFilter(); }

    /**
     * Récupère un {@link DaoAuthenticationProvider fournisseur d'authentification des objets d'accès aux données}.
     *
     * @return Un {@link DaoAuthenticationProvider fournisseur d'authentification des objets d'accès aux données}.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /*******************************************************************/

    /**
     * Permet de gérer toutes les autorisations d'accès ainsi que le mécanisme de partage de ressources entre multiples origines (CROSS-ORIGIN).
     *
     * @param http Un objet {@link HttpSecurity} qui permet de configurer la sécurité web pour les requêtes http.
     *
     * @return Une {@link SecurityFilterChain chaîne de filtrage sécurisé} pour le site.
     * @throws Exception Lorsqu'une erreur survient, une exception est levée.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable).exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(c -> c.configurationSource(_ -> {

                    CorsConfiguration cors = new CorsConfiguration();
                    String[] allowedOrigins = {
                            "http://localhost:8100",
                    };
                    String[] exposedHeaders = {
                            HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS,
                            HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                    };

                    cors.applyPermitDefaultValues();
                    cors.setAllowedOrigins(List.of(allowedOrigins));
                    cors.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
                    cors.setAllowedHeaders(List.of("*"));
                    cors.setExposedHeaders(List.of(exposedHeaders));
                    cors.setAllowCredentials(true);
                    cors.setMaxAge(3600L);

                    return cors;

                })).authorizeHttpRequests(auth ->
                    auth.requestMatchers(request -> {
                            String path = request.getServletPath();
                            return path.startsWith("/api/auth/signup");
                        }).permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/**").permitAll()
                        .requestMatchers("/api/languages/**").permitAll()
                        .requestMatchers("/api/projects/**").permitAll()
                        .requestMatchers("/api/roles/**").permitAll()
                        .requestMatchers("/api/images/**").permitAll()
                        .requestMatchers("/ressources/**", "/js/**,", "/css/**", "images/**").permitAll()
                        .anyRequest().authenticated()
                );

        // Correction de la console de base de données H2 : Refus d'afficher ' dans un cadre parce que la valeur 'X-Frame-Options' est 'deny'.
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}


