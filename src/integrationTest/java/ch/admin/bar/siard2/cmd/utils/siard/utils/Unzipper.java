package ch.admin.bar.siard2.cmd.utils.siard.utils;

import io.chandler.zip.patch64.ZipInputStreamPatch64;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility class for extracting the contents of a ZIP archive.
 * <p>
 * Usage example:
 * <pre>{@code
 * File zipFile = new File("path/to/archive.zip");
 * File extractionDirectory = new File("path/to/extract");
 * Unzipper unzipper = new Unzipper(zipFile, extractionDirectory);
 * unzipper.unzip();
 * }</pre>
 * </p>
 */
@RequiredArgsConstructor
public class Unzipper {

    private final File pathToArchive;
    private final File extractTo;

    /**
     * Unzips the contents of the ZIP archive to the specified directory.
     */
    public File unzip() throws IOException {
        final byte[] buffer = new byte[1024];
        final ZipInputStream zis = new ZipInputStreamPatch64(Files.newInputStream(pathToArchive.toPath()));

        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            final File newFile = newFile(extractTo, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() &&
                        !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                final File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                // write file content
                final FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();

        return extractTo;
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}
