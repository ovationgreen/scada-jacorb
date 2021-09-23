package org.jacorb.orb;

public interface HelperOverrideCreator {
  
  <T extends org.omg.CORBA.portable.IDLEntity> org.jacorb.orb.HelperOverride<T> create(Class<? extends org.jacorb.orb.Helper<T>> helperClass);
  <T extends org.omg.CORBA.portable.IDLEntity> org.jacorb.orb.HelperOverride<T> create(org.omg.CORBA.TypeCode type);
  
  org.omg.CORBA.Any read_any(org.omg.CORBA.portable.InputStream in);
  void write_any(org.omg.CORBA.portable.OutputStream out, org.omg.CORBA.Any value);
  
}