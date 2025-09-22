package ch.admin.bar.siard2.cmd.oracle.issues.siardgui41;

import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.utils.ConsoleLogConsumer;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.sql.SQLException;

public class UploadToOracleIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public final OracleContainer db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
            .withLogConsumer(new ConsoleLogConsumer())
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.CREATE_USER_WITH_ALL_PRIVILEGES).toPath()),
                    "/container-entrypoint-initdb.d/00_create_user.sql");

    @Test
    public void uploadSiard() throws IOException, SQLException {
        val expectedArchive = siardArchivesHandler.prepareResource("oracle/issues/siardgui41/submitted-bit-and-bit-varying-types.siard");

        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + "IT_USER",
                "-p:" + "password",
                "-s:" + expectedArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }
}
