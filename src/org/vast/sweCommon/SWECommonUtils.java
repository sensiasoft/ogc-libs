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

package org.vast.sweCommon;

import org.vast.cdm.common.CDMException;
import org.vast.cdm.common.DataComponent;
import org.vast.cdm.common.DataComponentReader;
import org.vast.cdm.common.DataComponentWriter;
import org.vast.cdm.common.DataEncoding;
import org.vast.cdm.common.DataEncodingReader;
import org.vast.cdm.common.DataEncodingWriter;
import org.vast.ogc.DocumentType;
import org.vast.ogc.OGCRegistry;
import org.vast.xml.DOMHelper;
import org.w3c.dom.Element;


/**
 * <p><b>Title:</b>
 * SWE Utils
 * </p>
 *
 * <p><b>Description:</b><br/>
 * Helper class providing a version agnostic access to SWE component
 * structure and encoding readers and writers.
 * </p>
 *
 * <p>Copyright (c) 2007</p>
 * @author Alexandre Robin
 * @date Feb 2, 2007
 * @version 1.0
 */
public class SWECommonUtils implements DataComponentReader, DataComponentWriter, DataEncodingReader, DataEncodingWriter
{
    private String version;
    private boolean versionChanged;
    private DOMHelper previousDom;
    private DataComponentReader componentReader = null;    
    private DataComponentWriter componentWriter = null;
    private DataEncodingReader encodingReader = null;
    private DataEncodingWriter encodingWriter = null;
    
    
    public DataComponent readComponent(DOMHelper dom, Element componentElement) throws CDMException
    {
        DataComponentReader reader = getDataComponentReader(dom, componentElement);
        return reader.readComponent(dom, componentElement);
    }


    public DataComponent readComponentProperty(DOMHelper dom, Element propertyElement) throws CDMException
    {
        Element componentElement = dom.getFirstChildElement(propertyElement);
        DataComponentReader reader = getDataComponentReader(dom, componentElement);
        return reader.readComponentProperty(dom, propertyElement);
    }
    
    
    public DataEncoding readEncoding(DOMHelper dom, Element encodingElement) throws CDMException
    {
        DataEncodingReader reader = getDataEncodingReader(dom, encodingElement);
        return reader.readEncoding(dom, encodingElement);
    }


    public DataEncoding readEncodingProperty(DOMHelper dom, Element propertyElement) throws CDMException
    {
        Element componentElement = dom.getFirstChildElement(propertyElement);
        DataEncodingReader reader = getDataEncodingReader(dom, componentElement);
        return reader.readEncodingProperty(dom, propertyElement);
    }


    public Element writeComponent(DOMHelper dom, DataComponent dataComponents) throws CDMException
    {
        DataComponentWriter writer = getDataComponentWriter();
        return writer.writeComponent(null, dataComponents);
    }


    public Element writeEncoding(DOMHelper dom, DataEncoding dataEncoding) throws CDMException
    {
        DataEncodingWriter writer = getDataEncodingWriter();
        return writer.writeEncoding(dom, dataEncoding);
    }
    
    
    private DataComponentReader getDataComponentReader(DOMHelper dom, Element componentElt)
    {
        if (dom == previousDom && componentReader != null)
        {
            return componentReader;
        }
        else
        {
            DataComponentReader reader = (DataComponentReader)OGCRegistry.createReader(
                                                              DocumentType.SWECOMMON.name(),
                                                              DocumentType.DATACOMPONENT.name(),
                                                              getVersion(dom, componentElt));
            componentReader = reader;
            return reader;
        }
    }
    
    
    private DataEncodingReader getDataEncodingReader(DOMHelper dom, Element componentElt)
    {
        if (dom == previousDom && encodingReader != null)
        {
            return encodingReader;
        }
        else
        {
            DataEncodingReader reader = (DataEncodingReader)OGCRegistry.createReader(
                                                            DocumentType.SWECOMMON.name(),
                                                            DocumentType.DATAENCODING.name(),
                                                            getVersion(dom, componentElt));
            encodingReader = reader;
            return reader;
        }
    }
    
    
    private DataComponentWriter getDataComponentWriter()
    {
        if (!versionChanged && componentWriter != null)
        {
            return componentWriter;
        }
        else
        {
            DataComponentWriter writer = (DataComponentWriter)OGCRegistry.createWriter(
                                                              DocumentType.SWECOMMON.name(),
                                                              DocumentType.DATACOMPONENT.name(),
                                                              this.version);
            componentWriter = writer;
            return writer;
        }
    }
    
    
    private DataEncodingWriter getDataEncodingWriter()
    {
        if (!versionChanged && encodingWriter != null)
        {
            return encodingWriter;
        }
        else
        {
            DataEncodingWriter writer = (DataEncodingWriter)OGCRegistry.createWriter(
                                                            DocumentType.SWECOMMON.name(),
                                                            DocumentType.DATACOMPONENT.name(),
                                                            this.version);
            encodingWriter = writer;
            return writer;
        }
    }
    
    
    /**
     * Logic to guess SWE version from namespace
     * @param dom
     * @return
     */
    private String getVersion(DOMHelper dom, Element sweElt)
    {
        // get version from the last character of namespace URI
        //String sweUri = dom.getXmlDocument().getNSUri("swe");
        String sweUri = sweElt.getNamespaceURI();
        String version = sweUri.substring(sweUri.lastIndexOf('/') + 1);
        
        // check if version is a valid version number otherwise defaults to 0
        if (!version.matches("^\\d+(\\.\\d+)?(\\.\\d+)?$"))
            version = "0.0";
        
        previousDom = dom;
        return version;
    }
    
    
    public void setOutputVersion(String version)
    {
        this.version = version;
        this.versionChanged = true;
    }
}
