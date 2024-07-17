package fr.thesakyo.portfolioapi.helpers;

import java.util.Arrays;
import java.util.Set;

public abstract class StrHelper {

    /**************************************************************************************************/
    /*************   ⬇️    MÉTHODES UTILES EN RAPPORT AVEC LES CHAÎNES DE CARACTÈRES  ⬇️   ***********/
    /*************************************************************************************************/

    /**
     * Vérifie si une {@link Set collection} de {@link String chaîne de caractère} contient une ou plusieurs {@link String chaîne de caractère}(s) spécifique avec la casse ignorée.
     *
     * @param array La {@link Set collection} de {@link String chaîne de caractère} cible.
     * @param searchValue La {@link String chaîne de caractère} à chercher.
     *
     * @return Une valeur booléenne ('true' ou 'false')
     */
    public static boolean containsIgnoreCase(Set<String> array, String ...searchValue) {

        return Arrays.stream(searchValue).anyMatch(value -> array.stream().anyMatch(element -> element.equalsIgnoreCase(value)));
    }
}
