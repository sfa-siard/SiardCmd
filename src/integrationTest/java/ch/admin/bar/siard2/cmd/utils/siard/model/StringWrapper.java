package ch.admin.bar.siard2.cmd.utils.siard.model;

import ch.admin.bar.siard2.cmd.utils.siard.update.Updatable;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Value;
import lombok.val;

@Value(staticConstructor = "of")
public class StringWrapper implements Updatable<StringWrapper> {
    @JsonValue
    String value;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public StringWrapper(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public StringWrapper applyUpdates(Updater updater) {
       return updater.applyUpdate(this);

        return updatedThis;
    }
}
