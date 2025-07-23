package ch.admin.bar.siard2.cmd.postgres.issues.siardsuite128;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
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

public class PrecisionTypesPostgresIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public PostgreSQLContainer<?> db = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"))
            .withInitScript(SqlScripts.Postgres.SIARDSUITE_128_PRECISION_TYPES);

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

        val siardArchive = siardArchivesHandler.prepareResource("postgres/issues/siardsuite128/postgres-created-precision-types.siard");
        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        // then
        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbtoSiard.getReturn());
        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }
}
