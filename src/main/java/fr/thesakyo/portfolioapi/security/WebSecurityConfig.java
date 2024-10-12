package fr.thesakyo.portfolioapi.security;

import fr.thesakyo.portfolioapi.enums.ERole;
import fr.thesakyo.portfolioapi.helpers.StrHelper;
import fr.thesakyo.portfolioapi.security.jwt.AuthEntryPointJwt;
import fr.thesakyo.portfolioapi.security.jwt.AuthTokenFilter;
import fr.thesakyo.portfolioapi.services.authentication.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
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

import java.util.Arrays;
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

                })).authorizeHttpRequests(auth -> {

                    /**
                     * Tableau des chemins (endpoints) de l'api à autoriser
                     */
                    String[] endpoints = {
                            "/api/auth/**",
                            "/api/users/**",
                            "/api/languages/**",
                            "/api/projects/**",
                            "/api/roles/**",
                            "api/images/**"
                    };

                    /**
                     * Tableau des chemins des ressources de l'API à autoriser
                     */
                    String[] ressources = {
                            "/ressources/**",
                            "/js/**",
                            "/css/**",
                            "/images/**"
                    };

                    /**************************************************/
                    /**************************************************/

                    /**
                     * Condition spéciale pour '/api/auth/signup' (Permission à la connexion)
                     */
                    auth.requestMatchers(request -> {
                        String path = request.getServletPath();
                        return path.startsWith("/api/auth/signup");
                    }).permitAll();

                    /**************************************************/

                    // Configure l'accès aux 'endpoints' avec la méthode http 'GET' (Autorisation d'accès pour tous)
                    auth = configureRequestMatchers(auth, HttpMethod.GET, StrHelper.combineArrays(endpoints, ressources));

                    // Configure l'accès aux 'endpoints' avec la méthode http 'POST' (Autorisation d'accès pour administrateur et super-administrateur uniquement)
                    auth = configureRequestMatchers(auth, HttpMethod.POST, endpoints, ERole.ROLE_ADMIN, ERole.ROLE_SUPERADMIN);

                    // Configure l'accès aux 'endpoints' avec la méthode http 'PATCH' (Autorisation d'accès pour administrateur et super-administrateur uniquement)
                    auth = configureRequestMatchers(auth, HttpMethod.PATCH, endpoints, ERole.ROLE_ADMIN, ERole.ROLE_SUPERADMIN);

                    // Configure l'accès aux 'endpoints' avec la méthode http 'DELETE' (Autorisation d'accès pour administrateur et super-administrateur uniquement)
                    auth = configureRequestMatchers(auth, HttpMethod.DELETE, endpoints, ERole.ROLE_ADMIN, ERole.ROLE_SUPERADMIN);

                    /**************************************************/

                    // Toute autre demande doit être authentifiée
                    auth.anyRequest().authenticated();
                });

        // Correction de la console de base de données H2 : Refus d'afficher ' dans un cadre parce que la valeur 'X-Frame-Options' est 'deny'.
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    /*******************************************************************/
    /*******************************************************************/
    /*******************************************************************/

    /**
     * Configure les autorisations d'accès pour les chemins (endpoints) ainsi que sa {@link HttpMethod méthode HTTP} spécifié.
     *
     * Cette méthode permet d'automatiser la configuration des permissions sur des endpoints
     * en fonction des méthodes HTTP ({@code GET}, {@code POST}, {@code PATCH}, {@code PUT}, {@code DELETE}, etc.) et des rôles définis.
     * Si aucun rôle n'est spécifié, l'accès sera ouvert à tout le monde ({@code permitAll()}).
     *
     * @param auth      L'Objet {@link AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry} utilisé pour configurer les permissions d'accès.
     * @param method    La  {@link HttpMethod méthode HTTP} ({@code GET}, {@code POST}, {@code PATCH}, {@code PUT}, {@code DELETE}, etc.) pour laquelle les permissions sont définies.
     * @param paths     Un tableau de {@link String chaînes de caractères} représentant les chemins (endpoints) à configurer.
     * @param roles     (Optionnel) Un tableau de {@link ERole rôle}s représentant les rôles autorisés à accéder aux endpoints.
     *                  Si ce paramètre est vide, l'accès est autorisé à tout le monde ({@code permitAll()}).
     */
    private AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry configureRequestMatchers(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth, HttpMethod method, String[] paths, ERole... roles)  {

        /**
         * Pour tous les chemins représentant les 'endpoints' à configurer,
         * on autorise l'accès avec sa méthode http associé en fonction des rôles spécifiés.
         */
        for(String path : paths) {

            // Si aucun rôle(s) n'est spécifié(s), on autorise l'accès à tous (permitAll())
            if(roles.length == 0) auth.requestMatchers(method, path).permitAll();

            // Sinon, si des rôle(s) sont spécifié(s), on restreint l'accès à ces rôles
            else auth.requestMatchers(method, path).hasAnyRole(Arrays.stream(roles).map(Enum::toString).toArray(String[]::new));
        }

        return auth; // Une fois la boucle terminée, on renvoie l'Objet AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry
    }
}


