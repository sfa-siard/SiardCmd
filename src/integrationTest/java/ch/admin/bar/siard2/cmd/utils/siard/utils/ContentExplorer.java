package ch.admin.bar.siard2.cmd.utils.siard.utils;

import ch.admin.bar.siard2.cmd.utils.siard.model.utils.FolderId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.QualifiedTableId;
import ch.admin.bar.siard2.cmd.utils.siard.model.SiardArchive;
import ch.admin.bar.siard2.cmd.utils.siard.model.content.Content;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ContentExplorer {

    private final SiardArchive siardArchive;
    private final Set<TableDataMapping> tableDataMappings;

    public ContentExplorer(final SiardArchive siardArchive) {
        this.siardArchive = siardArchive;
        this.tableDataMappings = findMappings(siardArchive);
    }

    public Set<QualifiedTableId> getAllIds() {
        return tableDataMappings.stream()
                .map(TableDataMapping::getTableId)
                .collect(Collectors.toSet());
    }

    public Optional<Content.Table> tryFindTable(final QualifiedTableId tableId) {
        return tableDataMappings.stream()
                .filter(tableDataMapping -> tableDataMapping.getTableId().equals(tableId))
                .findAny()
                .map(TableDataMapping::getTable);
    }

    public Content.Table findTable(final QualifiedTableId tableId) {
        return tryFindTable(tableId)
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "No table with id %s found. Available tables are: %s",
                        tableId,
                        getAllIds().stream()
                                .map(QualifiedTableId::toString)
                                .collect(Collectors.joining("\n - "))
                )));
    }

    private static Set<TableDataMapping> findMappings(final SiardArchive siardArchive) {
        return siardArchive.getSiardMetadata().getSchemas().stream()
                .flatMap(schemaMetaData -> schemaMetaData.getTables().stream()
                        .map(tableMetaData -> TableDataMapping.builder()
                                .tableId(QualifiedTableId.builder()
                                        .schemaId(schemaMetaData.getName())
                                        .tableId(tableMetaData.getName())
                                        .build())
                                .schemaFolder(schemaMetaData.getFolder())
                                .tableFolder(tableMetaData.getFolder())
                                .table(siardArchive.getSiardContent().findTable(schemaMetaData.getFolder(), tableMetaData.getFolder()))
                                .build()))
                .collect(Collectors.toSet());
    }

    @Value
    @Builder
    private static class TableDataMapping {
        @NonNull QualifiedTableId tableId;
        @NonNull FolderId schemaFolder; // FIXME: Not really needed
        @NonNull FolderId tableFolder; // FIXME: Not really needed
        @NonNull Content.Table table;
    }
}
