/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "SensorML DataProcessing Engine".
 
 The Initial Developer of the Original Code is the VAST team at the University of Alabama in Huntsville (UAH). <http://vast.uah.edu> Portions created by the Initial Developer are Copyright (C) 2007 the Initial Developer. All Rights Reserved. Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.data;

import net.opengis.swe.v20.AbstractSimpleComponent;
import org.vast.cdm.common.DataBlock;
import org.vast.cdm.common.DataType;


/**
 * <p>
 * Base implementation for all range data components with min/max values
 * 11-2014: Updated to implement new API autogenerated from XML schema
 * </p>
 *
 * <p>Copyright (c) 2014</p>
 * @author Alexandre Robin
 * @version 1.0
 */
public abstract class AbstractRangeComponentImpl extends AbstractSimpleComponentImpl implements AbstractSimpleComponent
{
    private static final long serialVersionUID = -7411819306997320628L;
    
    
    public AbstractRangeComponentImpl()
    {
        this.scalarCount = 2;
    }
    
    
    public AbstractRangeComponentImpl(DataType type)
    {
        this.scalarCount = 2;
        this.dataType = type;
    }
    
    
    public abstract AbstractRangeComponentImpl copy();
    
    
    protected void copyTo(AbstractRangeComponentImpl other)
    {
        super.copyTo(other);
        other.dataType = dataType;
        
        if (nilValues != null)
            other.nilValues = nilValues.copy();
        else
            other.nilValues = null;
        
        qualityList.copyTo(other.qualityList);
        other.referenceFrame = referenceFrame;
        other.axisID = axisID;
    }
    
    
    @Override
    public int getComponentCount()
    {
        return 0; //was 1 but i think it's wrong
    } 
   
    
    @Override
    protected void updateStartIndex(int startIndex)
    {
        dataBlock.startIndex = startIndex;
    }
        
    
    @Override
    public void setData(DataBlock dataBlock)
    {
    	if (dataBlock instanceof DataBlockTuple)
    	{
    		int blockIndex = ((DataBlockTuple)dataBlock).startIndex;
    		this.dataBlock = ((DataBlockTuple)dataBlock).blockArray[blockIndex];
    	}
    	else
    		this.dataBlock = (AbstractDataBlock)dataBlock;
    }
    
    
    @Override
    public AbstractDataBlock createDataBlock()
    {
    	switch (dataType)
        {
        	case BOOLEAN:
        		return new DataBlockBoolean(2);
            
        	case BYTE:
        		return new DataBlockByte(2);
                
            case UBYTE:
                return new DataBlockUByte(2);
                
            case SHORT:
            	return new DataBlockShort(2);
                
            case USHORT:
                return new DataBlockUShort(2);
                
            case INT:
            	return new DataBlockInt(2);
                
            case UINT:
                return new DataBlockUInt(2);
                
            case LONG:
            case ULONG:
            	return new DataBlockLong(2);
                                
            case FLOAT:
            	return new DataBlockFloat(2);
                
            case DOUBLE:
            	return new DataBlockDouble(2);
                
            case UTF_STRING:
            case ASCII_STRING:
            	return new DataBlockString(2);
                
            default:
            	throw new RuntimeException("Data type not allowed for a range component: " + dataType);
        }
    }
}
