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

public class BitTypesIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MySQLContainer<?> emptyDb = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5))
            .withUsername("root")
            .withPassword("public")
            .withDatabaseName("public")
            .withConfigurationOverride("config/mysql");

    @Rule
    public MySQLContainer<?> customDb = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5))
            .withUsername("root")
            .withPassword("public")
            .withDatabaseName("public")
            .withInitScript(SqlScripts.MySQL.SIARDGUI_29_BIT)
            .withConfigurationOverride("config/mysql");

    //This test fails using the archive provided in the issue because typeOriginal BIT was converted to BIT(1)
    @Ignore
    @Test
    public void uploadSubmittedArchive_expectNoException() throws SQLException, IOException {
        val siardArchive = siardArchivesHandler.prepareResource("issues/siardgui29/provided-bit-types.siard");

            SiardToDb siardToDb = new SiardToDb(new String[] {
                    "-o",
                    "-j:" + emptyDb.getJdbcUrl(),
                    "-u:" + emptyDb.getUsername(),
                    "-p:" + emptyDb.getPassword(),
                    "-s:" + siardArchive.getPathToArchiveFile()
            });
        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }

    //Assert that siard archive created by siardcmd does not fail with the same exception when uploaded to db
    @Test
    public void uploadCreatedArchive_expectNoException() throws SQLException, IOException, ClassNotFoundException {
        val createdArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb dbtoSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + customDb.getJdbcUrl(),
                "-u:" + customDb.getUsername(),
                "-p:" + customDb.getPassword(),
                "-s:" + createdArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbtoSiard.getReturn());

        val siardArchive = siardArchivesHandler.prepareResource("issues/siardgui29/created-bit-types.siard");
        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + emptyDb.getJdbcUrl(),
                "-u:" + emptyDb.getUsername(),
                "-p:" + emptyDb.getPassword(),
                "-s:" + siardArchive.getPathToArchiveFile()
        });
        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }
}
