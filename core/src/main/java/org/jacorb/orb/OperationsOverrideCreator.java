package org.jacorb.orb;

public interface OperationsOverrideCreator {
  
  <T extends org.jacorb.orb.Operations> org.jacorb.orb.OperationsOverride<T> create(Class<? extends org.jacorb.orb.Helper<? extends T>> helperClass);
  <T extends org.jacorb.orb.Operations> org.jacorb.orb.OperationsOverride<T> create(org.omg.CORBA.TypeCode type);
  
}