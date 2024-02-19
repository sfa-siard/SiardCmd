package ch.admin.bar.siard2.cmd.sql;

import ch.admin.bar.siard2.api.MetaForeignKey;
import ch.admin.bar.siard2.cmd.ArchiveMapping;
import ch.admin.bar.siard2.cmd.SchemaMapping;
import ch.admin.bar.siard2.cmd.TableMapping;
import ch.admin.bar.siard2.cmd.utils.ListAssembler;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class TableDependantStatementGenerator {

    private final String schemaName;
    private final String tableName;

    private final ArchiveMapping archiveMapping;

    // String createForeignKeyQuery = "ALTER TABLE deine_tabelle "
    //                    + "ADD CONSTRAINT fk_foreign_key_name "
    //                    + "FOREIGN KEY (spalte_in_deiner_tabelle) "
    //                    + "REFERENCES referenzierte_tabelle (referenzierte_spalte) "
    //                    + "ON DELETE CASCADE "
    //                    + "ON UPDATE CASCADE";
    private String createConstraintStatement(final MetaForeignKey foreignKeyMetaData) {
        if (foreignKeyMetaData.getReferences() == 0) {
            return "";
        }

        val references = resolveReferences(foreignKeyMetaData);

        val referencedTable = references.stream()
                .map(foreignKeyReference -> String.format(
                        "%s.%s",
                        foreignKeyReference.getReferenced().getSchema(),
                        foreignKeyReference.getReferenced().getTable()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No references found"));

        val sb = new StringBuilder("ADD CONSTRAINT ").append(foreignKeyMetaData.getName())
                .append(" FOREIGN KEY(")
                .append(references.stream()
                        .map(foreignKeyReference -> foreignKeyReference.getColumn().getColumn())
                        .collect(Collectors.joining(", ")))
                .append(")")
                .append("REFERENCES ")
                .append(referencedTable)
                .append(" (")
                .append(references.stream()
                        .map(foreignKeyReference -> foreignKeyReference.getReferenced().getColumn())
                        .collect(Collectors.joining(", ")))
                .append(")");

        // actions
        Optional.ofNullable(foreignKeyMetaData.getDeleteAction())
                .ifPresent(action -> sb.append(" ON DELETE ").append(action));

        Optional.ofNullable(foreignKeyMetaData.getUpdateAction())
                .ifPresent(action -> sb.append(" ON UPDATE ").append(action));

        return sb.toString();
    }

    private List<ForeignKeyReference> resolveReferences(final MetaForeignKey foreignKeyMetaData) {
        val referencedColumns = ListAssembler.assemble(
                foreignKeyMetaData.getReferences(),
                index -> {
                    val referenced = QualifiedColumnId.builder()
                            .schema(foreignKeyMetaData.getReferencedSchema())
                            .table(foreignKeyMetaData.getReferencedTable())
                            .column(foreignKeyMetaData.getReferenced(index))
                            .build();

                    val column = QualifiedColumnId.builder()
                            .schema(schemaName)
                            .table(tableName)
                            .column(foreignKeyMetaData.getColumn(index))
                            .build();

                    return ForeignKeyReference.builder()
                            .referenced(referenced)
                            .column(column)
                            .build();
                });

        return referencedColumns.stream()
                .map(this::resolveMappings)
                .collect(Collectors.toList());
    }

    private ForeignKeyReference resolveMappings(ForeignKeyReference origForeignKeyReference) {
        return origForeignKeyReference.toBuilder()
                .column(resolveMappings(origForeignKeyReference.getColumn()))
                .referenced(resolveMappings(origForeignKeyReference.getReferenced()))
                .build();
    }

    private QualifiedColumnId resolveMappings(QualifiedColumnId origQualifiedColumnId) {
        final SchemaMapping sm = archiveMapping.getSchemaMapping(origQualifiedColumnId.getSchema());  // TODO FIXME can return null
        final TableMapping tm = sm.getTableMapping(origQualifiedColumnId.getTable());  // TODO FIXME can return null

        return origQualifiedColumnId.toBuilder()
                .schema(sm.getMappedSchemaName())
                .table(tm.getMappedTableName())
                .column(tm.getMappedColumnName(origQualifiedColumnId.getColumn())) // TODO FIXME can return null
                .build();
    }

    @Value
    @Builder(toBuilder = true)
    private static class ForeignKeyReference {
        @NonNull QualifiedColumnId column;
        @NonNull QualifiedColumnId referenced;
    }

    @Value
    @Builder(toBuilder = true)
    private static class QualifiedColumnId {
        @NonNull String schema;
        @NonNull String table;
        @NonNull String column;
    }
}
