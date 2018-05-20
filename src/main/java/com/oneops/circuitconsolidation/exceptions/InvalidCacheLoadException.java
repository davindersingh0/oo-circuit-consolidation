package com.oneops.circuitconsolidation.exceptions;

public final class InvalidCacheLoadException extends RuntimeException {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public InvalidCacheLoadException() {
    super();
  }

  public InvalidCacheLoadException(String message) {
    super(message);
  }

  public InvalidCacheLoadException(Exception e) {
    super(e);
  }

  public InvalidCacheLoadException(String message, Exception e) {
    super(message, e);
  }
}

