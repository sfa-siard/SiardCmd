package ch.admin.bar.siard2.cmd.db.connector;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

@Value
@Builder
public class ConnectorProperties {
    @NonNull
    String jdbcUrl;

    @Nullable
    String user;

    @Nullable
    String password;

    @Builder.Default
    Duration queryTimeout = Duration.ofSeconds(30);

    @Builder.Default
    Duration loginTimeout = Duration.ofSeconds(30);
}
