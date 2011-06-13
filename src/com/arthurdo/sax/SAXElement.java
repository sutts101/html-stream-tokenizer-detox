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

import org.xml.sax.*;
import com.arthurdo.parser.*;

/**
 * <p>A basic wrapper to translate HtmlTag calls to AttributeList calls.
 *
 * @version 1.0 05/01/00
 * @author Arthur Do <arthur@arthurdo.com>
 */
public class SAXElement extends HtmlTag implements AttributeList
{
	public SAXElement()
	{
	}

	public int getLength()
	{
		return getParamCount();
	}

	public String getName(int i)
	{
		return getParamName(i);
	}

	public String getType(int i)
	{
		return "CDATA";
	}

	public String getValue(int i)
	{
		return getParamValue(i);
	}

	public String getType(String name)
	{
		return "CDATA";
	}

	public String getValue(String name)
	{
		return getParam(name);
	}
}
