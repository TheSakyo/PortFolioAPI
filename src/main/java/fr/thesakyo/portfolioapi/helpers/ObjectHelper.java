package fr.thesakyo.portfolioapi.helpers;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class ObjectHelper {

    /**
     * Compare deux objets pour voir s'ils ont les mêmes valeurs pour tous les attributs.
     *
     * @param obj1 Le premier objet.
     * @param obj2 Le deuxième objet.
     * @param fieldsToExclude Les noms des champs à exclure de la comparaison.
     *
     * @return 'vrai' si les objets ont les mêmes valeurs pour tous les attributs, sinon 'faux'.
     */
    public static boolean areObjectsIdentical(Object obj1, Object obj2, String ...fieldsToExclude) {

        // On a converti les noms des champs à exclure en HashSet
        Set<String> excludeFields = new HashSet<>(List.of(fieldsToExclude));

        // Si les deux objets sont null, ils sont identiques
        if(obj1 == null || obj2 == null) return obj1 == obj2;

        // Les objets doivent être de la même classe, sinon ils ne sont pas identiques, alors on renvoie 'null'
        if(obj1.getClass() != obj2.getClass()) return false;

        /***************************************************/

        // On essaie de comparer les valeurs des attributs
        try {

            Field[] fields = obj1.getClass().getDeclaredFields(); // On récupère tous les attributs de l'objet 1

            /**
             * Pour chaque attribut de l'objet 1, on compare sa valeur avec celle de l'objet 2
             */
            for(Field field : fields) {

                // Si le nom de l'attribut est dans la liste des champs à exclure, on passe au suivant
                if(!excludeFields.isEmpty() && excludeFields.contains(field.getName())) continue;

                field.setAccessible(true); // On autorise l'accès à l'attribut
                Object value1 = field.get(obj1); // On obtient la valeur de l'attribut de l'objet 1
                Object value2 = field.get(obj2); // On obtient la valeur de l'attribut de l'objet 2

                // Les valeurs des attributs doivent être identiques, sinon on renvoie 'faux'
                if(!Objects.equals(value1, value2)) return false;
            }

       // On capture l'exception en cas d'erreur
        } catch(IllegalAccessException e) {

            e.printStackTrace(System.err); // Renvoie une exception en cas d'erreur
            return false; // Renvoie 'faux'
        }

        /***************************************************/

        return true; // Renvoie 'vrai'
    }
}
