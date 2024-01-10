package ch.admin.bar.siard2.cmd.utils.siard.model.content;

import ch.admin.bar.siard2.cmd.utils.siard.update.Updatable;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;
import lombok.val;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Data
@Builder(toBuilder = true)
@JsonDeserialize(using = TableRow.Deserializer.class)
public class TableRow implements Updatable<TableRow> {

    @NonNull
    @Singular
    List<TableCell> cells;

    @Override
    public TableRow applyUpdates(Updater updater) {
        val updatedThis = updater.applyUpdate(this);

        return new TableRow(
                updatedThis.cells.stream()
                        .map(tableCell -> tableCell.applyUpdates(updater))
                        .collect(Collectors.toList()));
    }

    public static class Deserializer extends StdDeserializer<TableRow> {

        public Deserializer() {
            super(TableRow.class);
        }

        @Override
        public TableRow deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            JsonNode node = jp.getCodec().readTree(jp);

            val cells = stream(node.fieldNames())
                    .map(fieldName -> {
                        val number = Integer.parseInt(fieldName.substring(1));
                        val value = node.get(fieldName).asText();

                        return new TableCell(number, value);
                    })
                    .collect(Collectors.toList());

            return new TableRow(cells);
        }
    }

    private static <T> Stream<T> stream(Iterator<T> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false);
    }
}
