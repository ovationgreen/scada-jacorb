package org.jacorb.orb;

public interface OperationsOverride <T extends org.jacorb.orb.Operations> extends org.omg.CORBA.portable.InvokeHandler {
  
  <T> T _invoke(
      String operation,
      boolean responseExpected,
      RequestMethod _request,
      InvokeMethod _invoke,
      java.util.concurrent.atomic.AtomicBoolean overridden
  );
  
  public static interface RequestMethod {
    
    org.omg.CORBA.portable.OutputStream _request(String operation, boolean responseExpected);
    
  };
  
  public static interface InvokeMethod {
    
    org.omg.CORBA.portable.InputStream _invoke(org.omg.CORBA.portable.OutputStream output)
        throws org.omg.CORBA.portable.ApplicationException, org.omg.CORBA.portable.RemarshalException;
    
  };
  
}