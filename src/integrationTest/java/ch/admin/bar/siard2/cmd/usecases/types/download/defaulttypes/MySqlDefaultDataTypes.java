package ch.admin.bar.siard2.cmd.usecases.types.download.defaulttypes;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedTableId;
import lombok.SneakyThrows;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Assert;

public class MySqlDefaultDataTypes {

    public final static String INIT_SCRIPT = "usecases/types/use-all-default-data-types_mysql8.sql";

    private static final QualifiedTableId TABLE = QualifiedTableId.builder()
            .schemaId(Id.of("Schema1"))
            .tableId(Id.of("ExampleDataTable"))
            .build();

    // string types
    public final static QualifiedColumnId COLUMN_CHAR = TABLE.createQualifiedColumnId(Id.of("char_column"));
    public final static QualifiedColumnId COLUMN_VARCHAR = TABLE.createQualifiedColumnId(Id.of("varchar_column"));
    public final static QualifiedColumnId COLUMN_TINYTEXT = TABLE.createQualifiedColumnId(Id.of("tinytext_column"));
    public final static QualifiedColumnId COLUMN_TEXT = TABLE.createQualifiedColumnId(Id.of("text_column"));
    public final static QualifiedColumnId COLUMN_MEDIUMTEXT = TABLE.createQualifiedColumnId(Id.of("mediumtext_column"));
    public final static QualifiedColumnId COLUMN_LONGTEXT = TABLE.createQualifiedColumnId(Id.of("longtext_column"));
    public final static QualifiedColumnId COLUMN_ENUM = TABLE.createQualifiedColumnId(Id.of("enum_column"));
    public final static QualifiedColumnId COLUMN_SET = TABLE.createQualifiedColumnId(Id.of("set_column"));
    public final static QualifiedColumnId COLUMN_BINARY = TABLE.createQualifiedColumnId(Id.of("binary_column"));
    public final static QualifiedColumnId COLUMN_VARBINARY = TABLE.createQualifiedColumnId(Id.of("varbinary_column"));
    public final static QualifiedColumnId COLUMN_JSON = TABLE.createQualifiedColumnId(Id.of("json_column"));

    // numeric types
    public final static QualifiedColumnId COLUMN_BIT = TABLE.createQualifiedColumnId(Id.of("bit_column"));
    public final static QualifiedColumnId COLUMN_TINYINT = TABLE.createQualifiedColumnId(Id.of("tinyint_column"));
    public final static QualifiedColumnId COLUMN_SMALLINT = TABLE.createQualifiedColumnId(Id.of("smallint_column"));
    public final static QualifiedColumnId COLUMN_MEDIUMINT = TABLE.createQualifiedColumnId(Id.of("mediumint_column"));
    public final static QualifiedColumnId COLUMN_INT = TABLE.createQualifiedColumnId(Id.of("int_column"));
    public final static QualifiedColumnId COLUMN_BIGINT = TABLE.createQualifiedColumnId(Id.of("bigint_column"));
    public final static QualifiedColumnId COLUMN_DECIMAL = TABLE.createQualifiedColumnId(Id.of("decimal_column"));
    public final static QualifiedColumnId COLUMN_FLOAT = TABLE.createQualifiedColumnId(Id.of("float_column"));
    public final static QualifiedColumnId COLUMN_DOUBLE = TABLE.createQualifiedColumnId(Id.of("double_column"));
    public final static QualifiedColumnId COLUMN_BOOLEAN = TABLE.createQualifiedColumnId(Id.of("boolean_column"));

    // date/time types
    public final static QualifiedColumnId COLUMN_DATE = TABLE.createQualifiedColumnId(Id.of("date_column"));
    public final static QualifiedColumnId COLUMN_DATETIME = TABLE.createQualifiedColumnId(Id.of("datetime_column"));
    public final static QualifiedColumnId COLUMN_TIMESTAMP = TABLE.createQualifiedColumnId(Id.of("timestamp_column"));
    public final static QualifiedColumnId COLUMN_TIME = TABLE.createQualifiedColumnId(Id.of("time_column"));
    public final static QualifiedColumnId COLUMN_YEAR = TABLE.createQualifiedColumnId(Id.of("year_column"));

    // LOB types
    public final static QualifiedColumnId COLUMN_TINYBLOB = TABLE.createQualifiedColumnId(Id.of("date_column"));
    public final static QualifiedColumnId COLUMN_BLOB = TABLE.createQualifiedColumnId(Id.of("datetime_column"));
    public final static QualifiedColumnId COLUMN_MEDIUMBLOB = TABLE.createQualifiedColumnId(Id.of("timestamp_column"));
    public final static QualifiedColumnId COLUMN_LONGBLOB = TABLE.createQualifiedColumnId(Id.of("time_column"));

    @SneakyThrows
    public static void executeTest(SiardArchivesHandler siardArchivesHandler, String jdbcUrl) {
        // given
        val actualArchive = siardArchivesHandler.prepareEmpty();

        // when
        SiardFromDb siardFromDb = new SiardFromDb(new String[]{
                "-o",
                "-j:" + jdbcUrl,
                "-u:" + "it_user",
                "-p:" + "it_password",
                "-s:" + actualArchive.getPathToArchiveFile()
        });

        // then
        Assert.assertEquals(SiardFromDb.iRETURN_OK, siardFromDb.getReturn());

        actualArchive.preserveArchive();

        val metadataExplorer = actualArchive.exploreMetadata();

        // string types
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_CHAR)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_VARCHAR)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_TINYTEXT)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_TEXT)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_MEDIUMTEXT)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_LONGTEXT)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_ENUM)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_SET)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_BINARY)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_VARBINARY)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_JSON)).isPresent();

        // numeric types
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_BIT)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_TINYINT)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_SMALLINT)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_MEDIUMINT)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_INT)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_BIGINT )).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_DECIMAL)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_FLOAT )).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_DOUBLE)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_BOOLEAN )).isPresent();

        // date/time types
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_DATE)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_DATETIME )).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_TIMESTAMP)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_TIME)).isPresent();
        Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_YEAR)).isPresent();

        // LOB types
       Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_TINYBLOB)).isPresent();
       Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_BLOB)).isPresent();
       Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_MEDIUMBLOB)).isPresent();
       Assertions.assertThat(metadataExplorer.tryFindByColumnId(COLUMN_LONGBLOB)).isPresent();

        val contentExplorer = actualArchive.exploreContent();

        Assertions.assertThat(contentExplorer.findCellValue(COLUMN_DATE, 0)).isEqualTo("2022-01-01");
        // TODO test the rest of the rows
    }
}
