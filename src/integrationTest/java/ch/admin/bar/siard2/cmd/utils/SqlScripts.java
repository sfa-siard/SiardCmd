package ch.admin.bar.siard2.cmd.utils;

public class SqlScripts {
    public static class Oracle {
        public final static String CREATE_USER_WITH_ALL_PRIVILEGES = "oracle/00_create_user.sql";
        public final static String JDBCBASE_7 = "oracle/issues/jdbcbase7/jdbcbase7.sql";
        public final static String SIARDCMD_15 = "oracle/issues/siardcmd15/siardcmd_15.sql";
        public final static String MULTPLE_SCHEMAS = "oracle/issues/jdbcoracle6/multiple-schemas.sql";
        public static final String PACKAGE = "oracle/issues/jdbcoracle6/package.sql";
    }

    public static class Postgres {
        public final static String SIARDCMD_31 = "postgres/issues/siardcmd31/case-sensitive-column-names-postgres.sql";
        public static final String SIARDSUITE_128_BIT = "postgres/issues/siardsuite128/bit-types-schema-postgres.sql";
        public static final String SIARDSUITE_128_PRECISION_TYPES = "postgres/issues/siardsuite128/precision-types-schema-postgres.sql";
    }

    public static class MySQL {
        public final static String SIARDCMD_31 = "mysql/issues/siardcmd31/case-sensitive-column-names-mysql.sql";
        public final static String SIARDGUI_29_BIT = "mysql/issues/siardgui29/bit-types-schema.sql";
        public final static String SIARDGUI_29_VARCHAR = "mysql/issues/siardgui29/varchar-types-schema.sql";
        public final static String SIARDGUI_32_FOREIGN_KEY = "mysql/issues/siardgui32/foreign-key-with-spaces.sql";
        public final static String SIARDGUI_32_TABLE_NAME = "mysql/issues/siardgui32/table-with-underscore.sql";
    }

    public static class MsSQL {
        public final static String SIARDSUITE_115 = "mssql/issues/siardsuite115/mssql-varchar-types.sql";
        public final static String SIARDSUITE_125 = "mssql/issues/siardsuite125/mssql-bit-types.sql";
    }
}
