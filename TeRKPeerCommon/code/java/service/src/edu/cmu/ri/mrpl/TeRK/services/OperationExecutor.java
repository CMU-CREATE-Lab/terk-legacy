package edu.cmu.ri.mrpl.TeRK.services;

import edu.cmu.ri.createlab.TeRK.expression.XmlOperation;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface OperationExecutor
   {
   Object executeOperation(final XmlOperation o);
   }