package ch.admin.bar.siard2.cmd.usecases.types.upload.defaulttypes;

import ch.admin.bar.siard2.cmd.SupportedDbVersions;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public class MySql8DefaultDataTypesUploadIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MySQLContainer<?> db = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_8))
            .withUsername("root")
            .withPassword("test")
            .withCommand("--max-allowed-packet=1G --innodb_log_file_size=256M")
            .withInitScript(MySqlDefaultDataTypesUpload.CREATE_IT_USER_SQL_SCRIPT);

    @Test
    public void executeTest() {
        MySqlDefaultDataTypesUpload.executeTest(siardArchivesHandler, db.getJdbcUrl());
    }
}
