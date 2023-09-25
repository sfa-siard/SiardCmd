package ch.admin.bar.siard2.cmd.utils.siard.update;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.function.Function;

@Value
@Builder
public class UpdateInstruction<T> {
    Class<T> clazz;

    @EqualsAndHashCode.Exclude
    Function<T, T> updater;
}