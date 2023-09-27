package ch.admin.bar.siard2.cmd.utils.siard.model;

import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updatable;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import lombok.val;

import java.util.Set;
import java.util.stream.Collectors;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class SiardMetadata implements Updatable<SiardMetadata> {
    StringWrapper dbname;
    Set<Schema> schemas;

    @Override
    public SiardMetadata applyUpdates(Updater updater) {
        val updatedThis = updater.applyUpdate(this);

        return new SiardMetadata(
                updatedThis.dbname.applyUpdates(updater),
                updatedThis.schemas.stream()
                        .map(schema -> schema.applyUpdates(updater))
                        .collect(Collectors.toSet()));
    }
}
