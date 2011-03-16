package edu.cmu.ri.createlab.TeRK.client.robotdiaries;

import java.awt.Component;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface Application
   {
   /** Returns the name of this application. */
   String getName();

   /** Returns the main {@link Component} for this application. */
   Component getComponent();

   /** Causes this application to shut down, performing any required cleanup. */
   void shutdown();
   }