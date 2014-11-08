package org.vast.data;

import java.util.List;
import org.vast.cdm.common.CDMException;
import org.vast.cdm.common.DataType;
import net.opengis.OgcProperty;
import net.opengis.OgcPropertyImpl;
import net.opengis.swe.v20.AllowedTokens;
import net.opengis.swe.v20.Category;


/**
 * <p>
 * Extended SWE Category implementation adapted to old VAST framework
 * </p>
 *
 * <p>Copyright (c) 2014 Sensia Software LLC</p>
 * @author Alexandre Robin <alex.robin@sensiasoftware.com>
 * @since Aug 30, 2014
 */
public class CategoryImpl extends DataValue implements Category, HasCodeSpace
{
    static final long serialVersionUID = 1L;
    protected String codeSpace;
    protected OgcProperty<AllowedTokens> constraint;
    
    
    public CategoryImpl()
    {
        this.dataType = DataType.UTF_STRING;
    }
    
    
    @Override
    public CategoryImpl copy()
    {
        CategoryImpl newObj = new CategoryImpl();
        super.copyTo(newObj);
        
        if (codeSpace != null)
            newObj.codeSpace = codeSpace;
        else
            newObj.codeSpace = null;
        
        if (constraint != null)
            newObj.constraint = constraint.copy();
        else
            newObj.constraint = null;
        
        return newObj;
    }
    
    
    /**
     * Gets the codeSpace property
     */
    @Override
    public String getCodeSpace()
    {
        return codeSpace;
    }
    
    
    /**
     * Checks if codeSpace is set
     */
    @Override
    public boolean isSetCodeSpace()
    {
        return (codeSpace != null);
    }
    
    
    /**
     * Sets the codeSpace property
     */
    @Override
    public void setCodeSpace(String codeSpace)
    {
        this.codeSpace = codeSpace;
    }
    
    
    /**
     * Gets the constraint property
     */
    @Override
    public AllowedTokens getConstraint()
    {
        return constraint.getValue();
    }
    
    
    /**
     * Gets extra info (name, xlink, etc.) carried by the constraint property
     */
    @Override
    public OgcProperty<AllowedTokens> getConstraintProperty()
    {
        if (constraint == null)
            constraint = new OgcPropertyImpl<AllowedTokens>();
        return constraint;
    }
    
    
    /**
     * Checks if constraint is set
     */
    @Override
    public boolean isSetConstraint()
    {
        return (constraint != null && (constraint.hasValue() || constraint.hasHref()));
    }
    
    
    /**
     * Sets the constraint property
     */
    @Override
    public void setConstraint(AllowedTokens constraint)
    {
        if (this.constraint == null)
            this.constraint = new OgcPropertyImpl<AllowedTokens>();
        this.constraint.setValue(constraint);
    }
    
    
    /**
     * Gets the value property
     */
    @Override
    public String getValue()
    {
        if (dataBlock == null)
            return null;
        return dataBlock.getStringValue();
    }
    
    
    /**
     * Checks if value is set
     */
    @Override
    public boolean isSetValue()
    {
        return (dataBlock != null);
    }
    
    
    /**
     * Sets the value property
     */
    @Override
    public void setValue(String value)
    {
        if (dataBlock == null)
            assignNewDataBlock();
        dataBlock.setStringValue(value);
    }
    
    
    @Override
    public boolean hasConstraints()
    {
        return (constraint != null);
    }
    
    
    @Override
    public void validateData(List<CDMException> errorList)
    {
        if (constraint != null && isSetValue())
        {
            AllowedTokensImpl constraint = (AllowedTokensImpl)this.constraint;            
            if (!constraint.isValid(getValue()))
            {
                errorList.add(new CDMException(name, "Value '" + dataBlock.getStringValue() + 
                    "' is not valid for component '" + name + "': " + constraint.getAssertionMessage()));
            }
        }
    }
    
    
    @Override
    public String toString(String indent)
    {
        StringBuffer text = new StringBuffer();
        text.append("Category: ");                
        if (dataBlock != null)
        {
            text.append(" = ");
            text.append(dataBlock.getStringValue());
        }
        text.append("\n");
        return text.toString();
    }
}
