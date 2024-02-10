/**
 * generated by Xtext 2.10.0
 */
package scheduling.dsl.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import scheduling.dsl.DslPackage;
import scheduling.dsl.EnumType;
import scheduling.dsl.TypeName;
import scheduling.dsl.VDec;
import scheduling.dsl.VarDec;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Var Dec</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link scheduling.dsl.impl.VarDecImpl#getType <em>Type</em>}</li>
 *   <li>{@link scheduling.dsl.impl.VarDecImpl#getEnumtype <em>Enumtype</em>}</li>
 *   <li>{@link scheduling.dsl.impl.VarDecImpl#getName <em>Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class VarDecImpl extends MinimalEObjectImpl.Container implements VarDec
{
  /**
   * The default value of the '{@link #getType() <em>Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getType()
   * @generated
   * @ordered
   */
  protected static final TypeName TYPE_EDEFAULT = TypeName.BYTE;

  /**
   * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getType()
   * @generated
   * @ordered
   */
  protected TypeName type = TYPE_EDEFAULT;

  /**
   * The cached value of the '{@link #getEnumtype() <em>Enumtype</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEnumtype()
   * @generated
   * @ordered
   */
  protected EnumType enumtype;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected EList<VDec> name;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected VarDecImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return DslPackage.eINSTANCE.getVarDec();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public TypeName getType()
  {
    return type;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setType(TypeName newType)
  {
    TypeName oldType = type;
    type = newType == null ? TYPE_EDEFAULT : newType;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, DslPackage.VAR_DEC__TYPE, oldType, type));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EnumType getEnumtype()
  {
    if (enumtype != null && enumtype.eIsProxy())
    {
      InternalEObject oldEnumtype = (InternalEObject)enumtype;
      enumtype = (EnumType)eResolveProxy(oldEnumtype);
      if (enumtype != oldEnumtype)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, DslPackage.VAR_DEC__ENUMTYPE, oldEnumtype, enumtype));
      }
    }
    return enumtype;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EnumType basicGetEnumtype()
  {
    return enumtype;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setEnumtype(EnumType newEnumtype)
  {
    EnumType oldEnumtype = enumtype;
    enumtype = newEnumtype;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, DslPackage.VAR_DEC__ENUMTYPE, oldEnumtype, enumtype));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<VDec> getName()
  {
    if (name == null)
    {
      name = new EObjectContainmentEList<VDec>(VDec.class, this, DslPackage.VAR_DEC__NAME);
    }
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case DslPackage.VAR_DEC__NAME:
        return ((InternalEList<?>)getName()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case DslPackage.VAR_DEC__TYPE:
        return getType();
      case DslPackage.VAR_DEC__ENUMTYPE:
        if (resolve) return getEnumtype();
        return basicGetEnumtype();
      case DslPackage.VAR_DEC__NAME:
        return getName();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case DslPackage.VAR_DEC__TYPE:
        setType((TypeName)newValue);
        return;
      case DslPackage.VAR_DEC__ENUMTYPE:
        setEnumtype((EnumType)newValue);
        return;
      case DslPackage.VAR_DEC__NAME:
        getName().clear();
        getName().addAll((Collection<? extends VDec>)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case DslPackage.VAR_DEC__TYPE:
        setType(TYPE_EDEFAULT);
        return;
      case DslPackage.VAR_DEC__ENUMTYPE:
        setEnumtype((EnumType)null);
        return;
      case DslPackage.VAR_DEC__NAME:
        getName().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case DslPackage.VAR_DEC__TYPE:
        return type != TYPE_EDEFAULT;
      case DslPackage.VAR_DEC__ENUMTYPE:
        return enumtype != null;
      case DslPackage.VAR_DEC__NAME:
        return name != null && !name.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (type: ");
    result.append(type);
    result.append(')');
    return result.toString();
  }

} //VarDecImpl
