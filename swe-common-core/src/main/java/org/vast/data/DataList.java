/***************************** BEGIN LICENSE BLOCK ***************************

The contents of this file are subject to the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file, You can obtain one
at http://mozilla.org/MPL/2.0/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
for the specific language governing rights and limitations under the License.
 
Copyright (C) 2012-2015 Sensia Software LLC. All Rights Reserved.
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.data;

import java.util.List;
import java.util.ListIterator;
import net.opengis.OgcPropertyImpl;
import net.opengis.swe.v20.DataBlock;
import net.opengis.swe.v20.DataComponent;
import net.opengis.swe.v20.BlockComponent;
import net.opengis.swe.v20.DataArray;
import net.opengis.swe.v20.DataComponentVisitor;
import net.opengis.swe.v20.DataStream;
import net.opengis.swe.v20.ValidationException;


/**
 * <p>
 * Growable List of identical DataComponents.
 * Each new cluster of data must comply to the DataList component
 * structure. Data clusters are stored using a DataBlockList so
 * that data can be added and removed in real time.
 * 11-2014: Updated to implement new API autogenerated from XML schema
 * </p>
 *
 * @author Alex Robin
 * */
public class DataList extends AbstractArrayImpl implements DataArray, DataStream, BlockComponent
{
    protected ListIterator<DataBlock> blockIterator;
    protected AbstractDataComponentImpl tempComponent = null;
    

    public DataList()
    {
        // special property object to correctly set parent
        elementType = new OgcPropertyImpl<DataComponent>()
        {
            @Override
            public void setValue(DataComponent value)
            {
                super.setValue(value);
                ((AbstractDataComponentImpl)value).setName(this.name);
                ((AbstractDataComponentImpl)value).setParent(DataList.this);
                tempComponent = ((AbstractDataComponentImpl)value).copy();
            }    
        };
    }
    
    
    public DataList(String name)
    {
        this();
        this.setName(name);
    }
    
    
    @Override
    public DataList copy()
    {
        DataList newObj = new DataList();
        copyTo(newObj);        
        return newObj;
    }
    
    
    protected void copyTo(DataList other)
    {
        super.copyTo(other);
        other.tempComponent = this.tempComponent.copy();
    }
    
    
    @Override
    protected void updateStartIndex(int startIndex)
    {        
    }
    
    
    @Override
    protected void updateAtomCount(int childOffsetCount)
    {
    }
    
    
    @Override
    public AbstractDataComponentImpl getComponent(int index)
    {
        checkIndex(index);
        tempComponent.setData(((DataBlockList)dataBlock).blockList.get(index));
        
        return tempComponent;
    }
    
    
    public AbstractDataComponentImpl getListComponent()
    {
        return (AbstractDataComponentImpl)elementType.getValue();
    }
    
    
    public void resetIterator()
    {
        blockIterator = ((DataBlockList)dataBlock).blockIterator();
    }
    
    
    public boolean hasNext()
    {
        return blockIterator.hasNext();
    }
    
    
    public AbstractDataComponentImpl nextComponent()
    {
        if (blockIterator == null)
            resetIterator();
        
        tempComponent.setData(blockIterator.next());        
        return tempComponent;
    }
    
    
    public DataBlock nextDataBlock()
    {
        return blockIterator.next();
    }
    
    
    @Override
    public void setData(DataBlock dataBlock)
    {
    	this.dataBlock = (DataBlockList)dataBlock;
    }
    
    
    @Override
    public void clearData()
    {
    	this.dataBlock = null;
    	if (tempComponent != null)
    	    tempComponent.clearData();
    }
    
    
    @Override
    public void validateData(List<ValidationException> errorList)
    {
    	// do only if constraints are specified on descendants
    	if (hasConstraints())
    	{
    		int numErrors = errorList.size();
    		
    		for (int i = 0; i < getComponentCount(); i++)
    		{
    			getComponent(i).validateData(errorList);
    			
    			// max N errors generated!
    			if (errorList.size() > numErrors + MAX_ARRAY_ERRORS)
    				return;
    		}
    	}
    }
    
    
    public final void addData(DataBlock dataBlock)
    {
    	// if we start adding blocks without first creating the datablock
        // let the list grow on demand
        if (this.dataBlock == null)
    	    this.dataBlock = new DataBlockList();
        
        ((DataBlockList)this.dataBlock).add((AbstractDataBlock)dataBlock);
    }
    
    
    @Override
    public AbstractDataBlock createDataBlock()
    {
        int listSize = getComponentCount();
        DataBlockList newDataBlock = new DataBlockList(listSize);
        DataBlock childBlock = tempComponent.createDataBlock();
        newDataBlock.add(childBlock);
        newDataBlock.resize(listSize);
        return newDataBlock;
    }


    /**
     * Check that the integer index given is in range: 0 to size of array - 1 
     * @param index int
     * @throws DataException
     */
    protected void checkIndex(int index)
    {
        // error if index is out of range
        if ((index >= getComponentCount()) || (index < 0))
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds");
    }
    
    
    @Override
    public int getComponentCount()
    {
    	if (elementCount.hasValue() && elementCount.getValue().isSetValue())
    	    return elementCount.getValue().getValue();
    	else if (dataBlock != null)
    	    return ((DataBlockList)dataBlock).getListSize();
    	else
    	    return 0;
    }


    public String toString(String indent)
    {
        StringBuffer text = new StringBuffer();

        text.append("DataList[" + getComponentCount() + "] of:\n");
        text.append(indent + "  ");
        text.append(getComponent(0).toString(indent + "  "));

        return text.toString();
    }


    @Override
    public int getComponentIndex(String name)
    {
        if (!elementType.getName().equals(name))
            return -1;
        return 0;
    }


    @Override
    public AbstractDataComponentImpl getComponent(String name)
    {
        if (!name.equals(elementType.getName()))
            return null;
        
        return getListComponent();
    }


    @Override
    public boolean isSetElementCount()
    {
        return (elementCount != null && (elementCount.hasValue() || elementCount.hasHref()));
    }


    @Override
    public void updateSize()
    {        
    }


    @Override
    public void updateSize(int arraySize)
    {        
    }


    @Override
    public void accept(DataComponentVisitor visitor)
    {
        visitor.visit(this);
    }
}
