package ch.admin.bar.siard2.cmd.sql;

import ch.admin.bar.siard2.api.MetaForeignKey;
import ch.admin.bar.siard2.cmd.mapping.ColumnIdMapper;
import ch.admin.bar.siard2.cmd.mapping.TableIdMapper;
import ch.admin.bar.siard2.cmd.model.QualifiedColumnId;
import ch.admin.bar.siard2.cmd.model.QualifiedTableId;
import lombok.Data;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateForeignKeySqlGeneratorTest {

    private static final QualifiedTableId QUALIFIED_TABLE_ID = QualifiedTableId.builder()
            .table("table_name")
            .schema("schema_name")
            .build();

    private final TableIdMapper tableIdMapperMock = Mockito.mock(TableIdMapper.class);
    private final ColumnIdMapper columnIdMapper = Mockito.mock(ColumnIdMapper.class);

    private final CreateForeignKeySqlGenerator createForeignKeySqlGenerator = CreateForeignKeySqlGenerator.builder()
            .tableId(QUALIFIED_TABLE_ID)
            .tableIdMapper(tableIdMapperMock)
            .columnIdMapper(columnIdMapper)
            .idEncoder(new IdEncoder())
            .build();


    @Test
    public void createConstraintStatement_withSingleColumnPrimaryKey_expectValidSqlStatement() {
        // given
        val siardMetaDataFactory = new SiardForeignKeyDataFactory();

        Mockito.when(tableIdMapperMock.map(Mockito.any())).then(invocationOnMock -> {
            final QualifiedTableId orig = invocationOnMock.getArgument(0);

            return orig.toBuilder()
                    .schema(orig.getSchema() + "_mapped")
                    .table(orig.getTable() + "_mapped")
                    .build();
        });
        Mockito.when(columnIdMapper.map(Mockito.any())).then(invocationOnMock -> {
            final QualifiedColumnId orig = invocationOnMock.getArgument(0);

            return orig.toBuilder()
                    .schema(orig.getSchema() + "_mapped")
                    .table(orig.getTable() + "_mapped")
                    .column(orig.getColumn() + "_mapped")
                    .build();
        });

        // when
        val result = createForeignKeySqlGenerator.create(Arrays.asList(siardMetaDataFactory.create()));

        Assertions.assertThat(result)
                .isEqualTo(String.format(
                        "ALTER TABLE \"%s_mapped\".\"%s_mapped\" " +
                                "ADD CONSTRAINT %s " +
                                "FOREIGN KEY (\"%s_mapped\") " +
                                "REFERENCES \"%s_mapped\".\"%s_mapped\" (\"%s_mapped\") " +
                                "ON DELETE %s " +
                                "ON UPDATE %s",
                        QUALIFIED_TABLE_ID.getSchema(),
                        QUALIFIED_TABLE_ID.getTable(),
                        siardMetaDataFactory.getName(),
                        siardMetaDataFactory.getReferences().get(0).getColumn(),
                        siardMetaDataFactory.getReferencedSchema(),
                        siardMetaDataFactory.getReferencedTable(),
                        siardMetaDataFactory.getReferences().get(0).getReferenced(),
                        siardMetaDataFactory.getDeleteAction(),
                        siardMetaDataFactory.getUpdateAction()
                ));
    }

    @Test
    public void createConstraintStatement_withMultiColumnsPrimaryKey_expectValidSqlStatement() {
        // given
        val siardMetaDataFactory = new SiardForeignKeyDataFactory()
                .addReference()
                .addReference();

        Mockito.when(tableIdMapperMock.map(Mockito.any())).then(invocationOnMock -> {
            final QualifiedTableId orig = invocationOnMock.getArgument(0);

            return orig.toBuilder()
                    .schema(orig.getSchema() + "_mapped")
                    .table(orig.getTable() + "_mapped")
                    .build();
        });
        Mockito.when(columnIdMapper.map(Mockito.any())).then(invocationOnMock -> {
            final QualifiedColumnId orig = invocationOnMock.getArgument(0);

            return orig.toBuilder()
                    .schema(orig.getSchema() + "_mapped")
                    .table(orig.getTable() + "_mapped")
                    .column(orig.getColumn() + "_mapped")
                    .build();
        });

        // when
        val result = createForeignKeySqlGenerator.create(Arrays.asList(siardMetaDataFactory.create()));

        Assertions.assertThat(result)
                .isEqualTo(String.format(
                        "ALTER TABLE \"%s_mapped\".\"%s_mapped\" " +
                                "ADD CONSTRAINT %s " +
                                "FOREIGN KEY (\"%s_mapped\", \"%s_mapped\", \"%s_mapped\") " +
                                "REFERENCES \"%s_mapped\".\"%s_mapped\" (\"%s_mapped\", \"%s_mapped\", \"%s_mapped\") " +
                                "ON DELETE %s " +
                                "ON UPDATE %s",
                        QUALIFIED_TABLE_ID.getSchema(),
                        QUALIFIED_TABLE_ID.getTable(),
                        siardMetaDataFactory.getName(),
                        siardMetaDataFactory.getReferences().get(0).getColumn(),
                        siardMetaDataFactory.getReferences().get(1).getColumn(),
                        siardMetaDataFactory.getReferences().get(2).getColumn(),
                        siardMetaDataFactory.getReferencedSchema(),
                        siardMetaDataFactory.getReferencedTable(),
                        siardMetaDataFactory.getReferences().get(0).getReferenced(),
                        siardMetaDataFactory.getReferences().get(1).getReferenced(),
                        siardMetaDataFactory.getReferences().get(2).getReferenced(),
                        siardMetaDataFactory.getDeleteAction(),
                        siardMetaDataFactory.getUpdateAction()
                ));
    }

    @Test
    public void createConstraintStatement_withMultipleForeignKeysWithSingleColumnPrimaryKey_expectValidSqlStatement() {
        // given
        val siardMetaDataFactory1 = new SiardForeignKeyDataFactory();
        val siardMetaDataFactory2 = new SiardForeignKeyDataFactory();

        Mockito.when(tableIdMapperMock.map(Mockito.any())).then(invocationOnMock -> {
            final QualifiedTableId orig = invocationOnMock.getArgument(0);

            return orig.toBuilder()
                    .schema(orig.getSchema() + "_mapped")
                    .table(orig.getTable() + "_mapped")
                    .build();
        });
        Mockito.when(columnIdMapper.map(Mockito.any())).then(invocationOnMock -> {
            final QualifiedColumnId orig = invocationOnMock.getArgument(0);

            return orig.toBuilder()
                    .schema(orig.getSchema() + "_mapped")
                    .table(orig.getTable() + "_mapped")
                    .column(orig.getColumn() + "_mapped")
                    .build();
        });

        // when
        val result = createForeignKeySqlGenerator.create(Arrays.asList(
                siardMetaDataFactory1.create(),
                siardMetaDataFactory2.create()
                ));

        Assertions.assertThat(result)
                .isEqualTo(String.format(
                        "ALTER TABLE \"%s_mapped\".\"%s_mapped\" " +

                                "ADD CONSTRAINT %s " +
                                "FOREIGN KEY (\"%s_mapped\") " +
                                "REFERENCES \"%s_mapped\".\"%s_mapped\" (\"%s_mapped\") " +
                                "ON DELETE %s " +
                                "ON UPDATE %s, " +

                                "ADD CONSTRAINT %s " +
                                "FOREIGN KEY (\"%s_mapped\") " +
                                "REFERENCES \"%s_mapped\".\"%s_mapped\" (\"%s_mapped\") " +
                                "ON DELETE %s " +
                                "ON UPDATE %s",
                        QUALIFIED_TABLE_ID.getSchema(),
                        QUALIFIED_TABLE_ID.getTable(),

                        siardMetaDataFactory1.getName(),
                        siardMetaDataFactory1.getReferences().get(0).getColumn(),
                        siardMetaDataFactory1.getReferencedSchema(),
                        siardMetaDataFactory1.getReferencedTable(),
                        siardMetaDataFactory1.getReferences().get(0).getReferenced(),
                        siardMetaDataFactory1.getDeleteAction(),
                        siardMetaDataFactory1.getUpdateAction(),

                        siardMetaDataFactory2.getName(),
                        siardMetaDataFactory2.getReferences().get(0).getColumn(),
                        siardMetaDataFactory2.getReferencedSchema(),
                        siardMetaDataFactory2.getReferencedTable(),
                        siardMetaDataFactory2.getReferences().get(0).getReferenced(),
                        siardMetaDataFactory2.getDeleteAction(),
                        siardMetaDataFactory2.getUpdateAction()
                ));
    }



    @Data
    private static class SiardForeignKeyDataFactory {

        private static int instancesCounter = 0;

        String name = instancesCounter + "_foreign_key_name";
        String referencedSchema = instancesCounter + "_foreign_key_referenced_schema";
        String referencedTable = instancesCounter + "_foreign_key_referenced_table";

        String deleteAction = instancesCounter + "_foreign_key_delete_action";
        String updateAction = instancesCounter + "_foreign_key_update_action";

        List<Reference> references = new ArrayList<>();

        public SiardForeignKeyDataFactory() {
            instancesCounter++;
            addReference();
        }

        public SiardForeignKeyDataFactory addReference() {
            references.add(new Reference());
            return this;
        }

        public MetaForeignKey create() {
            val mock = Mockito.mock(MetaForeignKey.class);

            Mockito.when(mock.getName()).thenReturn(name);
            Mockito.when(mock.getDeleteAction()).thenReturn(deleteAction);
            Mockito.when(mock.getUpdateAction()).thenReturn(updateAction);

            Mockito.when(mock.getReferencedSchema()).thenReturn(referencedSchema);
            Mockito.when(mock.getReferencedTable()).thenReturn(referencedTable);

            Mockito.when(mock.getReferences()).thenReturn(references.size());
            Mockito.when(mock.getReferenced(Mockito.anyInt())).then(invocationOnMock -> {
                final int index = invocationOnMock.getArgument(0);
                return references.get(index).getReferenced();
            });
            Mockito.when(mock.getColumn(Mockito.anyInt())).then(invocationOnMock -> {
                final int index = invocationOnMock.getArgument(0);
                return references.get(index).getColumn();
            });

            return mock;
        }

        @Data
        public static class Reference {
            private static int instancesCounter = 0;

            String column;
            String referenced;

            public Reference() {
                instancesCounter++;
                this.column = "foreign_key_reference_column_" + instancesCounter;
                this.referenced = "foreign_key_reference_referenced_" + instancesCounter;
            }
        }
    }

}