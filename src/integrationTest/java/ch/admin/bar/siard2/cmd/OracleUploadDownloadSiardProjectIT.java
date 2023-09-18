package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.cmd.utils.ConsoleLogConsumer;
import ch.admin.bar.siard2.cmd.utils.ResourcesLoader;
import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.MountableFile;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

@Slf4j
public class OracleUploadDownloadSiardProjectIT {


    @Rule
    public final TemporaryFolder zippedDownloadedProjectFileTempFolder = new TemporaryFolder();

    @Rule
    public final OracleContainer db;

    public OracleUploadDownloadSiardProjectIT() {
        db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
                .withLogConsumer(new ConsoleLogConsumer())
                .withCopyFileToContainer(
                        MountableFile.forHostPath(ResourcesLoader.loadResource(ResourcesLoader.ORACLE_INIT).toPath()),
                        "/container-entrypoint-initdb.d/00_create_user.sql");
    }

    @Test
    public void uploadAndDownload_expectNoExceptions() throws IOException, SQLException, ClassNotFoundException {
        // given
        final File siardProject = ResourcesLoader.loadResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_ORACLE18_2_2);

        // when
        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + "IT_USER",
                "-p:" + "password",
                "-s:" + siardProject.getPath()
        });
        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + "IT_USER",
                "-p:" + "password",
                "-s:" + zippedDownloadedProjectFileTempFolder.getRoot().toString()
        });

        // then
        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());
    }
}
