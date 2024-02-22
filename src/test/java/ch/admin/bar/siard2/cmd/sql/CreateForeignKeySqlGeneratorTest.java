package ch.admin.bar.siard2.cmd.sql;

import ch.admin.bar.siard2.api.MetaForeignKey;
import ch.admin.bar.siard2.api.generated.ReferentialActionType;
import ch.admin.bar.siard2.cmd.mapping.IdMapper;
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

    private final IdMapper idMapperMock = new IdMapperMockFactory().create();

    private final CreateForeignKeySqlGenerator createForeignKeySqlGenerator = CreateForeignKeySqlGenerator.builder()
            .tableId(QUALIFIED_TABLE_ID)
            .idMapper(idMapperMock)
            .idEncoder(new IdEncoder())
            .build();


    @Test
    public void createConstraintStatement_withSingleColumnPrimaryKey_expectValidSqlStatement() {
        // given
        val siardMetaDataFactory = new MetaForeignKeyMockFactory();

        // when
        val result = createForeignKeySqlGenerator.create(QUALIFIED_TABLE_ID, siardMetaDataFactory.create());

        Assertions.assertThat(result)
                .isEqualTo(String.format(
                        "ALTER TABLE \"%s\".\"%s\" " +
                                "ADD CONSTRAINT %s " +
                                "FOREIGN KEY (\"%s\") " +
                                "REFERENCES \"%s\".\"%s\" (\"%s\") " +
                                "ON DELETE %s " +
                                "ON UPDATE %s;",
                        QUALIFIED_TABLE_ID.getSchema() + IdMapperMockFactory.ADDED_SUFFIX,
                        QUALIFIED_TABLE_ID.getTable() + IdMapperMockFactory.ADDED_SUFFIX,
                        siardMetaDataFactory.getName(),
                        siardMetaDataFactory.getReferences().get(0).getColumn() + IdMapperMockFactory.ADDED_SUFFIX,
                        siardMetaDataFactory.getReferencedSchema() + IdMapperMockFactory.ADDED_SUFFIX,
                        siardMetaDataFactory.getReferencedTable() + IdMapperMockFactory.ADDED_SUFFIX,
                        siardMetaDataFactory.getReferences().get(0).getReferenced() + IdMapperMockFactory.ADDED_SUFFIX,
                        siardMetaDataFactory.getDeleteAction().value(),
                        siardMetaDataFactory.getUpdateAction().value()
                ));
    }

    @Test
    public void createConstraintStatement_withMultiColumnsPrimaryKey_expectValidSqlStatement() {
        // given
        val siardMetaDataFactory = new MetaForeignKeyMockFactory()
                .addReference()
                .addReference();

        // when
        val result = createForeignKeySqlGenerator.create(QUALIFIED_TABLE_ID, siardMetaDataFactory.create());

        Assertions.assertThat(result)
                .isEqualTo(String.format(
                        "ALTER TABLE \"%s\".\"%s\" " +
                                "ADD CONSTRAINT %s " +
                                "FOREIGN KEY (\"%s\", \"%s\", \"%s\") " +
                                "REFERENCES \"%s\".\"%s\" (\"%s\", \"%s\", \"%s\") " +
                                "ON DELETE %s " +
                                "ON UPDATE %s;",
                        QUALIFIED_TABLE_ID.getSchema() + IdMapperMockFactory.ADDED_SUFFIX,
                        QUALIFIED_TABLE_ID.getTable() + IdMapperMockFactory.ADDED_SUFFIX,
                        siardMetaDataFactory.getName(),
                        siardMetaDataFactory.getReferences().get(0).getColumn() + IdMapperMockFactory.ADDED_SUFFIX,
                        siardMetaDataFactory.getReferences().get(1).getColumn() + IdMapperMockFactory.ADDED_SUFFIX,
                        siardMetaDataFactory.getReferences().get(2).getColumn() + IdMapperMockFactory.ADDED_SUFFIX,
                        siardMetaDataFactory.getReferencedSchema() + IdMapperMockFactory.ADDED_SUFFIX,
                        siardMetaDataFactory.getReferencedTable() + IdMapperMockFactory.ADDED_SUFFIX,
                        siardMetaDataFactory.getReferences().get(0).getReferenced() + IdMapperMockFactory.ADDED_SUFFIX,
                        siardMetaDataFactory.getReferences().get(1).getReferenced() + IdMapperMockFactory.ADDED_SUFFIX,
                        siardMetaDataFactory.getReferences().get(2).getReferenced() + IdMapperMockFactory.ADDED_SUFFIX,
                        siardMetaDataFactory.getDeleteAction().value(),
                        siardMetaDataFactory.getUpdateAction().value()
                ));
    }

    private static class IdMapperMockFactory {
        public static final String ADDED_SUFFIX = "_mapped";

        public IdMapper create() {
            val mock = Mockito.mock(IdMapper.class);

            Mockito.when(mock.map(Mockito.any(QualifiedTableId.class))).then(invocationOnMock -> {
                final QualifiedTableId orig = invocationOnMock.getArgument(0);

                return orig.toBuilder()
                        .schema(orig.getSchema() + ADDED_SUFFIX)
                        .table(orig.getTable() + ADDED_SUFFIX)
                        .build();
            });
            Mockito.when(mock.map(Mockito.any(QualifiedColumnId.class))).then(invocationOnMock -> {
                final QualifiedColumnId orig = invocationOnMock.getArgument(0);

                return orig.toBuilder()
                        .schema(orig.getSchema() + ADDED_SUFFIX)
                        .table(orig.getTable() + ADDED_SUFFIX)
                        .column(orig.getColumn() + ADDED_SUFFIX)
                        .build();
            });

            return mock;
        }
    }

    @Data
    private static class MetaForeignKeyMockFactory {

        private static int instancesCounter = 0;

        String name = instancesCounter + "_foreign_key_name";
        String referencedSchema = instancesCounter + "_foreign_key_referenced_schema";
        String referencedTable = instancesCounter + "_foreign_key_referenced_table";

        ReferentialActionType deleteAction = ReferentialActionType.NO_ACTION;
        ReferentialActionType updateAction = ReferentialActionType.CASCADE;

        List<Reference> references = new ArrayList<>();

        public MetaForeignKeyMockFactory() {
            instancesCounter++;
            addReference();
        }

        public MetaForeignKeyMockFactory addReference() {
            references.add(new Reference());
            return this;
        }

        public MetaForeignKey create() {
            val mock = Mockito.mock(MetaForeignKey.class);

            Mockito.when(mock.getName()).thenReturn(name);
            Mockito.when(mock.getDeleteAction()).thenReturn(deleteAction.value());
            Mockito.when(mock.getUpdateAction()).thenReturn(updateAction.value());

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