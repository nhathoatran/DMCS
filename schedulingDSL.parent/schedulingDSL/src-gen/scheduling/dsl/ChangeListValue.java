/**
 * generated by Xtext 2.10.0
 */
package scheduling.dsl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Change List Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link scheduling.dsl.ChangeListValue#getLvar <em>Lvar</em>}</li>
 *   <li>{@link scheduling.dsl.ChangeListValue#getExp <em>Exp</em>}</li>
 * </ul>
 *
 * @see scheduling.dsl.DslPackage#getChangeListValue()
 * @model
 * @generated
 */
public interface ChangeListValue extends ChangeValue
{
  /**
   * Returns the value of the '<em><b>Lvar</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Lvar</em>' containment reference.
   * @see #setLvar(ListElement)
   * @see scheduling.dsl.DslPackage#getChangeListValue_Lvar()
   * @model containment="true"
   * @generated
   */
  ListElement getLvar();

  /**
   * Sets the value of the '{@link scheduling.dsl.ChangeListValue#getLvar <em>Lvar</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Lvar</em>' containment reference.
   * @see #getLvar()
   * @generated
   */
  void setLvar(ListElement value);

  /**
   * Returns the value of the '<em><b>Exp</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Exp</em>' containment reference.
   * @see #setExp(Expression)
   * @see scheduling.dsl.DslPackage#getChangeListValue_Exp()
   * @model containment="true"
   * @generated
   */
  Expression getExp();

  /**
   * Sets the value of the '{@link scheduling.dsl.ChangeListValue#getExp <em>Exp</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Exp</em>' containment reference.
   * @see #getExp()
   * @generated
   */
  void setExp(Expression value);

} // ChangeListValue
