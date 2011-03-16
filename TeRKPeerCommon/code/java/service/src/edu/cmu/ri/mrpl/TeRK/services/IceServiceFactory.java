package edu.cmu.ri.mrpl.TeRK.services;

import Ice.ObjectPrx;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface IceServiceFactory
   {
   Service createService(final String serviceTypeId, final ObjectPrx serviceProxy);
   }
