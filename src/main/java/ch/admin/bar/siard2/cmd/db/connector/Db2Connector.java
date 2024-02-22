package ch.admin.bar.siard2.cmd.db.connector;

import ch.admin.bar.siard2.api.MetaTable;
import ch.admin.bar.siard2.api.generated.ReferentialActionType;
import ch.admin.bar.siard2.cmd.mapping.IdMapper;
import ch.admin.bar.siard2.cmd.model.QualifiedTableId;
import ch.admin.bar.siard2.cmd.sql.CreateForeignKeySqlGenerator;
import ch.admin.bar.siard2.cmd.sql.IdEncoder;
import ch.admin.bar.siard2.cmd.utils.ListAssembler;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.time.Duration;

public class Db2Connector extends DefaultConnector {
    public Db2Connector(ConnectorProperties properties, Connection connection) {
        super(properties, connection);
    }

    @Override
    public SqlExecutor createExecutor(IdMapper idMapper) {
        return new Db2Executor(idMapper, connection, dbMetaData, properties.getQueryTimeout());
    }

    @Slf4j
    public static class Db2Executor extends DefaultSqlExecutor {

        public Db2Executor(@NonNull IdMapper idMapper, @NonNull Connection connection, @NonNull DatabaseMetaData databaseMetaData, @NonNull Duration queryTimeout) {
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
                        .onUpdateActionMapper(referentialActionType -> {
                            /*
                            DB2 does support NO-ACTION and RESTRICT as update action. When you update the row in the
                            parent key column of the parent table, Db2 rejects the update if there is the corresponding
                            row exists in the child table for both RESTRICT and NO ACTION option.
                             */
                            if (!referentialActionType.equals(ReferentialActionType.RESTRICT)) {
                                log.warn("Tried to use {} as on-update action (not supported in DB2). Used {} instead.",
                                        referentialActionType,
                                        ReferentialActionType.NO_ACTION);
                                return ReferentialActionType.NO_ACTION;
                            }

                            return referentialActionType;
                        })
                        .onDeleteActionMapper(referentialActionType -> {
                            /*
                            DB2 does support NO-ACTION/RESTRICT, CASCADE and SET-NULL as delete action.
                             */
                            if (referentialActionType.equals(ReferentialActionType.SET_DEFAULT)) {
                                log.warn("Tried to use {} as on-delete action (not supported in DB2). Used {} instead.",
                                        referentialActionType,
                                        ReferentialActionType.NO_ACTION);

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
                    val sql = sqlGenerator.create(tableId, foreignKeyMetaData);

                    executeSql(sql);
                }
            }
        }
    }
}
