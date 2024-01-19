/* CVS $Id: $ */
package ch.epfl.gsn.vocab; 
import com.hp.hpl.jena.rdf.model.*;
 
/**
 * Vocabulary definitions from src/main/ontology/wgs84_pos.rdf 
 * @author Auto-generated by schemagen on 04 sept. 2014 15:23 
 */
public class Wgs84 {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.w3.org/2003/01/geo/wgs84_pos#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    public static final Property alt = m_model.createProperty( "http://www.w3.org/2003/01/geo/wgs84_pos#alt" );
    
    public static final Property lat = m_model.createProperty( "http://www.w3.org/2003/01/geo/wgs84_pos#lat" );
    
    public static final Property lat_long = m_model.createProperty( "http://www.w3.org/2003/01/geo/wgs84_pos#lat_long" );
    
    public static final Property location = m_model.createProperty( "http://www.w3.org/2003/01/geo/wgs84_pos#location" );
    
    public static final Property long_ = m_model.createProperty( "http://www.w3.org/2003/01/geo/wgs84_pos#long" );
    
    public static final Resource Point = m_model.createResource( "http://www.w3.org/2003/01/geo/wgs84_pos#Point" );
    
    public static final Resource SpatialThing = m_model.createResource( "http://www.w3.org/2003/01/geo/wgs84_pos#SpatialThing" );
    
}

/*
@footer@
*/
    
