package edu.cmu.ri.mrpl.TeRK.relay;

import Glacier2.CannotCreateSessionException;
import Glacier2.SessionControlPrx;
import Glacier2.SessionPrx;
import Glacier2.SessionPrxHelper;
import Glacier2._SessionManagerDisp;
import Ice.Current;
import Ice.Identity;
import Ice.Util;
import edu.cmu.ri.mrpl.TeRK.model.TerkUser;
import edu.cmu.ri.mrpl.TeRK.persistence.HibernateUtil;
import edu.cmu.ri.mrpl.ice.util.IceUtil;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class TerkSessionManagerServant extends _SessionManagerDisp
   {
   private static final Logger LOG = Logger.getLogger(TerkSessionManagerServant.class);

   private final RelayConnectionManager relayConnectionManager;

   TerkSessionManagerServant(final RelayConnectionManager relayConnectionManager)
      {
      this.relayConnectionManager = relayConnectionManager;
      }

   public SessionPrx create(final String userId, final SessionControlPrx sessionControlPrx, final Current current) throws CannotCreateSessionException
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("TerkSessionManagerServant.create()" + IceUtil.dumpCurrentToString(current));
         }

      if (userId != null)
         {
         Session session = null;

         // first make sure that no one is already registered with this id
         if (LOG.isDebugEnabled())
            {
            LOG.debug("TerkSessionManagerServant.create() is unregistering user [" + userId + "]...");
            }
         relayConnectionManager.forceUnregister(userId);
         if (LOG.isDebugEnabled())
            {
            LOG.debug("TerkSessionManagerServant.create() is done unregistering user [" + userId + "]!");
            }

         try
            {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            // try to find this user
            final TerkUser user = QueryHelper.findTerkUserByUserId(session, userId);

            // fail if the user can't be found
            if (user == null)
               {
               final String message = "User [" + userId + "] cannot log in because the user doesn't exist.";
               LOG.error(message);
               session.getTransaction().rollback();
               throw new CannotCreateSessionException(message);
               }

            session.getTransaction().commit();

            final Identity identity = IceUtil.createIdentity(userId);
            if (LOG.isInfoEnabled())
               {
               LOG.info("Creating session for user [" + userId + "] using session identity [" + Util.identityToString(identity) + "]");
               }
            final Ice.Object sessionServant = new TerkUserSessionServant(user, relayConnectionManager);

            return SessionPrxHelper.uncheckedCast(current.adapter.add(sessionServant, identity));
            }
         catch (HibernateException e)
            {
            if (LOG.isEnabledFor(Level.ERROR))
               {
               LOG.error("HibernateException while trying to retrieve user [" + userId + "].  Rolling back and aborting.", e);
               }

            if (session != null)
               {
               session.getTransaction().rollback();
               }
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
         }
      throw new CannotCreateSessionException("Cannot create session for null userId");
      }
   }
