package ch.admin.bar.siard2.cmd.utils.siard.update;

public interface Updatable<T> {
    T applyUpdates(final Updater updater);
}
