package ch.admin.bar.siard2.cmd.utils.siard.model;

import ch.admin.bar.siard2.cmd.utils.siard.model.Column.ColumnId;
import ch.admin.bar.siard2.cmd.utils.siard.model.Schema.SchemaId;
import ch.admin.bar.siard2.cmd.utils.siard.model.Table.TableId;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Optional;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class ForeignKey {
    ForeignKeyId name;
    SchemaId referencedSchema;
    TableId referencedTable;
    Reference reference; // TODO Probably a embedded set?
    @Builder.Default
    Optional<String> deleteAction = Optional.empty();
    @Builder.Default
    Optional<String> updateAction = Optional.empty();

    public ForeignKey capitalizeValues() {
        return new ForeignKey(
                name.capitalizeValues(),
                referencedSchema.capitalizeValues(),
                referencedTable.capitalizeValues(),
                reference.capitalizeValues(),
                deleteAction.map(String::toUpperCase),
                updateAction.map(String::toUpperCase));
    }

    @Value(staticConstructor = "of")
    public static class ForeignKeyId {
        @JsonValue
        String value;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public ForeignKeyId(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public ForeignKeyId capitalizeValues() {
            return ForeignKeyId.of(value.toUpperCase());
        }
    }

    @Value
    @Builder
    @Jacksonized
    public static class Reference {
        ColumnId column;
        ColumnId referenced;

        public Reference capitalizeValues() {
            return new Reference(
                    column.capitalizeValues(),
                    referenced.capitalizeValues());
        }
    }
}
