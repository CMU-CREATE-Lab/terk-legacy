package edu.cmu.ri.createlab.TeRK.client.expressomatic.expressions;

import java.io.File;
import java.util.List;
import java.util.Set;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.expression.XmlExpression;
import edu.cmu.ri.createlab.TeRK.expression.XmlOperation;
import edu.cmu.ri.createlab.TeRK.expression.XmlService;
import edu.cmu.ri.mrpl.TeRK.services.OperationExecutor;
import edu.cmu.ri.mrpl.TeRK.services.Service;
import edu.cmu.ri.mrpl.TeRK.services.ServiceManager;
import org.apache.log4j.Logger;

/**
 * This takes expressions and executes them on the ServiceManager
 * @author Alex Styler & Mel Ludowise
 */
public class ExpressionLoader
   {
   private static final Logger LOG = Logger.getLogger(ExpressionLoader.class);

   private static final ExpressionLoader INSTANCE = new ExpressionLoader();

   private static final File AUDIO_DIRECTORY = new File(TerkConstants.FilePaths.TERK_PATH + "Audio");

   public static ExpressionLoader getInstance()
      {
      return INSTANCE;
      }

   private ExpressionLoader()
      {
      // private to prevent instantiation
      }

   public void executeToServices(final ServiceManager serviceManager, final XmlExpression newExpression)
      {
      final Set<XmlService> serviceCommands = newExpression.getServices();
      if (serviceCommands != null)
         {
         for (final XmlService serviceCommand : serviceCommands)
            {
            final Service executingService = serviceManager.getServiceByTypeId(serviceCommand.getTypeId());
            if (executingService != null)
               {
               final List<XmlOperation> opCommands = serviceCommand.getOperations();
               for (final XmlOperation op : opCommands)
                  {
                  if (executingService instanceof OperationExecutor)
                     {
                     try
                        {
                        ((OperationExecutor)executingService).executeOperation(op);
                        }
                     catch (UnsupportedOperationException e)
                        {
                        LOG.error("UnsupportedOperationException while trying to execute the operation on the [" + serviceCommand.getTypeId() + "] service.  Ignoring and continuing.", e);
                        }
                     }
                  else
                     {
                     LOG.warn("Operation not executed since service [" + executingService.getTypeId() + "] does not implement the OperationExecutor interface.");
                     }
                  }
               }
            else
               {
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("Service " + serviceCommand.getTypeId() + " not available for execution.");
                  }
               }
            }
         }
      }

   @SuppressWarnings({"PrimitiveArrayArgumentToVariableArgMethod"})
   public void stop(final ServiceManager serviceManager)
      {

      }
   }
