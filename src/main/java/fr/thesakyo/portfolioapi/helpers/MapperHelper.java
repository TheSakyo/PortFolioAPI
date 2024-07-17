package fr.thesakyo.portfolioapi.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class MapperHelper {

    /************************************************************************************/
    /*************   ⬇️    MÉTHODES UTILES POUR LES MAPPAGE/CONVERSION  ⬇️   ***********/
    /***********************************************************************************/

    /**
     * Permet de récupérer un {@link Object objet} pour la convertir en {@link String chaîne de caractère} avec un format JSON.
     * <>*****<>*****<>*****<>*****<>*****<><>*****<>
     * Information : Si une erreur se produit, le nom de la classe sera renvoyé.
     * <>*****<>*****<>*****<>*****<>*****<><>*****<>
     *
     * @param object L'{@link Object Object} à convertir.
     *
     * @return Une {@link String chaîne de caractère} récupérant l'{@link Object objet} entièrement en format JSON.
     */
    public static String readJsonFromObjectAsString(Object object) {

        ObjectMapper objectMapper = new ObjectMapper();

        /******************************/

        // Cherche et enregistre les modules pour mapper l'objet en une chaîne de caractère de format JSON
        objectMapper.findAndRegisterModules();

        /*******************************/
        /******************************/

        /**
         * On essaie de mapper l'entité récupérée en paramètre en une chaîne de caractère de format JSON
         * tout en effectuant des retours à la ligne pour une meilleure lisibilité
         */
        try { return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object); }

        // En cas d'erreur survenue, on renvoie tous simplement le nom de classe (l'entité n'a pas pû être sérialisé)
        catch(JsonProcessingException ex) {

            System.err.println(ex.getMessage());
            return "Alternative result : " + object.getClass().getName() + " Object";
        }
    }
}
