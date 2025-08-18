package ch.admin.bar.siard2.cmd.mysql.issues.siardgui32;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.cmd.SupportedDbVersions;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedTableId;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.sql.SQLException;

public class TableNameUnderscoreIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public MySQLContainer<?> emptyDb = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5_6))
            .withUsername("root")
            .withPassword("public")
            .withDatabaseName("public")
            .withConfigurationOverride("mysql/config/with-blobs");

    @Rule
    public MySQLContainer<?> db = new MySQLContainer<>(DockerImageName.parse(SupportedDbVersions.MY_SQL_5_6))
            .withUsername("root")
            .withPassword("public")
            .withDatabaseName("public")
            .withInitScript(SqlScripts.MySQL.SIARDGUI_32_TABLE_NAME)
            .withConfigurationOverride("mysql/config/with-blobs");

    //This test fails because the provided archive may have not been created by siard software
    @Ignore
    @Test
    public void uploadSubmittedArchive_expectNoExceptions() throws SQLException, IOException {
        val submittedArchive = siardArchivesHandler.prepareResource("mysql/issues/siardgui32/submitted-table-names-underscore.siard");

        SiardToDb siardToDb = new SiardToDb(new String[] {
                "-o",
                "-j:" + emptyDb.getJdbcUrl(),
                "-u:" + emptyDb.getUsername(),
                "-p:" + emptyDb.getPassword(),
                "-s:" + submittedArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }

    @Test
    public void downloadArchive() throws SQLException, IOException, ClassNotFoundException {
        val siardArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        val metadataExplorer = siardArchive.exploreMetadata();

        val testTable = metadataExplorer.findByTableId(QualifiedTableId.builder()
                .schemaId(Id.of("public"))
                .tableId(Id.of("test_table"))
                .build());
        Assertions.assertThat(testTable.getName()).isEqualTo(Id.of("test_table"));

        val testTableId = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("public"))
                .tableId(Id.of("test_table"))
                .columnId(Id.of("id"))
                .build());
        Assertions.assertThat(testTableId.getType()).contains(Id.of("INT"));
        Assertions.assertThat(testTableId.getTypeOriginal()).contains(Id.of("int(11)"));

        val jobHistoryTable = metadataExplorer.findByTableId(QualifiedTableId.builder()
                .schemaId(Id.of("public"))
                .tableId(Id.of("job_history"))
                .build());
        Assertions.assertThat(jobHistoryTable.getName()).isEqualTo(Id.of("job_history"));

        val jobHistoryEmployeeId = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("public"))
                .tableId(Id.of("job_history"))
                .columnId(Id.of("employee_id"))
                .build());
        Assertions.assertThat(jobHistoryEmployeeId.getType()).contains(Id.of("INT"));
        Assertions.assertThat(jobHistoryEmployeeId.getTypeOriginal()).contains(Id.of("int(11)"));

        val jobHistoryJobId = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("public"))
                .tableId(Id.of("job_history"))
                .columnId(Id.of("job_id"))
                .build());
        Assertions.assertThat(jobHistoryJobId.getType()).contains(Id.of("VARCHAR(10)"));
        Assertions.assertThat(jobHistoryJobId.getTypeOriginal()).contains(Id.of("varchar(10)"));

        val employeesTable = metadataExplorer.findByTableId(QualifiedTableId.builder()
                .schemaId(Id.of("public"))
                .tableId(Id.of("employees"))
                .build());
        Assertions.assertThat(employeesTable.getName()).isEqualTo(Id.of("employees"));

        val employeesFirstName = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("public"))
                .tableId(Id.of("employees"))
                .columnId(Id.of("first_name"))
                .build());
        Assertions.assertThat(employeesFirstName.getType()).contains(Id.of("VARCHAR(50)"));
        Assertions.assertThat(employeesFirstName.getTypeOriginal()).contains(Id.of("varchar(50)"));
    }

    //The issue had reported an error when uploading back to db
    @Test
    public void uploadSiardArchive_expectNoExceptions() throws SQLException, IOException {
        val siardArchive = siardArchivesHandler.prepareResource("mysql/issues/siardgui32/created-table-names-underscore.siard");

        SiardToDb siardToDb = new SiardToDb(new String[] {
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + db.getUsername(),
                "-p:" + db.getPassword(),
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardToDb.iRETURN_OK, siardToDb.getReturn());
    }
}
