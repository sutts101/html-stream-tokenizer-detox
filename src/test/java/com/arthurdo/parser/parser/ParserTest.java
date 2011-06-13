// Parser driver for demos.
// No warranty; no copyright -- use this as you will.

package com.arthurdo.parser.parser;

import java.io.*;

import com.arthurdo.parser.*;

public class ParserTest
{
	public static void main(String[] args) throws Exception
	{
        test3();
	}

	static void test4()
		throws HtmlException, IOException
	{
		Reader in = new StringReader(
			"</html>\n"+
			"<?XML version=\"1.0\" encoding=\"UTF8\" ?>\n"+
			"<!DOCTYPE>\n"+
			"<IMG/>\n"+
			"<IMG src=a.gif / >\n"+
			""
			);

        tokenizeAndPrintOutputs(in);
	}

    static void test3() throws IOException {

        Reader in = new StringReader(
            "<P><B>Please contact Public Affairs Division for the text of the paper	" +
            "&amp; the authors for further comment</B> </P> " +
            "a &amp; b" +
            "a AT&T&amp b" +
            "a &amp&amp b" +
            "a &lt&amp b" +
            "a &amp&gt b" +
            ""
            );

        tokenizeAndPrintOutputs(in);
	}

	static void test2() throws IOException {
        Reader in = new StringReader(
            "<INPUT TYPE=HIDDEN NAME=\"MfcISAPICommand\" VALUE = \"MailBox\">\n"+
            "<applet code=\"htmlstreamer.class\" codebase=\"/classes/demo/parser\"\nalign=\"baseline\" width=\"500\" height=\"800\"\nid=\"htmlstreamer\">\n"+
            "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML//EN\" id=foo disabled>"+
            ""
            );

        tokenizeAndPrintOutputs(in);
	}

	static void test1() throws IOException {
        Reader in = new StringReader(
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
            ""
            );

        tokenizeAndPrintOutputs(in);
	}

    private static void tokenizeAndPrintOutputs(Reader in) throws IOException {
        HtmlStreamTokenizer tok = new HtmlStreamTokenizer(in);
        HtmlTag tag = new HtmlTag();
        while (tok.nextToken() != HtmlStreamTokenizer.TT_EOF)
        {
            StringBuffer buf = tok.getWhiteSpace();
            if (buf.length() > 0)
                System.out.print(buf.toString());

            if (tok.getTokenType() == HtmlStreamTokenizer.TT_TAG)
            {
                try
                {
                    tok.parseTag(tok.getStringValue(), tag);
                    if (tag.getTagType() == HtmlTag.T_UNKNOWN)
                        throw new HtmlException("unkown tag");
                    if (tag.isEmpty())
                        System.out.print("EMPTY ");
                    System.out.print(tag.toString());
                }
                catch (HtmlException e)
                {
                    // invalid tag, spit it out
                    System.out.print("\ninvalid: ");
                    System.out.print("<" + tok.getStringValue() + ">");
                    System.out.println("");
                }
            }
            else
            {
                buf = tok.getStringValue();
                HtmlStreamTokenizer.unescape(buf);
                if (buf.length() > 0)
                    System.out.print(buf.toString());
            }
        }
    }
}
