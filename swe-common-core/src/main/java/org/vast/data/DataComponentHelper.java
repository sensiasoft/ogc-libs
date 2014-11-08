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
 University of Alabama in Huntsville (UAH). <http://vast.uah.edu>
 Portions created by the Initial Developer are Copyright (C) 2007
 the Initial Developer. All Rights Reserved.

 Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Alexandre Robin <alexandre.robin@spotimage.com>
 
******************************* END LICENSE BLOCK ***************************/
package org.vast.data;

import net.opengis.swe.v20.AbstractDataComponent;
import org.vast.cdm.common.CDMException;
import org.vast.cdm.common.DataComponent;


/**
 * <p>
 * Provides helper methods to access components by name,
 * definition, full path, etc...
 * </p>
 *
 * <p>Copyright (c) 2008</p>
 * @author Alexandre Robin <alexandre.robin@spotimage.fr>
 * @since Mar, 11 2008
 * @version 1.0
 */
public class DataComponentHelper
{


	public static DataComponent findParameterByName(DataComponent parent, String name)
	{
		if (parent instanceof DataArrayImpl)
			parent = ((DataArrayImpl)parent).getArrayComponent();
		
		int childCount = parent.getComponentCount();
		for (int i=0; i<childCount; i++)
		{
			DataComponent child = parent.getComponent(i);
			String childName = child.getName();
			
			if (childName.equals(name))
				return child;
			
			// try to find it recursively!
			DataComponent desiredParam = findParameterByName(child, name);
			if (desiredParam != null)
				return desiredParam;
		}
		
		return null;
	}


	public static DataComponent findParameterByDefinition(DataComponent parent, String defUri)
	{
		if (parent instanceof DataArrayImpl)
			parent = ((DataArrayImpl)parent).getArrayComponent();
		
		int childCount = parent.getComponentCount();
		for (int i=0; i<childCount; i++)
		{
		    DataComponent child = parent.getComponent(i);
			String childDef = ((AbstractDataComponent)child).getDefinition();
			
			if (childDef != null && childDef.equals(defUri))
				return child;
			
			// try to find it recursively!
			DataComponent desiredParam = findParameterByDefinition(child, defUri);
			if (desiredParam != null)
				return desiredParam;
		}
		
		return null;
	}
	
	
	public static DataComponent findComponentByPath(String path, DataComponent parent) throws CDMException
	{
	    return findComponentByPath(path.split("/"), parent);
	}
	
	
	public static DataComponent findComponentByPath(String [] dataPath, DataComponent parent) throws CDMException
    {
        DataComponent data = parent;
        
        for (int i=0; i<dataPath.length; i++)
        {
            data = data.getComponent(dataPath[i]);            
            if (data == null)
                throw new CDMException("Unknown component " + dataPath[i]);
        }
        
        return data;
    }
}