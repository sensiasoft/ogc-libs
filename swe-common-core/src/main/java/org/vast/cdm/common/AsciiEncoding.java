/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "SensorML DataProcessing Engine".
 
 The Initial Developer of the Original Code is the VAST team at the
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.cdm.common;

import java.io.Serializable;


public class AsciiEncoding implements DataEncoding, Serializable
{
	private static final long serialVersionUID = 705357395103964244L;	
    public String blockSeparator;
	public String tokenSeparator;
	public char decimalSeparator = '.';
	public boolean collapseWhiteSpaces = true;
	
	
    public AsciiEncoding()
    {        
    }
    
    
    public AsciiEncoding(String blockSeparator, String tokenSeparator)
    {
        this.blockSeparator = blockSeparator;
        this.tokenSeparator = tokenSeparator;
    }
    
    
	public EncodingType getEncodingType()
	{
		return EncodingType.ASCII;
	}
	
	
	public String toString()
	{
		return "Ascii Encoding:\n" +
		       "  decimal separator = '" + decimalSeparator + "'\n" +
		       "  token separator = '" + tokenSeparator + "'\n" +
		       "  block separator = '" + blockSeparator + "'\n";
	}
}