/**
 * generated by Xtext 2.10.0
 */
package scheduling.dsl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Process Data Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link scheduling.dsl.ProcessDataDef#getProperties <em>Properties</em>}</li>
 * </ul>
 *
 * @see scheduling.dsl.DslPackage#getProcessDataDef()
 * @model
 * @generated
 */
public interface ProcessDataDef extends EObject
{
  /**
   * Returns the value of the '<em><b>Properties</b></em>' containment reference list.
   * The list contents are of type {@link scheduling.dsl.ProcessPropertyDef}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Properties</em>' containment reference list.
   * @see scheduling.dsl.DslPackage#getProcessDataDef_Properties()
   * @model containment="true"
   * @generated
   */
  EList<ProcessPropertyDef> getProperties();

} // ProcessDataDef