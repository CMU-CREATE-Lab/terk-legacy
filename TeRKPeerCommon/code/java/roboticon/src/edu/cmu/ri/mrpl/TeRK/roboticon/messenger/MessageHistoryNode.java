package edu.cmu.ri.mrpl.TeRK.roboticon.messenger;

import javax.swing.tree.DefaultMutableTreeNode;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessage;

abstract public class MessageHistoryNode extends DefaultMutableTreeNode
   {
   public RoboticonMessage roboticonMessage;

   public boolean hasAttachment()
      {
      if (roboticonMessage == null)
         {
         return false;
         }
      if (roboticonMessage.roboticons == null)
         {
         return false;
         }
      if (roboticonMessage.roboticons.size() > 0)
         {
         return true;
         }
      if (this.children == null)
         {
         return false;
         }

      for (Object n : this.children)
         {
         MessageHistoryNode node = (MessageHistoryNode)n;
         if (node.hasAttachment())
            {
            return true;
            }
         }

      return false;
      }
   }
