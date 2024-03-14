package ch.admin.bar.siard2.cmd.usecases.types.download.defaulttypes;

import ch.admin.bar.siard2.cmd.SiardFromDb;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedColumnId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedTableId;
import ch.admin.bar.siard2.cmd.utils.siard.utils.ContentExplorer;
import ch.admin.bar.siard2.cmd.utils.siard.utils.MetadataExplorer;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.CanIgnoreReturnValue;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class MySqlDefaultDataTypes {

    /**
     * Creates one table which contains all supported data types (one column for each type)
     */
    public final static String INIT_SCRIPT = "usecases/types/default/use-all-default-data-types_mysql8.sql";

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
    public final static QualifiedColumnId COLUMN_TINYBLOB = TABLE.createQualifiedColumnId(Id.of("tinyblob_column"));
    public final static QualifiedColumnId COLUMN_BLOB = TABLE.createQualifiedColumnId(Id.of("blob_column"));
    public final static QualifiedColumnId COLUMN_MEDIUMBLOB = TABLE.createQualifiedColumnId(Id.of("mediumblob_column"));
    public final static QualifiedColumnId COLUMN_LONGBLOB = TABLE.createQualifiedColumnId(Id.of("longblob_column"));

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

        val assertionsHelper = new AssertionsHelper(
                actualArchive.exploreMetadata(),
                actualArchive.exploreContent()
        );

        // string types
        assertionsHelper.testThat(COLUMN_CHAR).containsExactly("abc");

        assertionsHelper.testThat(COLUMN_CHAR).containsExactly("abc");
        assertionsHelper.testThat(COLUMN_VARCHAR).containsExactly("varchar example");
        assertionsHelper.testThat(COLUMN_TINYTEXT).containsExactly("tinytext example");

        // TODO Content of blob's (TEXT, MEDIUMTEXT, LONGTEXT are handled as blob) can currently not be tested trough testing framework
//        assertionsHelper.testThat(COLUMN_TEXT).containsExactly("text example");
//        assertionsHelper.testThat(COLUMN_MEDIUMTEXT).containsExactly("mediumtext example");
//        assertionsHelper.testThat(COLUMN_LONGTEXT).containsExactly("longtext example");

        assertionsHelper.testThat(COLUMN_ENUM).containsExactly("value1");
        assertionsHelper.testThat(COLUMN_SET).containsExactly("option1,option2");
        assertionsHelper.testThat(COLUMN_BINARY).containsExactly("binary");
        assertionsHelper.testThat(COLUMN_VARBINARY).containsExactly("varbinary");

        // numeric types
        assertionsHelper.testThat(COLUMN_BIT).containsExactly("1");
        assertionsHelper.testThat(COLUMN_TINYINT).containsExactly("42");
        assertionsHelper.testThat(COLUMN_SMALLINT).containsExactly("32767");
        assertionsHelper.testThat(COLUMN_MEDIUMINT).containsExactly("8388607");
        assertionsHelper.testThat(COLUMN_INT).containsExactly("2147483647");
        assertionsHelper.testThat(COLUMN_BIGINT).containsExactly("9223372036854775807");
        assertionsHelper.testThat(COLUMN_DECIMAL).containsExactly("123.45");
        assertionsHelper.testThat(COLUMN_FLOAT ).containsExactly("123.45");
        assertionsHelper.testThat(COLUMN_DOUBLE).containsExactly("123.45");
        assertionsHelper.testThat(COLUMN_BOOLEAN).containsExactly("true");

        // date/time types
        assertionsHelper.testThat(COLUMN_DATE).containsExactly("2022-01-01");
        assertionsHelper.testThat(COLUMN_DATETIME).containsExactly("2022-01-01T12:34:56Z");
        assertionsHelper.testThat(COLUMN_TIMESTAMP).containsExactly("2022-01-01T12:34:56Z");
        assertionsHelper.testThat(COLUMN_TIME).containsExactly("12:34:56Z");
        assertionsHelper.testThat(COLUMN_YEAR).containsExactly("2022");

        // LOB types
       assertionsHelper.testThat(COLUMN_TINYBLOB).containsExactly("tinyblob data");
       // TODO Content of blob's can currently not be tested trough testing framework
//       assertionsHelper.testThat(COLUMN_BLOB).containsExactly("blob data");
//       assertionsHelper.testThat(COLUMN_MEDIUMBLOB).containsExactly("mediumblob data");
//       assertionsHelper.testThat(COLUMN_LONGBLOB).containsExactly("longblob data");

       // assertionsHelper.assertAllTestsSuccessful(); FIXME
    }

    @RequiredArgsConstructor
    private static class AssertionsHelper {
        private final MetadataExplorer metadataExplorer;
        private final ContentExplorer contentExplorer;

        private final List<ColumValueMissmatch> valueMissmatches = new ArrayList<>();
        public ColumWarnings testThat(final QualifiedColumnId qualifiedColumnId) {
            return expected -> {
                Assertions.assertThat(metadataExplorer.tryFindByColumnId(qualifiedColumnId))
                        .isPresent();

                val actual = contentExplorer.findCellValue(qualifiedColumnId, 0);

                if (!expected.equals(actual)) {
                    log.warn("Column '{}' contains value '{}' instead of '{}'", qualifiedColumnId, actual, expected);

                    valueMissmatches.add(ColumValueMissmatch.builder()
                            .columnId(qualifiedColumnId)
                            .expected(expected)
                            .actual(actual)
                            .build());
                }
            };
        }

        public void assertAllTestsSuccessful() {
            Assertions.assertThat(valueMissmatches)
                    .as("Expecting successful tests only")
                    .hasSize(0);
        }
    }

    @Value
    @Builder
    private static class ColumValueMissmatch {
        QualifiedColumnId columnId;
        String expected;
        String actual;
    }
    private interface ColumWarnings {
        void containsExactly(String expected);
    }
}
