package edu.cmu.ri.mrpl.TeRK.servants;

import Ice.ObjectImpl;
import Ice.ObjectPrx;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface ServiceServantRegistrar
   {
   void registerServiceServant(final ObjectImpl serviceServant, final ObjectPrx serviceServantProxy);
   }
