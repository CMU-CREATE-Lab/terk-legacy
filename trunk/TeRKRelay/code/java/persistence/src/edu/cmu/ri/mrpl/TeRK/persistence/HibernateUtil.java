package edu.cmu.ri.mrpl.TeRK.persistence;

import org.apache.log4j.Logger;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * <p>
 * Basic Hibernate helper class for Hibernate configuration and startup.
 * </p>
 * <p>
 * Uses a static initializer to read startup options and initialize {@link Configuration} and
 * {@link SessionFactory}.
 * </p>
 * <p>
 * If you want to assign a global interceptor, set its fully qualified class name with the system (or
 * <code>hibernate.properties</code>/<code>hibernate.cfg.xml</code>) property
 * <code>hibernate.util.interceptor_class</code>. It will be loaded and instantiated on static initialization of
 * HibernateUtil; it has to have a no-argument constructor.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 * @author christian@hibernate.org
 */
public final class HibernateUtil
   {
   private static final Logger LOG = Logger.getLogger(HibernateUtil.class);

   private static final String INTERCEPTOR_CLASS = "hibernate.util.interceptor_class";

   private static final SessionFactory SESSION_FACTORY;

   static
      {
      try
         {
         // Read not only hibernate.properties, but also hibernate.cfg.xml
         final Configuration configuration = new Configuration().configure();

         // Load the global interceptor class, if specified in the config file
         final String interceptorClassName = configuration.getProperty(INTERCEPTOR_CLASS);
         if (interceptorClassName != null)
            {
            try
               {
               final Class interceptorClass = HibernateUtil.class.getClassLoader().loadClass(interceptorClassName);
               final Interceptor interceptor = (Interceptor)interceptorClass.newInstance();
               configuration.setInterceptor(interceptor);
               if (LOG.isInfoEnabled())
                  {
                  LOG.info("Registered Hibernate interceptor class: " + interceptorClass);
                  }
               }
            catch (Exception ex)
               {
               throw new RuntimeException("Could not configure interceptor: " + interceptorClassName, ex);
               }
            }

         // Create the SessionFactory from hibernate.cfg.xml
         SESSION_FACTORY = configuration.buildSessionFactory();
         }
      catch (Throwable ex)
         {
         // Make sure to log the exception, as it might be swallowed
         LOG.fatal("Initial SessionFactory creation failed." + ex);
         throw new ExceptionInInitializerError(ex);
         }
      }

   public static SessionFactory getSessionFactory()
      {
      return SESSION_FACTORY;
      }

   private HibernateUtil()
      {
      // private to prevent instantiation
      }
   }