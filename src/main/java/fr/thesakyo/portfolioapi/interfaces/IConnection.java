package fr.thesakyo.portfolioapi.interfaces;

public interface IConnection {

    /**
     * Récupère l'{@link Long identifiant} de l'utilisateur connecté.
     *
     * @return L'{@link Long Identifiant} de l'utilisateur actuellement connecté.
     */
    Long getId();

    /**
     * Récupère le {@link String nom d'utilisateur} de l'utilisateur connecté.
     *
     * @return Le {@link String nom d'utilisateur de l'utilisateur actuellement connecté.
     */
    String getUsername();
}