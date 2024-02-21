package ch.admin.bar.siard2.cmd.db.features;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DbFeatures {
    int maxColumnNameLength;
    int maxTableNameLength;
    boolean arraysSupported;
    boolean distinctsSupported;
    boolean udtsSupported;
}
