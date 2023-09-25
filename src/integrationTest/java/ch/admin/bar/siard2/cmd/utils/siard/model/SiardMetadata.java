package ch.admin.bar.siard2.cmd.utils.siard.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class SiardMetadata {
    String dbname;
    Set<Schema> schemas;

    public SiardMetadata capitalizeValues() {
        return new SiardMetadata(
                dbname.toUpperCase(),
                schemas.stream()
                        .map(Schema::capitalizeValues)
                        .collect(Collectors.toSet())
        );
    }
}
