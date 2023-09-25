package ch.admin.bar.siard2.cmd.utils.siard;

import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class SiardArchiveExplorerTest {

    @Test
    public void test() {
        // given
        val siardArchive = TestResourcesResolver.loadResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_ORACLE18_2_2);
        val explorer = new SiardArchiveExplorer(siardArchive);

        // when
        val metadata = explorer.exploreMetadata();

        // then
        Assertions.assertThat(metadata).isNotNull();
    }

}