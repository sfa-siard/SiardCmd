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
public class PrimaryKey implements Updatable<PrimaryKey> {
    StringWrapper name;
    Id<Column> column;

    @Override
    public PrimaryKey applyUpdates(Updater updater) {
        val updatedThis = updater.applyUpdate(this);

        return new PrimaryKey(
                updatedThis.name.applyUpdates(updater),
                updatedThis.column.applyUpdates(updater));
    }
}
