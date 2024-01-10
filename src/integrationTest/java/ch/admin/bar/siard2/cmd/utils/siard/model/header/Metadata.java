package ch.admin.bar.siard2.cmd.utils.siard.model.header;

import ch.admin.bar.siard2.cmd.utils.siard.model.utils.FolderId;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.Id;
import ch.admin.bar.siard2.cmd.utils.siard.model.utils.StringWrapper;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updatable;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import lombok.val;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Value
@Builder(toBuilder = true)
@Jacksonized
@JacksonXmlRootElement(localName = "siardArchive")
public class Metadata implements Updatable<Metadata> {
    StringWrapper dbname;
    Set<Schema> schemas;

    @Override
    public Metadata applyUpdates(Updater updater) {
        val updatedThis = updater.applyUpdate(this);

        return new Metadata(
                updatedThis.dbname.applyUpdates(updater),
                updatedThis.schemas.stream()
                        .map(schema -> schema.applyUpdates(updater))
                        .collect(Collectors.toSet()));
    }

    @Value
    @Builder(toBuilder = true)
    @Jacksonized
    public static class Schema implements Updatable<Schema> {
        Id<Schema> name;
        Set<Table> tables;
        FolderId folder;

        @Override
        public Schema applyUpdates(Updater updater) {
            val updatedThis = updater.applyUpdate(this);

            return new Schema(
                    updatedThis.name.applyUpdates(updater),
                    updatedThis.tables.stream()
                            .map(table -> table.applyUpdates(updater))
                            .collect(Collectors.toSet()),
                    updatedThis.folder.applyUpdates(updater));
        }
    }

    @Value
    @Builder(toBuilder = true)
    @Jacksonized
    public static class Table implements Updatable<Table> {
        Id<Table> name;

        @Builder.Default
        Set<Column> columns = new HashSet<>();

        @NonNull
        @Builder.Default
        Optional<PrimaryKey> primaryKey = Optional.empty();

        @Builder.Default
        Set<ForeignKey> foreignKeys = new HashSet<>();

        FolderId folder;

        @Override
        public Table applyUpdates(Updater updater) {
            val updatedThis = updater.applyUpdate(this);

            return new Table(
                    updatedThis.name.applyUpdates(updater),
                    updatedThis.columns.stream()
                            .map(column -> column.applyUpdates(updater))
                            .collect(Collectors.toSet()),
                    updatedThis.primaryKey.map(primaryKey -> primaryKey.applyUpdates(updater)),
                    updatedThis.foreignKeys.stream()
                            .map(foreignKey -> foreignKey.applyUpdates(updater))
                            .collect(Collectors.toSet()),
                    updatedThis.folder.applyUpdates(updater));
        }
    }

    @Value
    @Builder(toBuilder = true)
    @Jacksonized
    public static class Column implements Updatable<Column> {
        Id<Column> name;

        @NonNull
        @Builder.Default
        Optional<Boolean> nullable = Optional.empty();

        @Override
        public Column applyUpdates(Updater updater) {
            val updatedThis = updater.applyUpdate(this);

            return new Column(
                    updatedThis.name.applyUpdates(updater),
                    updatedThis.nullable
            );
        }
    }

    @Value
    @Builder(toBuilder = true)
    @Jacksonized
    public static class PrimaryKey implements Updatable<PrimaryKey> {
        StringWrapper name;
        Id<Column> column;

        @Override
        public PrimaryKey applyUpdates(Updater updater) {
            val updatedThis = updater.applyUpdate(this);

            return new PrimaryKey(
                    updatedThis.name.applyUpdates(updater),
                    updatedThis.column.applyUpdates(updater));
        }
    }

    @Value
    @Builder(toBuilder = true)
    @Jacksonized
    public static class ForeignKey implements Updatable<ForeignKey> {
        Id<ForeignKey> name;
        Id<Schema> referencedSchema;
        Id<Table> referencedTable;
        Reference reference; // TODO Probably a embedded set?
        @Builder.Default
        Optional<StringWrapper> deleteAction = Optional.empty();
        @Builder.Default
        Optional<StringWrapper> updateAction = Optional.empty();

        @Override
        public ForeignKey applyUpdates(Updater updater) {
            val updatedThis = updater.applyUpdate(this);

            return new ForeignKey(
                    updatedThis.name.applyUpdates(updater),
                    updatedThis.referencedSchema.applyUpdates(updater),
                    updatedThis.referencedTable.applyUpdates(updater),
                    updatedThis.reference.applyUpdates(updater),
                    updatedThis.deleteAction.map(s -> s.applyUpdates(updater)),
                    updatedThis.updateAction.map(s -> s.applyUpdates(updater)));
        }
    }

    @Value
    @Builder
    @Jacksonized
    public static class Reference implements Updatable<Reference> {
        Id<Column> column;
        Id<Column> referenced;

        @Override
        public Reference applyUpdates(Updater updater) {
            val updatedThis = updater.applyUpdate(this);

            return new Reference(
                    updatedThis.column.applyUpdates(updater),
                    updatedThis.referenced.applyUpdates(updater));
        }
    }
}
