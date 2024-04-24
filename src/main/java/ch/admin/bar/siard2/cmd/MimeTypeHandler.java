package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.api.Cell;
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

    /**
     * Detect and add the mime type from a {@link byte[]}
     */
    public void add(Cell cell, byte[] bytes) throws IOException {
        add(cell, tika.detect(bytes));
    }

    /**
     * Detect and add the mime type from a {@link Clob}
     */
    public void add(Cell cell, Clob clob) throws SQLException, IOException {
        add(cell, tika.detect(clob.getAsciiStream()));
    }

    /**
     * Detect and add the mime type from a {@link Blob}
     */
    public void add(Cell cell, Blob blob) throws SQLException, IOException {
        add(cell, tika.detect(blob.getBinaryStream()));
    }

    /**
     * Applies the most suitable mime type to the cells meta column.
     * Most suitable means:
     * - The meta column should contain the mime type if exactly 1 mime type is present in the column
     * - no mime type should be written, if there are blobs, clobs or byte arrays (aka. "files") with different mime types present in the same column
     * @param cell - the cell to apply the mime type to
     */
    public void applyMimeType(Cell cell) throws IOException {
        Set<String> types = mimeTypes.get(cell.getMetaColumn().getName());
        if (types == null) return;
        if (types.size() == 1) cell.getMetaColumn().setMimeType((String) types.toArray()[0]);
        if (types.size() != 1) cell.getMetaColumn().setMimeType("");
    }

    private void add(Cell cell, String mimeType) {
        String name = cell.getMetaColumn().getName();
        if (!mimeTypes.containsKey(name)) {
            mimeTypes.put(name, new HashSet<>(Arrays.asList(mimeType)));
        } else {
            mimeTypes.get(name).add(mimeType);
        }
    }
}
