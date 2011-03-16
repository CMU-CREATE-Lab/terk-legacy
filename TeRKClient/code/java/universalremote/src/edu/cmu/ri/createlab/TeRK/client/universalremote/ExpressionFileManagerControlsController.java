package edu.cmu.ri.createlab.TeRK.client.universalremote;

import edu.cmu.ri.createlab.TeRK.expression.XmlExpression;
import edu.cmu.ri.createlab.TeRK.expression.manager.ExpressionFile;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
interface ExpressionFileManagerControlsController
   {
   void openExpression(final XmlExpression expression);

   void deleteExpression(final ExpressionFile selectedIndex);
   }
