package edu.cmu.ri.mrpl.TeRK.roboticon.messenger;

import java.util.ArrayList;
import java.util.List;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessage;

public class SubjectNode extends MessageHistoryNode
   {
   public MessageHistoryNode parent = null;
   public List<MessageHistoryNode> children = new ArrayList<MessageHistoryNode>(2);
   public String subject = "<no subject>";

   public SubjectNode(RoboticonMessage roboticonMessage)
      {
      this.roboticonMessage = roboticonMessage;
      String subject = roboticonMessage.theMessage.subject;
      if (subject != null && !"".equals(subject.trim()))
         {
         this.subject = subject;
         }
      }

   public SubjectNode(String subject)
      {
      if (subject != null && !"".equals(subject.trim()))
         {
         this.subject = subject;
         }

      this.roboticonMessage = null;
      }
   }
