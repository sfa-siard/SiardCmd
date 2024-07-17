package ch.admin.bar.siard2.cmd.utils;

public class SqlScripts {
    public static class Oracle {
        public final static String CREATE_USER_WITH_ALL_PRIVILEGES = "config/oracle/00_create_user.sql";
    }

    public static class Postgres {
        public final static String ISSUE_31 = "config/postgres/issue-31_case-sensitive-column-names.sql";
    }
}
