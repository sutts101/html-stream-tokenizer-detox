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

package com.arthurdo.sax;

import java.util.*;
import java.net.*;
import java.io.*;

import org.xml.sax.*;

import com.arthurdo.parser.*;

/**
 * <p>A basic wrapper to translate HtmlStreamTokenizer parser
 * values into SAX events.
 *
 * @version 1.0 05/01/00
 * @author Arthur Do <arthur@arthurdo.com>
 */
public class SAXDriver implements Parser, Locator
{
	public SAXDriver()
	{
	}

	/**
	 * @param handler  a callback interface that receive parse events
	 */
	public SAXDriver(DocumentHandler handler)
	{
		m_docHandler = handler;
	}

	public String getPublicId()
	{
		return m_publicId;
	}

	public String getSystemId()
	{
		return m_systemId;
	}

	public int getLineNumber()
	{
		int line = 0;
		if (m_tok != null)
			line = m_tok.getLineNumber();
		return line;
	}

	public int getColumnNumber()
	{
		// TODO:
		return 0;
	}

	public void setLocale(Locale locale)
		throws SAXException
	{
		if (!locale.getCountry().equals(Locale.US.getCountry()))
			throw new SAXException("only US Locale is supported");
	}

	public void setEntityResolver(EntityResolver resolver)
	{
		m_entResolver = resolver;
	}

	public void setDTDHandler(DTDHandler handler)
	{
		m_dTDHandler = handler;
	}

	public void setDocumentHandler(DocumentHandler handler)
	{
		m_docHandler = handler;
	}

	public void setErrorHandler(ErrorHandler handler)
	{
		m_errHandler = handler;
	}

	public void parse(String systemId)
		throws SAXException, IOException
	{
		m_systemId = systemId;
		InputStream in = null;
		try
		{
			URL url = new URL(systemId);
			in = new BufferedInputStream(url.openStream());
		}
		catch (MalformedURLException e)
		{
			try
			{
				// try local file system
				in = new FileInputStream(systemId);
			}
			catch (IOException e1)
			{
				m_errHandler.fatalError(new SAXParseException(null, this, e1));
			}
		}
		// at this point 'in' is non-null
		parse(new HtmlStreamTokenizer(new InputStreamReader(in)));
	}

	public void parse(InputSource source)
		throws SAXException, IOException
	{
		InputStream in = source.getByteStream();
		if (in != null)
		{
			in = new BufferedInputStream(in);
			parse(new HtmlStreamTokenizer(new InputStreamReader(in)));
		}
		else
		{
			Reader rd = source.getCharacterStream();
			if (rd != null)
			{
				parse(new HtmlStreamTokenizer(rd));
			}
			else
			{
				String uri = source.getSystemId();
				if (uri != null)
				{
					parse(uri);
				}
				else
				{
					m_errHandler.fatalError(new SAXParseException("invalid input source", this));
				}
			}
		}
	}

	/**
	 * Let HtmlStreamTokenizer do the parsing and convert the return values
	 * into DocumentHandler events
	 */
	public void parse(HtmlStreamTokenizer tok)
		throws SAXException, IOException
	{
		m_tok = tok;
		m_docHandler.setDocumentLocator(this);
		m_docHandler.startDocument();
		try
		{
			SAXElement tag = new SAXElement();
			int ttype;
			while ((ttype = tok.nextToken()) != HtmlStreamTokenizer.TT_EOF)
			{
				// white space
				if (ttype != HtmlStreamTokenizer.TT_TEXT)
				{
					StringBuffer ws = tok.getWhiteSpace();
					final int len = ws.length();
					if (len != 0)
					{
						m_docHandler.characters(getCharArray(ws, len), 0, len);
					}
				}

				if (ttype == HtmlStreamTokenizer.TT_TEXT)
				{
					StringBuffer stringbuf = tok.getStringValue();
					StringBuffer whitespace = tok.getWhiteSpace();
					int wslen = whitespace.length();
					char[] dataBuf = new char[wslen + stringbuf.length()];
					if (wslen != 0)
						whitespace.getChars(0, wslen, dataBuf, 0);
					if (stringbuf.length() != 0)
						stringbuf.getChars(0, stringbuf.length(), dataBuf, wslen);

					m_docHandler.characters(dataBuf, 0, dataBuf.length);
				}
				else if (ttype == HtmlStreamTokenizer.TT_TAG)
				{
					StringBuffer text = tok.getStringValue();
					if (text.charAt(0) == '?')
					{
						String buf = text.toString().substring(1);
						final int len = buf.length();
						int idx = buf.indexOf(' ');
						if (idx == -1)
							idx = len;
						String instruction = buf.substring(0, idx);
						String attributes = "";
						if (idx < len)
							attributes = buf.substring(idx+1).trim();
						final int alen = attributes.length();
						if (alen > 0)
						{
							// strip off trailling '?'
							if (attributes.charAt(len-1) == '?')
								attributes = attributes.substring(0, len-1);
						}
						m_docHandler.processingInstruction(instruction, attributes);
					}
					else
					{
						try
						{
							tok.parseTag(text, tag);
							String tagName = tag.getTagString();
							if (tag.isEndTag())
								m_docHandler.endElement(tagName);
							else
							{
								m_docHandler.startElement(tagName, tag);
								if (tag.isEmpty())
									m_docHandler.endElement(tagName);
							}
						}
						catch (HtmlException e)
						{
							m_errHandler.error(new SAXParseException(null, this, e));
						}
					}
				}
				else if (ttype == HtmlStreamTokenizer.TT_BANGTAG)
				{
					// TODO:
				}
				else if (ttype == HtmlStreamTokenizer.TT_COMMENT)
				{
					// TODO:
				}
			}
			m_docHandler.endDocument();
		}
		finally
		{
			m_tok = null;
		}
	}

	private static char[] getCharArray(StringBuffer b, final int len)
	{
		char[] buf = new char[len];
		b.getChars(0, len, buf,  0);

		return buf;
	}

	private static HandlerBase defaultHandler = new HandlerBase();
	private DocumentHandler m_docHandler = defaultHandler;
	private ErrorHandler m_errHandler = defaultHandler;
	private EntityResolver m_entResolver = defaultHandler;
	private DTDHandler m_dTDHandler = defaultHandler;
	private String m_publicId;
	private String m_systemId;
	private HtmlStreamTokenizer m_tok;
}
