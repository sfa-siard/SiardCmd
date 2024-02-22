package ch.admin.bar.siard2.cmd.db.connector;

import ch.admin.bar.siard2.cmd.utils.CollectionsHelper;
import ch.admin.bar.siard2.jdbc.AccessDriver;
import ch.admin.bar.siard2.jdbc.Db2Driver;
import ch.admin.bar.siard2.jdbc.MsSqlDriver;
import ch.admin.bar.siard2.jdbc.MySqlDriver;
import ch.admin.bar.siard2.jdbc.OracleDriver;
import ch.admin.bar.siard2.jdbc.PostgresDriver;
import lombok.val;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class ConnectorsRegistry {

    public static final ConnectorsRegistry INSTANCE = new ConnectorsRegistry();

    public static final ConnectorId POSTGRES = ConnectorId.builder()
            .name("PostgreSQL")
            .jdbcIdentifier("postgresql")
            .connectorBuilder(DefaultConnector::new)
            .jdbcDriver(PostgresDriver.class)
            .build();

    public static final ConnectorId ORACLE = ConnectorId.builder()
            .name("Oracle")
            .jdbcIdentifier("oracle")
            .connectorBuilder(DefaultConnector::new)
            .jdbcDriver(OracleDriver.class)
            .build();

    public static final ConnectorId MS_SQL = ConnectorId.builder()
            .name("SQL Server")
            .jdbcIdentifier("sqlserver")
            .connectorBuilder(DefaultConnector::new)
            .connectorBuilder(MsSqlConnector::new)
            .jdbcDriver(MsSqlDriver.class)
            .build();

    public static final ConnectorId MY_SQL = ConnectorId.builder()
            .name("MySQL")
            .jdbcIdentifier("mysql")
            .connectorBuilder(DefaultConnector::new)
            .jdbcDriver(MySqlDriver.class)
            .build();

    public static final ConnectorId DB2 = ConnectorId.builder()
            .name("DB/2")
            .jdbcIdentifier("db2")
            .connectorBuilder(Db2Connector::new)
            .jdbcDriver(Db2Driver.class)
            .build();

    public static final ConnectorId MS_ACCESS = ConnectorId.builder()
            .name("Microsoft Access")
            .jdbcIdentifier("access")
            .connectorBuilder(DefaultConnector::new)
            .jdbcDriver(AccessDriver.class)
            .build();

    private static final List<ConnectorId> AVAILABLE_CONNECTORS = CollectionsHelper.listOf(
            POSTGRES,
            ORACLE,
            MS_SQL,
            MY_SQL,
            DB2,
            MS_ACCESS
    );

    public Connector getConnector(final ConnectorProperties properties) throws SQLException {
        val connectorId = findConnectorIdByJdbcUrl(properties.getJdbcUrl());

        DriverManager.setLoginTimeout((int)properties.getLoginTimeout().getSeconds());
        val connection = DriverManager.getConnection(
                properties.getJdbcUrl(),
                properties.getUser(),
                properties.getPassword()
        );

        if (connection == null || connection.isClosed()) {
            throw new IllegalStateException("Can not establish connection for " + properties.getJdbcUrl());
        }

        connection.setAutoCommit(false);

        return connectorId.getConnectorBuilder().apply(properties, connection);
    }

    private static ConnectorId findConnectorIdByJdbcUrl(final String jdbcUrl) {
        val searchedJdbcId = extractJdbcIdentifier(jdbcUrl);

        return AVAILABLE_CONNECTORS.stream()
                .filter(connectorId -> connectorId.getJdbcIdentifier().equals(searchedJdbcId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "No connector for jdbc url %s found",
                        jdbcUrl
                )));
    }

    private static String extractJdbcIdentifier(final String jdbcUrl) {
        val split = jdbcUrl.split(":");

        if (split.length < 2) {
            throw new IllegalArgumentException(jdbcUrl + "is not a valid jdbc url");
        }

        return split[1];
    }
}
