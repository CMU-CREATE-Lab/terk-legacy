package edu.cmu.ri.mrpl.TeRK.roboticon.messenger;

import java.io.IOException;
import java.util.List;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessage;
import org.jdom.Document;
import org.jdom.JDOMException;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface RoboticonMessengerHistory
   {
   void addRoboticonMessage(final RoboticonMessage roboticonMessage);

   List<RoboticonMessage> getRoboticonMessages();

   Document getDocument() throws IOException, JDOMException;

   void clear();
   }