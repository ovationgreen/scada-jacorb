package org.jacorb.orb;

public interface HelperOverrideCreator {
  
  <T> org.jacorb.orb.HelperOverride<T> create(Class<?> helperClass);
  
}