package ch.admin.bar.siard2.cmd.utils;

import lombok.Value;

import java.util.HashSet;
import java.util.Set;

@Value
public class SetDeltas<T> {
    Set<T> inBoth;
    Set<T> justInA;
    Set<T> justInB;

    public boolean deltasAvailable() {
        return !justInA.isEmpty() || !justInB.isEmpty();
    }

    public static <T> SetDeltas<T> findDeltas(final Set<T> a, final Set<T> b) {
        final Set<T> inBoth = new HashSet<>();
        final Set<T> justInA = new HashSet<>();
        final Set<T> justInB = new HashSet<>();

        a.forEach(entry -> {
            if (b.contains(entry)) {
                inBoth.add(entry);
            } else {
                justInA.add(entry);
            }
        });

        b.forEach(entry -> {
            if (!a.contains(entry)) {
                justInB.add(entry);
            }
        });

        return new SetDeltas<>(inBoth, justInA, justInB);
    }
}
