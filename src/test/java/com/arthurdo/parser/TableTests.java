package com.arthurdo.parser;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static junit.framework.Assert.*;

public class TableTests {

    @Test
    public void shouldReadSimpleTable() {

        String row1 = "<tr>  <td>a</td> <td>b</td>   </tr>";
        String row2 = "<tr>  <td>c</td> <td>d</td>   </tr>";
        String htmlTable = makeHtmlTable(row1, row2);

        Table table = readTable(htmlTable);

        assertEquals(2, table.getRows());
        assertEquals("a", table.elementAt(0, 0).getCharacterData());
        assertEquals("b", table.elementAt(0, 1).getCharacterData());
        assertEquals("c", table.elementAt(1, 0).getCharacterData());
        assertEquals("d", table.elementAt(1, 1).getCharacterData());
    }

    @Test
    public void shouldReadTableWithHeaderRow_ThoughItSeemsToSwallowTheHeaderCells() {

        String row1 = "<th>  <td>a</td> <td>b</td>   </th>";
        String row2 = "<tr>  <td>c</td> <td>d</td>   </tr>";
        String htmlTable = makeHtmlTable(row1, row2);

        Table table = readTable(htmlTable);

        assertEquals(1, table.getRows());
        assertEquals("c", table.elementAt(0, 0).getCharacterData());
        assertEquals("d", table.elementAt(0, 1).getCharacterData());
    }

    @Test
    public void shouldReadTableWithColSpan_ThoughReturnsEmptyStringOnNonExistentCell() {

        String row1 = "<tr>   <td>a</td> <td>b</td>   </tr>";
        String row2 = "<tr>   <td colspan=2>c</td>    </tr>";
        String htmlTable = makeHtmlTable(row1, row2);

        Table table = readTable(htmlTable);

        assertEquals(2, table.getRows());
        assertEquals("a", table.elementAt(0, 0).getCharacterData());
        assertEquals("b", table.elementAt(0, 1).getCharacterData());
        assertEquals("c", table.elementAt(1, 0).getCharacterData());
        assertEquals("", table.elementAt(1, 1).getCharacterData());
    }

    @Test
    public void tableTagIsNullWhenHtmlIsNotTable() {
         Table table = readTable("<p>Hallo world</p>");
         assertNull(table.getTableTag());
    }

    @Test(expected = NullPointerException.class)
    public void getRowsExplodesWithHtmlWotAintATable() {
         Table table = readTable("<p>Hallo world</p>");
         table.getRows();
    }

    private Table readTable(String html) {
        try {
            Table table = new Table();
            table.parseTable(new StringReader(html));
            return table;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String makeHtmlTable(String... rows) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        for (String row : rows) {
            sb.append(row);
        }
        sb.append("</table>");
        return sb.toString();
    }

}
