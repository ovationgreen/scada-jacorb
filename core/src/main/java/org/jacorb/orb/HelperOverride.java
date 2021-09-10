package org.jacorb.orb;

public interface HelperOverride <T> {
  
  void read(org.omg.CORBA.portable.InputStream in, T object) throws org.omg.CORBA.SystemException;
  void write(org.omg.CORBA.portable.OutputStream out, T object) throws org.omg.CORBA.SystemException;
  
}