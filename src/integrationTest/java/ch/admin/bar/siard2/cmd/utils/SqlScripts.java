package ch.admin.bar.siard2.cmd.utils;

public class SqlScripts {

    public static class MsSql {
        public final static String ISSUE_25 = "config/mssql/issue-25.sql";
        public final static String DUPLICATE_EXTENDED_PROPERTIES = "config/mssql/duplicated-extended-properties.sql";
    }

    public static class Oracle {
        public final static String CREATE_USER_WITH_ALL_PRIVILEGES = "config/oracle/00_create_user.sql";
    }
}
