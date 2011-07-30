package com.arthurdo.parser;

import java.util.Hashtable;

import static com.arthurdo.parser.HtmlUtils.isPunct;
import static com.arthurdo.parser.HtmlUtils.isSpace;

public class HtmlEscaping {

    static Hashtable m_escapes = new Hashtable();
    
    static {
        m_escapes.put(new String("Aacute"), new Character('\u00c1'));
        m_escapes.put(new String("aacute"), new Character('\u00e1'));
        m_escapes.put(new String("Acirc"), new Character('\u00c2'));
        m_escapes.put(new String("acirc"), new Character('\u00e2'));
        m_escapes.put(new String("AElig"), new Character('\u00c6'));
        m_escapes.put(new String("aelig"), new Character('\u00e6'));
        m_escapes.put(new String("Agrave"), new Character('\u00c0'));
        m_escapes.put(new String("agrave"), new Character('\u00e0'));
        m_escapes.put(new String("amp"), new Character('&'));
        m_escapes.put(new String("aring"), new Character('\u00e5'));
        m_escapes.put(new String("Atilde"), new Character('\u00c3'));
        m_escapes.put(new String("atilde"), new Character('\u00e3'));
        m_escapes.put(new String("Auml"), new Character('\u00c4'));
        m_escapes.put(new String("auml"), new Character('\u00e4'));
        m_escapes.put(new String("brvbar"), new Character('\u00a6'));
        m_escapes.put(new String("Ccedil"), new Character('\u00c7'));
        m_escapes.put(new String("ccedil"), new Character('\u00e7'));
        m_escapes.put(new String("cent"), new Character('\u00a2'));
        m_escapes.put(new String("copy"), new Character('\u00a9'));
        m_escapes.put(new String("deg"), new Character('\u00b0'));
        m_escapes.put(new String("Eacute"), new Character('\u00c9'));
        m_escapes.put(new String("eacute"), new Character('\u00e9'));
        m_escapes.put(new String("Ecirc"), new Character('\u00ca'));
        m_escapes.put(new String("ecirc"), new Character('\u00ea'));
        m_escapes.put(new String("Egrave"), new Character('\u00c8'));
        m_escapes.put(new String("egrave"), new Character('\u00e8'));
        m_escapes.put(new String("ETH"), new Character('\u00d0'));
        m_escapes.put(new String("eth"), new Character('\u00f0'));
        m_escapes.put(new String("Euml"), new Character('\u00cb'));
        m_escapes.put(new String("euml"), new Character('\u00eb'));
        m_escapes.put(new String("frac12"), new Character('\u00bd'));
        m_escapes.put(new String("frac14"), new Character('\u00bc'));
        m_escapes.put(new String("frac34"), new Character('\u00be'));
        m_escapes.put(new String("gt"), new Character('>'));
        m_escapes.put(new String("iacute"), new Character('\u00ed'));
        m_escapes.put(new String("Icirc"), new Character('\u00ce'));
        m_escapes.put(new String("icirc"), new Character('\u00ee'));
        m_escapes.put(new String("iexcl"), new Character('\u00a1'));
        m_escapes.put(new String("Igrave"), new Character('\u00cc'));
        m_escapes.put(new String("igrave"), new Character('\u00ec'));
        m_escapes.put(new String("iquest"), new Character('\u00bf'));
        m_escapes.put(new String("Iuml"), new Character('\u00cf'));
        m_escapes.put(new String("iuml"), new Character('\u00ef'));
        m_escapes.put(new String("laquo"), new Character('\u00ab'));
        m_escapes.put(new String("lt"), new Character('<'));
        m_escapes.put(new String("middot"), new Character('\u00b7'));
        m_escapes.put(new String("nbsp"), new Character('\u00A0'));
        m_escapes.put(new String("not"), new Character('\u00ac'));
        m_escapes.put(new String("Ntilde"), new Character('\u00d1'));
        m_escapes.put(new String("ntilde"), new Character('\u00f1'));
        m_escapes.put(new String("Oacute"), new Character('\u00d3'));
        m_escapes.put(new String("oacute"), new Character('\u00f3'));
        m_escapes.put(new String("Ocirc"), new Character('\u00d4'));
        m_escapes.put(new String("ocirc"), new Character('\u00f4'));
        m_escapes.put(new String("Ograve"), new Character('\u00d2'));
        m_escapes.put(new String("ograve"), new Character('\u00f2'));
        m_escapes.put(new String("Oslash"), new Character('\u00d8'));
        m_escapes.put(new String("oslash"), new Character('\u00f8'));
        m_escapes.put(new String("Otilde"), new Character('\u00d5'));
        m_escapes.put(new String("otilde"), new Character('\u00f5'));
        m_escapes.put(new String("Ouml"), new Character('\u00d6'));
        m_escapes.put(new String("ouml"), new Character('\u00f6'));
        m_escapes.put(new String("para"), new Character('\u00b6'));
        m_escapes.put(new String("plusmn"), new Character('\u00b1'));
        m_escapes.put(new String("pound"), new Character('\u00a3'));
        m_escapes.put(new String("quot"), new Character('"'));
        m_escapes.put(new String("reg"), new Character('\u00ae'));
        m_escapes.put(new String("sect"), new Character('\u00a7'));
        m_escapes.put(new String("sup1"), new Character('\u00b9'));
        m_escapes.put(new String("sup2"), new Character('\u00b2'));
        m_escapes.put(new String("sup3"), new Character('\u00b3'));
        m_escapes.put(new String("szlig"), new Character('\u00df'));
        m_escapes.put(new String("THORN"), new Character('\u00de'));
        m_escapes.put(new String("thorn"), new Character('\u00fe'));
        m_escapes.put(new String("Uacute"), new Character('\u00da'));
        m_escapes.put(new String("uacute"), new Character('\u00fa'));
        m_escapes.put(new String("Ucirc"), new Character('\u00db'));
        m_escapes.put(new String("ucirc"), new Character('\u00fb'));
        m_escapes.put(new String("Ugrave"), new Character('\u00d9'));
        m_escapes.put(new String("ugrave"), new Character('\u00f9'));
        m_escapes.put(new String("Uuml"), new Character('\u00dc'));
        m_escapes.put(new String("uuml"), new Character('\u00fc'));
        m_escapes.put(new String("Yacute"), new Character('\u00dd'));
        m_escapes.put(new String("yacute"), new Character('\u00fd'));
        m_escapes.put(new String("yen"), new Character('\u00a5'));
        m_escapes.put(new String("yuml"), new Character('\u00ff'));
        
    }

    /**
     * Replaces HTML escape sequences with its character equivalent, e.g.
     * <b>&amp;amp;copy;</b> becomes <b>&amp;copy;</b>.
     *
     * @param	buf  text buffer to unescape
     * @return	a string with all HTML escape sequences removed
     */
    public static String unescape(String buf)
    {
        // quick check to see if there are any escape characters
        if (buf.indexOf('&') == -1)
            return buf;

        StringBuffer b = new StringBuffer(buf);
        unescape(b);
        return b.toString();
    }


    /**
     * Replaces HTML escape sequences with its character equivalent, e.g.
     * <b>&amp;copy;</b> becomes <b>&copy;</b>.
     *
     * @param	buf  will remove all HTML escape sequences from this buffer
     */
    public static void unescape(StringBuffer buf)
    {
        int len = buf.length();
        int i = 0;
        int r = i;
        while (i<len)
        {
            char ch = buf.charAt(i);
            if (ch == '&')
            {
                int saver = r;
                String esc = "";
                int j = i+1;
                for (; j<len; j++)
                {
                    buf.setCharAt(r++, ch);
                    ch = buf.charAt(j);
                    if (ch == ';' || ch == '<' || (isPunct(ch) && ch != '#') || isSpace(ch))
                    {
                        Character e = HtmlEscaping.parseEscape(esc);
                        if (e != null)
                        {
                            // found escape sequence
                            // as opposed to false or unrecognized escape, e.g. AT&T.
                            r = saver;
                            char v = e.charValue();
                            buf.setCharAt(r++, v);
                        }
                        i = j;
                        // this handles things like &lt&gt
                        if (ch != '&')
                            i++;	// if not '&' then discard char
                        break;
                    }
                    esc += ch;
                }
                if (j == len)
                {
                    Character e = HtmlEscaping.parseEscape(esc);
                    if (e != null)
                    {
                        r = saver;
                        buf.setCharAt(r++, e.charValue());
                    }
                    break;
                }
            }
            else
            {
                buf.setCharAt(r++, ch);
                i++;
            }
        }
        buf.setLength(r);
    }


    static Character parseEscape(String s)
    {
        int len = s.length();
        if (len == 0)
            return null;
        Character ch = null;

        if (s.charAt(0) == '#')
        {
            if (len <= 1)
                return null;

            int code = 0;
            for (int i=1; i<len; i++)
            {
                if (!Character.isDigit(s.charAt(i)))
                    return null;
                code = (code * 10) + Character.digit(s.charAt(i), 10);
            }
            ch = new Character((char)code);
        }
        else
        {
            ch = (Character) m_escapes.get(s);
        }

        return ch;
    }
}
