package ch.admin.bar.siard2.cmd.utils.siard.model;

import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updatable;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import lombok.val;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Column implements Updatable<Column> {
    Id<Column> name;
    boolean nullable;

    @Override
    public Column applyUpdates(Updater updater) {
        val updatedThis = updater.applyUpdate(this);

        return new Column(
                updatedThis.name.applyUpdates(updater),
                updatedThis.nullable
        );
    }
}
