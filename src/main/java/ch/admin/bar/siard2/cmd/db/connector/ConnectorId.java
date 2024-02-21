package ch.admin.bar.siard2.cmd.db.connector;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.sql.Connection;
import java.sql.Driver;
import java.util.function.BiFunction;

@Value
@Builder
public class ConnectorId {
    @NonNull String name;
    @NonNull String jdbcIdentifier;
    @NonNull BiFunction<ConnectorProperties, Connection, Connector> connectorBuilder;

    @NonNull Class<? extends Driver> jdbcDriver;
}
