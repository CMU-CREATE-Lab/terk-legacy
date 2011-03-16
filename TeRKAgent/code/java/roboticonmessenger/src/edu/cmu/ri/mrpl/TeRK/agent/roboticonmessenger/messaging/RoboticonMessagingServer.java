package edu.cmu.ri.mrpl.TeRK.agent.roboticonmessenger.messaging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import edu.cmu.ri.createlab.xml.XmlHelper;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.ClientRoboticonMessage;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.RoboticonMessengerHistory;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.RoboticonMessengerModel;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.XmlRoboticonMessengerHistory;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.Message;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.Roboticon;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessage;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class RoboticonMessagingServer
   {
   private static final Logger LOG = Logger.getLogger(RoboticonMessagingServer.class);

   private final Map<String, RoboticonMessagingClientService> clients = Collections.synchronizedMap(new HashMap<String, RoboticonMessagingClientService>());
   private final RoboticonMessengerModel roboticonMessengerModel;
   private final File messengerHistoryDirectory;
   private final String messengerHistoryFilename;
   private final String historyBackupFilename;
   private final Map<String, Long> userLastLogoutTimestampsMap = Collections.synchronizedMap(new HashMap<String, Long>());
   private final byte[] dataSynchronizationLock = new byte[0];

   public RoboticonMessagingServer(final RoboticonMessengerModel roboticonMessengerModel,
                                   final File messengerHistoryDirectory,
                                   final String messengerHistoryFilename,
                                   final String historyBackupFilename)
      {
      this.roboticonMessengerModel = roboticonMessengerModel;
      this.messengerHistoryDirectory = messengerHistoryDirectory;
      this.messengerHistoryFilename = messengerHistoryFilename;
      this.historyBackupFilename = historyBackupFilename;
      }

   public void loadHistory()
      {
      synchronized (dataSynchronizationLock)
         {
         final File historyFile = new File(messengerHistoryDirectory, messengerHistoryFilename);
         final File backupFile = new File(messengerHistoryDirectory, historyBackupFilename);
         RoboticonMessengerHistory history;
         RoboticonMessengerHistory backup;

         try
            {
            history = XmlRoboticonMessengerHistory.create(new FileInputStream(historyFile));
            LOG.debug("History file found and loaded");
            }
         catch (IOException x)
            {
            history = XmlRoboticonMessengerHistory.create();
            LOG.debug("History file not found, starting new history: ", x);
            }
         catch (JDOMException x)
            {
            history = XmlRoboticonMessengerHistory.create();
            LOG.fatal("History file corrupted : ", x);
            LOG.fatal("Check the " + messengerHistoryDirectory.getAbsolutePath() + " directory to inspect the backup file(s).");
            copyToBackup(historyFile, messengerHistoryDirectory, messengerHistoryFilename + ".corrupted.backup");
            }
         try
            {
            backup = XmlRoboticonMessengerHistory.create(new FileInputStream(backupFile));
            LOG.debug("Backup file found");
            }
         catch (IOException x)
            {
            backup = XmlRoboticonMessengerHistory.create();
            LOG.debug("Backup file not found: ", x);
            }
         catch (JDOMException x)
            {
            backup = XmlRoboticonMessengerHistory.create();
            LOG.fatal("Backup file corrupted : ", x);
            LOG.fatal("Check the " + messengerHistoryDirectory.getAbsolutePath() + " directory to inspect the backup file(s).");
            copyToBackup(historyFile, messengerHistoryDirectory, historyBackupFilename + ".corrupted.backup");
            }

         if (backup.getRoboticonMessages().size() > history.getRoboticonMessages().size())
            {
            history = backup;
            LOG.debug("Backup file exceeds history file, using backup..");
            }

         roboticonMessengerModel.setMessageHistory(history);
         }
      }

   public void sendPublicMessage(final String parentMessageId, final String senderUserId, final ClientRoboticonMessage clientRoboticonMessage)
      {
      if ((senderUserId != null) && (clientRoboticonMessage != null))
         {
         sendPublicMessage(parentMessageId,
                           senderUserId,
                           clientRoboticonMessage.getMessage(),
                           clientRoboticonMessage.getRoboticons());
         }
      }

   public void sendPublicMessage(final String parentMessageId, final String senderUserId, final Message message, final List<Roboticon> roboticons)
      {
      if ((senderUserId != null) && (message != null) && (roboticons != null))
         {
         // create the RoboticonMessage
         final RoboticonMessage roboticonMessage = new RoboticonMessage(System.currentTimeMillis(),
                                                                        senderUserId,
                                                                        "",
                                                                        false,
                                                                        message,
                                                                        roboticons,
                                                                        createMessageId(),
                                                                        (parentMessageId == null) ? "" : parentMessageId);

         // add it to the model
         roboticonMessengerModel.addMessage(roboticonMessage);

         // save history to disk
         saveHistory();

         // now broadcast the roboticon to all connected users
         synchronized (clients)
            {
            if (!clients.isEmpty())
               {
               for (final String userId : clients.keySet())
                  {
                  sendRoboticonToUser(roboticonMessage, userId);
                  }
               }
            }
         }
      }

   public void sendPrivateMessage(final String parentMessageId, final String senderUserId, final String recipientUserId, final ClientRoboticonMessage clientRoboticonMessage)
      {
      if ((senderUserId != null) && (recipientUserId != null) && (clientRoboticonMessage != null))
         {
         sendPrivateMessage(parentMessageId,
                            senderUserId,
                            recipientUserId,
                            clientRoboticonMessage.getMessage(),
                            clientRoboticonMessage.getRoboticons());
         }
      }

   public void sendPrivateMessage(final String parentMessageId, final String senderUserId, final String recipientUserId, final Message message, final List<Roboticon> roboticons)
      {
      if ((senderUserId != null) && (recipientUserId != null) && (message != null) && (roboticons != null))
         {
         // create the RoboticonMessage that will be sent to the recipient (only)
         final RoboticonMessage roboticonMessage = new RoboticonMessage(System.currentTimeMillis(),
                                                                        senderUserId,
                                                                        recipientUserId,
                                                                        true,
                                                                        message,
                                                                        roboticons,
                                                                        createMessageId(),
                                                                        (parentMessageId == null) ? "" : parentMessageId);

         // add it to the model
         roboticonMessengerModel.addMessage(roboticonMessage);

         // append it to the messenger history log
         saveHistory();

         // now send the roboticon to the sender and the intended recipient (unless they're
         // the same user, which shouldn't really happen anyway, but it's better to be safe)
         sendRoboticonToUser(roboticonMessage, recipientUserId);
         if (!recipientUserId.equals(senderUserId))
            {
            sendRoboticonToUser(roboticonMessage, senderUserId);
            }

         // now broadcast a censored version of the roboticon to all other connected users, iff the message
         // contains roboticon XML (since the XML part is always public)
         final RoboticonMessage censoredRoboticonMessage = censorRoboticonMessage(roboticonMessage);
         if (censoredRoboticonMessage != null)
            {
            synchronized (clients)
               {
               if (!clients.isEmpty())
                  {
                  for (final String userId : clients.keySet())
                     {
                     // don't send a duplicate to the sender or the private recipient
                     if ((userId != null) && !userId.equals(senderUserId) && !userId.equals(recipientUserId))
                        {
                        sendRoboticonToUser(censoredRoboticonMessage, userId);
                        }
                     }
                  }
               }
            }
         }
      }

   /**
    * Censors the given {@link RoboticonMessage}, if necessary.  If the given {@link RoboticonMessage} is public,
    * this method just returns the original object.  If the given {@link RoboticonMessage} is private and contains
    * a non-empty collection of Roboticons, then this method censors out the receipientUserId, the message, and the
    * message IDs.  If the given {@link RoboticonMessage} is private but doesn't contain any attached roboticons, this
    * method just returns <code>null</code>
    */
   private RoboticonMessage censorRoboticonMessage(final RoboticonMessage originalMessage)
      {
      if (originalMessage != null)
         {
         // we only need to censor private messages
         if (originalMessage.isPrivate)
            {
            // just return null for private messages that don't contain roboticon XML
            if (originalMessage.roboticons != null && originalMessage.roboticons.size() > 0)
               {
               // create a copy of the roboticons collection
               final List<Roboticon> roboticonsCopy = new ArrayList<Roboticon>(originalMessage.roboticons.size());
               for (final Roboticon roboticon : originalMessage.roboticons)
                  {
                  if (roboticon != null)
                     {
                     roboticonsCopy.add((Roboticon)roboticon.clone());
                     }
                  }

               return new RoboticonMessage(originalMessage.timestamp,
                                           originalMessage.senderUserId,
                                           "", // censor out the recipient
                                           true, // it's a private message
                                           new Message(), // censor out the message portion
                                           roboticonsCopy,
                                           "", // censor out the message id
                                           "");// censor out the parent message id
               }
            return null;
            }
         }
      return originalMessage;
      }

   private void sendRoboticonToUser(final RoboticonMessage roboticonMessage, final String userId)
      {
      final RoboticonMessagingClientService service = clients.get(userId);

      try
         {
         if (service != null)
            {
            service.handleRoboticonMessage(roboticonMessage);
            }
         else
            {
            LOG.debug("Client not connected to server, unable to handle message");
            }
         }
      catch (Exception e)
         {
         LOG.error("Exception while sending roboticon to user [" + userId + "]", e);
         }
      }

   private String createMessageId()
      {
      return UUID.randomUUID().toString();
      }

   private void copyToBackup(final File historyFile, final File tDirectory, final String tFilename)
      {
      if (!historyFile.exists())
         {
         return;
         }

      final File backupFile = new File(tDirectory, tFilename);

      //copy to backup
      FileInputStream historyStream = null;
      FileOutputStream backupStream = null;

      try
         {
         historyStream = new FileInputStream(historyFile);
         try
            {
            if (!backupFile.exists())
               {
               //noinspection ResultOfMethodCallIgnored
               backupFile.createNewFile();
               }
            backupStream = new FileOutputStream(backupFile, false);
            }
         catch (FileNotFoundException x)
            {
            LOG.error("Could not create backup file", x);
            return;
            }
         final byte[] buffer = new byte[4096];
         int bytesRead;

         while ((bytesRead = historyStream.read(buffer)) != -1)
            {
            backupStream.write(buffer, 0, bytesRead);// write
            }
         }
      catch (IOException x)
         {
         LOG.error("Error while copying data on disk", x);
         }
      finally
         {
         if (historyStream != null)
            {
            try
               {
               historyStream.close();
               }
            catch (IOException e)
               {
               LOG.error("Error closing file input stream", e);
               }
            }
         if (backupStream != null)
            {
            try
               {
               backupStream.close();
               }
            catch (IOException e)
               {
               LOG.error("Error closing backup output stream", e);
               }
            }
         }
      }

   private void saveHistory()
      {
      synchronized (dataSynchronizationLock)
         {
         if (!messengerHistoryDirectory.exists())
            {
            //noinspection ResultOfMethodCallIgnored
            messengerHistoryDirectory.mkdirs();
            }
         final File historyFile = new File(messengerHistoryDirectory, messengerHistoryFilename);

         LOG.debug("Backing up existing history...");
         copyToBackup(historyFile, messengerHistoryDirectory, historyBackupFilename);

         BufferedWriter writer = null;
         try
            {
            writer = new BufferedWriter(new FileWriter(historyFile, false));
            LOG.debug("Writing " + roboticonMessengerModel.getMessageHistory().size() + " messages to history file...");
            XmlHelper.writeDocToOutputStream(roboticonMessengerModel.getDocument(), writer);
            writer.close();
            LOG.debug("Write complete with no errors");
            }
         catch (Exception e)
            {
            LOG.error("Exception " + e + " while trying to save a message to the messenger history file [" + historyFile + "]", e);
            }
         finally
            {
            if (writer != null)
               {
               try
                  {
                  writer.close();
                  }
               catch (Exception e)
                  {
                  LOG.error("Exception while trying to close the BufferedWriter", e);
                  }
               }
            }
         }
      }

   List<RoboticonMessage> getMessageHistory(final String userId)
      {
      final List<RoboticonMessage> messages = roboticonMessengerModel.getMessageHistory();
      final List<RoboticonMessage> censoredMessages = new ArrayList<RoboticonMessage>();

      for (final RoboticonMessage message : messages)
         {
         // always include messages sent BY this user, or sent TO this user
         if (userId.equals(message.senderUserId) ||
             userId.equals(message.recipientUserId))
            {
            censoredMessages.add(message);
            }
         else
            {
            // censor private messages that weren't sent to or by this user
            final RoboticonMessage censoredMessage = censorRoboticonMessage(message);
            if (censoredMessage != null)
               {
               censoredMessages.add(censoredMessage);
               }
            }
         }
      return censoredMessages;
      }

   public long getLastLogoutTimestamp(final String userId)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("RoboticonMessagingServer.getLastLogoutTimestamp(" + userId + ")");
         }

      final Long timestamp = userLastLogoutTimestampsMap.get(userId);
      if (timestamp != null)
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("RoboticonMessagingServer.getLastLogoutTimestamp(): returning [" + timestamp + "]");
            }
         return timestamp;
         }
      LOG.debug("RoboticonMessagingServer.getLastLogoutTimestamp(): returning [-1]");
      return -1;
      }

   public void addClient(final String userId, final RoboticonMessagingClientService roboticonMessagingClientService)
      {
      if ((userId != null) && (roboticonMessagingClientService != null))
         {
         clients.put(userId, roboticonMessagingClientService);
         if (LOG.isDebugEnabled())
            {
            LOG.debug("There are now [" + clients.size() + "] messenger clients");
            }
         }
      }

   public void removeClient(final String userId)
      {
      if (userId != null)
         {
         // record this user's logout timestamp
         userLastLogoutTimestampsMap.put(userId, System.currentTimeMillis());

         // remove the client
         clients.remove(userId);
         if (LOG.isDebugEnabled())
            {
            LOG.debug("There are now [" + clients.size() + "] messenger clients");
            }
         }
      }

   public void clearHistory()
      {
      roboticonMessengerModel.clearHistory();
      }
   }