package edu.mit.cci.sna.jung;

import edu.uci.ics.jung.io.GraphMLMetadata;
import edu.uci.ics.jung.io.GraphMLWriter;
import org.apache.commons.collections15.Transformer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: jintrone
 * Date: 5/17/12
 * Time: 8:09 AM
 */
public class MyGraphMLWriter<V,E> extends GraphMLWriter<V,E> {

    Map<String,String> nodeAttributeClasses = new HashMap<String, String>();
    Map<String,String> edgeAttributeClasses = new HashMap<String,String>();

    protected void writeKeySpecification(String key, String type,
			GraphMLMetadata<?> ds, BufferedWriter bw) throws IOException
	{

        bw.write("<key attr.name=\""+key+"\" attr.type=\""+ (type.equalsIgnoreCase("node")?nodeAttributeClasses.get(key):edgeAttributeClasses.get(key))+"\" id=\"" + key + "\" for=\"" + type + "\"");
		boolean closed = false;
		// write out description if any
		String desc = ds.description;
		if (desc != null)
		{
			if (!closed)
			{
				bw.write(">\n");
				closed = true;
			}
			bw.write("<desc>" + desc + "</desc>\n");
		}
		// write out default if any
		Object def = ds.default_value;
		if (def != null)
		{
			if (!closed)
			{
				bw.write(">\n");
				closed = true;
			}
			bw.write("<default>" + def.toString() + "</default>\n");
		}
		if (!closed)
		    bw.write("/>\n");
		else
		    bw.write("</key>\n");
	}

    public void addVertexData(String id, String description, String clazz, String default_value,
			Transformer<V, String> vertex_transformer)
	{
		addVertexData(id,description,default_value,vertex_transformer);
        this.nodeAttributeClasses.put(id, clazz);
	}

    public void addEdgeData(String id, String description, String clazz, String default_value,
                              Transformer<E, String> vertex_transformer)
    {
        addEdgeData(id, description, default_value, vertex_transformer);
        this.edgeAttributeClasses.put(id, clazz);
    }

}
