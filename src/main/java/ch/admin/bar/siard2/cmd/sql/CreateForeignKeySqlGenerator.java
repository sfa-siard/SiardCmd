package ch.admin.bar.siard2.cmd.sql;

import ch.admin.bar.siard2.api.MetaForeignKey;
import ch.admin.bar.siard2.cmd.mapping.IdMapper;
import ch.admin.bar.siard2.cmd.model.QualifiedColumnId;
import ch.admin.bar.siard2.cmd.model.QualifiedTableId;
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

/**
 * The {@code CreateForeignKeySqlGenerator} class is responsible for generating SQL statements to add foreign key constraints to a table.
 * <p>
 * It takes into account the SIARD metadata for foreign keys, such as names, referenced columns, and actions (e.g., ON DELETE, ON UPDATE).
 * The generated SQL statements can be used to alter a table by adding the specified foreign key constraints.
 * </p>
 * <p>
 * The class supports mapping identifiers and encoding keys as needed, making it adaptable to various requirements.
 * </p>
 * <p>
 * Note: Match-Type is currently not supported and marked with TODO FIXME.
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
@Builder
public class CreateForeignKeySqlGenerator {

    /**
     * The qualified identifier of the table for which foreign key constraints are being generated.
     */
    @NonNull
    private final QualifiedTableId tableId;

    /**
     * The identifier mapper for mapping table and column identifiers.
     */
    @NonNull
    private final IdMapper idMapper;

    /**
     * The identifier encoder for encoding keys.
     */
    @NonNull
    private final IdEncoder idEncoder;

    /**
     * Generates SQL statements to create foreign key constraints based on the provided SIARD metadata.
     *
     * <p>Example:
     * <pre>
     * ALTER TABLE your_table_name
     *   ADD CONSTRAINT your_foreign_key_name
     *   FOREIGN KEY ("column1", "column2")
     *   REFERENCES referenced_table_name ("referenced_column1", "referenced_column2")
     *   ON DELETE CASCADE ON UPDATE CASCADE
     * </pre>
     * </p>
     *
     * @param foreignKeyMetaData List of SIARD metadata for foreign keys.
     * @return The generated SQL statement for creating foreign key constraints.
     */
    public String create(final List<MetaForeignKey> foreignKeyMetaData) {
        if (foreignKeyMetaData.isEmpty()) {
            return "";
        }

        val mappedTableId = idMapper.map(tableId);

        val sb = new StringBuilder()
                .append("ALTER TABLE ")
                .append(idEncoder.encodeKeySensitive(mappedTableId));

        val addConstraintStatements = foreignKeyMetaData.stream()
                .map(this::addConstraintStatement)
                .collect(Collectors.joining(", "));

        sb.append(" ")
                .append(addConstraintStatements);

        log.info("SQL statement for creating foreign-keys: {}", sb);

        return sb.toString();
    }

    private String addConstraintStatement(final MetaForeignKey foreignKeyMetaData) {
        if (foreignKeyMetaData.getReferences() == 0) {
            log.error("SIARD metadata for foreign-key {} has no references and will be ignored.", foreignKeyMetaData.getName());
            return "";
        }

        val references = resolveReferences(foreignKeyMetaData);

        val referencedTable = references.stream()
                .map(foreignKeyReference -> foreignKeyReference.getReferenced().getQualifiedTableId())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No references found"));

        val sb = new StringBuilder()
                .append("ADD CONSTRAINT ")
                .append(foreignKeyMetaData.getName())
                .append(" FOREIGN KEY (")
                .append(references.stream()
                        .map(foreignKeyReference -> idEncoder.encodeKeySensitive(foreignKeyReference.getColumn().getColumn()))
                        .collect(Collectors.joining(", ")))
                .append(") ")
                .append("REFERENCES ")
                .append(idEncoder.encodeKeySensitive(referencedTable))
                .append(" (")
                .append(references.stream()
                        .map(foreignKeyReference -> idEncoder.encodeKeySensitive(foreignKeyReference.getReferenced().getColumn()))
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
                            .qualifiedTable(tableId)
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
                .column(idMapper.map(origForeignKeyReference.getColumn()))
                .referenced(idMapper.map(origForeignKeyReference.getReferenced()))
                .build();
    }

    @Value
    @Builder(toBuilder = true)
    private static class ForeignKeyReference {
        @NonNull QualifiedColumnId column;
        @NonNull QualifiedColumnId referenced;
    }
}