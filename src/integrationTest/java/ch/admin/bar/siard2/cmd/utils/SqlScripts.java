package ch.admin.bar.siard2.cmd.utils;

public class SqlScripts {
    public static class Oracle {
        public final static String CREATE_USER_WITH_ALL_PRIVILEGES = "config/oracle/00_create_user.sql";
        public final static String JDBCBASE_7 = "config/oracle/jdbcbase7.sql";
    }

    public static class Postgres {
        public final static String ISSUE_31 = "issues/siardcmd31/case-sensitive-column-names-postgres.sql";
    }

    public static class MySQL {
        public final static String ISSUE_31 = "issues/siardcmd31/case-sensitive-column-names-mysql.sql";
        public final static String SIARDGUI_29 = "issues/siardgui29/bit-type-schema.sql";
        public final static String ISSUE_SIARDGUI_32 = "issues/siardgui32/foreign-key-with-spaces.sql";
    }
}
