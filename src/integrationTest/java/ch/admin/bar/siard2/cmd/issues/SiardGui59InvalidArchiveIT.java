package ch.admin.bar.siard2.cmd.issues;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchiveAssertions;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class SiardGui59InvalidArchiveIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MSSQLServerContainer<?> db = new MSSQLServerContainer<>(DockerImageName.parse("mcr.microsoft.com/mssql/server:2017-CU12"))
            .acceptLicense();

    @Test
    public void uploadAndDownload_expectNoExceptions() throws IOException, SQLException, ClassNotFoundException {
        // given
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.Issues.SIARD_GUI_59);
        val actualArchive = siardArchivesHandler.prepareEmpty();

        // when
        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + expectedArchive.getPathToArchiveFile()
        });
        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + actualArchive.getPathToArchiveFile()
        });

        // then
        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        actualArchive.preserveArchive();

        SiardArchiveAssertions.builder()
                .expectedArchive(expectedArchive)
                .actualArchive(actualArchive)
                .updateInstruction(SiardArchiveAssertions.IGNORE_DBNAME) // FIXME ?
                .updateInstruction(SiardArchiveAssertions.IGNORE_PRIMARY_KEY_NAME) // Probably a DB-restriction (primary key names are generated)
                .updateInstruction(SiardArchiveAssertions.IGNORE_FOREIGN_KEY_UPDATE_ACTION) // FIXME https://github.com/sfa-siard/SiardGui/issues/58
                .updateInstruction(SiardArchiveAssertions.IGNORE_FOREIGN_KEY_DELETE_ACTION) // FIXME https://github.com/sfa-siard/SiardGui/issues/58
                .updateInstruction(SiardArchiveAssertions.IGNORE_COLUMN_NULLABLE_FLAG) // FIXME https://github.com/sfa-siard/SiardGui/issues/55
                .assertEqual();
    }
}
