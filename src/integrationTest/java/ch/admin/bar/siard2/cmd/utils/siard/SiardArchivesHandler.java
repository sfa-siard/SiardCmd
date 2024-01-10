package ch.admin.bar.siard2.cmd.utils.siard;

import ch.admin.bar.siard2.cmd.utils.siard.model.FolderId;
import ch.admin.bar.siard2.cmd.utils.siard.model.SiardArchive;
import ch.admin.bar.siard2.cmd.utils.siard.model.SiardMetadata;
import ch.admin.bar.siard2.cmd.utils.siard.model.content.SiardContent;
import ch.admin.bar.siard2.cmd.utils.siard.model.content.Table;
import ch.admin.bar.siard2.cmd.utils.siard.model.content.TableContent;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ch.admin.bar.siard2.cmd.utils.TestResourcesResolver.resolve;

/**
 * This class is used for handling SIARD archives and their file paths. It supports two use cases:
 * - Exploring an existing SIARD archive. In this case, use {@link #prepareResource(String)}.
 * - Creating a temporary directory for downloading a new SIARD archive and exploring it.
 * In this case, use {@link #prepareEmpty()}.
 * <p>
 * Note: Annotate a {@link SiardArchivesHandler} instance always as {@link org.junit.Rule}
 */
public class SiardArchivesHandler extends ExternalResource {

    private final TemporaryFolder temporaryFolder = new TemporaryFolder();
    private final XmlMapper xmlMapper = new XmlMapper();

    private String testClassName;
    private String testName;

    @SneakyThrows
    public SiardArchivesHandler() {
        xmlMapper
                .registerModule(new Jdk8Module())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    }

    @SneakyThrows
    public SiardArchiveExplorer prepareResource(String resource) {
        final File pathToArchive = resolve(resource);
        final File pathToExtracted = temporaryFolder.newFolder();

        return createExplorer(pathToArchive, pathToExtracted);
    }

    @SneakyThrows
    public SiardArchiveExplorer prepareEmpty() {
        final File pathToArchive = temporaryFolder.newFolder();
        final File pathToExtracted = temporaryFolder.newFolder();

        return createExplorer(pathToArchive, pathToExtracted);
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        temporaryFolder.create();
    }

    @NotNull
    @Override
    public Statement apply(@NotNull Statement base, @NotNull Description description) {
        testClassName = description.getClassName();
        testName = description.getMethodName();

        return super.apply(base, description);
    }

    @Override
    protected void after() {
        super.after();
        temporaryFolder.delete();
    }

    private SiardArchiveExplorer createExplorer(final File pathToArchive, final File pathToExtracted) {
        return SiardArchiveExplorer.builder()
                .xmlMapper(xmlMapper)
                .pathToArchiveFile(pathToArchive)
                .pathToExtractingDirectory(pathToExtracted)
                .testClassName(testClassName)
                .testName(testName)
                .build();
    }

    @Builder
    @RequiredArgsConstructor
    public static class SiardArchiveExplorer {

        @NonNull
        private final XmlMapper xmlMapper;
        @NonNull
        @Getter
        private final File pathToArchiveFile;
        @NonNull
        private final File pathToExtractingDirectory;

        @NonNull
        private final String testClassName;
        @NonNull
        private final String testName;

        @SneakyThrows
        public SiardMetadata exploreMetadata() {
            if (!isArchiveAvailable()) {
                throw new IllegalStateException("No SIARD archive is available");
            }
            if (!isExtractedAvailable()) {
                extractArchive();
            }

            val metadataFile = findMetadataFile();
            return xmlMapper.readValue(metadataFile, SiardMetadata.class);
        }

        public SiardArchive explore() {
            val metadata = exploreMetadata();
            val content = exploreContent();

            return new SiardArchive(metadata, content);
        }

        private SiardContent exploreContent() {
            val tableXmlFiles = findTableXmlFiles();

            val tables = tableXmlFiles.stream()
                    .map(file -> {
                        val tableContent = deserialize(file.getFile(), TableContent.class);
                        return Table.builder()
                                .schemaFolder(file.getSchemaFolder())
                                .tableFolder(file.getTableFolder())
                                .tableContent(tableContent)
                                .build();

                    })
                    .collect(Collectors.toList());

            return new SiardContent(tables);
        }

        @SneakyThrows
        public SiardArchiveExplorer preserveArchive() {
            val filename = pathToArchiveFile.getName();

            val outputFile = new File(String.format("build/test-outputs/%s/%s/%s",
                    testClassName,
                    testName,
                    filename));

            Files.createDirectories(outputFile.getParentFile().toPath());

            if (outputFile.exists()) {
                outputFile.delete();
            }

            Files.copy(
                    pathToArchiveFile.toPath(),
                    outputFile.toPath());

            return this;
        }

        @SneakyThrows
        private <T> T deserialize(final File file, final Class<T> clazz) {
            return xmlMapper.readValue(file, clazz);
        }

        private File findMetadataFile() {
            return findFile("/header/metadata.xml");
        }

        @Value
        @Builder
        private static class TableXmlFile {
            @NonNull FolderId schemaFolder;
            @NonNull FolderId tableFolder;
            @NonNull File file;
        }

        private List<TableXmlFile> findTableXmlFiles() {
            val files = Arrays.stream(findFile("/content").listFiles())
                    .filter(File::isDirectory)
                    .flatMap(schemaDir -> Arrays.stream(schemaDir.listFiles()))
                    .filter(File::isDirectory)
                    .flatMap(tableDir -> Arrays.stream(tableDir.listFiles()))
                    .filter(file -> file.getName().endsWith(".xml"))
                    .collect(Collectors.toList());

            return files.stream()
                    .map(file -> {
                        val tableDir = file.getParentFile();
                        val schemaDir = tableDir.getParentFile();

                        return TableXmlFile.builder()
                                .schemaFolder(FolderId.of(schemaDir.getName()))
                                .tableFolder(FolderId.of(tableDir.getName()))
                                .file(file)
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        private File findFile(String fileInSiardArchive) {
            val metadataFile = new File(pathToExtractingDirectory + fileInSiardArchive);

            if (!metadataFile.exists()) {
                throw new UnsupportedOperationException(String.format(
                        "Illegal SIARD archive, %s not found",
                        fileInSiardArchive));
            }

            return metadataFile;
        }

        private boolean isArchiveAvailable() {
            return pathToArchiveFile.exists();
        }

        private boolean isExtractedAvailable() {
            val children = pathToExtractingDirectory.list();
            if (children == null) {
                throw new IllegalStateException("The 'Path to extracting location' does not refer to a valid directory.");
            }

            return children.length > 0;
        }

        private void extractArchive() {
            try {
                new Unzipper(pathToArchiveFile, pathToExtractingDirectory)
                        .unzip();
            } catch (IOException e) {
                throw new RuntimeException("Failed to unzip " + pathToArchiveFile, e);
            }
        }
    }
}
