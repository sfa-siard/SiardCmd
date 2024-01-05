package ch.admin.bar.siard2.cmd.issues;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchiveAssertions;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import lombok.val;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

public class CaseSensitiveColumnNamesInPostgresIT {
    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public PostgreSQLContainer<?> db = new PostgreSQLContainer<>(loadDockerfile())
            .waitingFor(new LogMessageWaitStrategy()
                    // oops
                    .withRegEx(".*Datenbanksystem ist bereit, um Verbindungen anzunehmen.*\\s")
                    .withTimes(2)
                    .withStartupTimeout(Duration.of(60, SECONDS)))
            .withEnv("LANG", "de_DE.utf8");
//            .withInitScript(SqlScripts.Postgres.ISSUE_31);

    @Test
    public void download_expectNoExceptions() throws IOException, SQLException, ClassNotFoundException {
        // given
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_POSTGRES13_2_2);
        val actualArchive = siardArchivesHandler.prepareEmpty();

        // when
        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + actualArchive.getPathToArchiveFile()
        });

        // then
        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        SiardArchiveAssertions.builder()
                .actualArchive(actualArchive.preserveArchive())
                .expectedArchive(expectedArchive.preserveArchive())
                .updateInstruction(SiardArchiveAssertions.IGNORE_DBNAME) // FIXME ?
                .assertEqual();
    }

    @Test
    public void uploadAndDownload_expectNoExceptions() throws IOException, SQLException, ClassNotFoundException {
        // given
        val expectedArchive = siardArchivesHandler.prepareResource(SiardProjectExamples.ISSUE_31);
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

        SiardArchiveAssertions.builder()
                .actualArchive(actualArchive.preserveArchive())
                .expectedArchive(expectedArchive.preserveArchive())
                .updateInstruction(SiardArchiveAssertions.IGNORE_DBNAME) // FIXME ?
                .assertEqual();
    }

    private static DockerImageName loadDockerfile() {
        ImageFromDockerfile image = new ImageFromDockerfile()
                .withDockerfile(TestResourcesResolver.resolve("config/postgres/issue31/Dockerfile").toPath());
        return DockerImageName.parse(image.get())
                .asCompatibleSubstituteFor(PostgreSQLContainer.IMAGE);
    }
}
