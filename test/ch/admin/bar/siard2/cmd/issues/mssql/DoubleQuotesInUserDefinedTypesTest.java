package ch.admin.bar.siard2.cmd.issues.mssql;

import ch.admin.bar.siard2.cmd.BaseFromDbTester;
import ch.admin.bar.siard2.cmd.SiardFromDb;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MSSQLServerContainer;

import java.io.IOException;
import java.sql.SQLException;

public class DoubleQuotesInUserDefinedTypesTest extends BaseFromDbTester {

    @Rule
    public MSSQLServerContainer mssqlserver = (MSSQLServerContainer) new MSSQLServerContainer()
            .acceptLicense()
            .withInitScript("scripts/mssql/issue-25.sql");

    @Test
    public void shouldCreateSiardArchiveFromDb() throws SQLException, IOException, ClassNotFoundException {

        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + mssqlserver.getJdbcUrl(),
                "-u:" + mssqlserver.getUsername(),
                "-p:" + mssqlserver.getPassword(),
                "-s:/tmp/issue25.siard"
        });

        Assert.assertEquals(0, siardFromDb.getReturn());
        Assert.assertTrue(true);

    }
}
