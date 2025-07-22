package ch.admin.bar.siard2.cmd.postgres.issues.siardsuite128;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VarCharTypesPostgresIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public PostgreSQLContainer<?> db = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"))
            .withInitScript(SqlScripts.Postgres.SIARDSUITE_128_VARCHAR);

    @Test
    public void uploadAndDownload_expectNoExceptions() throws SQLException, IOException, ClassNotFoundException {
        // given
        val createdArchive = siardArchivesHandler.prepareEmpty();

        // when
        SiardFromDb dbtoSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + createdArchive.getPathToArchiveFile()
        });

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File desktopDir = new File("/home/bbertagna/Desktop");
        File destFile = new File(desktopDir, "bit_type_test_" + timestamp + ".siard");
        Files.copy(createdArchive.getPathToArchiveFile().toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Archive copied to: " + destFile.getAbsolutePath());

 //       val siardArchive = siardArchivesHandler.prepareResource("issues/siardgui29/created-bit-type.siard");
//        SiardToDb siardToDb = new SiardToDb(new String[]{
//                "-o",
//                "-j:" + db.getJdbcUrl(),
//                "-u:" + db.getUsername(),
//                "-p:" + db.getPassword(),
//                "-s:" + siardArchive.getPathToArchiveFile()
//        });

        // then
        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbtoSiard.getReturn());
        //Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }
}
