package com.arthurdo.parser;

public class HtmlUtils {

    /*package*/ static final char C_ENDTAG = '/';
    static final char C_EMPTY = '/';	// XML char for empty tags
    static final char C_SINGLEQUOTE = '\'';
    static final char C_DOUBLEQUOTE = '"';
    static final int CTYPE_LEN = 256;
    static byte[] m_ctype = new byte[CTYPE_LEN];
    static final byte CT_WHITESPACE = 1;
    private static final byte CT_DIGIT = 2;
    private static final byte CT_ALPHA = 4;
    private static final byte CT_QUOTE = 8;
    private static final byte CT_COMMENT = 16;
    
    
    static
    {
        int len = m_ctype.length;
        for (int i = 0; i < len; i++)
            m_ctype[i] = 0;

        m_ctype[' '] = CT_WHITESPACE;
        m_ctype['\r'] = CT_WHITESPACE;
        m_ctype['\n'] = CT_WHITESPACE;
        m_ctype['\t'] = CT_WHITESPACE;
        for (int i = 0x0E; i <= 0x1F; i++)
            m_ctype[i] = CT_WHITESPACE;

    }

    static boolean isSpace(int c)
    {
         return c >=0 && c < CTYPE_LEN ? (m_ctype[c] & CT_WHITESPACE) != 0: false;
    }

    static boolean isPunct(char c)
    {
        return !Character.isLetterOrDigit(c);
    }
}
