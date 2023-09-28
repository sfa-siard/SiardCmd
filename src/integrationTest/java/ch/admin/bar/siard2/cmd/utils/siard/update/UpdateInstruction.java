package ch.admin.bar.siard2.cmd.utils.siard.update;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.function.Function;

@Value
@Builder
public class UpdateInstruction<T> {
    @NonNull Class<T> clazz;
    @NonNull Function<T, T> updater;

    @NonNull
    @Builder.Default
    String description = "unknown";
}