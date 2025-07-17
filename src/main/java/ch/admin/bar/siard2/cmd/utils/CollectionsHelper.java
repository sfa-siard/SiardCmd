package ch.admin.bar.siard2.cmd.utils;

import lombok.val;

import java.util.*;

public class CollectionsHelper {
    public static <T> Set<T> setOf(final T... entries) {
        val set = new HashSet<>(Arrays.asList(entries));
        return Collections.unmodifiableSet(set);
    }

    public static <T> List<T> listOf(final T... entries) {
        return Collections.unmodifiableList(Arrays.asList(entries));
    }
}
