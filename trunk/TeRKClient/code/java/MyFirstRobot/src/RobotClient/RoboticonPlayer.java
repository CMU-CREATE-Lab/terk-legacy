package RobotClient;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkController;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions.Expression;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions.ExpressionFileHandler;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions.ExpressionLoader;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions.ExpressionSpeed;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.sequence.Sequence;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.sequence.SequenceFileHandler;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.sequence.SequencePlayer;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>RoboticonPlayer</code> is a helper class for playing roboticons (sequences and expressions).
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class RoboticonPlayer
   {
   private static final Logger LOG = Logger.getLogger(RoboticonPlayer.class);
   private static final RoboticonPlayer INSTANCE = new RoboticonPlayer();
   private static final String TERK_PATH = System.getProperty("user.home") + File.separator + "TeRK" + File.separator;
   private static final List<File> SEQUENCE_DIRECTORIES;
   private static final List<File> EXPRESSION_DIRECTORIES;
   private static final String XML_FILE_EXTENSION = ".xml";
   private static final ExpressionSpeed EXPRESSION_SPEED = new ExpressionSpeed(ExpressionSpeed.MEDIUM_VELOCITY);

   static
      {
      // build the collections of search directories for sequences and expressions
      final List<File> sequenceDirectories = new ArrayList<File>(3);
      sequenceDirectories.add(new File(TERK_PATH, "Sequences"));
      sequenceDirectories.add(new File(TERK_PATH, "Express-O-Matic" + File.separator + "Sequences"));
      sequenceDirectories.add(new File(TERK_PATH, "Flower" + File.separator + "Sequences"));
      SEQUENCE_DIRECTORIES = Collections.unmodifiableList(sequenceDirectories);

      final List<File> expressionDirectories = new ArrayList<File>(1);
      expressionDirectories.add(new File(TERK_PATH, "Expressions"));
      EXPRESSION_DIRECTORIES = Collections.unmodifiableList(expressionDirectories);
      }

   public static RoboticonPlayer getInstance()
      {
      return INSTANCE;
      }

   public void play(final String roboticonFilename, final QwerkController qwerkController, final MessageHandler messageHandler)
      {
      if (roboticonFilename != null)
         {
         if (qwerkController != null)
            {
            if (!playIfSequence(roboticonFilename, qwerkController, messageHandler))
               {
               if (!playIfExpression(roboticonFilename, qwerkController, messageHandler))
                  {
                  messageHandler.handleMessage("No sequence or expression exists with the name '" + roboticonFilename + "'");
                  }
               }
            }
         else
            {
            LOG.debug("RoboticonPlayer.play() is doing nothing since the given QwerkController was null");
            }
         }
      else
         {
         LOG.debug("RoboticonPlayer.play() is doing nothing since the given filename was null");
         }
      }

   private boolean playIfSequence(final String roboticonFilename, final QwerkController qwerkController, final MessageHandler messageHandler)
      {
      // try to find the sequence
      final File targetFile = findRoboticonFile(roboticonFilename, SEQUENCE_DIRECTORIES);

      // if this is a sequence, then try to play it
      if (targetFile != null)
         {
         // load the sequence
         final Sequence sequence = SequenceFileHandler.getInstance().openFile(targetFile);

         // if the load was successful, then play it
         if (sequence != null)
            {
            final SequencePlayer sequencePlayer = new SequencePlayer();
            messageHandler.handleMessage("Playing sequence '" + roboticonFilename + "'");
            sequencePlayer.playSequence(qwerkController, sequence);
            return true;
            }
         }

      return false;
      }

   private boolean playIfExpression(final String roboticonFilename, final QwerkController qwerkController, final MessageHandler messageHandler)
      {
      final File targetFile = findRoboticonFile(roboticonFilename, EXPRESSION_DIRECTORIES);

      // if this is an expression, then try to play it
      if (targetFile != null)
         {
         // load the expression
         final Expression expression = ExpressionFileHandler.getInstance().openFile(targetFile);

         // if the load was successful, then play it
         if (expression != null)
            {
            messageHandler.handleMessage("Playing expression '" + roboticonFilename + "'");
            ExpressionLoader.getInstance().loadToQwerk(qwerkController, expression, EXPRESSION_SPEED);
            return true;
            }
         }

      return false;
      }

   private File findRoboticonFile(final String roboticonFilename, final List<File> searchDirectories)
      {
      for (final File searchDirectory : searchDirectories)
         {
         if (searchDirectory.isDirectory() && searchDirectory.exists())
            {
            final File targetFile;

            // auto-append the xml file extension if necessary
            if (roboticonFilename.endsWith(XML_FILE_EXTENSION))
               {
               targetFile = new File(searchDirectory, roboticonFilename);
               }
            else
               {
               targetFile = new File(searchDirectory, roboticonFilename + XML_FILE_EXTENSION);
               }

            // return the file if it exists and is not a directory
            if (targetFile.exists() && targetFile.isFile())
               {
               return targetFile;
               }
            }
         }

      return null;
      }

   private RoboticonPlayer()
      {
      // private to prevent instantiation
      }
   }
