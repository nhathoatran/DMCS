/**
 * generated by Xtext 2.10.0
 */
package scheduling.dsl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Template</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link scheduling.dsl.Template#getTemplate <em>Template</em>}</li>
 *   <li>{@link scheduling.dsl.Template#getBehavior <em>Behavior</em>}</li>
 *   <li>{@link scheduling.dsl.Template#getND_behavior <em>ND behavior</em>}</li>
 * </ul>
 *
 * @see scheduling.dsl.DslPackage#getTemplate()
 * @model
 * @generated
 */
public interface Template extends EObject
{
  /**
   * Returns the value of the '<em><b>Template</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Template</em>' containment reference.
   * @see #setTemplate(SetTemplate)
   * @see scheduling.dsl.DslPackage#getTemplate_Template()
   * @model containment="true"
   * @generated
   */
  SetTemplate getTemplate();

  /**
   * Sets the value of the '{@link scheduling.dsl.Template#getTemplate <em>Template</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Template</em>' containment reference.
   * @see #getTemplate()
   * @generated
   */
  void setTemplate(SetTemplate value);

  /**
   * Returns the value of the '<em><b>Behavior</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Behavior</em>' containment reference.
   * @see #setBehavior(Behavior)
   * @see scheduling.dsl.DslPackage#getTemplate_Behavior()
   * @model containment="true"
   * @generated
   */
  Behavior getBehavior();

  /**
   * Sets the value of the '{@link scheduling.dsl.Template#getBehavior <em>Behavior</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Behavior</em>' containment reference.
   * @see #getBehavior()
   * @generated
   */
  void setBehavior(Behavior value);

  /**
   * Returns the value of the '<em><b>ND behavior</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>ND behavior</em>' containment reference.
   * @see #setND_behavior(ND_Behavior)
   * @see scheduling.dsl.DslPackage#getTemplate_ND_behavior()
   * @model containment="true"
   * @generated
   */
  ND_Behavior getND_behavior();

  /**
   * Sets the value of the '{@link scheduling.dsl.Template#getND_behavior <em>ND behavior</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>ND behavior</em>' containment reference.
   * @see #getND_behavior()
   * @generated
   */
  void setND_behavior(ND_Behavior value);

} // Template
