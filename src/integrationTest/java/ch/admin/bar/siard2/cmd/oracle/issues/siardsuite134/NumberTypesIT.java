package ch.admin.bar.siard2.cmd.oracle.issues.siardsuite134;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.utils.ConsoleLogConsumer;
import ch.admin.bar.siard2.cmd.utils.SqlScripts;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.sql.SQLException;

public class NumberTypesIT {

    @Rule
    public SiardArchivesHandler siardArchivesHandler = new SiardArchivesHandler();

    @Rule
    public OracleContainer db = new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
            .withLogConsumer(new ConsoleLogConsumer())
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.CREATE_USER_WITH_ALL_PRIVILEGES).toPath()),
                    "/container-entrypoint-initdb.d/00_create_user.sql")
            .withCopyFileToContainer(
                    MountableFile.forHostPath(TestResourcesResolver.resolve(SqlScripts.Oracle.SIARDSUITE_134).toPath()),
                    "/container-entrypoint-initdb.d/01_number_types.sql");

    @Test
    public void downloadArchive() throws SQLException, IOException, ClassNotFoundException {
        val siardArchive = siardArchivesHandler.prepareEmpty();

        SiardFromDb dbtoSiard = new SiardFromDb(new String[]{
                "-o",
                "-j:" + db.getJdbcUrl(),
                "-u:" + "IT_USER",
                "-p:" + "password",
                "-s:" + siardArchive.getPathToArchiveFile()
        });

        Assert.assertEquals(SiardFromDb.iRETURN_OK, dbtoSiard.getReturn());

        val metadataExplorer = siardArchive.exploreMetadata();

        // NUMBER without precision/scale: usually FLOAT(38), should be BIGINT for BAZG Quickfix
        val columnId = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("SAMPLE_TABLE"))
                .columnId(Id.of("ID"))
                .build());
        Assertions.assertThat(columnId.getType()).contains(Id.of("FLOAT(38)"));
        Assertions.assertThat(columnId.getTypeOriginal()).contains(Id.of("NUMBER"));

        // NUMBER without precision/scale: usually FLOAT(38), should be BIGINT for BAZG Quickfix
        val columnAge = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("SAMPLE_TABLE"))
                .columnId(Id.of("AGE"))
                .build());
        Assertions.assertThat(columnAge.getType()).contains(Id.of("FLOAT(38)"));
        Assertions.assertThat(columnAge.getTypeOriginal()).contains(Id.of("NUMBER"));

        val columnHeightCm = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("SAMPLE_TABLE"))
                .columnId(Id.of("HEIGHT_IN_CM"))
                .build());
        Assertions.assertThat(columnHeightCm.getType()).contains(Id.of("SMALLINT"));
        Assertions.assertThat(columnHeightCm.getTypeOriginal()).contains(Id.of("NUMBER(3,0)"));

        val columnHeightM = metadataExplorer.findByColumnId(QualifiedColumnId.builder()
                .schemaId(Id.of("IT_USER"))
                .tableId(Id.of("SAMPLE_TABLE"))
                .columnId(Id.of("HEIGHT_IN_M"))
                .build());
        Assertions.assertThat(columnHeightM.getType()).contains(Id.of("DEC(3, 2)"));
        Assertions.assertThat(columnHeightM.getTypeOriginal()).contains(Id.of("NUMBER(3,2)"));
    }
}
