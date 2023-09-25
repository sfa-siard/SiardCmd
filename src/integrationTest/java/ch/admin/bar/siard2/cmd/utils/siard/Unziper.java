package ch.admin.bar.siard2.cmd.utils.siard;

import lombok.SneakyThrows;
import lombok.val;
import net.lingala.zip4j.ZipFile;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

public class Unziper {

    private final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final File pathToArchive;

    @SneakyThrows
    public Unziper(final File pathToArchive) {
        this.pathToArchive = pathToArchive;
        temporaryFolder.create();
    }

    public File unzip() throws IOException {
        val pathToUnziped = temporaryFolder.newFolder("extracted");

        try (ZipFile zipFile = new ZipFile(pathToArchive)) {
            zipFile.extractAll(pathToUnziped.getPath());
        }

        return pathToUnziped;
    }

    public void delete() {
        this.temporaryFolder.delete();
    }
}
