package org.jacorb.orb;

public interface Helper <T extends org.omg.CORBA.portable.IDLEntity> {
  
  org.omg.CORBA.TypeCode typeCode ();
  
}