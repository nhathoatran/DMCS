/**
 * generated by Xtext 2.10.0
 */
package scheduling.dsl;

import java.lang.String;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Qualified Names</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link scheduling.dsl.QualifiedNames#getName <em>Name</em>}</li>
 *   <li>{@link scheduling.dsl.QualifiedNames#getProp <em>Prop</em>}</li>
 * </ul>
 *
 * @see scheduling.dsl.DslPackage#getQualifiedNames()
 * @model
 * @generated
 */
public interface QualifiedNames extends EObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see scheduling.dsl.DslPackage#getQualifiedNames_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link scheduling.dsl.QualifiedNames#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Prop</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Prop</em>' containment reference.
   * @see #setProp(ProcessPropertyName)
   * @see scheduling.dsl.DslPackage#getQualifiedNames_Prop()
   * @model containment="true"
   * @generated
   */
  ProcessPropertyName getProp();

  /**
   * Sets the value of the '{@link scheduling.dsl.QualifiedNames#getProp <em>Prop</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Prop</em>' containment reference.
   * @see #getProp()
   * @generated
   */
  void setProp(ProcessPropertyName value);

} // QualifiedNames