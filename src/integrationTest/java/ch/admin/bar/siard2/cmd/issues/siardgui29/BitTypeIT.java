package ch.admin.bar.siard2.cmd.issues.siardgui29;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.SupportedDbVersions;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import lombok.val;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class BitTypeIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MySQLContainer<?> uploadDb = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5))
            .withUsername("root")
            .withPassword("public")
            .withDatabaseName("public")
            .withConfigurationOverride("config/mysql");

    @Rule
    public MySQLContainer<?> downloadDb = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5))
            .withUsername("root")
            .withPassword("public")
            .withDatabaseName("public")
            .withInitScript(SqlScripts.MySQL.SIARDGUI_29)
            .withConfigurationOverride("config/mysql");

    //This test will fail until type BIT(1) is supported
    @Ignore
    @Test
    public void uploadSubmittedArchive_expectNoException() throws SQLException, IOException {
        val siardArchive = siardArchivesHandler.prepareResource("issues/siardgui29/provided-bit-type.siard");

            SiardToDb siardToDb = new SiardToDb(new String[] {
                    "-o",
                    "-j:" + uploadDb.getJdbcUrl(),
                    "-u:" + uploadDb.getUsername(),
                    "-p:" + uploadDb.getPassword(),
                    "-s:" + siardArchive.getPathToArchiveFile()
            });
        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }

    //Assert that siard archive created by siardcmd does not fail with the same exception
    @Test
    public void uploadCreatedArchive_expectNoException() throws SQLException, IOException, ClassNotFoundException {
        val createdArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + downloadDb.getJdbcUrl(),
                "-u:" + downloadDb.getUsername(),
                "-p:" + downloadDb.getPassword(),
                "-s:" + createdArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        val siardArchive = siardArchivesHandler.prepareResource("issues/siardgui29/created-bit-type.siard");
        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + uploadDb.getJdbcUrl(),
                "-u:" + uploadDb.getUsername(),
                "-p:" + uploadDb.getPassword(),
                "-s:" + siardArchive.getPathToArchiveFile()
        });
        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }
}
