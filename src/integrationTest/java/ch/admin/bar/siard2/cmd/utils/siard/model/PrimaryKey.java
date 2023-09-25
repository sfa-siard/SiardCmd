package ch.admin.bar.siard2.cmd.utils.siard.model;

import ch.admin.bar.siard2.cmd.utils.siard.model.Column.ColumnId;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class PrimaryKey {
    String name;
    ColumnId column;

    public PrimaryKey capitalizeValues() {
        return new PrimaryKey(
                name.toUpperCase(),
                column.capitalizeValues()
        );
    }
}
