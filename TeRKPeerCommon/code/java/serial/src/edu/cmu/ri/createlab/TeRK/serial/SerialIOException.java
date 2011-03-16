package edu.cmu.ri.createlab.TeRK.serial;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class SerialIOException extends Exception
   {
   public SerialIOException()
      {
      }

   public SerialIOException(final String message)
      {
      super(message);
      }

   public SerialIOException(final String message, final Throwable cause)
      {
      super(message, cause);
      }

   public SerialIOException(final Throwable cause)
      {
      super(cause);
      }
   }
