package ch.admin.bar.siard2.cmd.utils;

public class SiardProjectExamples {

    /**
     * Complex example. TODO: Describe example and use-cases.
     * <p>
     * FIXME: This project seems to have a problem. It seems that it can't be uploaded in any DB. The following error-log
     * appear while upload:
     * - DB2:
     *     com.ibm.db2.jcc.am.SqlIntegrityConstraintViolationException: DB2 SQL Error: SQLCODE=-667, SQLSTATE=23520, SQLERRMC=FKCOMPLEX, DRIVER=4.21.29
     * - Postgres:
     *     org.postgresql.util.PSQLException: ERROR: insert or update on table "tcomplex" violates foreign key constraint "fkcomplex"
     *     Detail: Key (cid)=(1234567890) is not present in table "tsimple".
     * - MS SQL:
     *     com.microsoft.sqlserver.jdbc.SQLServerException: The ALTER TABLE statement conflicted with the FOREIGN KEY constraint
     *     "FKCOMPLEX". The conflict occurred in database "master", table "SampleSchema.TSIMPLE", column 'CINTEGER'.
     *
     * <p>
     * Probably not exported from any DB.
     */
    public final static String SAMPLE_DATALINK_2_2_SIARD = "siard-projects/2_2/sample-datalink-2-2.siard";

    /**
     * Trivial example with just two tables (Teams and Teammembers). Can be used for basic tests.
     * <p>
     * Exported from an Oracle 18 DB.
     */
    public final static String SIMPLE_TEAMS_EXAMPLE_ORACLE18_2_2 = "siard-projects/2_2/simple-teams-example_oracle18_2-2.siard";

    /**
     * Trivial example with just two tables (Teams and Teammembers). Can be used for basic tests.
     * <p>
     * Exported from an Oracle 18 DB.
     */
    public final static String SIMPLE_TEAMS_EXAMPLE_POSTGRES13_2_2 = "siard-projects/2_2/simple-teams-example_postgres13_2-2.siard";

    /**
     * Trivial example with just two tables (Teams and Teammembers). Can be used for basic tests.
     * <p>
     * Exported from an Oracle 18 DB.
     */
    public final static String SIMPLE_TEAMS_EXAMPLE_MSSQL2017CU12_2_2 = "siard-projects/2_2/simple-teams-example_mssql2017cu12_2-2.siard";

    /**
     * Trivial example with just two tables (Teams and Teammembers). Can be used for basic tests.
     * <p>
     * Exported from an Oracle 18 DB.
     */
    public final static String SIMPLE_TEAMS_EXAMPLE_MYSQL5_2_2 = "siard-projects/2_2/simple-teams-example_mysql5_2-2.siard";

    /**
     * Complex example. TODO: Describe example and use-cases.
     * <p>
     * Exported from a Microsoft Access 2010 DB.
     */
    public final static String NORTHWIND_MSACCESS2010_2_1_SIARD = "siard-projects/2_1/northwind_msaccess2010-2-1.siard";

    /**
     * Simple scheme but a lot of records.
     * <p>
     * Exported from a Postgres 11 DB.
     */
    public final static String DVD_RENTAL_2_1_SIARD = "siard-projects/2_1/dvd-rental_postgres11_2-1.siard";

    private SiardProjectExamples() {
    }
}
