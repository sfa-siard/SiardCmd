package ch.admin.bar.siard2.cmd.utils.siard.update;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Builder
public class Updater {

    @NonNull
    @Singular
    private final Set<UpdateInstruction<?>> instructions;

    public <T> T applyUpdate(T original) {
        Function<T, T> overrider = (Function<T, T>) findUpdaterForType(original.getClass());
        return overrider.apply(original);
    }

    private <T> Function<T, T> findUpdaterForType(final Class<T> type) {
        final Optional<Function<T, T>> overriderOptional = instructions.stream()
                .filter(fieldOverrideInformation -> fieldOverrideInformation.getClazz().equals(type))
                .map(fieldOverrideInformation -> (Function<T, T>) fieldOverrideInformation.getUpdater())
                .findAny();

        final Function<T, T> overrideNothing = t -> t;
        return overriderOptional.orElse(overrideNothing);
    }
}
