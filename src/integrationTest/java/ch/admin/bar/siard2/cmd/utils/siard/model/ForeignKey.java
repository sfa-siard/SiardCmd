package ch.admin.bar.siard2.cmd.utils.siard.model;

import ch.admin.bar.siard2.cmd.utils.siard.update.Updatable;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import lombok.val;

import java.util.Optional;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class ForeignKey implements Updatable<ForeignKey> {
    Id<ForeignKey> name;
    Id<SchemaMetaData> referencedSchema;
    Id<TableMetaData> referencedTable;
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
