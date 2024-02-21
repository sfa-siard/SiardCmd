package ch.admin.bar.siard2.cmd.db.connector;

import ch.admin.bar.siard2.cmd.mapping.IdMapper;
import lombok.NonNull;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.time.Duration;


public class MsSqlConnector extends DefaultConnector {
    public MsSqlConnector(ConnectorProperties properties, Connection connection) {
        super(properties, connection);
    }

    @Override
    public SqlExecutor createExecutor(IdMapper idMapper) {
        return super.createExecutor(idMapper);
    }

    public static class MsSqlExecutor extends DefaultSqlExecutor {

        public MsSqlExecutor(@NonNull IdMapper idMapper, @NonNull Connection connection, @NonNull DatabaseMetaData dbMetaData, @NonNull Duration queryTimeout) {
            super(idMapper, connection, dbMetaData, queryTimeout);
        }
    }
}
