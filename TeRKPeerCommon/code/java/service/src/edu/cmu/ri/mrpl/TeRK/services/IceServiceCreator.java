package edu.cmu.ri.mrpl.TeRK.services;

import Ice.ObjectPrx;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface IceServiceCreator
   {
   Service create(final ObjectPrx serviceProxy);
   }
