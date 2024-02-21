package ch.admin.bar.siard2.cmd.db.connector;

import ch.admin.bar.siard2.api.MetaTable;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public interface SqlExecutor {
    void addForeignKeys(MetaTable tableMetadata) throws SQLException;

    @Deprecated
    void executeSql(String sql) throws SQLException;

    @Deprecated
    DatabaseMetaData getDatabaseMetaData();
}
