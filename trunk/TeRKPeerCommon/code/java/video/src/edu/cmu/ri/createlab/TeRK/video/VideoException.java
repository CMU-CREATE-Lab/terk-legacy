package edu.cmu.ri.createlab.TeRK.video;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class VideoException extends Exception
   {
   public VideoException()
      {
      }

   public VideoException(final String message)
      {
      super(message);
      }

   public VideoException(final String message, final Throwable cause)
      {
      super(message, cause);
      }

   public VideoException(final Throwable cause)
      {
      super(cause);
      }
   }
