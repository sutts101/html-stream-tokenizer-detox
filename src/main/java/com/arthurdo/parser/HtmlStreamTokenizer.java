/*
 * Copyright (c) 1996, 2001 by Arthur Do <arthur@cs.stanford.edu>.
 * All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.arthurdo.parser;

import java.io.*;

/**
 * <p>HtmlStreamTokenizer is an HTML parser that is similar
 * to the StreamTokenizer class but is specialized for
 * HTML streams. This class is useful when you need to
 * parse the structure of an HTML document.</p>
 *
 * <pre>
 * import com.arthurdo.parser.*;
 * <p>
 * HtmlStreamTokenizer tok = new HtmlStreamTokenizer(inputstream);
 * HtmlTag tag = new HtmlTag();
 *
 * while (tok.nextToken() != HtmlStreamTokenizer.TT_EOF) {
 *	int ttype = tok.getTokenType();
 *
 *	if (ttype == HtmlStreamTokenizer.TT_TAG) {
 *		tok.parseTag(tok.getStringValue(), tag);
 *		System.out.println(&quot;tag: &quot; + tag.toString());
 *	}
 *	else if (ttype == HtmlStreamTokenizer.TT_TEXT) {
 *		System.out.println(&quot;text: &quot; + tok.getStringValue());
 *	}
 *	else if (ttype == HtmlStreamTokenizer.TT_COMMENT) {
 *		System.out.println(&quot;comment: &lt;!--&quot; + tok.getStringValue() + &quot;--&gt;&quot;);
 *	}
 * }
 * </pre>
 *
 * <p>One of the motivations for designing <i>parseTag()</i> to take
 * an HtmlTag argument rather than having <i>parseTag()</i> return
 * a newly created HtmlTag is so you can create your own tag class
 * derived from HtmlTag.
 *
 * <ul>
 * <li> 02/09/98 Thomas Horster-M�ller, fixed bug with counting
 * newlines twice on character pushback.
 * <li> 06/14/99 text is now returned as 'runs' instead of being
 * broken up into words as in previous versions. You can use a StringTokenizer
 * to break your text into words.
 * </ul>
 *
 * @version 2.01 09/12/97
 * @author Arthur Do <arthur@cs.stanford.edu>
 * @see     com.arthurdo.parser.HtmlTag
 * @see     com.arthurdo.parser.Table
 */
public class HtmlStreamTokenizer
{

    private int m_ttype;
	private StringBuffer m_buf = new StringBuffer(128);
	private StringBuffer m_whitespace = new StringBuffer();
	private int m_pushback = 0;
	private int m_lineno = 1;
	private int m_comment = 0;

	private char[] m_cdata_end = null;
	private int m_cdata = -1;
	private boolean m_cdata_pushback = false;
	private boolean m_isCDTATA = false;
	private static char[] m_xmlcdata_end = "]]>".toCharArray();

    private static final int STATE_EOF = -1;
    private static final int STATE_COMMENT = -2;
    private static final int STATE_TEXT = -3;
    private static final int STATE_TAG = -4;
    private static final int STATE_WS = -5;
	private static final int STATE_TAG_QUOTE = -6;
	private static final int STATE_BANGTAG = -7;
	private static final int STATE_ENTITYREF = -8;

	private int m_state = STATE_TEXT;

    private int m_tagquote;

	//private InputStream m_in;
	private Reader m_in; //input reader appears to be an order of magnitude slower than inputstream!

    private boolean m_unescape = false;
	private boolean m_getEntities = false; //return TT_ENTITYREFERENCE


	/**
	 * end of stream.
	 */
    public static final int TT_EOF = -1;
	/**
	 * text token.
	 */
    public static final int TT_TEXT = -2;
	/**
	 * tag token.
	 */
    public static final int TT_TAG = -3;
	/**
	 * comment token.
	 */
	public static final int TT_COMMENT = -4;

	/**
	 * inside <! to provide support for doctypes with internal dtd, <![CDATA sections, and degenerate html comments
	 */
	public static final int TT_BANGTAG = -5;

	/**
	 * entity reference token (&*;)
	 */
	public static final int TT_ENTITYREFERENCE = -6;

	/**
	 * @deprecated	use HtmlStreamTokenizer(Reader) instead.
	 *				This version of the constructor can lead to 10x slower code
	 *				because of the InputStreamReader wrapper.
	 * @param	in  input stream
	 */
	public HtmlStreamTokenizer(InputStream in)
	{
		this(new BufferedReader(new InputStreamReader(in)));
	}

	/**
	 * @param	in  Reader. The input is assumed to be buffered as needed.
	 */
	public HtmlStreamTokenizer(Reader in)
	{
		m_in = in;
		m_state = STATE_TEXT;
	}

    public boolean isUnescaped()
    {
        return m_unescape;
    }

    public void setUnescaped(boolean unescape)
    {
        m_unescape = unescape;
    }

	/**
	 * @return	the next token
     * @exception  IOException  if error reading input stream.
	 */
	public int nextToken()
		throws IOException
	{
		m_buf.setLength(0);
		m_whitespace.setLength(0);
		int ltcount = 0;
		m_isCDTATA = false;
		boolean hasAmp = false;

		while (true)
		{
			int c;

			if (m_pushback != 0)
			{
				c = m_pushback;
				if (c == '\n')
					m_lineno--;		// don't count newline twice
				m_pushback = 0;
			}
			else if (m_cdata < -1)
				c = m_cdata_end[m_cdata++ + m_cdata_end.length + 1];
			else
			{
				c = m_in.read();
			}

			if (c < 0)
			{
				int state = m_state;
				m_state = STATE_EOF;

				if (m_buf.length() > 0 && state == STATE_TEXT)
				{
					if (m_unescape && hasAmp)
						unescape(m_buf);
					return m_ttype = TT_TEXT;
				}
				else
					return m_ttype = TT_EOF;
			}

			if (c == '\n')
				m_lineno++;

			switch (m_state)
			{
			case STATE_TEXT:
				{
					if (m_cdata > -1)
					{
						//we're in cdata mode
						if ((Character.toUpperCase(m_cdata_end[m_cdata]) == c) ||
							(Character.toLowerCase(m_cdata_end[m_cdata]) == c)) // support case sensitive exit strings
						{
							if (++m_cdata == m_cdata_end.length)
							{
								if (m_cdata_pushback)
									m_cdata = -m_cdata_end.length -1;
								else
									m_cdata = -1;
								m_isCDTATA = true;
								return m_ttype = TT_TEXT;
							}
						}
						else
						{
							if (m_cdata > 0)
							{
								m_buf.append(m_cdata_end, 0, m_cdata);
								m_cdata = 0;
							}
							m_buf.append((char)c);
						}

					}
					else if (c == '<')
					{
						boolean inCDATApushback = m_cdata < -1;
						int peek = inCDATApushback ? m_cdata_end[m_cdata++  + m_cdata_end.length + 1] : m_in.read();

						if (peek == '!')
							m_state = STATE_BANGTAG;
						else if (peek == '<')
						{
							// handle <<, some people use it in <pre>
							m_buf.append("<<");
							break;
						}
						else
						{
							m_pushback = peek;
							if (inCDATApushback)
								--m_cdata;
							m_state = STATE_TAG;
						}

						if (m_buf.length() > 0)
						{
							if (m_unescape && hasAmp)
								unescape(m_buf);
							return m_ttype = TT_TEXT;
						}
					}
					/*
					else if (isSpace(c))
					{
						m_pushback = c;
						m_state = STATE_WS;
						if (m_buf.length() > 0)
						{
							if (m_unescape && hasAmp)
								unescape(m_buf);
							return m_ttype = TT_TEXT;
						}
					}
					*/
					else
					{
						if (c == '&')
						{
							if (m_getEntities)
							{
								m_state = STATE_ENTITYREF;
								return m_ttype = TT_TEXT;
							}
							else
								hasAmp = true;
						}
						m_buf.append((char)c);
					}
				}
				break;

			case STATE_WS:
				{
					if (!HtmlUtils.isSpace(c))
					{
						m_pushback = c;
						m_state = STATE_TEXT;
					}
					else
					{
						m_whitespace.append((char)c);
					}
				}
				break;

			case STATE_TAG:
				{
					if (c == '>')
					{
						m_state = STATE_TEXT;
						return m_ttype = TT_TAG;
					}
					else if (c == HtmlUtils.C_SINGLEQUOTE || c == HtmlUtils.C_DOUBLEQUOTE)
					{
						// handle quotes inside tag
						m_tagquote = c;
						m_buf.append((char)c);
						m_state = STATE_TAG_QUOTE;
					}
					else
					{
						m_buf.append((char)c);
					}
				}
				break;
			case STATE_BANGTAG:
				{
					int buflen = m_buf.length();
					if (c == '<')
					{
						++ltcount;
						m_buf.append((char)c);
					}
					else if (c == '>' && --ltcount < 0)
					{
						m_state = STATE_TEXT;
						return m_ttype = TT_BANGTAG;
					}
				    else if (c == '-' && buflen == 1 && m_buf.charAt(0) == '-')
					{
						// handle <!--
						m_buf.setLength(0);
						m_state = STATE_COMMENT;
					}
					else if (buflen == 6 && c == '[' && m_buf.toString().equals("[CDATA[") )
					{
						// handle <![CDATA[
						m_buf.setLength(0);
						enterCDATAMode(m_xmlcdata_end, false);
						m_state = STATE_TEXT;
					}
					else
					{
						m_buf.append((char)c);
					}
				}
				break;
			case STATE_TAG_QUOTE:
				{
					// the only way out out of this state is to close the quote
					// special case: some people forget to end quote in a tag
					if (c == '>')
					{
						m_pushback = c;
						m_state = STATE_TAG;
					}
					else
					{
						m_buf.append((char)c);
						if (c == m_tagquote)
						{
							m_state = STATE_TAG;
						}
					}
				}
				break;

			case STATE_COMMENT:
				{
					if (c == '>' && m_comment >= 2)
					{
						m_buf.setLength(m_buf.length() - 2);
						m_comment = 0;
						m_state = STATE_TEXT;
						return m_ttype = TT_COMMENT;
					}
					else if (c == '-')
					{
						m_comment++;
					}
					else
					{
						m_comment = 0;
					}

					m_buf.append((char)c);
				}
				break;
			case STATE_ENTITYREF:
				{
					if (c == ';' || c == '<' || (HtmlUtils.isPunct((char) c) && c != '#') || HtmlUtils.isSpace(c)) //accept any of these as terminating the entity
					{
						if (c != ';')
							m_pushback = c;
						m_state = STATE_TEXT;
						return m_ttype = TT_ENTITYREFERENCE;
					}
					m_buf.append((char)c);
				}
				break;
			}
		}
	}

    /**
     * @return	token type, one of the <b>TT_</b> defines
     */
    public final int getTokenType()
    {
        return m_ttype;
    }

    /**
     * @return	string value of the token
     */
    public final StringBuffer getStringValue()
    {
        return m_buf;
    }

    /**
     * @return string value of the token, including characters stripped off by the tokenizer
     */
    public final String getRawString()
    {
        switch (m_ttype)
        {
        case TT_TAG:
            return "<" + m_buf.toString() + ">";
        case TT_BANGTAG:
            return "<!" + m_buf.toString() + ">";
        case TT_COMMENT:
            return "<!--" + m_buf.toString() + "-->";
        case TT_ENTITYREFERENCE:
            return "&" + m_buf.toString() + ";";
        default:
            return m_buf.toString();
        }
    }

    /**
     * @deprecated	white space is now returned as TT_TEXT. This buffer is always
     *				empty.
     * @return	any white space accumulated since last call to nextToken
     */
    public final StringBuffer getWhiteSpace()
    {
        return m_whitespace;
    }

    /**
     * @return	current line number. Every time nextToken() sees a new
     *			line character ('\n'), it increments the line number.
     */
    public int getLineNumber()
    {
        return m_lineno;
    }

    /**
    * @param exitString CDATA mode will terminate when it encounters this string
     * @param pushbackExitString whether to parse the exit string again or not
     * it'd be an error to call enterCDATAMode(exitString, true); getToken()==TT_CDATA; enterCDATAMode(differentExitString, true); 'cause the next getToken() call will parse differentExitString instead of exitString
    */
    public void enterCDATAMode(char[] exitString, boolean pushbackExitString)
    {
        m_cdata_end = exitString;
        m_cdata = 0;
        m_cdata_pushback = pushbackExitString;
    }

    public boolean isCDATA() { return m_isCDTATA; }



    public void parseTag(StringBuffer sbuf, HtmlTag tag) throws HtmlException
    {
        HtmlTagParser.parseTag(sbuf, tag, m_unescape);
    }

    public static String unescape(String buf)
    {
        return HtmlEscaping.unescape(buf);
    }

    public static void unescape(StringBuffer buf)
    {
        HtmlEscaping.unescape(buf);
    }

}

