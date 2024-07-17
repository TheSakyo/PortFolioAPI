package fr.thesakyo.portfolioapi.services.authentication;

import fr.thesakyo.portfolioapi.models.entities.User;
import fr.thesakyo.portfolioapi.models.entities.authentication.UserDetailsImpl;
import fr.thesakyo.portfolioapi.exceptions.UnauthorizedException;
import fr.thesakyo.portfolioapi.exceptions.throwables.UserNameNotMatchingCause;
import fr.thesakyo.portfolioapi.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

     @Autowired
     private UserRepository userRepository; // Interface référentielle pour concorder à la table des utilisateurs dans la base de données.

     private String validUsername = ""; // Permettra de récupérer le nom d'utilisateur valide de l'utilisateur authentifié

    /*******************************************************************/
    /**************   ⬇️    MÉTHODES DE CONNEXION   ⬇️   **************/
    /******************************************************************/

    /**
     * Permet de générer un {@link UserDetails utilisateur authentifié} à partir de son {@link String nom d'utilisateur} et son {@link Long identifiant} associé.
     *
     * @param id L'{@link Long identifiant} de l'utilisateur à authentifier.
     * @param username Le {@link String nom d'utilisateur} de l'utilisateur à authentifier.
     *
     * @return Un {@link UserDetails utilisateur authentifié}.
     *
     * @throws UnauthorizedException Une exception est levée en cas d'action non autorisé (utilisateur étant authentifié avec un nom d'utilisateur non correspondant).
     */
      @Transactional
      public UserDetails loadCompletelyUser(Long id, String username) throws UnauthorizedException {

        userRepository.findById(id).ifPresent(user -> this.validUsername = user.getEmail());

        /*****************************************/

        try { return loadUserByUsername(username); }
        catch(UsernameNotFoundException ex) {

            UserNameNotMatchingCause cause = new UserNameNotMatchingCause(username, validUsername);
            throw new UnauthorizedException("Unauthorized : Non-matching usernames.", cause);
        }
      }

    /********************************************************************************/
    /********************************************************************************/

    /**
     * Permet de générer un {@link UserDetails utilisateur authentifié} à partir de son {@link String nom d'utilisateur} associé.
     *
     * @param username Le {@link String nom d'utilisateur} de l'utilisateur à authentifier.
     *
     * @return Un {@link UserDetails utilisateur authentifié}.
     *
     * @throws UsernameNotFoundException Une exception est levée en cas de nom d'utilisateur non trouvé.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found."));
        return UserDetailsImpl.build(user);
    }
}
