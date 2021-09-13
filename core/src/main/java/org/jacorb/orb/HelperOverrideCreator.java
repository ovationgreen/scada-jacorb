package org.jacorb.orb;

public interface HelperOverrideCreator {
  
  <T extends org.omg.CORBA.portable.IDLEntity> org.jacorb.orb.HelperOverride<T> create(Class<? extends org.jacorb.orb.Helper<T>> helperClass);
  
}