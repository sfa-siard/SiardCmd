package ch.admin.bar.siard2.cmd.utils.siard.model.content;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DeserializationTest {

    private static final String EXAMPLE_TABLE_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<table xsi:schemaLocation=\"http://www.bar.admin.ch/xmlns/siard/2/table.xsd table3.xsd\" version=\"2.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.bar.admin.ch/xmlns/siard/2/table.xsd\">\n" +
            "\t<row>\n" +
            "\t\t<c1>10248</c1>\n" +
            "\t\t<c2>11</c2>\n" +
            "\t\t<c3>14.0000</c3>\n" +
            "\t\t<c4>12</c4>\n" +
            "\t\t<c5>0</c5>\n" +
            "\t</row>\n" +
            "\t<row>\n" +
            "\t\t<c1>10248</c1>\n" +
            "\t\t<c2>42</c2>\n" +
            "\t\t<c3>9.8000</c3>\n" +
            "\t\t<c4>10</c4>\n" +
            "\t\t<c5>0</c5>\n" +
            "\t</row>" +
            "</table>";

    @Test
    public void test() throws JsonProcessingException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper
                .registerModule(new Jdk8Module())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        val content = xmlMapper.readValue(EXAMPLE_TABLE_XML, TableContent.class);

        Assertions.assertThat(content).isEqualTo(TableContent.builder()
                .row(TableRow.builder()
                        .cell(new TableCell(1, "10248"))
                        .cell(new TableCell(2, "11"))
                        .cell(new TableCell(3, "14.0000"))
                        .cell(new TableCell(4, "12"))
                        .cell(new TableCell(5, "0"))
                        .build())
                .row(TableRow.builder()
                        .cell(new TableCell(1, "10248"))
                        .cell(new TableCell(2, "42"))
                        .cell(new TableCell(3, "9.8000"))
                        .cell(new TableCell(4, "10"))
                        .cell(new TableCell(5, "0"))
                        .build())

                .build());
    }
}
