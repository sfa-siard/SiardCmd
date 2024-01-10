package ch.admin.bar.siard2.cmd.utils.siard.assertions;

import ch.admin.bar.siard2.cmd.utils.StringUtils;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler.SiardArchiveExplorer;
import ch.admin.bar.siard2.cmd.utils.siard.model.content.Content;
import ch.admin.bar.siard2.cmd.utils.siard.model.header.Metadata;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.StringWrapper;
import ch.admin.bar.siard2.cmd.utils.siard.update.UpdateInstruction;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.val;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

    public static final UpdateInstruction<Metadata> IGNORE_DBNAME = UpdateInstruction.<Metadata>builder()
            .clazz(Metadata.class)
            .updater(metadata -> metadata.toBuilder()
                    .dbname(SiardArchiveAssertions.IGNORED_PLACEHOLDER)
                    .build())
            .description("Ignore the DB name")
            .build();

    public static final UpdateInstruction<Metadata.PrimaryKey> IGNORE_PRIMARY_KEY_NAME = UpdateInstruction.<Metadata.PrimaryKey>builder()
            .clazz(Metadata.PrimaryKey.class)
            .updater(primaryKey -> primaryKey.toBuilder()
                    .name(SiardArchiveAssertions.IGNORED_PLACEHOLDER)
                    .build())
            .description("Ignore all primary-key names")
            .build();

    public static final UpdateInstruction<Metadata.ForeignKey> IGNORE_FOREIGN_KEY_DELETE_ACTION = UpdateInstruction.<Metadata.ForeignKey>builder()
            .clazz(Metadata.ForeignKey.class)
            .updater(foreignKey -> foreignKey.toBuilder()
                    .deleteAction(Optional.empty())
                    .build())
            .description("Ignore all foreign-key delete-actions")
            .build();

    public static final UpdateInstruction<Metadata.ForeignKey> IGNORE_FOREIGN_KEY_UPDATE_ACTION = UpdateInstruction.<Metadata.ForeignKey>builder()
            .clazz(Metadata.ForeignKey.class)
            .updater(foreignKey -> foreignKey.toBuilder()
                    .updateAction(Optional.empty())
                    .build())
            .description("Ignore all foreign-key update-actions")
            .build();

    public static final UpdateInstruction<Metadata.Column> IGNORE_COLUMN_NULLABLE_FLAG = UpdateInstruction.<Metadata.Column>builder()
            .clazz(Metadata.Column.class)
            .updater(column -> column.toBuilder()
                    .nullable(Optional.empty())
                    .build())
            .description("Ignore the nullable flag of columns")
            .build();

    public static final UpdateInstruction<Content.TableCell> TRIM_TABLE_CELL_CONTENT = UpdateInstruction.<Content.TableCell>builder()
            .clazz(Content.TableCell.class)
            .updater(tableCell -> tableCell.toBuilder()
                    .value(StringUtils.trim(tableCell.getValue(), 50))
                    .build())
            .description("Trim the content of table cells to max. 50 chars")
            .build();

    @Builder(buildMethodName = "assertEqual")
    public SiardArchiveAssertions(
            @NonNull SiardArchiveExplorer expectedArchive,
            @NonNull SiardArchiveExplorer actualArchive,
            @Singular Set<UpdateInstruction<?>> updateInstructions

    ) {
        val updater = Updater.builder()
                .instructions(Optional.ofNullable(updateInstructions).orElse(new HashSet<>()))
                .build();

        expectedArchive.explore();

        val expected = expectedArchive
                .explore()
                .applyUpdates(updater);
        val actual = actualArchive
                .explore()
                .applyUpdates(updater);

        MetadataAssertions.builder()
                .expected(expected.getSiardMetadata())
                .actual(actual.getSiardMetadata())
                .assertEqual();

        ContentAssertions.builder()
                .expected(expected)
                .actual(actual)
                .assertEqual();
    }
}
