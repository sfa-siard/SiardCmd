package ch.admin.bar.siard2.cmd.utils.siard.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Column {
    ColumnId name;
    boolean nullable;

    public Column capitalizeValues() {
        return new Column(name.capitalizeValues(), nullable);
    }

    @Value(staticConstructor = "of")
    public static class ColumnId {
        @JsonValue
        String value;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public ColumnId(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public ColumnId capitalizeValues() {
            return ColumnId.of(value.toUpperCase());
        }
    }
}
