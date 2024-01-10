package ch.admin.bar.siard2.cmd.utils.siard.model.content;

import ch.admin.bar.siard2.cmd.utils.siard.update.Updatable;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class TableCell implements Updatable<TableCell> {
    int columnNumber;
    String value;

    @Override
    public TableCell applyUpdates(Updater updater) {
        return updater.applyUpdate(this);
    }
}
