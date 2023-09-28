package ch.admin.bar.siard2.cmd.utils.siard;

import ch.admin.bar.siard2.cmd.utils.siard.SiardArchivesHandler.SiardArchiveExplorer;
import ch.admin.bar.siard2.cmd.utils.siard.model.ForeignKey;
import ch.admin.bar.siard2.cmd.utils.siard.model.PrimaryKey;
import ch.admin.bar.siard2.cmd.utils.siard.model.SiardMetadata;
import ch.admin.bar.siard2.cmd.utils.siard.model.StringWrapper;
import ch.admin.bar.siard2.cmd.utils.siard.update.UpdateInstruction;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.val;
import org.assertj.core.api.Assertions;

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

    @Builder(buildMethodName = "assertEqual")
    public SiardArchiveAssertions(
            @NonNull SiardArchiveExplorer expectedArchive,
            @NonNull SiardArchiveExplorer actualArchive,
            @Singular Set<UpdateInstruction<?>> updateInstructions

    ) {
        val nonNullableUpdateInstructions = Optional.ofNullable(updateInstructions).orElse(new HashSet<>());

        val updater = Updater.builder()
                .instructions(nonNullableUpdateInstructions)
                .build();

        val expectedMetadata = expectedArchive
                .exploreMetadata()
                .applyUpdates(updater);
        val actualMetadata = actualArchive
                .exploreMetadata()
                .applyUpdates(updater);

        Assertions.assertThat(actualMetadata)
                .as(String.format(
                        "SIARD archives are not equal. Applied update instructions:\n%s\n",
                        nonNullableUpdateInstructions.stream()
                                .map(updateInstruction -> " - " + updateInstruction.getDescription())
                                .collect(Collectors.joining("\n"))))
                .isEqualTo(expectedMetadata);
    }
}
