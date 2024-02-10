/**
 * generated by Xtext 2.10.0
 */
package scheduling.dsl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Proc Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link scheduling.dsl.ProcType#getProctype <em>Proctype</em>}</li>
 * </ul>
 *
 * @see scheduling.dsl.DslPackage#getProcType()
 * @model
 * @generated
 */
public interface ProcType extends EObject
{
  /**
   * Returns the value of the '<em><b>Proctype</b></em>' containment reference list.
   * The list contents are of type {@link scheduling.dsl.ProcessType}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Proctype</em>' containment reference list.
   * @see scheduling.dsl.DslPackage#getProcType_Proctype()
   * @model containment="true"
   * @generated
   */
  EList<ProcessType> getProctype();

} // ProcType
