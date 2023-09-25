package ch.admin.bar.siard2.cmd.utils.siard;

import ch.admin.bar.siard2.cmd.utils.siard.model.SiardMetadata;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.assertj.core.api.Assertions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Builder
public class SiardArchiveComparer {

    public static final String IGNORED_PLACEHOLDER = "IGNORED";

    private final File pathToExpectedArchive;
    private final File pathToActualArchive;

    private final Collection<FieldOverrideInformation<?>> fieldOverrideInformations = new ArrayList<>();

    public <T> SiardArchiveComparer overrideField(Class<T> clazz, Function<T, T> overrider) {
        fieldOverrideInformations.add(FieldOverrideInformation.<T>builder()
                .clazz(clazz)
                .overrider(overrider)
                .build());
        return this;
    }

    @SneakyThrows
    public void compare() {
        val expected = new SiardArchiveExplorer(pathToExpectedArchive);
        val actual = new SiardArchiveExplorer(pathToActualArchive);

        val expectedMetadata = expected.exploreMetadata().capitalizeValues();
        val actualMetadata = actual.exploreMetadata().capitalizeValues();

        Assertions.assertThat(actualMetadata).isEqualTo(expectedMetadata);
    }

    @Value
    @Builder
    private static class FieldOverrideInformation<T> {
        Class<T> clazz;
        Function<T, T> overrider;
    }
}
