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
        val result = createForeignKeySqlGenerator.create(siardMetaDataFactory.create());

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
                .addReference(new SiardForeignKeyDataFactory.Reference(1))
                .addReference(new SiardForeignKeyDataFactory.Reference(2));

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
        val result = createForeignKeySqlGenerator.create(siardMetaDataFactory.create());

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

    @Data
    private static class SiardForeignKeyDataFactory {

        String name = "foreign_key_name";
        String referencedSchema = "foreign_key_referenced_schema";
        String referencedTable = "foreign_key_referenced_table";

        String deleteAction = "foreign_key_delete_action";
        String updateAction = "foreign_key_update_action";

        List<Reference> references = new ArrayList<>();

        public SiardForeignKeyDataFactory() {
            references.add(new Reference(0));
        }

        public SiardForeignKeyDataFactory addReference(final Reference reference) {
            references.add(reference);
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
            String column;
            String referenced;

            public Reference(String column, String referenced) {
                this.column = column;
                this.referenced = referenced;
            }

            public Reference(final int index) {
                this.column = "foreign_key_reference_column_" + index;
                this.referenced = "foreign_key_reference_referenced_" + index;
            }
        }
    }

}