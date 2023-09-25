package ch.admin.bar.siard2.cmd.utils.siard.model;

import ch.admin.bar.siard2.cmd.utils.siard.update.Updatable;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import lombok.val;

import java.util.Set;
import java.util.stream.Collectors;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Schema implements Updatable<Schema> {
    Id<Schema> name;
    Set<Table> tables;

    @Override
    public Schema applyUpdates(Updater updater) {
        val updatedThis = updater.applyUpdate(this);

        return new Schema(
                updatedThis.name.applyUpdates(updater),
                tables.stream()
                        .map(table -> table.applyUpdates(updater))
                        .collect(Collectors.toSet()));
    }
}
