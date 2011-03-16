package edu.cmu.ri.mrpl.TeRK.roboticon.messenger;

import java.util.HashMap;
import java.util.List;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessage;
import org.apache.log4j.Logger;

public class AltMessageHistoryTreeModel extends DefaultTreeModel
   {

   private static final long serialVersionUID = 4068700159772929959L;

   private static final Logger LOG = Logger.getLogger(AltMessageHistoryTreeModel.class);

   private SubjectNode _rootNode;
   private HashMap<String, MessageHistoryNode> _messageMap;

   public AltMessageHistoryTreeModel(String rootNodeName)
      {
      super(null);
      super.setAsksAllowsChildren(false);
      _messageMap = new HashMap<String, MessageHistoryNode>();
      _rootNode = new SubjectNode(rootNodeName);
      super.setRoot(_rootNode);
      }

   public void clear()
      {
      //CLEAR existing messages
      int[] indices = new int[_rootNode.getChildCount()];
      Object[] children = new Object[_rootNode.getChildCount()];
      if (_rootNode.getChildCount() > 0)
         {
         for (int index = _rootNode.getChildCount() - 1; index >= 0; index--)
            {
            indices[index] = index;
            children[index] = _rootNode.getChildAt(index);
            _rootNode.remove(index);
            }
         _messageMap.clear();
         }

      super.nodesWereRemoved(_rootNode, indices, children);
      }

   /** Removes current contents and loads list of messages into model */
   public void loadMessages(List<RoboticonMessage> messages)
      {
      //Load new messages
      if (messages != null && !messages.isEmpty())
         {
         LOG.debug("Loading " + messages.size()
                   + " new messages into MessageHistoryTreeModel");
         for (RoboticonMessage message : messages)
            {
            this.addMessage(message);
            }
         }
      }

   public MessageHistoryNode addMessage(RoboticonMessage message)
      {
      MessageHistoryNode newNode = null;
      if (message != null)
         {
         newNode = this.addMessageToModel(message);
         }
      return newNode;
      }

   private MessageHistoryNode addMessageToModel(RoboticonMessage message)
      {
      MessageHistoryNode newNode = null;
      if (message != null)
         {
         MessageNode messageNode = new MessageNode(message);
         _messageMap.put(message.messageId, messageNode);
         int childIndex = -1;
         //IF message has parent in the message map
         //ADD message as child of parent
         if (_messageMap.containsKey(message.parentMessageId))
            {
            MessageHistoryNode parentNode = _messageMap.get(message.parentMessageId);
            newNode = messageNode;
            childIndex = parentNode.getChildCount();
            parentNode.insert(messageNode, childIndex);
            super.nodesWereInserted(parentNode, new int[]{childIndex});
            do
               {
               super.nodeChanged(parentNode);
               parentNode = (MessageHistoryNode)parentNode.getParent();
               }
            while (parentNode != null && parentNode != _rootNode);
            }
         //ELSE, message does not have a parent
         //ADD new subject and add message as child of subject
         else
            {
            SubjectNode newSubjectNode =
                  new SubjectNode(message);
            newNode = newSubjectNode;
            childIndex = _rootNode.getChildCount();
            _rootNode.insert(newSubjectNode, childIndex);
            newSubjectNode.insert(messageNode, newSubjectNode.getChildCount());
            super.nodesWereInserted(_rootNode, new int[]{childIndex});
            }
         }
      return newNode;
      }

   public void addTreeModelListener(TreeModelListener listener)
      {
      super.addTreeModelListener(listener);
      }

   public boolean isLeaf(Object node)
      {
      MessageHistoryNode messageHistoryNode = (MessageHistoryNode)node;
      boolean leafFlag = true;
      if (messageHistoryNode != null)
         {
         leafFlag = messageHistoryNode.isLeaf();
         }
      return leafFlag;
      }

   public MessageHistoryNode getNode(RoboticonMessage message)
      {
      MessageHistoryNode messageNode = null;
      if (message != null)
         {
         messageNode = _messageMap.get(message.messageId);
         }
      return messageNode;
      }
   }
