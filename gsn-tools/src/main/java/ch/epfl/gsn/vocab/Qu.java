/* CVS $Id: $ */
package ch.epfl.gsn.vocab; 
import com.hp.hpl.jena.rdf.model.*;
 
/**
 * Vocabulary definitions from src/main/ontology/qu.owl 
 * @author Auto-generated by schemagen on 03 sept. 2014 22:42 
 */
public class Qu {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://purl.oclc.org/NET/ssnx/qu/qu#";
    
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    public static final Property baseDimension = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#baseDimension" );
    
    public static final Property baseQuantityKind = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#baseQuantityKind" );
    
    public static final Property baseUnit = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#baseUnit" );
    
    public static final Property code = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#code" );
    
    public static final Property conversionFactor = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#conversionFactor" );
    
    public static final Property conversionOffset = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#conversionOffset" );
    
    public static final Property definitionURI = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#definitionURI" );
    
    public static final Property description = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#description" );
    
    public static final Property dimension = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#dimension" );
    
    public static final Property dimensionFactor = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#dimensionFactor" );
    
    public static final Property exponent = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#exponent" );
    
    public static final Property expression = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#expression" );
    
    public static final Property factorQuantityKind = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#factorQuantityKind" );
    
    public static final Property generalQuantityKind = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#generalQuantityKind" );
    
    public static final Property name = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#name" );
    
    public static final Property numericalValue = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#numericalValue" );
    
    public static final Property prefix = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#prefix" );
    
    public static final Property propertyType = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#propertyType" );
    
    public static final Property quantityComponent = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#quantityComponent" );
    
    public static final Property quantityKind = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#quantityKind" );
    
    public static final Property quantityKindFactor = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#quantityKindFactor" );
    
    public static final Property referenceUnit = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#referenceUnit" );
    
    public static final Property scale = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#scale" );
    
    public static final Property scaleValueDefinition = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#scaleValueDefinition" );
    
    public static final Property specificQuantityKind = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#specificQuantityKind" );
    
    public static final Property symbol = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#symbol" );
    
    public static final Property symbolicExpression = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#symbolicExpression" );
    
    public static final Property systemOfQuantities = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#systemOfQuantities" );
    
    public static final Property unit = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#unit" );
    
    public static final Property unitComponent = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#unitComponent" );
    
    public static final Property unitFactor = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#unitFactor" );
    
    public static final Property unitKind = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#unitKind" );
    
    public static final Property unitMultipleFactor = m_model.createProperty( "http://purl.oclc.org/NET/ssnx/qu/qu#unitMultipleFactor" );
    
    public static final Resource ConversionBasedUnit = m_model.createResource( "http://purl.oclc.org/NET/ssnx/qu/qu#ConversionBasedUnit" );
    
    public static final Resource DerivedQuantityKind = m_model.createResource( "http://purl.oclc.org/NET/ssnx/qu/qu#DerivedQuantityKind" );
    
    public static final Resource DerivedUnit = m_model.createResource( "http://purl.oclc.org/NET/ssnx/qu/qu#DerivedUnit" );
    
    public static final Resource Dimension = m_model.createResource( "http://purl.oclc.org/NET/ssnx/qu/qu#Dimension" );
    
    public static final Resource DimensionFactor = m_model.createResource( "http://purl.oclc.org/NET/ssnx/qu/qu#DimensionFactor" );
    
    public static final Resource GeneralConversionUnit = m_model.createResource( "http://purl.oclc.org/NET/ssnx/qu/qu#GeneralConversionUnit" );
    
    public static final Resource LinearConversionUnit = m_model.createResource( "http://purl.oclc.org/NET/ssnx/qu/qu#LinearConversionUnit" );
    
    public static final Resource Prefix = m_model.createResource( "http://purl.oclc.org/NET/ssnx/qu/qu#Prefix" );
    
    public static final Resource PrefixedUnit = m_model.createResource( "http://purl.oclc.org/NET/ssnx/qu/qu#PrefixedUnit" );
    
    public static final Resource PropertyKind = m_model.createResource( "http://purl.oclc.org/NET/ssnx/qu/qu#PropertyKind" );
    
    public static final Resource QuantityKind = m_model.createResource( "http://purl.oclc.org/NET/ssnx/qu/qu#QuantityKind" );
    
    public static final Resource QuantityKindFactor = m_model.createResource( "http://purl.oclc.org/NET/ssnx/qu/qu#QuantityKindFactor" );
    
    public static final Resource Scale = m_model.createResource( "http://purl.oclc.org/NET/ssnx/qu/qu#Scale" );
    
    public static final Resource ScaleValueDefinition = m_model.createResource( "http://purl.oclc.org/NET/ssnx/qu/qu#ScaleValueDefinition" );
    
    public static final Resource SimpleQuantityKind = m_model.createResource( "http://purl.oclc.org/NET/ssnx/qu/qu#SimpleQuantityKind" );
    
    public static final Resource SimpleUnit = m_model.createResource( "http://purl.oclc.org/NET/ssnx/qu/qu#SimpleUnit" );
    
    public static final Resource SpecializedQuantityKind = m_model.createResource( "http://purl.oclc.org/NET/ssnx/qu/qu#SpecializedQuantityKind" );
    
    public static final Resource SystemOfQuantities = m_model.createResource( "http://purl.oclc.org/NET/ssnx/qu/qu#SystemOfQuantities" );
    
    public static final Resource SystemOfUnits = m_model.createResource( "http://purl.oclc.org/NET/ssnx/qu/qu#SystemOfUnits" );
    
    public static final Resource Unit = m_model.createResource( "http://purl.oclc.org/NET/ssnx/qu/qu#Unit" );
    
    public static final Resource UnitFactor = m_model.createResource( "http://purl.oclc.org/NET/ssnx/qu/qu#UnitFactor" );
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
}

/*
@footer@
*/
    
