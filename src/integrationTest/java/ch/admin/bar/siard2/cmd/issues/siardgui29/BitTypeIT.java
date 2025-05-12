package ch.admin.bar.siard2.cmd.issues.siardgui29;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.SupportedDbVersions;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class BitTypeIT {

    public static final String BIT_TYPE_SQL = "issues/siardgui29/bit_type_schema.sql";

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
            .withInitScript(BIT_TYPE_SQL)
            .withConfigurationOverride("config/mysql");

    //Assert that the provided siard archive fails with the expected error
    @Test
    public void uploadSubmittedArchive_expectException() throws SQLException, IOException {
        val siardArchive = siardArchivesHandler.prepareResource("issues/siardgui29/provided-bit-type.siard");

        try {
            SiardToDb siardToDb = new SiardToDb(new String[] {
                    "-o",
                    "-j:" + db.getJdbcUrl(),
                    "-u:" + db.getUsername(),
                    "-p:" + db.getPassword(),
                    "-s:" + siardArchive.getPathToArchiveFile()
            });

            Assert.fail("Expected IllegalArgumentException with message: 'Hex string must have even number of hex digits!'");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Hex string must have even number of hex digits!", e.getMessage());
        }
    }

    //Assert that siard archive created by siardcmd does not fail with the same exception
    @Test
    public void uploadCreatedArchive_expectNoException() throws SQLException, IOException, ClassNotFoundException {
        val createdArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + uploadDb.getJdbcUrl(),
                "-u:" + uploadDb.getUsername(),
                "-p:" + uploadDb.getPassword(),
                "-s:" + createdArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        val siardArchive = siardArchivesHandler.prepareResource("issues/siardgui29/created-bit-type.siard");
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
