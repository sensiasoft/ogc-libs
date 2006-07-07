/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "SensorML DataProcessing Engine".
 
 The Initial Developer of the Original Code is the
 University of Alabama in Huntsville (UAH).
 Portions created by the Initial Developer are Copyright (C) 2006
 the Initial Developer. All Rights Reserved.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.ows.tml;

import java.io.*;

import org.w3c.dom.*;
//import org.vast.io.xml.*;
//import org.vast.util.*;
import org.vast.ows.SweDataWriter;
import org.vast.ows.SweResultWriter;
import org.vast.ows.util.TimeInfo;


/**
 * <p><b>Title:</b><br/> ObservationWriter</p>
 *
 * <p><b>Description:</b><br/>
 * TODO ObservationWriter type description
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @version 1.0
 */
public class TMLWriter extends SweResultWriter
{
	SweDataWriter resultWriter;
	
	
	public TMLWriter()
	{		
	}
	
	
	public void setDataWriter(SweDataWriter dataWriter)
	{
		this.resultWriter = dataWriter;
	}
	
	
	/**
	 * Change eventTime element in the DOM to contain the request times
	 * @param time
	 */
	public void setTime(TimeInfo time)
	{
		/*
		DOMReader templates = null;
		
		try
		{
			// preload templates doc
			InputStream templateFile = TMLWriter.class.getResourceAsStream("templates.xml");
			templates = new DOMReader(templateFile, false);
		}
		catch (DOMReaderException e)
		{
			e.printStackTrace();
		}
		
		// keep pointers to needed nodes
		NodeList eventTimes = templates.getRootElement().getElementsByTagName("eventTime");
		
		for (int i=0; i<eventTimes.getLength(); i++)
		{
			Element eventTime = (Element)eventTimes.item(i);
			Element timeInstantElt = (Element)respDocument.importNode(templates.getElement("TimeInstant"), true);
			Element timePeriodElt = (Element)respDocument.importNode(templates.getElement("TimePeriod"), true);
			Text timeText = (Text)respXML.getElement(timeInstantElt, "timePosition").getFirstChild();
			Text beginText = (Text)respXML.getElement(timePeriodElt, "beginPosition").getFirstChild();
			Text endText = (Text)respXML.getElement(timePeriodElt, "endPosition").getFirstChild();
						
			// erase old time parameters
			if (eventTime.hasChildNodes())
				eventTime.removeChild(eventTime.getFirstChild());	
			
			// TimeInstant case
			if (time.startTime == time.stopTime)
			{
				timeText.setData(DateTimeFormat.formatIso(time.startTime, -1));
				eventTime.appendChild(timeInstantElt);
			}
			
			// TimePeriod case
			else
			{
				beginText.setData(DateTimeFormat.formatIso(time.startTime, -1));
				endText.setData(DateTimeFormat.formatIso(time.stopTime, -1));
				eventTime.appendChild(timePeriodElt);
			}
		}
		*/
	}
	
	
	protected void serializeElement(Element elt) throws IOException
	{
		if (elt.getLocalName().equals("tmlData"))
		{
			this._printer.printText("\n<tmlData>");
			this._printer.flush();
			
			this.writeResultData(elt);
			
			this._printer.printText("</tmlData>");
			this._printer.flush();
		}
		else
		{
			super.serializeElement(elt);
		}
	}
	
	
	protected void writeResultDefinition(Element elt)
	{
		
	}
	
	
	protected void writeResultEncoding(Element elt)
	{
		
	}
	
	
	protected void writeResultData(Element elt) throws IOException
	{
		resultWriter.write();
	}
}
