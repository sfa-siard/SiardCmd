package ch.admin.bar.siard2.cmd.sql;

import ch.admin.bar.siard2.api.MetaForeignKey;
import ch.admin.bar.siard2.api.generated.ReferentialActionType;
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
import java.util.function.Function;
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

    @Builder.Default
    @NonNull
    private final Function<ReferentialActionType, ReferentialActionType> onUpdateActionMapper = type -> type;

    @Builder.Default
    @NonNull
    private final Function<ReferentialActionType, ReferentialActionType> onDeleteActionMapper = type -> type;

    /**
     * The identifier encoder for encoding keys.
     */
    @NonNull
    private final IdEncoder idEncoder;

    /**
     * Generates SQL statement to create foreign key constraint based on the provided SIARD metadata.
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
     * @param tableId The qualified identifier of the table for which foreign key constraint is being generated.
     * @param foreignKeyMetaData SIARD metadata for foreign key.
     * @return The generated SQL statement for creating foreign key constraint.
     */
    public String create(final QualifiedTableId tableId, final MetaForeignKey foreignKeyMetaData) {
        if (foreignKeyMetaData.getReferences() == 0) {
            log.error("SIARD metadata for foreign-key {} has no references and will be ignored.", foreignKeyMetaData.getName());
            return "";
        }

        val mappedTableId = idMapper.map(tableId);

        val references = resolveReferences(foreignKeyMetaData);

        val referencedTable = references.stream()
                .map(foreignKeyReference -> foreignKeyReference.getReferenced().getQualifiedTableId())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No references found"));

        val stringBuilder = new StringBuilder()
                .append("ALTER TABLE ")
                .append(idEncoder.encodeKeySensitive(mappedTableId))
                .append(" ADD CONSTRAINT ")
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
                .ifPresent(action -> stringBuilder.append(" ON DELETE ")
                        .append(onDeleteActionMapper.apply(ReferentialActionType.fromValue(action)).value()));

        Optional.ofNullable(foreignKeyMetaData.getUpdateAction())
                .ifPresent(action -> stringBuilder.append(" ON UPDATE ")
                        .append(onUpdateActionMapper.apply(ReferentialActionType.fromValue(action)).value()));

        stringBuilder.append(";");

        log.info("SQL statement for creating foreign-key: {}", stringBuilder);

        return stringBuilder.toString();
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

    public static class CreateForeignKeySqlGeneratorBuilder {
        public CreateForeignKeySqlGeneratorBuilder referentialActionsMapper(final Function<ReferentialActionType, ReferentialActionType> mapper) {
            onDeleteActionMapper(mapper);
            onUpdateActionMapper(mapper);

            return this;
        }
    }
}
