package ch.admin.bar.siard2.cmd.db.connector;

import ch.admin.bar.siard2.api.MetaTable;
import ch.admin.bar.siard2.api.generated.ReferentialActionType;
import ch.admin.bar.siard2.cmd.mapping.IdMapper;
import ch.admin.bar.siard2.cmd.model.QualifiedTableId;
import ch.admin.bar.siard2.cmd.sql.CreateForeignKeySqlGenerator;
import ch.admin.bar.siard2.cmd.sql.IdEncoder;
import ch.admin.bar.siard2.cmd.utils.ListAssembler;
import lombok.Builder;
import lombok.NonNull;
import lombok.val;

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
        return new MsSqlExecutor(idMapper, connection, dbMetaData, properties.getQueryTimeout());
    }

    public static class MsSqlExecutor extends DefaultSqlExecutor {

        public MsSqlExecutor(@NonNull IdMapper idMapper, @NonNull Connection connection, @NonNull DatabaseMetaData databaseMetaData, @NonNull Duration queryTimeout) {
            super(idMapper, connection, databaseMetaData, queryTimeout);
        }

        @Override
        public void addForeignKeys(MetaTable tableMetadata) throws SQLException {
            if (tableMetadata.getMetaForeignKeys() > 0) {
                val tableId = QualifiedTableId.builder()
                        .schema(tableMetadata.getParentMetaSchema().getName())
                        .table(tableMetadata.getName())
                        .build();

                val sqlGenerator = CreateForeignKeySqlGenerator.builder()
                        .tableId(QualifiedTableId.builder()
                                .schema(tableMetadata.getParentMetaSchema().getName())
                                .table(tableMetadata.getName())
                                .build())
                        .idEncoder(new IdEncoder())
                        .referentialActionsMapper(referentialActionType -> {
                            // RESTRICT is unknown for MS SQL
                            if (referentialActionType.equals(ReferentialActionType.RESTRICT)) {
                                return ReferentialActionType.NO_ACTION;
                            }
                            return referentialActionType;
                        })
                        .idMapper(idMapper)
                        .build();

                val foreignKeysMetaData = ListAssembler.assemble(
                        tableMetadata.getMetaForeignKeys(),
                        tableMetadata::getMetaForeignKey);

                for (val foreignKeyMetaData : foreignKeysMetaData) {
                    executeSql(sqlGenerator.create(tableId, foreignKeyMetaData));
                }
            }
        }
    }
}
