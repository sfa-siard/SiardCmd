package ch.admin.bar.siard2.cmd.usecases.keys.download;

import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

public class MariaDBDownloadSiardProjectIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MariaDBContainer<?> db = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.5.5"))
            .withUsername("admin")
            .withPassword("password")
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource(MySqlCreateSimpleTeamsExample.INIT_SCRIPT),
                    "/docker-entrypoint-initdb.d/"
            );

    @Test
    public void download_expectNoExceptions() {
        MySqlCreateSimpleTeamsExample.executeTest(siardArchivesHandler, db.getJdbcUrl().replace("jdbc:mariadb", "jdbc:mysql"));
    }
}
