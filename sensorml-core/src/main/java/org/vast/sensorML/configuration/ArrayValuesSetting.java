/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Initial Developer of the Original Code is SENSIA SOFTWARE LLC.
 Portions created by the Initial Developer are Copyright (C) 2012
 the Initial Developer. All Rights Reserved.

 Please Contact Alexandre Robin <alex.robin@sensiasoftware.com> for more
 information.
 
 Contributor(s): 
    Alexandre Robin <alex.robin@sensiasoftware.com>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.sensorML.configuration;

import org.vast.cdm.common.DataEncoding;


public class ArrayValuesSetting extends AbstractSetting
{
    protected DataEncoding encoding;
    protected String values;


    public DataEncoding getEncoding()
    {
        return encoding;
    }


    public void setEncoding(DataEncoding encoding)
    {
        this.encoding = encoding;
    }


    public String getValues()
    {
        return values;
    }


    public void setValues(String values)
    {
        this.values = values;
    }
}
