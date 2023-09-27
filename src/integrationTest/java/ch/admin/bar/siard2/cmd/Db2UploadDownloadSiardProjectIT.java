package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.cmd.utils.TemporaryFolderPreserver;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchiveComparer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testcontainers.containers.Db2Container;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Db2UploadDownloadSiardProjectIT {

    @Rule
    public TemporaryFolder zippedDownloadedProjectFileTempFolder = new TemporaryFolder();

    @Rule
    public Db2Container db = new Db2Container(DockerImageName.parse("ibmcom/db2:11.5.8.0"))
            .acceptLicense();

    @Test
    public void uploadAndDownload_expectNoExceptions() throws IOException, SQLException, ClassNotFoundException {
        // given
        final File siardProject = TestResourcesResolver.loadResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_ORACLE18_2_2);

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
                .updateInstruction(SiardArchiveComparer.IGNORE_FOREIGN_KEY_UPDATE_ACTION) // FIXME ?
                .updateInstruction(SiardArchiveComparer.IGNORE_FOREIGN_KEY_DELETE_ACTION) // FIXME ?
                .compare();
    }
}
