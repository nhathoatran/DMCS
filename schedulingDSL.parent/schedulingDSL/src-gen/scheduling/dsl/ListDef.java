/**
 * generated by Xtext 2.10.0
 */
package scheduling.dsl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>List Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link scheduling.dsl.ListDef#getList <em>List</em>}</li>
 * </ul>
 *
 * @see scheduling.dsl.DslPackage#getListDef()
 * @model
 * @generated
 */
public interface ListDef extends EObject
{
  /**
   * Returns the value of the '<em><b>List</b></em>' containment reference list.
   * The list contents are of type {@link scheduling.dsl.List}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>List</em>' containment reference list.
   * @see scheduling.dsl.DslPackage#getListDef_List()
   * @model containment="true"
   * @generated
   */
  EList<List> getList();

} // ListDef