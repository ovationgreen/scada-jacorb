package org.jacorb.orb;

public interface OperationsOverride <T extends org.jacorb.orb.Operations> {
  
  org.omg.CORBA.portable.OutputStream _invoke(
      String method,
      org.omg.CORBA.portable.InputStream is,
      org.omg.CORBA.portable.ResponseHandler handler,
      OperationsBase operations
  );
  
  <T> T _invoke(
      String method,
      boolean responseExpected,
      RequestMethod _request,
      InvokeMethod _invoke,
      java.util.concurrent.atomic.AtomicBoolean overridden,
      Object ... arguments
  )
    throws
      org.omg.CORBA.portable.ApplicationException,
      org.omg.CORBA.portable.RemarshalException
  ;
  
  @FunctionalInterface
  public static interface RequestMethod {
    org.omg.CORBA.portable.OutputStream call(String method, boolean responseExpected);
  };
  
  @FunctionalInterface
  public static interface InvokeMethod {
    org.omg.CORBA.portable.InputStream call(org.omg.CORBA.portable.OutputStream output)
        throws org.omg.CORBA.portable.ApplicationException, org.omg.CORBA.portable.RemarshalException;
  };
  
}