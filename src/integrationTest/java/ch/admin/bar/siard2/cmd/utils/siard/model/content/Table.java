package ch.admin.bar.siard2.cmd.utils.siard.model.content;

import ch.admin.bar.siard2.cmd.utils.siard.model.FolderId;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updatable;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import lombok.Builder;
import lombok.Value;
import lombok.val;

@Value
@Builder
public class Table implements Updatable<Table> {
    FolderId schemaFolder;
    FolderId tableFolder;
    TableContent tableContent;

    @Override
    public Table applyUpdates(Updater updater) {
        val updatedThis = updater.applyUpdate(this);

        return new Table(
                updatedThis.schemaFolder.applyUpdates(updater),
                updatedThis.tableFolder.applyUpdates(updater),
                updatedThis.tableContent.applyUpdates(updater));
    }
}
