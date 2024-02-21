package ch.admin.bar.siard2.cmd.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class QualifiedTypeId {
    @NonNull String schema;
    @NonNull String type;
}
