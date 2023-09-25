package ch.admin.bar.siard2.cmd.utils.siard.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;
import java.util.stream.Collectors;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Schema {
    SchemaId name;
    Set<Table> tables;

    public Schema capitalizeValues() {
        return new Schema(
                name.capitalizeValues(),
                tables.stream()
                        .map(Table::capitalizeValues)
                        .collect(Collectors.toSet())
        );
    }

    @Value(staticConstructor = "of")
    public static class SchemaId {
        @JsonValue
        String value;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public SchemaId(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public SchemaId capitalizeValues() {
            return SchemaId.of(value.toUpperCase());
        }
    }
}
