package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import ch.admin.bar.siard2.cmd.utils.TemporaryFolderPreserver;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchiveComparer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class MySql5UploadDownloadSiardProjectIT {

    @Rule
    public TemporaryFolder zippedDownloadedProjectFileTempFolder = new TemporaryFolder();

    @Rule
    public MySQLContainer<?> db = new MySQLContainer<>(DockerImageName.parse("mysql:5.6.51"))
            .withUsername("root")
            .withPassword("test")
            .withCommand("--max-allowed-packet=1G --innodb_log_file_size=256M");

    @Test
    public void uploadAndDownload_expectNoExceptions() throws IOException, SQLException, ClassNotFoundException {
        // given
        final File siardProject = TestResourcesResolver.loadResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_MYSQL5_2_2);

        // when
        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + "root",
                "-p:" + "test",
                "-s:" + siardProject.getPath()
        });
        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + "root",
                "-p:" + "test",
                "-s:" + zippedDownloadedProjectFileTempFolder.getRoot().toString()
        });

        // then
        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        TemporaryFolderPreserver.builder()
                .caller(this.getClass())
                .tempFolder(zippedDownloadedProjectFileTempFolder)
                .filename(siardProject.getName())
                .preserve();

        SiardArchiveComparer.builder()
                .pathToExpectedArchive(siardProject)
                .pathToActualArchive(zippedDownloadedProjectFileTempFolder.getRoot())
                .compare();
    }
}
