package org.jacorb.orb;

public abstract class ServantPOA <T extends org.jacorb.orb.Operations> extends org.omg.PortableServer.Servant {
  
  public static org.jacorb.orb.OperationsOverrideHook operationsOverrideHook;
  
}