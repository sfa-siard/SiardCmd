package ch.admin.bar.siard2.cmd.utils;

public class SqlScripts {
    public static class Oracle {
        public final static String CREATE_USER_WITH_ALL_PRIVILEGES = "config/oracle/00_create_user.sql";
        public final static String JDBCBASE_7 = "issues/jdbcbase7/jdbcbase7.sql";
        public final static String SIARDCMD_15 = "issues/siardcmd15/siardcmd_15.sql";
        public final static String MULTIPLE_SCHEMAS = "databases/oracle/multiple-schemas.sql";
        public final static String ADVANCED_SETUP = "databases/oracle/advanced_setup.sql";
    }

    public static class Postgres {
        public final static String ISSUE_31 = "issues/siardcmd31/case-sensitive-column-names-postgres.sql";
    }

    public static class MySQL {
        public final static String ISSUE_31 = "issues/siardcmd31/case-sensitive-column-names-mysql.sql";
        public final static String SIARDGUI_29 = "issues/siardgui29/bit-type-schema.sql";
        public final static String SIARDGUI_32_FOREIGN_KEY = "issues/siardgui32/foreign-key-with-spaces.sql";
        public final static String SIARDGUI_32_TABLE_NAME = "issues/siardgui32/table-with-underscore.sql";
    }
}
