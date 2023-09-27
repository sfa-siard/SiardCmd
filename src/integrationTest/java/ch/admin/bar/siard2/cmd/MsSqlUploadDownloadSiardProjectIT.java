package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.cmd.utils.TemporaryFolderPreserver;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchiveComparer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class MsSqlUploadDownloadSiardProjectIT {

    @Rule
    public TemporaryFolder zippedDownloadedProjectFileTempFolder = new TemporaryFolder();

    @Rule
    public MSSQLServerContainer<?> db = new MSSQLServerContainer<>(DockerImageName.parse("mcr.microsoft.com/mssql/server:2017-CU12"))
            .acceptLicense();

    @Test
    public void uploadAndDownload_expectNoExceptions() throws IOException, SQLException, ClassNotFoundException {
        // given
        final File siardProject = TestResourcesResolver.loadResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_MSSQL2017CU12_2_2);

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
                .updateInstruction(SiardArchiveComparer.IGNORE_PRIMARY_KEY_NAME) // Probably a DB-restriction (primary key names are generated)
                .compare();
    }
}
