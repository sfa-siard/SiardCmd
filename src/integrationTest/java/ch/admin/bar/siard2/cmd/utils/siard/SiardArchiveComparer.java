package ch.admin.bar.siard2.cmd.utils.siard;

import ch.admin.bar.siard2.cmd.utils.siard.model.PrimaryKey;
import ch.admin.bar.siard2.cmd.utils.siard.model.SiardMetadata;
import ch.admin.bar.siard2.cmd.utils.siard.model.StringWrapper;
import ch.admin.bar.siard2.cmd.utils.siard.update.UpdateInstruction;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import lombok.Builder;
import lombok.Singular;
import lombok.val;
import org.assertj.core.api.Assertions;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


public class SiardArchiveComparer {

    public static final StringWrapper IGNORED_PLACEHOLDER = StringWrapper.of("IGNORED");

    public static final UpdateInstruction<StringWrapper> CAPITALIZE_ALL_STRINGS = UpdateInstruction.<StringWrapper>builder()
            .clazz(StringWrapper.class)
            .updater(stringWrapper -> StringWrapper.of(stringWrapper.getValue().toUpperCase()))
            .build();

    public static final UpdateInstruction<SiardMetadata> IGNORE_DBNAME = UpdateInstruction.<SiardMetadata>builder()
            .clazz(SiardMetadata.class)
            .updater(metadata -> metadata.toBuilder()
                    .dbname(SiardArchiveComparer.IGNORED_PLACEHOLDER)
                    .build())
            .build();

    public static final UpdateInstruction<PrimaryKey> IGNORE_PRIMARY_KEY_NAME = UpdateInstruction.<PrimaryKey>builder()
            .clazz(PrimaryKey.class)
            .updater(primaryKey -> primaryKey.toBuilder()
                    .name(SiardArchiveComparer.IGNORED_PLACEHOLDER)
                    .build())
            .build();

    private final File pathToExpectedArchive;
    private final File pathToActualArchive;

    private final Updater updater;

    @Builder(buildMethodName = "compare")
    public SiardArchiveComparer(
            File pathToExpectedArchive,
            File pathToActualArchive,
            @Singular Set<UpdateInstruction<?>> updateInstructions

    ) {
        this.pathToExpectedArchive = pathToExpectedArchive;
        this.pathToActualArchive = pathToActualArchive;
        this.updater = Updater.builder()
                .instructions(Optional.ofNullable(updateInstructions).orElse(new HashSet<>()))
                .build();

        val expected = new SiardArchiveExplorer(pathToExpectedArchive);
        val actual = new SiardArchiveExplorer(pathToActualArchive);

        val expectedMetadata = expected.exploreMetadata().applyUpdates(updater);
        val actualMetadata = actual.exploreMetadata().applyUpdates(updater);

        Assertions.assertThat(actualMetadata).isEqualTo(expectedMetadata);
    }
}
