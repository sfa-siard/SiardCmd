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
public class SchemaMetaData implements Updatable<SchemaMetaData> {
    Id<SchemaMetaData> name;
    Set<TableMetaData> tables;
    FolderId folder;

    @Override
    public SchemaMetaData applyUpdates(Updater updater) {
        val updatedThis = updater.applyUpdate(this);

        return new SchemaMetaData(
                updatedThis.name.applyUpdates(updater),
                updatedThis.tables.stream()
                        .map(table -> table.applyUpdates(updater))
                        .collect(Collectors.toSet()),
                updatedThis.folder.applyUpdates(updater));
    }
}
