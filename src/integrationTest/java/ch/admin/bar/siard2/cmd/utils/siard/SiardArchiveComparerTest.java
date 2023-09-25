package ch.admin.bar.siard2.cmd.utils.siard;

import ch.admin.bar.siard2.cmd.utils.SiardProjectExamples;
import ch.admin.bar.siard2.cmd.utils.TestResourcesResolver;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.junit.Assert.*;

public class SiardArchiveComparerTest {

    @Test
    public void test() {
        // given
        val siardArchive = TestResourcesResolver.loadResource(SiardProjectExamples.SIMPLE_TEAMS_EXAMPLE_ORACLE18_2_2);

        // when
        val comparer = SiardArchiveComparer.builder()
                .pathToExpectedArchive(siardArchive)
                .pathToActualArchive(siardArchive)
                .build();

        // then
        comparer.compare();
    }

}