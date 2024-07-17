package fr.thesakyo.portfolioapi.exceptions;

import jakarta.annotation.Nullable;

public abstract class BaseRuntimeException extends RuntimeException {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    Class<? extends Throwable> initialCauseClass; // Permet de récupérer la classe éxacte de la cause de l'exception (en cas de cause customisé).

    /*****************************************************************/
    /*****************    ⬇️   CONSTRUCTEUR    ⬇️   *****************/
    /****************************************************************/

    /**
     * <p>Construit une nouvelle exception basique d'exécution étendu par {@link RuntimeException} avec le message détaillé et la cause spécifiés.</p>
     * <p>Notez que le message détaillé associé à la cause n'est pas automatiquement incorporé dans le message détaillé de cette exception d'exécution.</p>
     *
     * @param message Le {@link String  message} détaillé (qui est enregistré pour une récupération ultérieure par la méthode '{@code getMessage()}').
     * @param cause La {@link Throwable cause} (qui est enregistrée pour une récupération ultérieure par la méthode '{@code getCause()}').
     * (Une valeur nulle est autorisée et indique que la cause est inexistante ou inconnue.)
     */
    public <T extends Throwable> BaseRuntimeException(String message, @Nullable T cause) {

        super(message, cause);
        if(cause != null) this.initialCauseClass = cause.getClass();
    }

    /**
     * <p>Construit une nouvelle exception basique d'exécution étendu par {@link RuntimeException}.</p>
     *
     * @param message Le {@link String  message} détaillé (qui est enregistré pour une récupération ultérieure par la méthode '{@code getMessage()}').
     */
    public <T extends Throwable> BaseRuntimeException(String message) { super(message); }

    /******************************************************/
    /**************   ⬇️    GETTERS    ⬇️   **************/
    /*****************************************************/

    /**
     * <p>Récupère La {@link Throwable cause} initiale de l'exception (Utilise la méthode '{@code getCause()}' tout en le convertissant par la {@link T cause} donnée).</p>
     * <p>La {@link Throwable cause} sera converti par sa {@link Class<T> classe} entrée en paramètre.</p>
     *
     * @param throwableClass La {@link Class<T> classe} de la {@link Throwable cause} à convertir.
     *
     * @return La {@link T cause} initiale convertie grâce à la {@link Class<T> classe} donnée - 'null', si la conversion fût un échec.
     */
    public <T extends Throwable> T getInitialCause(Class<T> throwableClass) {

        try { return (T)throwableClass.asSubclass(initialCauseClass).cast(this.getCause()); }
        catch(Throwable ignored) { return null; }
    }
}
