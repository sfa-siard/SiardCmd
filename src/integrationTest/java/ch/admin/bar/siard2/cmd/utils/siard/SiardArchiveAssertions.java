package ch.admin.bar.siard2.cmd.utils.siard;

import ch.admin.bar.siard2.cmd.utils.StringUtils;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler.SiardArchiveExplorer;
import ch.admin.bar.siard2.cmd.utils.siard.assertions.ContentAssertions;
import ch.admin.bar.siard2.cmd.utils.siard.model.Column;
import ch.admin.bar.siard2.cmd.utils.siard.model.ForeignKey;
import ch.admin.bar.siard2.cmd.utils.siard.model.PrimaryKey;
import ch.admin.bar.siard2.cmd.utils.siard.model.SiardMetadata;
import ch.admin.bar.siard2.cmd.utils.siard.model.StringWrapper;
import ch.admin.bar.siard2.cmd.utils.siard.model.content.TableCell;
import ch.admin.bar.siard2.cmd.utils.siard.model.content.TableContent;
import ch.admin.bar.siard2.cmd.utils.siard.update.UpdateInstruction;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import lombok.val;
import org.assertj.core.api.Assertions;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is used to test the equality of two SIARD archives. Currently, only a few nodes from the header/metadata.xml files
 * are compared. For further information, please refer to the {@link ch.admin.bar.siard2.cmd.utils.siard.model} package.
 * <p>
 * Individual fields can be excluded from the equality check using {@link UpdateInstruction}s (in fact, they are not excluded
 * but set to the same value through the {@link UpdateInstruction}).
 */

public class SiardArchiveAssertions {

    public static final StringWrapper IGNORED_PLACEHOLDER = StringWrapper.of("IGNORED");

    public static final UpdateInstruction<StringWrapper> CAPITALIZE_ALL_STRINGS = UpdateInstruction.<StringWrapper>builder()
            .clazz(StringWrapper.class)
            .updater(stringWrapper -> StringWrapper.of(stringWrapper.getValue().toUpperCase()))
            .description("Capitalize all strings")
            .build();

    public static final UpdateInstruction<SiardMetadata> IGNORE_DBNAME = UpdateInstruction.<SiardMetadata>builder()
            .clazz(SiardMetadata.class)
            .updater(metadata -> metadata.toBuilder()
                    .dbname(SiardArchiveAssertions.IGNORED_PLACEHOLDER)
                    .build())
            .description("Ignore the DB name")
            .build();

    public static final UpdateInstruction<PrimaryKey> IGNORE_PRIMARY_KEY_NAME = UpdateInstruction.<PrimaryKey>builder()
            .clazz(PrimaryKey.class)
            .updater(primaryKey -> primaryKey.toBuilder()
                    .name(SiardArchiveAssertions.IGNORED_PLACEHOLDER)
                    .build())
            .description("Ignore all primary-key names")
            .build();

    public static final UpdateInstruction<ForeignKey> IGNORE_FOREIGN_KEY_DELETE_ACTION = UpdateInstruction.<ForeignKey>builder()
            .clazz(ForeignKey.class)
            .updater(foreignKey -> foreignKey.toBuilder()
                    .deleteAction(Optional.empty())
                    .build())
            .description("Ignore all foreign-key delete-actions")
            .build();

    public static final UpdateInstruction<ForeignKey> IGNORE_FOREIGN_KEY_UPDATE_ACTION = UpdateInstruction.<ForeignKey>builder()
            .clazz(ForeignKey.class)
            .updater(foreignKey -> foreignKey.toBuilder()
                    .updateAction(Optional.empty())
                    .build())
            .description("Ignore all foreign-key update-actions")
            .build();

    public static final UpdateInstruction<Column> IGNORE_COLUMN_NULLABLE_FLAG = UpdateInstruction.<Column>builder()
            .clazz(Column.class)
            .updater(column -> column.toBuilder()
                    .nullable(Optional.empty())
                    .build())
            .description("Ignore the nullable flag of columns")
            .build();

    public static final UpdateInstruction<TableCell> TRIM_TABLE_CELL_CONTENT = UpdateInstruction.<TableCell>builder()
            .clazz(TableCell.class)
            .updater(tableCell -> tableCell.toBuilder()
                    .value(StringUtils.trim(tableCell.getValue(), 50))
                    .build())
            .description("Trim the content of table cells to max. 50 chars")
            .build();

    private final Set<UpdateInstruction<?>> updateInstructions;

    @Builder(buildMethodName = "assertEqual")
    public SiardArchiveAssertions(
            @NonNull SiardArchiveExplorer expectedArchive,
            @NonNull SiardArchiveExplorer actualArchive,
            @Singular Set<UpdateInstruction<?>> updateInstructions

    ) {
        this.updateInstructions = Optional.ofNullable(updateInstructions).orElse(new HashSet<>());

        val updater = Updater.builder()
                .instructions(this.updateInstructions)
                .build();

        expectedArchive.explore();

        val expected = expectedArchive
                .explore()
                .applyUpdates(updater);
        val actual = actualArchive
                .explore()
                .applyUpdates(updater);


        // columns
        Assertions
                .assertThat(extractQualifiedColumns(actual.getSiardMetadata()))
                .as("Columns of SIARD archives are not equal" + createAppliedUpdateInstructionsText())
                .isEqualTo(extractQualifiedColumns(expected.getSiardMetadata()));

        // primary keys
        Assertions
                .assertThat(extractQualifiedPrimaryKeys(actual.getSiardMetadata()))
                .as("Primary keys of SIARD archives are not equal" + createAppliedUpdateInstructionsText())
                .isEqualTo(extractQualifiedPrimaryKeys(expected.getSiardMetadata()));

        // foreign keys
        Assertions
                .assertThat(extractQualifiedForeignKeys(actual.getSiardMetadata()))
                .as("Foreign keys of SIARD archives are not equal" + createAppliedUpdateInstructionsText())
                .isEqualTo(extractQualifiedForeignKeys(expected.getSiardMetadata()));

        // content
        ContentAssertions.builder()
                .actual(actual)
                .expected(expected)
                .assertEqual();

        Assertions.assertThat(actual)
                .as("SIARD archives are not equal")
                .isEqualTo(expected);
    }

    private static List<Qualifier<Column>> extractQualifiedColumns(final SiardMetadata siardMetadata) {
        return siardMetadata.getSchemas().stream()
                .flatMap(schema -> schema.getTables().stream()
                        .flatMap(table -> table.getColumns().stream()
                                .map(column -> new Qualifier<>(
                                        String.format("%s.%s.%s",
                                                table.getName(),
                                                schema.getName(),
                                                column.getName()),
                                        column))))
                .sorted(Comparator.comparing(Qualifier::getQualifier))
                .collect(Collectors.toList());
    }

    private static List<Qualifier<PrimaryKey>> extractQualifiedPrimaryKeys(final SiardMetadata siardMetadata) {
        return siardMetadata.getSchemas().stream()
                .flatMap(schema -> schema.getTables().stream()
                        .flatMap(table -> stream(table.getPrimaryKey())
                                .map(primaryKey -> new Qualifier<>(
                                        String.format("%s.%s.%s->%s",
                                                table.getName(),
                                                schema.getName(),
                                                primaryKey.getName(),
                                                primaryKey.getColumn()),
                                        primaryKey))))
                .sorted(Comparator.comparing(Qualifier::getQualifier))
                .collect(Collectors.toList());
    }

    private static List<Qualifier<ForeignKey>> extractQualifiedForeignKeys(final SiardMetadata siardMetadata) {
        return siardMetadata.getSchemas().stream()
                .flatMap(schema -> schema.getTables().stream()
                        .flatMap(table -> table.getForeignKeys().stream()
                                .map(foreignKey -> new Qualifier<>(
                                        String.format("%s.%s.%s",
                                                table.getName(),
                                                schema.getName(),
                                                foreignKey.getName()),
                                        foreignKey
                                ))))
                .sorted(Comparator.comparing(Qualifier::getQualifier))
                .collect(Collectors.toList());
    }

    private String createAppliedUpdateInstructionsText() {
        return String.format(
                "\nApplied update instructions:\n%s\n",
                updateInstructions.stream()
                        .map(updateInstruction -> " - " + updateInstruction.getDescription())
                        .collect(Collectors.joining("\n")));
    }

    private static <T> Stream<T> stream(final Optional<T> optional) {
        return optional
                .map(Stream::of)
                .orElseGet(Stream::empty);
    }

    @Value
    private static class Qualifier<T> {
        String qualifier;
        T value;
    }
}
