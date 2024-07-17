package ch.admin.bar.siard2.cmd.usecases.types.upload.spatialtypes;

import ch.admin.bar.siard2.cmd.SupportedDbVersions;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

public class MariaDBSpatialDataTypesUploadIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MariaDBContainer<?> db = new MariaDBContainer<>(DockerImageName.parse(SupportedDbVersions.MARIA_DB_10))
            .withUsername("admin")
            .withPassword("password")
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource(MySqlSpatialDataTypesUpload.CREATE_IT_USER_SQL_SCRIPT),
                    "/docker-entrypoint-initdb.d/"
            );

    @Test
    public void executeTest() {
        MySqlSpatialDataTypesUpload.executeTest(siardArchivesHandler, db.getJdbcUrl().replace("jdbc:mariadb", "jdbc:mysql"));
    }
}
