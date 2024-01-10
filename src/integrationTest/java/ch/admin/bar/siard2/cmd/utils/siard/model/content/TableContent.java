package ch.admin.bar.siard2.cmd.utils.siard.model.content;

import ch.admin.bar.siard2.cmd.utils.siard.update.Updatable;
import ch.admin.bar.siard2.cmd.utils.siard.update.Updater;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder(toBuilder = true)
@Jacksonized
@JacksonXmlRootElement(localName = "table")
public class TableContent implements Updatable<TableContent> {

    @NonNull
    @Singular
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "row")
    List<TableRow> rows;

    @Override
    public TableContent applyUpdates(Updater updater) {
        val updatedThis = updater.applyUpdate(this);

        return new TableContent(updatedThis.rows.stream()
                .map(tableRow -> tableRow.applyUpdates(updater))
                .collect(Collectors.toList()));
    }
}
