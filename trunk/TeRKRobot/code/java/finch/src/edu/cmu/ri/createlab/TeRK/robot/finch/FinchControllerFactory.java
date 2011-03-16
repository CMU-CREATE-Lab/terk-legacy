package edu.cmu.ri.createlab.TeRK.robot.finch;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.DefaultFinchController;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>FinchControllerFactory</code> creates {@link FinchController}s.  Users of this class must specify the
 * {@link FinchController} to be used by defining a Java system property called <code>finch-controller.class.name</code>
 * whose value should be the full classname of the implementation class.  The implementation class must have a
 * constructor which takes a single {@link String} argument which is the name of the serial port.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FinchControllerFactory
   {
   private static final Logger LOG = Logger.getLogger(FinchControllerFactory.class);

   private static final FinchControllerFactory INSTANCE = new FinchControllerFactory();

   public static final String FINCH_CONTROLLER_CLASS_NAME_PROPERTY = "finch-controller.class.name";

   public static FinchControllerFactory getInstance()
      {
      return INSTANCE;
      }

   private static boolean isFinchControllerImplementationClassDefined()
      {
      return System.getProperty(FINCH_CONTROLLER_CLASS_NAME_PROPERTY) != null;
      }

   private static FinchController instantiateFinchController(final String finchControllerClassName, final String serialPortName)
      {
      try
         {
         final Class clazz = Class.forName(finchControllerClassName);
         final Constructor constructor = clazz.getConstructor(String.class);
         if (constructor != null)
            {
            final FinchController tempFinchController = (FinchController)constructor.newInstance(serialPortName);
            if (tempFinchController == null)
               {
               LOG.error("FinchControllerFactory.instantiateFinchController():  Instantiation of FinchController implementation [" + finchControllerClassName + "] returned null.  Weird.");
               }
            else
               {
               return tempFinchController;
               }
            }
         }
      catch (ClassNotFoundException e)
         {
         LOG.error("ClassNotFoundException while trying to find FinchController implementation [" + finchControllerClassName + "]", e);
         }
      catch (NoSuchMethodException e)
         {
         LOG.error("NoSuchMethodException while trying to find the constructor accepting a single String for FinchController implementation [" + finchControllerClassName + "]", e);
         }
      catch (IllegalAccessException e)
         {
         LOG.error("IllegalAccessException while trying to instantiate FinchController implementation [" + finchControllerClassName + "]", e);
         }
      catch (InvocationTargetException e)
         {
         LOG.error("InvocationTargetException while trying to instantiate FinchController implementation [" + finchControllerClassName + "]", e);
         }
      catch (InstantiationException e)
         {
         LOG.error("InstantiationException while trying to instantiate FinchController implementation [" + finchControllerClassName + "]", e);
         }
      catch (Exception e)
         {
         LOG.error("Exception while trying to instantiate FinchController implementation [" + finchControllerClassName + "]", e);
         }

      return null;
      }

   private FinchControllerFactory()
      {
      // private to prevent instantiation
      }

   /**
    * Creates a {@link FinchController} for the serial port specified by the given <code>serialPortName</code>.  Throws
    * an {@link Exception} if creation fails.
    */
   public FinchController create(final String serialPortName) throws Exception
      {
      if (isFinchControllerImplementationClassDefined())
         {
         final String systemPropertyFinchControllerClassName = System.getProperty(FINCH_CONTROLLER_CLASS_NAME_PROPERTY);
         if (LOG.isDebugEnabled())
            {
            LOG.debug("FinchControllerFactory.create(): attempting to instantiate FinchController implementation class [" + systemPropertyFinchControllerClassName + "]");
            }
         FinchController finchController = instantiateFinchController(systemPropertyFinchControllerClassName, serialPortName);
         if (finchController == null)
            {
            if (LOG.isEnabledFor(Level.ERROR))
               {
               LOG.error("FinchControllerFactory.create(): System property [" + FINCH_CONTROLLER_CLASS_NAME_PROPERTY + "] specifies an invalid FinchController implementation class [" + systemPropertyFinchControllerClassName + "].  Attempting to use default implementation class instead.");
               }

            finchController = new DefaultFinchController(serialPortName);
            if (LOG.isDebugEnabled())
               {
               LOG.debug("FinchControllerFactory.create(): successfully instantiated DefaultFinchController implementation class");
               }
            return finchController;
            }
         else
            {
            if (LOG.isDebugEnabled())
               {
               LOG.debug("FinchControllerFactory.create(): successfully instantiated FinchController implementation class [" + systemPropertyFinchControllerClassName + "]");
               }
            return finchController;
            }
         }
      else
         {
         if (LOG.isInfoEnabled())
            {
            LOG.info("FinchControllerFactory.create(): System property [" + FINCH_CONTROLLER_CLASS_NAME_PROPERTY + "] is not defined.  Attempting to use default implementation class instead.");
            }
         final FinchController finchController = new DefaultFinchController(serialPortName);
         if (LOG.isDebugEnabled())
            {
            LOG.debug("FinchControllerFactory.create(): successfully instantiated DefaultFinchController implementation class");
            }
         return finchController;
         }
      }
   }
