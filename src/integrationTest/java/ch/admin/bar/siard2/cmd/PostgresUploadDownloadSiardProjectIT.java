package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.cmd.utils.TemporaryFolderPreserver;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchiveComparer;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class PostgresUploadDownloadSiardProjectIT {

    @Rule
    public TemporaryFolder zippedDownloadedProjectFileTempFolder = new TemporaryFolder();

    @Rule
    public PostgreSQLContainer<?> db = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"));

    @Test
    public void uploadAndDownload_expectNoExceptions() throws IOException, SQLException, ClassNotFoundException {
        // given
        final File siardProject = TestResourcesResolver.loadResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_POSTGRES13_2_2);

        // when
        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + siardProject.getPath()
        });
        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
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
                .updateInstruction(SiardArchiveComparer.IGNORE_DBNAME) // FIXME ?
                .compare();
    }
}
