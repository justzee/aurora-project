/*
 * Created on 2009-6-26
 */
package uncertain.schema;


public interface IType extends IQualifiedNamed  {
    
    public boolean isComplex();
    
    /** if this type is extension of specified IType */
    public boolean isExtensionOf( IType another );    

}
