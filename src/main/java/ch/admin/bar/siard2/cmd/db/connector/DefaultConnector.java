package ch.admin.bar.siard2.cmd.db.connector;

import ch.admin.bar.siard2.api.MetaTable;
import ch.admin.bar.siard2.cmd.db.features.DbFeatures;
import ch.admin.bar.siard2.cmd.db.features.DbFeaturesChecker;
import ch.admin.bar.siard2.cmd.mapping.IdMapper;
import ch.admin.bar.siard2.cmd.model.QualifiedTableId;
import ch.admin.bar.siard2.cmd.sql.CreateForeignKeySqlGenerator;
import ch.admin.bar.siard2.cmd.sql.IdEncoder;
import ch.admin.bar.siard2.cmd.utils.ListAssembler;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.time.Duration;

public class DefaultConnector implements Connector {

    private final ConnectorProperties properties;

    @Getter
    private final Connection connection;

    private final DatabaseMetaData dbMetaData;

    @Getter
    private final DbFeatures dbFeatures;

    @SneakyThrows // TODO
    public DefaultConnector(final ConnectorProperties properties, final Connection connection) {
        this.properties = properties;
        this.connection = connection;

        dbMetaData = connection.getMetaData();
        this.dbFeatures = new DbFeaturesChecker(connection, dbMetaData).checkFeatures();
    }

    @Override
    public SqlExecutor createExecutor(IdMapper idMapper) {
        return DefaultSqlExecutor.builder()
                .idMapper(idMapper)
                .connection(connection)
                .databaseMetaData(dbMetaData)
                .queryTimeout(properties.getQueryTimeout())
                .build();
    }

    @SneakyThrows // TODO
    @Override
    public void close() {
        connection.close();
    }

    @Builder
    @RequiredArgsConstructor
    public static class DefaultSqlExecutor implements SqlExecutor {

        @NonNull
        private final IdMapper idMapper;

        @NonNull
        private final Connection connection;

        @Getter
        @NonNull
        private final DatabaseMetaData databaseMetaData;

        @NonNull
        private final Duration queryTimeout;

        @Override
        public void addForeignKeys(MetaTable tableMetadata) throws SQLException {
            if (tableMetadata.getMetaForeignKeys() > 0) {
                val sqlGenerator = CreateForeignKeySqlGenerator.builder()
                        .tableId(QualifiedTableId.builder()
                                .schema(tableMetadata.getParentMetaSchema().getName())
                                .table(tableMetadata.getName())
                                .build())
                        .idEncoder(new IdEncoder())
                        .idMapper(idMapper)
                        .build();

                val sql = sqlGenerator.create(ListAssembler.assemble(tableMetadata.getMetaForeignKeys(), tableMetadata::getMetaForeignKey));

                executeSql(sql);
            }
        }

        @Override
        public void executeSql(String sql) throws SQLException {
            val statement = connection.createStatement();
            statement.setQueryTimeout((int)queryTimeout.getSeconds());
            statement.execute(sql);
            statement.close();
        }
    }
}
