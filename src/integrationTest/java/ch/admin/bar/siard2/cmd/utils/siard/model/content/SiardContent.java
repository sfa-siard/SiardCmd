package ch.admin.bar.siard2.cmd.utils.siard.model.content;

import ch.admin.bar.siard2.cmd.utils.siard.model.FolderId;
import ch.admin.bar.siard2.cmd.utils.siard.model.SiardArchive;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updatable;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

@Value
public class SiardContent implements Updatable<SiardContent> {

    @NonNull List<Table> tables;

    @Override
    public SiardContent applyUpdates(Updater updater) {
        val updatedThis = updater.applyUpdate(this);

        return new SiardContent(
                updatedThis.tables.stream()
                        .map(table -> table.applyUpdates(updater))
                        .collect(Collectors.toList()));
    }

    public Table findTable(final FolderId schemaFolder, final FolderId tableFolder) {
        return tables.stream()
                .filter(table -> table.getSchemaFolder().equals(schemaFolder) && table.getTableFolder().equals(tableFolder))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("No table in folders %s.%s found", schemaFolder.getValue(), tableFolder.getValue())));
    }
}
