package ch.admin.bar.siard2.cmd.utils.siard.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Table {
    TableId name;
    @Builder.Default Set<Column> columns = new HashSet<>();
    PrimaryKey primaryKey;
    @Builder.Default Set<ForeignKey> foreignKeys = new HashSet<>();

    public Table capitalizeValues() {
        return new Table(
                name.capitalizeValues(),
                columns.stream()
                        .map(Column::capitalizeValues)
                        .collect(Collectors.toSet()),
                primaryKey.capitalizeValues(),
                foreignKeys.stream()
                        .map(ForeignKey::capitalizeValues)
                        .collect(Collectors.toSet())
        );
    }

    @Value(staticConstructor = "of")
    public static class TableId {
        @JsonValue
        String value;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public TableId(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public TableId capitalizeValues() {
            return TableId.of(value.toUpperCase());
        }
    }
}
