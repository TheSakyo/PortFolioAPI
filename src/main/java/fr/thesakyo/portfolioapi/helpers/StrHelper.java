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

    /**
     * Combine deux tableaux de {@link String chaînes de caractères} en un seul tableau.
     *
     * @param firstArray  Le premier tableau de {@link String chaînes de caractères} à combiner.
     * @param secondArray Le deuxième tableau de {@link String chaînes de caractères} à combiner.
     *
     * @return Un tableau de {@link String chaînes de caractères} combiné contenant tous les éléments des deux tableaux.
     */
    public static String[] combineArrays(String[] firstArray, String[] secondArray) {

        // Crée un tableau de la taille combinée des deux tableaux.
        String[] combinedArray = new String[firstArray.length + secondArray.length];

        /********************/

        // Copie le premier tableau dans le tableau combiné.
        System.arraycopy(firstArray, 0, combinedArray, 0, firstArray.length);

        // Copie le deuxième tableau dans le tableau combiné.
        System.arraycopy(secondArray, 0, combinedArray, firstArray.length, secondArray.length);

        /********************/

        return combinedArray; // Renvoie le tableau combiné.
    }
}
