package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.cmd.utils.CollectionsHelper;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MappingTest {

    @Test
    void disambiguates_identfifiers_for_a_given_max_length() {
        // given
        String[] identifiers = {"tab", "table_1", "table_2", "table_3"};

        int maxLength = 4;

        // when
        Map<String, String> mappedIdentifiers = Mapping.getDisambiguated(CollectionsHelper.listOf(identifiers), maxLength);

        // then
        assertEquals(4, mappedIdentifiers.size());
        assertEquals("tab", mappedIdentifiers.get("tab"));
        assertEquals("tab_", mappedIdentifiers.get("table_1")); // TODO: this seems off. It would be nice if it was tab_0
        assertEquals("ta_0", mappedIdentifiers.get("table_2"));
        assertEquals("ta_1", mappedIdentifiers.get("table_3"));
    }

    @Test
    void keeps_identifiers_if_max_length_is_zero() {
        // given
        String[] identifiers = {"tab", "table_1", "table_2", "table_3"};
        int maxLength = 0;

        // when
        Map<String, String> mappedIdentifiers = Mapping.getDisambiguated(CollectionsHelper.listOf(identifiers), maxLength);

        // then
        assertEquals(4, mappedIdentifiers.size());
        assertEquals("tab", mappedIdentifiers.get("tab"));
        assertEquals("table_1", mappedIdentifiers.get("table_1"));
        assertEquals("table_2", mappedIdentifiers.get("table_2"));
        assertEquals("table_3", mappedIdentifiers.get("table_3"));
    }
}