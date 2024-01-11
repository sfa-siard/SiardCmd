package ch.admin.bar.siard2.cmd.utils.siard.assertions;

import ch.admin.bar.siard2.cmd.utils.CastHelper;
import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler.SiardArchiveExplorer;
import ch.admin.bar.siard2.cmd.utils.siard.model.header.Metadata;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.StringWrapper;
import ch.admin.bar.siard2.cmd.utils.siard.update.UpdateInstruction;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is used to test the equality of two SIARD archives. Currently, only a few nodes from the header/metadata.xml files
 * are compared. For further information, please refer to the {@link ch.admin.bar.siard2.cmd.utils.siard.model} package.
 * <p>
 * Individual fields can be excluded from the equality check using {@link UpdateInstruction}s (in fact, they are not excluded
 * but set to the same value through the {@link UpdateInstruction}).
 */
@Slf4j
public class SiardArchiveAssertions {

    public static final StringWrapper IGNORED_PLACEHOLDER = StringWrapper.of("IGNORED");

    public static final AssertionModifier IGNORE_METADATA = () -> "Ignore metadata of SIARD archives";

    public static final AssertionModifier IGNORE_CONTENT = () -> "Ignore content of SIARD archives";

    public static final AssertionModifier IGNORE_CASE_OF_STRINGS = UpdateInstructionAssertionModifier.builder()
            .description("Ignore the case of all strings")
            .updateInstruction(UpdateInstruction.<StringWrapper>builder()
                    .clazz(StringWrapper.class)
                    .updater(stringWrapper -> StringWrapper.of(stringWrapper.getValue().toUpperCase()))
                    .build())
            .build();

    public static final AssertionModifier IGNORE_DBNAME = UpdateInstructionAssertionModifier.builder()
            .description("Ignore the DB name")
            .updateInstruction(UpdateInstruction.<Metadata>builder()
                    .clazz(Metadata.class)
                    .updater(metadata -> metadata.toBuilder()
                            .dbname(SiardArchiveAssertions.IGNORED_PLACEHOLDER)
                            .build())
                    .build())
            .build();

    public static final AssertionModifier IGNORE_PRIMARY_KEY_NAME = UpdateInstructionAssertionModifier.builder()
            .description("Ignore all primary-key names")
            .updateInstruction(
                    UpdateInstruction.<Metadata.PrimaryKey>builder()
                            .clazz(Metadata.PrimaryKey.class)
                            .updater(primaryKey -> primaryKey.toBuilder()
                                    .name(SiardArchiveAssertions.IGNORED_PLACEHOLDER)
                                    .build())
                            .build())
            .build();

    public static final AssertionModifier IGNORE_FOREIGN_KEY_DELETE_ACTION = UpdateInstructionAssertionModifier.builder()
            .description("Ignore all foreign-key delete-actions")
            .updateInstruction(
                    UpdateInstruction.<Metadata.ForeignKey>builder()
                            .clazz(Metadata.ForeignKey.class)
                            .updater(foreignKey -> foreignKey.toBuilder()
                                    .deleteAction(Optional.empty())
                                    .build())

                            .build())
            .build();

    public static final AssertionModifier IGNORE_FOREIGN_KEY_UPDATE_ACTION = UpdateInstructionAssertionModifier.builder()
            .description("Ignore all foreign-key update-actions")
            .updateInstruction(
                    UpdateInstruction.<Metadata.ForeignKey>builder()
                            .clazz(Metadata.ForeignKey.class)
                            .updater(foreignKey -> foreignKey.toBuilder()
                                    .updateAction(Optional.empty())
                                    .build())
                            .build())
            .build();

    public static final AssertionModifier IGNORE_TABLE_DESCRIPTION = UpdateInstructionAssertionModifier.builder()
            .description("Ignore all foreign-key delete-actions")
            .updateInstruction(
                    UpdateInstruction.<Metadata.Table>builder()
                            .clazz(Metadata.Table.class)
                            .updater(table -> table.toBuilder()
                                    .description(Optional.of(IGNORED_PLACEHOLDER))
                                    .build())
                            .build())
            .build();

    @Builder(buildMethodName = "assertEqual")
    public SiardArchiveAssertions(
            @NonNull SiardArchiveExplorer expectedArchive,
            @NonNull SiardArchiveExplorer actualArchive,
            @Singular Set<AssertionModifier> assertionModifiers
    ) {
        val modifiers = Optional.ofNullable(assertionModifiers).orElse(new HashSet<>());

        if (!modifiers.isEmpty()) {
            log.warn("Assert the equality of two SIARD archives with the following modifiers:\n{}",
                    modifiers.stream()
                            .map(assertionModifier -> "\n - " + assertionModifier.getDescription())
                            .collect(Collectors.joining()));
        }

        val updater = Updater.builder()
                .instructions(modifiers.stream()
                        .flatMap(CastHelper.tryCast(UpdateInstructionAssertionModifier.class))
                        .map(UpdateInstructionAssertionModifier::getUpdateInstruction)
                        .collect(Collectors.toSet()))
                .build();

        val expected = expectedArchive
                .readArchive()
                .applyUpdates(updater);
        val actual = actualArchive
                .readArchive()
                .applyUpdates(updater);

        if (modifiers.stream()
                .noneMatch(assertionModifier -> assertionModifier == IGNORE_METADATA)) {
            MetadataAssertions.builder()
                    .expected(expected.getSiardMetadata())
                    .actual(actual.getSiardMetadata())
                    .assertEqual();
        }

        if (modifiers.stream()
                .noneMatch(assertionModifier -> assertionModifier == IGNORE_CONTENT)) {
            ContentAssertions.builder()
                    .expected(expected)
                    .actual(actual)
                    .assertEqual();
        }
    }

    public interface AssertionModifier {
        String getDescription();
    }

    @Value
    @Builder
    public static class UpdateInstructionAssertionModifier implements AssertionModifier {
        @NonNull
        UpdateInstruction<?> updateInstruction;

        @NonNull
        @Builder.Default
        String description = "unknown";
    }
}
