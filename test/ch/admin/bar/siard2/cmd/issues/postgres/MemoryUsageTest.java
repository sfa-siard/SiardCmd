package ch.admin.bar.siard2.cmd.issues.postgres;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.testcontainers.utility.MountableFile.forClasspathResource;

/* this test covers the following issues:
- https://github.com/sfa-siard/SiardGui/issues/60
- https://github.com/sfa-siard/SiardGui/issues/61
- https://github.com/users/sfa-siard/projects/1/views/1?pane=issue&itemId=31404142
*/
public class MemoryUsageTest {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    // set up a database that is about 5GB in size
    @Rule
    public PostgreSQLContainer postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:9.6.12"))
            .withCopyFileToContainer(forClasspathResource("scripts/postgres/duplicate-rows.sql"), "/tmp/duplicate-rows.sql")
            .withInitScript("scripts/postgres/memory-usage.sql");

    @Test
    public void shouldExportDatabaseWithLowMemoryFootPrint() throws IOException, InterruptedException, SQLException, ClassNotFoundException {
        final String archiveLocation = tmpFolder.getRoot() + "/archive.siard";
        // given
        postgres.execInContainer("psql", "-d", postgres.getDatabaseName(), "-U", postgres.getUsername(), "-w", "-f", "/tmp/duplicate-rows.sql");

        // when
        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + postgres.getJdbcUrl(),
                "-u:" + postgres.getUsername(),
                "-p:" + postgres.getPassword(),
                "-s:" + archiveLocation
        });

        // then
        assertEquals(0, siardFromDb.getReturn());
    }
}
