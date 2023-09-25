package ch.admin.bar.siard2.cmd.utils;

import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Files;

@Value
public class TemporaryFolderPreserver {

    @Builder(buildMethodName = "preserve")
    @SneakyThrows
    public TemporaryFolderPreserver(
            Class<?> caller,
            TemporaryFolder tempFolder,
            String filename) {

        val outputFile = new File(String.format("build/test-outputs/%s/%s",
                caller.getSimpleName(),
                filename));

        Files.createDirectories(outputFile.getParentFile().toPath());

        if (outputFile.exists()) {
            outputFile.delete();
        }

        Files.copy(
                tempFolder.getRoot().toPath(),
                outputFile.toPath());
    }
}
