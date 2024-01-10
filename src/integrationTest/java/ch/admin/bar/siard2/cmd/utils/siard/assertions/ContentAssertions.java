package ch.admin.bar.siard2.cmd.utils.siard.assertions;

import ch.admin.bar.siard2.cmd.utils.SetDeltas;
import ch.admin.bar.siard2.cmd.utils.siard.model.SiardArchive;
import ch.admin.bar.siard2.cmd.utils.siard.utils.ContentExplorer;
import lombok.Builder;
import lombok.NonNull;
import lombok.val;
import org.assertj.core.api.Assertions;

public class ContentAssertions {
    @Builder(buildMethodName = "assertEqual")
    public ContentAssertions(
            @NonNull SiardArchive expected,
            @NonNull SiardArchive actual
    ) {
        val expectedContentExplorer = new ContentExplorer(expected);
        val actualContentExplorer = new ContentExplorer(actual);

        val tableIdsDeltas = SetDeltas.findDeltas(expectedContentExplorer.getAllIds(), actualContentExplorer.getAllIds());

        Assertions.assertThat(tableIdsDeltas.deltasAvailable())
                .as("Not the same table ids available: " + tableIdsDeltas)
                .isFalse();

        expectedContentExplorer.getAllIds().forEach(tableId -> {
            val expectedTable = expectedContentExplorer.findTable(tableId);
            val actualTable = actualContentExplorer.findTable(tableId);

            Assertions.assertThat(actualTable)
                    .as("The table %s is not equal", tableId)
                    .isEqualTo(expectedTable);
        });
    }
}
