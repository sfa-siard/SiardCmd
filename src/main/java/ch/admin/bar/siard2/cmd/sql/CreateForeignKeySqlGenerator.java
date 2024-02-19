package ch.admin.bar.siard2.cmd.sql;

import ch.admin.bar.siard2.api.MetaForeignKey;
import ch.admin.bar.siard2.cmd.mapping.ColumnIdMapper;
import ch.admin.bar.siard2.cmd.mapping.TableIdMapper;
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

// TODO FIXME Match-Type currently not supported
@Slf4j
@RequiredArgsConstructor
@Builder
public class CreateForeignKeySqlGenerator {

    @NonNull
    private final QualifiedTableId tableId;

    @NonNull
    private final TableIdMapper tableIdMapper;
    @NonNull
    private final ColumnIdMapper columnIdMapper;
    @NonNull
    private final IdEncoder idEncoder;

    public String create(final List<MetaForeignKey> foreignKeyMetaData) {
        if (foreignKeyMetaData.isEmpty()) {
            return "";
        }

        val mappedTableId = tableIdMapper.map(tableId);

        val sb = new StringBuilder()
                .append("ALTER TABLE ")
                .append(idEncoder.encodeKeySensitive(mappedTableId));

        val addConstraintStatements = foreignKeyMetaData.stream()
                .map(this::addConstraintStatement)
                .collect(Collectors.joining(", "));

        sb.append(" ")
                .append(addConstraintStatements);

        log.debug("SQL statement for creating foreign-keys: {}", sb);

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

        log.error("SQL statement for creating foreign-key {}: {}", foreignKeyMetaData.getName(), sb);

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
                .column(columnIdMapper.map(origForeignKeyReference.getColumn()))
                .referenced(columnIdMapper.map(origForeignKeyReference.getReferenced()))
                .build();
    }

    @Value
    @Builder(toBuilder = true)
    private static class ForeignKeyReference {
        @NonNull QualifiedColumnId column;
        @NonNull QualifiedColumnId referenced;
    }
}
