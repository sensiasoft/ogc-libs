/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "OGC Service Framework".
 
 The Initial Developer of the Original Code is the VAST team at the University of Alabama in Huntsville (UAH). <http://vast.uah.edu> Portions created by the Initial Developer are Copyright (C) 2007 the Initial Developer. All Rights Reserved. Please Contact Mike Botts <mike.botts@uah.edu> for more information.
 
 Contributor(s): 
 Alexandre Robin <alex.robin@sensiasoftware.com>
 
 ******************************* END LICENSE BLOCK ***************************/

package org.vast.ogc.gml;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import net.opengis.gml.v32.impl.AbstractFeatureImpl;


/**
 * <p>
 * Generic implementation of a GML feature.
 * </p>
 *
 * @author Alex Robin <alex.robin@sensiasoftware.com>
 * @since Feb 20, 2007
 * */
public class GenericFeatureImpl extends AbstractFeatureImpl implements GenericFeature
{
    protected QName qname;
    protected String type;
    protected Map<QName, Object> properties;


    public GenericFeatureImpl(QName qname)
    {
        properties = new LinkedHashMap<QName, Object>();
        this.qname = qname;
    }
    
    
    @Override
    public QName getQName()
    {
        return qname;
    }

    
    @Override
    public Map<QName, Object> getProperties()
    {
        return properties;
    }
  
    
    @Override
    public void setProperty(QName qname, Object prop)
    {
        if (prop != null)
            properties.put(qname, prop);
    }
    
    
    @Override
    public Object getProperty(QName qname)
    {
        return properties.get(qname);
    }
    
    
    @Override
    public void setProperty(String name, Object prop)
    {
        if (prop != null)
            properties.put(new QName(name), prop);
    }
    
    
    @Override
    public Object getProperty(String name)
    {
        return properties.get(new QName(name));
    }


    @Override
    public String getType()
    {
        return type;
    }


    @Override
    public void setType(String type)
    {
        this.type = type;
    }
}
