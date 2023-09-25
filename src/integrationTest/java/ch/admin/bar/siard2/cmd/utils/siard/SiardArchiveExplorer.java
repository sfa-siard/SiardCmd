package ch.admin.bar.siard2.cmd.utils.siard;

import ch.admin.bar.siard2.cmd.utils.siard.model.SiardMetadata;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import lombok.SneakyThrows;
import lombok.val;

import java.io.File;

public class SiardArchiveExplorer {

    private final XmlMapper xmlMapper = new XmlMapper();

    private final File pathToArchive;
    private final File pathToUnzipedArchive;

    @SneakyThrows
    public SiardArchiveExplorer(final File pathToArchive) {
        this.pathToArchive = pathToArchive;

        val unziper = new Unziper(pathToArchive);
        this.pathToUnzipedArchive = unziper.unzip();

        xmlMapper
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        xmlMapper.registerModule(new Jdk8Module());
    }

    @SneakyThrows
    public SiardMetadata exploreMetadata() {
        val metadataFile = findMetadataFile();
        val metadata = xmlMapper.readValue(metadataFile, SiardMetadata.class);

        return metadata;
    }

    public File findMetadataFile() {
        val metadataFile = new File(pathToUnzipedArchive + "/header/metadata.xml");

        if (!metadataFile.exists()) {
            throw new UnsupportedOperationException(String.format("Illegal SIARD archive, %s not found", "/header/metadata.xml")); // TODO
        }

        return metadataFile;
    }

}
