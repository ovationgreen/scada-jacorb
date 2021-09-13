package org.jacorb.orb;

public interface HelperOverride <T extends org.omg.CORBA.portable.IDLEntity> {
  
  void read(org.omg.CORBA.portable.InputStream in, T result) throws org.omg.CORBA.SystemException;
  void write(org.omg.CORBA.portable.OutputStream out, T s) throws org.omg.CORBA.SystemException;
  
}