// Parser driver for demos.
// No warranty; no copyright -- use this as you will.

package demo.parser;

import java.io.*;

import com.arthurdo.parser.*;
import org.junit.Assert;
import org.junit.Test;

public class ParserTestMinusDuplication
{
    @Test
    public void test4() {
		String html =
			"</html>\n"+
			"<?XML version=\"1.0\" encoding=\"UTF8\" ?>\n"+
			"<!DOCTYPE>\n"+
			"<IMG/>\n"+
			"<IMG src=a.gif / >\n"+
			"";

        String expected =
                "</html>\n" +
                "\n" +
                "invalid: <?XML version=\"1.0\" encoding=\"UTF8\" ?>\n" +
                "\n" +
                "DOCTYPE\n" +
                "EMPTY <IMG />\n" +
                "EMPTY <IMG src=\"a.gif\" />\n";

        String collected = tokenizeAndCollect(html);
        Assert.assertEquals(expected, collected);
	}

    @Test
    public void test3() {

        String html =
            "<P><B>Please contact Public Affairs Division for the text of the paper	" +
            "&amp; the authors for further comment</B> </P> " +
            "a &amp; b" +
            "a AT&T&amp b" +
            "a &amp&amp b" +
            "a &lt&amp b" +
            "a &amp&gt b" +
            "";

        String expected = "<P><B>Please contact Public Affairs Division for the text of the paper\t& the authors for further comment</B> </P> a & ba AT&T&ba &&ba <&ba &>b";

        String collected = tokenizeAndCollect(html);
        Assert.assertEquals(expected, collected);
	}

    @Test
    public void test2() {
        String html =
            "<INPUT TYPE=HIDDEN NAME=\"MfcISAPICommand\" VALUE = \"MailBox\">\n"+
            "<applet code=\"htmlstreamer.class\" codebase=\"/classes/demo/parser\"\nalign=\"baseline\" width=\"500\" height=\"800\"\nid=\"htmlstreamer\">\n"+
            "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML//EN\" id=foo disabled>"+
            "";

        String expected =
                "<INPUT TYPE=\"HIDDEN\" NAME=\"MfcISAPICommand\" VALUE=\"MailBox\">\n" +
                        "<applet code=\"htmlstreamer.class\" codebase=\"/classes/demo/parser\" align=\"baseline\" width=\"500\" height=\"800\" id=\"htmlstreamer\">\n" +
                        "DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML//EN\" id=foo disabled";

        String collected = tokenizeAndCollect(html);
        Assert.assertEquals(expected, collected);
	}

    @Test
    public void test1() {
        String html =
            "<p><UL><LI>    left	terms and list operators (leftward)</LI>\n"+
            "<LI>    left	-></LI>\n"+
            "<LI>    nonassoc	++ --</LI>\n"+
            "<LI>    right	**</LI>\n"+
            "<LI>    right	! ~ \\ and unary + and -</LI>\n"+
            "<LI>    left	=~ !~ </LI>\n"+
            "<LI>    left	* / % x</LI>\n"+
            "<LI>    left	+ - .</LI>\n"+
            "<LI>    left	<< >></LI>\n"+
            "<LI>    nonassoc	named unary operators</LI>\n"+
            "<LI>    nonassoc	< > <= >= lt gt le ge</LI>\n"+
            "<LI>    nonassoc	== != <=> eq ne cmp</LI>\n"+
            "<LI>    left	&</LI>\n"+
            "<LI>    left	| ^</LI>\n"+
            "<LI>    left	&&</LI>\n"+
            "<LI>    left	||</LI>\n"+
            "<LI>    nonassoc	..</LI>\n"+
            "<LI>    right	?:</LI>\n"+
            "<LI>    right	= += -= *= etc.</LI>\n"+
            "<LI>    left	, =></LI>\n"+
            "<LI>    nonassoc	list operators (rightward)</LI>\n"+
            "<LI>    left	not</LI>\n"+
            "<LI>    left	and</LI>\n"+
            "<LI>    left	or xor</LI>\n"+
            "</UL>\n"+
            "<a href=\"/cgi-bin/query?opt&\">"+
            "";

        String expected =
                "<p><UL><LI>    left\tterms and list operators (leftward)</LI>\n" +
                        "<LI>    left\t-></LI>\n" +
                        "<LI>    nonassoc\t++ --</LI>\n" +
                        "<LI>    right\t**</LI>\n" +
                        "<LI>    right\t! ~ \\ and unary + and -</LI>\n" +
                        "<LI>    left\t=~ !~ </LI>\n" +
                        "<LI>    left\t* / % x</LI>\n" +
                        "<LI>    left\t+ - .</LI>\n" +
                        "<LI>    left\t<< >></LI>\n" +
                        "<LI>    nonassoc\tnamed unary operators</LI>\n" +
                        "<LI>    nonassoc\t\n" +
                        "invalid: < >\n" +
                        " \n" +
                        "invalid: <= >\n" +
                        "= lt gt le ge</LI>\n" +
                        "<LI>    nonassoc\t== != \n" +
                        "invalid: <=>\n" +
                        " eq ne cmp</LI>\n" +
                        "<LI>    left\t</LI>\n" +
                        "<LI>    left\t| ^</LI>\n" +
                        "<LI>    left\t&</LI>\n" +
                        "<LI>    left\t||</LI>\n" +
                        "<LI>    nonassoc\t..</LI>\n" +
                        "<LI>    right\t?:</LI>\n" +
                        "<LI>    right\t= += -= *= etc.</LI>\n" +
                        "<LI>    left\t, =></LI>\n" +
                        "<LI>    nonassoc\tlist operators (rightward)</LI>\n" +
                        "<LI>    left\tnot</LI>\n" +
                        "<LI>    left\tand</LI>\n" +
                        "<LI>    left\tor xor</LI>\n" +
                        "</UL>\n" +
                        "<a href=\"/cgi-bin/query?opt&\">";

        String collected = tokenizeAndCollect(html);
        Assert.assertEquals(expected, collected);
	}

    private static String tokenizeAndCollect(String html) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream out = new PrintStream(buffer);
            Reader in = new StringReader(html);
            HtmlStreamTokenizer tok = new HtmlStreamTokenizer(in);
            HtmlTag tag = new HtmlTag();
            while (tok.nextToken() != HtmlStreamTokenizer.TT_EOF)
            {
                StringBuffer buf = tok.getWhiteSpace();
                if (buf.length() > 0)
                    out.print(buf.toString());

                if (tok.getTokenType() == HtmlStreamTokenizer.TT_TAG)
                {
                    try
                    {
                        tok.parseTag(tok.getStringValue(), tag);
                        if (tag.getTagType() == HtmlTag.T_UNKNOWN)
                            throw new HtmlException("unkown tag");
                        if (tag.isEmpty())
                            out.print("EMPTY ");
                        out.print(tag.toString());
                    }
                    catch (HtmlException e)
                    {
                        // invalid tag, spit it out
                        out.print("\ninvalid: ");
                        out.print("<" + tok.getStringValue() + ">");
                        out.print("\n");
                    }
                }
                else
                {
                    buf = tok.getStringValue();
                    HtmlStreamTokenizer.unescape(buf);
                    if (buf.length() > 0)
                        out.print(buf.toString());
                }
            }
            return buffer.toString();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
