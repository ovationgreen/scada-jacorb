package org.jacorb.orb;

public interface TypeCodeOverride {
  
  org.omg.CORBA.TypeCode replace(org.omg.CORBA.TypeCode tc) throws org.omg.CORBA.SystemException;
  
}