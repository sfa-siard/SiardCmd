package ch.admin.bar.siard2.cmd;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class PostgresUploadDownloadSiardProjectIT {

    @Rule
    public TemporaryFolder zippedDownloadedProjectFileTempFolder = new TemporaryFolder();

    @Rule
    public PostgreSQLContainer postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:9.6.12"));

    @Test
    public void test() throws IOException, InterruptedException, SQLException, ClassNotFoundException {
        final String siardProjectUrl = this.getClass()
                .getClassLoader()
                .getResource("siard-projects/sample-datalink-2.2.siard")
                .getFile();


        // given
        postgres.execInContainer("psql", "-d", postgres.getDatabaseName(), "-U", postgres.getUsername(), "-w", "-f", "/tmp/duplicate-rows.sql");

        // when
        SiardToDb siardToDb = new SiardToDb(new String[]{
                "-o",
                "-j:" + postgres.getJdbcUrl(),
                "-u:" + postgres.getUsername(),
                "-p:" + postgres.getPassword(),
                "-s:" + siardProjectUrl
        });
        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + postgres.getJdbcUrl(),
                "-u:" + postgres.getUsername(),
                "-p:" + postgres.getPassword(),
                "-s:" + zippedDownloadedProjectFileTempFolder.getRoot().toString()
        });

        // then
        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());
    }
}
