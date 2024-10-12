package fr.thesakyo.portfolioapi.enums;

public enum ERole {

    /**
     * Rôle SUPERADMIN.
     */
    ROLE_SUPERADMIN {

        /**
         * Converti un l'{@link ERole énumération} en {@link String chaîne de caractères}.
         *
         * @return Une {@link String chaîne de caractères} de notre {@link ERole énumération}.
         */
        @Override
        public String toString() {  return "SUPERADMIN"; }
    },

    /**
     * Rôle ADMIN.
     */
    ROLE_ADMIN {

        /**
         * Converti un l'{@link ERole énumération} en {@link String chaîne de caractères}.
         *
         * @return Une {@link String chaîne de caractères} de notre {@link ERole énumération}.
         */
        @Override
        public String toString() {  return "ADMIN"; }
    },

    /**
     * Rôle UNKNOWN.
     */
    ROLE_UNKNOWN {

        /**
         * Converti un l'{@link ERole énumération} en {@link String chaîne de caractères}.
         *
         * @return Une {@link String chaîne de caractères} de notre {@link ERole énumération}.
         */
        @Override
        public String toString() {  return "UNKNOWN"; }
    },
}
