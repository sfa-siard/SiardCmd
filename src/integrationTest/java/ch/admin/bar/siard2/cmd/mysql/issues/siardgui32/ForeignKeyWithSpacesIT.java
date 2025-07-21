package ch.admin.bar.siard2.cmd.mysql.issues.siardgui32;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.SupportedDbVersions;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class ForeignKeyWithSpacesIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MySQLContainer<?> uploadDb = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5))
            .withUsername("root")
            .withPassword("public")
            .withDatabaseName("public")
            .withConfigurationOverride("mysql/config/with-blobs");

    @Rule
    public MySQLContainer<?> downloadDb = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5))
            .withUsername("root")
            .withPassword("public")
            .withDatabaseName("public")
            .withInitScript(SqlScripts.MySQL.SIARDGUI_32_FOREIGN_KEY)
            .withConfigurationOverride("mysql/config/with-blobs");


    @Test
    public void uploadSubmittedArchive_expectNoException() throws SQLException, IOException {
        val siardArchive = siardArchivesHandler.prepareResource("mysql/issues/siardgui32/provided-foreign-key.siard");


            SiardToDb siardToDb = new SiardToDb(new String[] {
                    "-o",
                    "-j:" + uploadDb.getJdbcUrl(),
                    "-u:" + uploadDb.getUsername(),
                    "-p:" + uploadDb.getPassword(),
                    "-s:" + siardArchive.getPathToArchiveFile()
            });

        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }

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

        val siardArchive = siardArchivesHandler.prepareResource("mysql/issues/siardgui32/created-foreign-key.siard");
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