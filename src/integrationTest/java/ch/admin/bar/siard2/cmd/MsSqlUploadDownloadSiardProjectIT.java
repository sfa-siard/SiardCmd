package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.cmd.utils.ResourcesLoader;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testcontainers.containers.MSSQLServerContainer;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class MsSqlUploadDownloadSiardProjectIT {

    @Rule
    public TemporaryFolder zippedDownloadedProjectFileTempFolder = new TemporaryFolder();

    @Rule
    public MSSQLServerContainer db = new MSSQLServerContainer()
            .acceptLicense();

    @Test
    public void uploadAndDownload_expectNoExceptions() throws IOException, SQLException, ClassNotFoundException {
        // given
        final File siardProject = ResourcesLoader.loadResource(ResourcesLoader.SAMPLE_DATALINK_2_2_SIARD);

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
    }
}