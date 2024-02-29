package ch.admin.bar.siard2.cmd.usecases.keys.download;

import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public class MySql5DownloadSiardProjectIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MySQLContainer<?> db = new MySQLContainer<>(DockerImageName.parse("mysql:5.6.51"))
            .withUsername("root")
            .withPassword("test")
            .withCommand("--max-allowed-packet=1G --innodb_log_file_size=256M")
            .withInitScript(MySqlCreateSimpleTeamsExample.INIT_SCRIPT);

    @Test
    public void download_expectNoExceptions() {
        MySqlCreateSimpleTeamsExample.executeTest(siardArchivesHandler, db);
    }
}
