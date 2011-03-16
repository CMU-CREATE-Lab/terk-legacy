package edu.cmu.ri.mrpl.TeRK.relay;

import edu.cmu.ri.mrpl.TeRK.model.TerkUser;
import edu.cmu.ri.mrpl.TeRK.persistence.HibernateUtil;
import edu.cmu.ri.mrpl.util.security.EncryptionService;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 * <p>
 * <code>AuthenticationService</code> authenticates {@link TerkUser}s.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class AuthenticationService
   {
   private static final Logger LOG = Logger.getLogger(AuthenticationService.class);
   private static final AuthenticationService INSTANCE = new AuthenticationService();

   public static AuthenticationService getInstance()
      {
      return INSTANCE;
      }

   boolean isValidLogin(final String userId, final String password)
      {
      final String encryptedPassword = EncryptionService.getInstance().encrypt(password);
      Session session = null;
      boolean isValidLogin = false;

      try
         {
         session = HibernateUtil.getSessionFactory().openSession();
         session.beginTransaction();

         isValidLogin = QueryHelper.isValidLogin(session, userId, encryptedPassword);

         session.getTransaction().commit();
         }
      catch (HibernateException e)
         {
         if (session != null)
            {
            session.getTransaction().rollback();
            }
         LOG.error("Ignoring HibernateException while trying to validate user [" + userId + "]", e);
         }
      finally
         {
         if (session != null && session.isOpen())
            {
            try
               {
               session.close();
               }
            catch (HibernateException e)
               {
               LOG.error("HibernateException while trying to close the session.", e);
               }
            }
         }

      return isValidLogin;
      }

   private AuthenticationService()
      {
      // private to prevent instantiation
      }
   }
