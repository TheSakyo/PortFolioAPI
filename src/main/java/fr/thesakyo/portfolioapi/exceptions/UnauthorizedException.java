package fr.thesakyo.portfolioapi.exceptions;

public class UnauthorizedException extends BaseRuntimeException {

    /*****************************************************************/
    /*****************    ⬇️   CONSTRUCTEUR    ⬇️   *****************/
    /****************************************************************/

    /**
     * <p>Construit une nouvelle exception en cas d'action 'Non Autorisée'.</p>
     *
     * @param message Le {@link String  message} détaillé (qui est enregistré pour une récupération ultérieure par la méthode '{@code getMessage()}').
     * @param cause La {@link Throwable cause} (qui est enregistrée pour une récupération ultérieure par la méthode '{@code getCause()}' ou '{@code getInitialCause()}').
     * (Une valeur nulle est autorisée et indique que la cause est inexistante ou inconnue.)
     */
    public <T extends Throwable> UnauthorizedException(String message, T cause) { super(message, cause); }

    /**
     * <p>Construit une nouvelle exception en cas d'action 'Non Autorisée'.</p>
     *
     * @param message Le {@link String  message} détaillé (qui est enregistré pour une récupération ultérieure par la méthode '{@code getMessage()}').
     */
    public <T extends Throwable> UnauthorizedException(String message) { super(message); }
}
