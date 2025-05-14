package ch.admin.bar.siard2.cmd.issues.siardgui32;

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

public class TableNameUnderscoreIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MySQLContainer<?> db = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5))
            .withUsername("root")
            .withPassword("public")
            .withDatabaseName("public")
            .withConfigurationOverride("config/mysql");

    @Rule
    public MySQLContainer<?> uploadDb = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5))
            .withUsername("root")
            .withPassword("public")
            .withDatabaseName("public")
            .withInitScript(SqlScripts.MySQL.SIARDGUI_32_TABLE_NAME)
            .withConfigurationOverride("config/mysql");

    @Test
    public void uploadSubmittedArchive_expectNoException() throws SQLException, IOException {
        val siardArchive = siardArchivesHandler.prepareResource("issues/siardgui32/provided-table-names-underscore.siard");


        SiardToDb siardToDb = new SiardToDb(new String[] {
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }

    @Test
    public void uploadCreatedArchive_expectNoException() throws SQLException, IOException, ClassNotFoundException {

        val createdArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + uploadDb.getJdbcUrl(),
                "-u:" + uploadDb.getUsername(),
                "-p:" + uploadDb.getPassword(),
                "-s:" + createdArchive
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        val siardArchive = siardArchivesHandler.prepareResource("issues/siardgui32/created-table-names-underscore.siard");
        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + siardArchive.getPathToArchiveFile()
        });
        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }
}
