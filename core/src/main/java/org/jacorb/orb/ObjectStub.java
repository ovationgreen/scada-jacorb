package org.jacorb.orb;

public abstract class ObjectStub <T extends org.jacorb.orb.Operations> extends org.omg.CORBA.portable.ObjectImpl {
  
  public static org.jacorb.orb.OperationsOverrideHook operationsOverrideHook;
  
}