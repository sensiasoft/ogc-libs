/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are copyright (C) 2018, Sensia Software LLC
 All Rights Reserved. This software is the property of Sensia Software LLC.
 It cannot be duplicated, used, or distributed without the express written
 consent of Sensia Software LLC.
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.ogc.gml;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.namespace.QName;
import org.vast.ogc.xlink.IXlinkReference;
import org.vast.swe.SWEConstants;
import org.vast.util.Asserts;
import org.vast.util.DateTimeFormat;
import org.vast.util.TimeExtent;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import gnu.trove.list.array.TDoubleArrayList;
import net.opengis.gml.v32.AbstractFeature;
import net.opengis.gml.v32.AbstractGeometry;
import net.opengis.gml.v32.AbstractTimeGeometricPrimitive;
import net.opengis.gml.v32.Envelope;
import net.opengis.gml.v32.LineString;
import net.opengis.gml.v32.LinearRing;
import net.opengis.gml.v32.Measure;
import net.opengis.gml.v32.Point;
import net.opengis.gml.v32.Polygon;
import net.opengis.gml.v32.impl.GMLFactory;


/**
 * <p>
 * GeoJSON bindings using Gson JsonWriter/JsonReader.<br/>
 * This class is NOT threadsafe.
 * </p>
 *
 * @author Alex Robin
 * @date Nov 12, 2018
 */
public class GeoJsonBindings
{
    public static final String MIME_TYPE ="application/geo+json";
    public static final String ERROR_UNSUPPORTED_TYPE = "Unsupported type: ";
    public static final String ERROR_INVALID_COORDINATES = "Invalid coordinate array";
    public static final String ERROR_INVALID_TIMERANGE = "Invalid time extent";
    DecimalFormat formatter = new DecimalFormat(GMLFactory.COORDINATE_FORMAT);
    GMLFactory factory;
    enum CrsType {CRS84, CRS84_FLIP, CUSTOM}
    
    
    public GeoJsonBindings()
    {
        this(false);
    }
    
    
    public GeoJsonBindings(boolean useJTS)
    {
        this(new GMLFactory(useJTS));
    }
    
    
    public GeoJsonBindings(GMLFactory factory)
    {
        this.factory = factory;
    }
    
    
    /////////////////////
    // Writing methods //
    /////////////////////
    
    public void writeFeature(JsonWriter writer, IFeature bean) throws IOException
    {
        writer.beginObject();
        writeStandardGeoJsonProperties(writer, bean);
        
        // other properties
        if (hasNonGeoJsonProperties(bean))
        {
            writer.name("properties").beginObject();
            writeCommonFeatureProperties(writer, bean);
            writeCustomFeatureProperties(writer, bean);
            writer.endObject();
        }
        
        writeCustomJsonProperties(writer, bean);
        writer.endObject();
    }
    
    
    protected void writeStandardGeoJsonProperties(JsonWriter writer, IFeature bean) throws IOException
    {
        writer.name("type").value("Feature");
        
        // common properties
        writer.name("id").value(encodeFeatureID(bean));
        
        // geometry
        if (bean.getGeometry() != null)
        {
            writer.name("geometry");
            writeGeometry(writer, bean.getGeometry());
        }
    }
    
    
    protected void writeCommonFeatureProperties(JsonWriter writer, IFeature bean) throws IOException
    {
        if (bean.getUniqueIdentifier() != null)
            writer.name("uid").value(bean.getUniqueIdentifier());
     
        if (bean.getName() != null)
            writer.name("name").value(bean.getName());
        
        if (bean.getDescription() != null)
            writer.name("description").value(bean.getDescription());
        
        if (bean.getType() != null)
            writer.name("type").value(bean.getType());
        
        if (bean.getValidTime() != null)
        {
            writer.name("validTime");
            writeTimeExtent(writer, bean.getValidTime());
        }
    }
    
    
    protected void writeCustomFeatureProperties(JsonWriter writer, IFeature bean) throws IOException
    {
        var featureType = bean.getType();
        
        // write all custom properties with supported data type
        for (Entry<QName, Object> prop: bean.getProperties().entrySet())
        {
            QName propName = prop.getKey();
            Object val = prop.getValue();
            
            if (val instanceof Boolean)
            {
                writer.name(propName.getLocalPart());
                writer.value((Boolean)val);
            }
            else if (val instanceof Number)
            {
                writer.name(propName.getLocalPart());
                writer.value((Number)val);
            }
            else if (val instanceof String && !val.equals(featureType))
            {
                writer.name(propName.getLocalPart());
                writer.value((String)val);
            }
            else if (val instanceof IXlinkReference<?>)
            {
                String href = ((IXlinkReference<?>) val).getHref();
                if (href != null && !href.equals(featureType)) 
                {
                    writer.name(propName.getLocalPart());
                    writer.value(href);
                }
            }
            else if (val instanceof Measure)
            {
                writer.name(propName.getLocalPart());
                writer.beginObject();
                writer.name("uom").value(((Measure) val).getUom());
                writer.name("value").value(((Measure) val).getValue());
                writer.endObject();
            }
            else if (val instanceof AbstractGeometry && val != bean.getGeometry())
            {
                writer.name(propName.getLocalPart());
                writeGeometry(writer, (AbstractGeometry)val);
            }
            else if (val instanceof AbstractTimeGeometricPrimitive &&
                !GenericTemporalFeatureImpl.PROP_VALID_TIME.equals(propName))
            {
                writer.name(propName.getLocalPart());
                var te = GMLUtils.timePrimitiveToTimeExtent((AbstractTimeGeometricPrimitive)val);
                writeTimeExtent(writer, te);
            }
        }
    }
    
    
    protected void writeCustomJsonProperties(JsonWriter writer, IFeature bean) throws IOException
    {
    }
    
    
    protected boolean hasNonGeoJsonProperties(IFeature bean)
    {
        return bean.getUniqueIdentifier() != null ||
               bean.getName() != null ||
               bean.getDescription() != null ||
               bean.getValidTime() != null ||
               !bean.getProperties().isEmpty();
    }
    
    
    protected String encodeFeatureID(IFeature bean) throws IOException
    {
        return bean.getId();
    }
    
    
    public void writeEnvelope(JsonWriter writer, Envelope bean) throws IOException
    {
        writer.beginArray();
        double[] low = bean.getLowerCorner();
        writer.jsonValue(formatter.format(low[0]));
        writer.jsonValue(formatter.format(low[1]));
        double[] high = bean.getUpperCorner();
        writer.jsonValue(formatter.format(high[0]));
        writer.jsonValue(formatter.format(high[1]));
        writer.endArray();
    }
    
    
    public void writeGeometry(JsonWriter writer, AbstractGeometry bean) throws IOException
    {
        if (bean instanceof Point)
            writePoint(writer, (Point)bean);
        else if (bean instanceof LineString)
            writeLineString(writer, (LineString)bean);
        else if (bean instanceof Polygon)
            writePolygon(writer, (Polygon)bean);
        else
            throw new JsonParseException(ERROR_UNSUPPORTED_TYPE + bean.getClass().getCanonicalName());
    }
    
    
    protected CrsType getCrsType(AbstractGeometry bean)
    {
        if (bean.isSetSrsName())
        {
            var crs = bean.getSrsName();
            if (SWEConstants.REF_FRAME_4326.equals(crs))
                return CrsType.CRS84_FLIP;
            else if (SWEConstants.REF_FRAME_4979.equals(crs))
                return CrsType.CRS84_FLIP;
            else if (SWEConstants.REF_FRAME_CRS84.equals(crs))
                return CrsType.CRS84;
            else
                return CrsType.CUSTOM;
        }
        
        return CrsType.CRS84;
    }
    
    
    public CrsType writeCommonGeometryProperties(JsonWriter writer, AbstractGeometry bean) throws IOException
    {
        int dims = bean.getSrsDimension();
        Asserts.checkArgument(dims == 2 || dims == 3, "Invalid SRS dimension");
        
        var crsType = getCrsType(bean);
        
        if (bean.isSetSrsName() && crsType == CrsType.CUSTOM)
            writer.name("crs").value(bean.getSrsName());
        
        return crsType;
    }
    
    
    public void writePoint(JsonWriter writer, Point bean) throws IOException
    {
        writer.beginObject();
        writer.name("type").value("Point");
        
        var crsType = writeCommonGeometryProperties(writer, bean);
        int dims = bean.getSrsDimension();
        
        // coordinates
        if (bean.isSetPos())
        {
            writer.name("coordinates");
            writeCoordinates(writer, bean.getPos(), 0, dims, crsType);
        }
        
        writer.endObject();
    }
    
    
    public void writeLineString(JsonWriter writer, LineString bean) throws IOException
    {
        writer.beginObject();
        writer.name("type").value("LineString");
        
        var crsType = writeCommonGeometryProperties(writer, bean);
        int dims = bean.getSrsDimension();
        
        // coordinates
        if (bean.isSetPosList())
        {
            writer.name("coordinates");
            writer.beginArray();
            for (int i = 0; i < bean.getPosList().length; i += dims)
                writeCoordinates(writer, bean.getPosList(), i, dims, crsType);
            writer.endArray();
        }
        
        writer.endObject();
    }
    
    
    public void writePolygon(JsonWriter writer, Polygon bean) throws IOException
    {
        writer.beginObject();
        writer.name("type").value("Polygon");
        
        var crsType = writeCommonGeometryProperties(writer, bean);
        int dims = bean.getSrsDimension();
        
        // coordinates
        if (bean.isSetExterior())
        {
            writer.name("coordinates");
            
            // there is one more level of array nesting in polygons
            writer.beginArray(); 
                        
            // exterior
            if (bean.isSetExterior())
                writeLinearRing(writer, bean.getExterior(), dims, crsType);
            
            // interior holes
            int numHoles = bean.getNumInteriors();
            for (int i = 0; i < numHoles; i++)
            {
                LinearRing item = bean.getInteriorList().get(i);
                writeLinearRing(writer, item, dims, crsType);
            }
            
            writer.endArray();
        }
        
        writer.endObject();
    }
    

    public void writeLinearRing(JsonWriter writer, LinearRing bean, int dims, CrsType crsType) throws IOException
    {
        writer.beginArray();
        for (int i = 0; i < bean.getPosList().length; i += dims)
            writeCoordinates(writer, bean.getPosList(), i, dims, crsType);
        writer.endArray();
    }
    

    public void writeCoordinates(JsonWriter writer, double[] coords, int index, int dims, CrsType crsType) throws IOException
    {
        writer.beginArray();
        
        if (crsType == CrsType.CRS84_FLIP)
        {
            int i = index;
            writer.jsonValue(formatter.format(coords[i+1]));
            writer.jsonValue(formatter.format(coords[i]));
            if (dims > 2)
                writer.jsonValue(formatter.format(coords[i+2]));
        }
        else
        {
            for (int i = index; i < index + dims; i++)
                writer.jsonValue(formatter.format(coords[i]));
        }
        
        writer.endArray();
    }
    
    
    public void writeTimeExtent(JsonWriter writer, TimeExtent bean) throws IOException
    {
        writer.beginArray();
        writeDateTimeValue(writer, bean.begin().atOffset(ZoneOffset.UTC));
        writeDateTimeValue(writer, bean.end().atOffset(ZoneOffset.UTC));
        writer.endArray();
    }
    
    
    protected void writeDateTimeValue(JsonWriter writer, OffsetDateTime dateTime) throws IOException
    {
        String isoString = dateTime.format(DateTimeFormat.ISO_DATE_OR_TIME_FORMAT);
        writer.value(isoString);
    }
    
    
    /////////////////////
    // Reading methods //
    /////////////////////
    
    public IFeature readFeature(JsonReader reader) throws IOException
    {
        reader.beginObject();
        
        String type = readObjectType(reader);
        GenericFeature f = createFeatureObject(type);
        
        while (reader.hasNext())
        {
            String name = reader.nextName();
            if (!readStandardGeoJsonProperty(reader, f, name))
                reader.skipValue();
        }
        
        reader.endObject();
        return f;
    }
    
    
    protected GenericFeature createFeatureObject(String type)
    {
        if (!"Feature".equals(type))
            throw new JsonParseException("The type of a GeoJSON feature must be 'Feature'");
        
        return new GenericTemporalFeatureImpl(new QName(type));
    }
    
    
    protected String readObjectType(JsonReader reader) throws IOException
    {
        if (!reader.hasNext() || !"type".equals(reader.nextName()))
            throw new JsonParseException("'type' must be the first property of a GeoJSON object");
        
        return reader.nextString();
    }
    
    
    protected boolean readStandardGeoJsonProperty(JsonReader reader, AbstractFeature f, String name) throws IOException
    {
        if ("id".equals(name))
            f.setId(reader.nextString());
        
        else if ("bbox".equals(name))
            f.setBoundedByAsEnvelope(readEnvelope(reader));
        
        else if ("geometry".equals(name))
            f.setGeometry(readGeometry(reader));
        
        else if ("properties".equals(name))
        {
            reader.beginObject();
            while (reader.hasNext())
            {
                String propName = reader.nextName();
                if (!readCommonFeatureProperty(reader, f, propName))
                {
                    if (f instanceof GenericFeatureImpl)
                        readCustomFeatureProperty(reader, (GenericFeatureImpl)f, propName);
                    else
                        reader.skipValue();
                }
            }
            reader.endObject();
        }
        
        else
            return false;
        
        return true;
    }

    
    protected boolean readCommonFeatureProperty(JsonReader reader, AbstractFeature f, String name) throws IOException
    {
        if ("uid".equals(name))
            f.setUniqueIdentifier(reader.nextString());
        
        else if ("name".equals(name))
            f.setName(reader.nextString());
        
        else if ("description".equals(name))
            f.setDescription(reader.nextString());
        
        else if ("type".equals(name))
            ((GenericFeatureImpl)f).setType(reader.nextString());
        
        else if ("validTime".equals(name) && f instanceof GenericTemporalFeatureImpl)
            readValidTime(reader, (GenericTemporalFeatureImpl)f);
        
        else
            return false;
        
        return true;
    }
    
    
    protected void readCustomFeatureProperty(JsonReader reader, GenericFeatureImpl f, String name) throws IOException
    {
        switch(reader.peek())
        {
            case STRING:
                f.setProperty(name, reader.nextString());
                break;
                
            case NUMBER:
                f.setProperty(name, reader.nextDouble());
                break;
                
            default:
                reader.skipValue();
        }
    }
    
    
    protected String decodeFeatureID(String idVal, AbstractFeature bean) throws IOException
    {
        return idVal;
    }
    
    
    public void readCustomProperties(JsonReader reader, Map<QName, Object> map) throws IOException
    {
        reader.beginObject();
        
        while (reader.hasNext())
        {
            QName name = new QName(reader.nextName());
            JsonToken type = reader.peek();
            
            if (type == JsonToken.NUMBER)
            {
                map.put(name, (Double)reader.nextDouble());
            }
            else if (type == JsonToken.STRING)
            {
                // TODO add support for date/time as ISO string
                String val = reader.nextString();
                map.put(name, val);
            }
            /*else if (type == JsonToken.BEGIN_OBJECT)
            {
                reader.beginObject();
                Map<QName, Object> objMap = new HashMap<>();
                readCustomObjectProperties(reader, objMap);                
                reader.endObject();
                map.put(name, objMap);
            }*/
            else
                reader.skipValue();
        }
        
        reader.endObject();
    }
    
    
    public Envelope readEnvelope(JsonReader reader) throws IOException
    {
        reader.beginArray();
        Envelope env = factory.newEnvelope();
        env.setLowerCorner(new double[] {reader.nextDouble(), reader.nextDouble()});
        env.setUpperCorner(new double[] {reader.nextDouble(), reader.nextDouble()});
        reader.endArray();
        return env;
    }
    
    
    public AbstractGeometry readGeometry(JsonReader reader) throws IOException
    {
        reader.beginObject();
        String type = readObjectType(reader);
        
        AbstractGeometry geom = null;
        String crs = null;
        while (reader.hasNext())
        {
            String name = reader.nextName();
            
            if ("crs".equals(name))
                crs = reader.nextString();
            
            else if ("coordinates".equals(name))
            {
                if ("Point".equals(type))
                {
                    geom = factory.newPoint();
                    readCoordinates(reader, (Point)geom);
                }
                else if ("LineString".equals(type))
                {
                    geom = factory.newLineString();
                    readCoordinates(reader, (LineString)geom);
                }
                else if ("Polygon".equals(type))
                {
                    geom = factory.newPolygon();
                    readCoordinates(reader, (Polygon)geom);
                }
                else
                    throw new JsonParseException(ERROR_UNSUPPORTED_TYPE + type);
            }
                
            else
                reader.skipValue();
        }
        
        if (geom == null)
            throw new IOException("Missing coordinates array");
        
        geom.setSrsName(crs != null ? crs : SWEConstants.REF_FRAME_CRS84);
        
        reader.endObject();
        return geom;
    }
    
    
    public void readCoordinates(JsonReader reader, Point geom) throws IOException
    {
        TDoubleArrayList posList = new TDoubleArrayList(3);
        int numDims = readCoordinates(reader, posList, 0);
        
        geom.setPos(posList.toArray());
        geom.setSrsDimension(numDims);
    }
    
    
    public void readCoordinates(JsonReader reader, LineString geom) throws IOException
    {
        int numDims = 0;
        
        reader.beginArray();
        TDoubleArrayList posList = new TDoubleArrayList(10);
        while (reader.hasNext())
            numDims = readCoordinates(reader, posList, numDims);
        reader.endArray();
        
        geom.setPosList(posList.toArray());
        geom.setSrsDimension(numDims);
    }
    
    
    public void readCoordinates(JsonReader reader, Polygon geom) throws IOException
    {
        int numDims = 0;
        
        reader.beginArray();
        numDims = readLinearRing(reader, geom, false, numDims);
        while (reader.hasNext())
            numDims = readLinearRing(reader, geom, true, numDims);
        
        reader.endArray();
    }
    

    public int readLinearRing(JsonReader reader, Polygon geom, boolean interior, int prevNumDims) throws IOException
    {
        int numDims = 0;
        
        reader.beginArray();
        TDoubleArrayList posList = new TDoubleArrayList(10);
        while (reader.hasNext())
            numDims = readCoordinates(reader, posList, numDims);
        reader.endArray();
        
        LinearRing ring = factory.newLinearRing();
        ring.setPosList(posList.toArray());

        geom.setSrsDimension(numDims);
        if (interior)
            geom.addInterior(ring);
        else
            geom.setExterior(ring);
        
        return numDims;
    }
    

    /*
     * Read coordinate tuple and return number of dimensions
     */
    public int readCoordinates(JsonReader reader, TDoubleArrayList coords, int prevNumDims) throws IOException
    {
        int numDims = 0;
        
        try
        {
            reader.beginArray();
            while (reader.hasNext())
            {
                coords.add(reader.nextDouble());
                numDims++;
            }
            reader.endArray();
        }
        catch (Exception e)
        {
            throw new JsonParseException(ERROR_INVALID_COORDINATES);
        }
        
        // check dimensionality is same as previous tuple in same geometry!
        if (prevNumDims > 0 && prevNumDims != numDims)
            throw new JsonParseException(ERROR_INVALID_COORDINATES + ": Tuples have different dimensionality");
        
        return numDims;
    }
    
    
    public void readValidTime(JsonReader reader, GenericTemporalFeatureImpl bean) throws IOException
    {
        reader.beginArray();
        
        try
        {
            var timeExtent = TimeExtent.parse(reader.nextString() + "/" + reader.nextString());
            bean.setValidTime(timeExtent);
        }
        catch (Exception e)
        {
            throw new JsonParseException(ERROR_INVALID_TIMERANGE);
        }
        
        reader.endArray();
    }
}
