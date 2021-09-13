package org.jacorb.orb;

public interface HelperOverride <T extends org.omg.CORBA.portable.IDLEntity> {
  
  T read(org.omg.CORBA.portable.InputStream in) throws org.omg.CORBA.SystemException;
  void write(org.omg.CORBA.portable.OutputStream out, T s) throws org.omg.CORBA.SystemException;
  
}