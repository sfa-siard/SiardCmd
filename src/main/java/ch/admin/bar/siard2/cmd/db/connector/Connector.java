package ch.admin.bar.siard2.cmd.db.connector;

import ch.admin.bar.siard2.cmd.db.features.DbFeatures;
import ch.admin.bar.siard2.cmd.mapping.IdMapper;

import java.sql.Connection;
import java.sql.SQLException;

public interface Connector {
    DbFeatures getDbFeatures();

    @Deprecated
    Connection getConnection();

    SqlExecutor createExecutor(IdMapper idMapper) throws SQLException;

    void close();
}
