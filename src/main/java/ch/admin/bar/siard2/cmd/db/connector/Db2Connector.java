package ch.admin.bar.siard2.cmd.db.connector;

import ch.admin.bar.siard2.api.MetaTable;
import ch.admin.bar.siard2.api.generated.ReferentialActionType;
import ch.admin.bar.siard2.cmd.mapping.IdMapper;
import ch.admin.bar.siard2.cmd.model.QualifiedTableId;
import ch.admin.bar.siard2.cmd.sql.CreateForeignKeySqlGenerator;
import ch.admin.bar.siard2.cmd.sql.IdEncoder;
import ch.admin.bar.siard2.cmd.utils.ListAssembler;
import lombok.NonNull;
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

                /*
                [CONSTRAINT constraint_name]
                    FOREIGN KEY (fk1, fk2,...)
                    REFERENCES parent_table(c1,2,..)
                        ON UPDATE [ NO ACTION | RESTRICT]
                        ON DELETE [ NO ACTION | RESTRICT | CASCADE | SET NULL];
                 */

                val sqlGenerator = CreateForeignKeySqlGenerator.builder()
                        .tableId(QualifiedTableId.builder()
                                .schema(tableMetadata.getParentMetaSchema().getName())
                                .table(tableMetadata.getName())
                                .build())
                        .idEncoder(new IdEncoder())
                        .referentialActionsMapper(action -> {
                            // RESTRICT is unknown for MS SQL
//                            if (ReferentialActionType.fromValue(action).equals(ReferentialActionType.RESTRICT)) {
//                                return ReferentialActionType.NO_ACTION.value();
//                            }
                            return action;
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

        private String getReferentialAction(int iReferentialAction) {
            ReferentialActionType rat = null;
            switch (iReferentialAction) {
                case DatabaseMetaData.importedKeyCascade:
                    rat = ReferentialActionType.CASCADE;
                    break;
                case DatabaseMetaData.importedKeySetNull:
                    rat = ReferentialActionType.SET_NULL;
                    break;
                case DatabaseMetaData.importedKeySetDefault:
                    rat = ReferentialActionType.SET_DEFAULT;
                    break;
                case DatabaseMetaData.importedKeyRestrict:
                    rat = ReferentialActionType.RESTRICT;
                    break;
                case DatabaseMetaData.importedKeyNoAction:
                    rat = ReferentialActionType.NO_ACTION;
            }
            return rat.value();
        }
    }
}
