package edu.cmu.ri.createlab.TeRK.client.robotdiaries;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface HummingbirdClientApplication extends Application
   {
   /** Tells the application to connect to the peer at the given hostname. */
   void connectToPeer(final String hostname);
   }