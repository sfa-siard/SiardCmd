package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.api.*;
import org.apache.tika.Tika;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.*;


/**
 * understands how to set the mime type for a cell (aka. column) based on the actual content
 */
class MimeTypeHandler {

    // used to keep track of detected mime types per column (name)
    private final Map<String, Set<String>> mimeTypes;

    // tika is able to detect a mime type from byte[] or input streams (and more)
    private final Tika tika;

    public MimeTypeHandler(Tika tika) {
        this.mimeTypes = new HashMap<>();
        this.tika = tika;
    }

    public void add(Value value, Blob blob) throws SQLException, IOException {
        if (value instanceof Cell) this.add((Cell) value, blob);
        if (value instanceof Field) this.add((Field) value, blob);
    }

    public void add(Value value, byte[] bytes) throws IOException {
        if (value instanceof Cell) this.add((Cell) value, bytes);
        if (value instanceof Field) this.add((Field) value, bytes);
    }

    public void add(Value value, Clob clob) throws SQLException, IOException {
        if (value instanceof Cell) this.add((Cell) value, clob);
        if (value instanceof Field) this.add((Field) value, clob);
    }

    /**
     * Applies the most suitable mime type to the cells meta column.
     * Most suitable means:
     * - The meta column should contain the mime type if exactly 1 mime type is present in the column
     * - no mime type should be written, if there are blobs, clobs or byte arrays (aka. "files") with different mime types present in the same column
     *
     * @param value - the value to apply the mime type to
     */
    public void applyMimeType(Value value) throws IOException {
        if (value instanceof Cell) this.applyMimeType((Cell) value);
        if (value instanceof Field) this.applyMimeType((Field) value);
    }

    private void add(Cell cell, byte[] bytes) {
        add(cell, tika.detect(bytes));
    }

    private void add(Field field, byte[] bytes) {
        add(field, tika.detect(bytes));
    }

    private void add(Cell cell, Clob clob) throws SQLException, IOException {
        add(cell, tika.detect(clob.getAsciiStream()));
    }

    private void add(Field field, Clob clob) throws SQLException, IOException {
        add(field, tika.detect(clob.getAsciiStream()));
    }

    private void add(Cell cell, Blob blob) throws SQLException, IOException {
        add(cell, tika.detect(blob.getBinaryStream()));
    }

    private void add(Field field, Blob blob) throws SQLException, IOException {
        add(field, tika.detect(blob.getBinaryStream()));
    }

    private void add(Cell cell, String mimeType) {
        add(mimeType, cell.getMetaColumn().getName());
    }

    private void add(Field field, String mimeType) {
        add(mimeType, field.getMetaField().getName());
    }

    private void add(String mimeType, String name) {
        if (!mimeTypes.containsKey(name)) {
            mimeTypes.put(name, new HashSet<>(Collections.singletonList(mimeType)));
        } else {
            mimeTypes.get(name).add(mimeType);
        }
    }

    private void applyMimeType(Cell cell) throws IOException {
        applyMimeType(cell.getMetaColumn());
    }

    private void applyMimeType(Field field) throws IOException {
        applyMimeType(field.getMetaField());
    }

    private void applyMimeType(MetaValue metaValue) throws IOException {
        Set<String> types = mimeTypes.get(metaValue.getName());
        if (types == null) return;
        if (types.size() == 1) metaValue.setMimeType((String) types.toArray()[0]);
        if (types.size() != 1) metaValue.setMimeType("");
    }
}
