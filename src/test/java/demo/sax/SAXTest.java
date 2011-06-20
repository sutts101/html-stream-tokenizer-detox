// SAX driver for demos.
// No warranty; no copyright -- use this as you will.

package demo.sax;

import java.io.*;

import demo.sax.DemoHandler;
import org.xml.sax.*;
import com.arthurdo.sax.*;

/**
 * Sample program that uses com.arthurdo.SAXDriver (SAX parser)
 * and a simple handler (DemoHandler) that does nothing more
 * than print out the SAX events.
 */
public class SAXTest
{
	public static void main(String[] args)
	{
		try
		{
			SAXDriver parser = new SAXDriver(new DemoHandler());
			parser.parse("article1.html");
		}
		catch (IOException e)
		{
			System.out.println("error: " + e.getMessage());
		}
		catch (SAXException e)
		{
			System.out.println("error: " + e.getMessage());
		}
	}
}
