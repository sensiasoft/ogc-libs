package org.vast.data;

import net.opengis.swe.v20.XMLEncoding;


/**
 * POJO class for XML type XMLEncodingType(@http://www.opengis.net/swe/2.0).
 *
 * This is a complex type.
 */
public class XMLEncodingImpl extends AbstractEncodingImpl implements XMLEncoding
{
    static final long serialVersionUID = 1L;
    String namespace;
    String prefix;
    
    
    public XMLEncodingImpl()
    {        
    }
    
    
    public XMLEncodingImpl(String namespace, String prefix)
    {
        this.namespace = namespace;
        this.prefix = prefix;
    }
    
    
    @Override
    public XMLEncodingImpl copy()
    {
        XMLEncodingImpl newObj = new XMLEncodingImpl(namespace, prefix);
        return newObj;
    }


    public String getNamespace()
    {
        return namespace;
    }


    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }


    public String getPrefix()
    {
        return prefix;
    }


    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }
}
