package ch.admin.bar.siard2.cmd.utils.siard.model;

import ch.admin.bar.siard2.cmd.utils.siard.update.Updatable;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import lombok.val;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class TableMetaData implements Updatable<TableMetaData> {
    Id<TableMetaData> name;
    @Builder.Default
    Set<Column> columns = new HashSet<>();

    @NonNull
    @Builder.Default
    Optional<PrimaryKey> primaryKey = Optional.empty();

    @Builder.Default
    Set<ForeignKey> foreignKeys = new HashSet<>();

    FolderId folder;

    @Override
    public TableMetaData applyUpdates(Updater updater) {
        val updatedThis = updater.applyUpdate(this);

        return new TableMetaData(
                updatedThis.name.applyUpdates(updater),
                updatedThis.columns.stream()
                        .map(column -> column.applyUpdates(updater))
                        .collect(Collectors.toSet()),
                updatedThis.primaryKey.map(primaryKey -> primaryKey.applyUpdates(updater)),
                updatedThis.foreignKeys.stream()
                        .map(foreignKey -> foreignKey.applyUpdates(updater))
                        .collect(Collectors.toSet()),
                updatedThis.folder.applyUpdates(updater));
    }
}
