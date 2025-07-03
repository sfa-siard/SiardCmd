package ch.admin.bar.siard2.cmd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapping {

    /**
     * limitations on maximum length of identifiers leads to a need to
     * truncate and disambiguate identifiers in a set which need to be unique.
     *
     * @param identifiers list of original identifiers.
     * @param maxLength   maximum length.
     * @return mapping from original to mapped names.
     */
    protected static Map<String, String> getDisambiguated(List<String> identifiers, int maxLength) {
        Map<String, String> mappedIdentifiers = new HashMap<>();
        for (String identifier : identifiers) {
            String mappedName = identifier;
            if (maxLength > 0) {
                if (identifier.length() > maxLength) mappedName = identifier.substring(0, maxLength - 1) + "_";
                for (int i = 0; mappedIdentifiers.containsValue(mappedName); i++) {
                    String sSuffix = "_" + i;
                    mappedName = identifier.substring(0, maxLength - sSuffix.length()) + sSuffix;
                }
            }
            mappedIdentifiers.put(identifier, mappedName);
        }
        return mappedIdentifiers;
    }
}
