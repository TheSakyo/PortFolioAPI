package fr.thesakyo.portfolioapi.exceptions.throwables;

public class UserNameNotMatchingCause extends Throwable {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    private final String targetUsername; // Permet de récupérer le nom d'utilisateur cible qui généré la cause.
    private final String validUsername; // Permet de récupérer le nom d'utilisateur censé être valide.

    /*****************************************************************/
    /*****************    ⬇️   CONSTRUCTEUR    ⬇️   *****************/
    /****************************************************************/

    /**
     * Construit un nouvel objet jetable avec le message détaillé spécifié.
     * La cause n'est pas initialisée, et peut l'être ultérieurement par un appel à {@link #initCause}.
     *
     * <br/>
     * <br/>
     *
     * <p>La méthode {@link #fillInStackTrace()} est appelée pour initialiser
     * les données de la trace de la pile dans l'objet jetable nouvellement créé.</p>
     *
     * @param targetUsername Le {@link String nom d'utilisateur} cible qui généré la {@link Throwable cause}.
     * @param validUsername Le {@link String nom d'utilisateur} qui est censé être valide.
     */
    public UserNameNotMatchingCause(String targetUsername, String validUsername) {

        super("Username '" + targetUsername + "' does not match username '" + validUsername + "'.");

        /************************************/

        this.targetUsername = targetUsername;
        this.validUsername = validUsername;
    }

    /******************************************************/
    /**************   ⬇️    GETTERS    ⬇️   **************/
    /*****************************************************/

    /**
     * Récupère le {@link String nom d'utilisateur} qui a géré la {@link Throwable cause}.
     *
     * @return Le {@link String nom d'utilisateur} qui a géré la {@link Throwable cause}
     */
    public String getTargetUsername() { return this.targetUsername; }

    /**
     * Récupère le {@link String nom d'utilisateur} qui devrait être valide.
     *
     * @return Le {@link String nom d'utilisateur} qui devrait être valide
     */
    public String getValidUsername() { return this.validUsername; }
}
