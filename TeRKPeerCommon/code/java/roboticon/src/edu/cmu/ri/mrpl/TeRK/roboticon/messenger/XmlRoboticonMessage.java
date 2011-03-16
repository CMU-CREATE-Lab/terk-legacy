package edu.cmu.ri.mrpl.TeRK.roboticon.messenger;

import java.util.ArrayList;
import java.util.List;
import edu.cmu.ri.createlab.xml.XmlObject;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.Message;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.Roboticon;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessage;
import org.jdom.Element;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class XmlRoboticonMessage extends XmlObject
   {
   static final String ELEMENT_NAME = "roboticon-message";
   private static final String ATTR_ID = "id";
   private static final String ATTR_PARENT_ID = "parent-id";
   private static final String ATTR_TIMESTAMP = "timestamp";
   private static final String ATTR_SENDER_USER_ID = "sender-user-id";
   private static final String ATTR_RECIPIENT_USER_ID = "recipient-user-id";
   private static final String ATTR_IS_PRIVATE = "is-private";

   private final RoboticonMessage roboticonMessage;

   XmlRoboticonMessage(final Element element)
      {
      super(element);

      final Element roboticonsElement = getElement().getChild(XmlRoboticons.ELEMENT_NAME);
      final List<Roboticon> roboticons = (roboticonsElement == null) ? null : new XmlRoboticons(roboticonsElement).getRoboticons();

      final String parentId = getElement().getAttributeValue(ATTR_PARENT_ID);
      final String parentMessageId = (parentId == null) ? "" : parentId;

      roboticonMessage = new RoboticonMessage(Long.parseLong(getElement().getAttributeValue(ATTR_TIMESTAMP)),
                                              getElement().getAttributeValue(ATTR_SENDER_USER_ID),
                                              getElement().getAttributeValue(ATTR_RECIPIENT_USER_ID),
                                              Boolean.parseBoolean(getElement().getAttributeValue(ATTR_IS_PRIVATE)),
                                              new XmlMessage(getElement().getChild(XmlMessage.ELEMENT_NAME)).toMessage(),
                                              roboticons,
                                              getElement().getAttributeValue(ATTR_ID),
                                              parentMessageId);
      }

   XmlRoboticonMessage(final RoboticonMessage roboticonMessage)
      {
      // clone the RoboticonMessage (so we can be sure this object remains immutable)
      this.roboticonMessage = deepClone(roboticonMessage);

      // set the XML attributes and content
      getElement().setName(ELEMENT_NAME);
      getElement().setAttribute(ATTR_ID, this.roboticonMessage.messageId);
      if (this.roboticonMessage.parentMessageId != null)
         {
         getElement().setAttribute(ATTR_PARENT_ID, this.roboticonMessage.parentMessageId);
         }
      getElement().setAttribute(ATTR_TIMESTAMP, String.valueOf(this.roboticonMessage.timestamp));
      getElement().setAttribute(ATTR_SENDER_USER_ID, this.roboticonMessage.senderUserId);
      if (this.roboticonMessage.recipientUserId != null)
         {
         getElement().setAttribute(ATTR_RECIPIENT_USER_ID, this.roboticonMessage.recipientUserId);
         }
      getElement().setAttribute(ATTR_IS_PRIVATE, String.valueOf(this.roboticonMessage.isPrivate));
      getElement().addContent(new XmlMessage(this.roboticonMessage.theMessage).toElement());
      final XmlRoboticons xmlRoboticons = (this.roboticonMessage.roboticons == null) ? new XmlRoboticons() : new XmlRoboticons(this.roboticonMessage.roboticons);
      getElement().addContent(xmlRoboticons.toElement());
      }

   RoboticonMessage toRoboticonMessage()
      {
      // return a clone so we can be sure this object remains immutable
      return deepClone(roboticonMessage);
      }

   private RoboticonMessage deepClone(final RoboticonMessage roboticonMessage)
      {
      final RoboticonMessage newRoboticonMessage = (RoboticonMessage)roboticonMessage.clone();
      newRoboticonMessage.theMessage = (Message)roboticonMessage.theMessage.clone();
      newRoboticonMessage.roboticons = new ArrayList<Roboticon>();
      if ((roboticonMessage.roboticons != null) && (!roboticonMessage.roboticons.isEmpty()))
         {
         for (final Roboticon roboticon : roboticonMessage.roboticons)
            {
            newRoboticonMessage.roboticons.add((Roboticon)roboticon.clone());
            }
         }

      return newRoboticonMessage;
      }

   public boolean equals(final Object o)
      {
      if (this == o)
         {
         return true;
         }
      if (o == null || getClass() != o.getClass())
         {
         return false;
         }

      final XmlRoboticonMessage that = (XmlRoboticonMessage)o;

      if (roboticonMessage != null ? !roboticonMessage.equals(that.roboticonMessage) : that.roboticonMessage != null)
         {
         return false;
         }

      return true;
      }

   public int hashCode()
      {
      return (roboticonMessage != null ? roboticonMessage.hashCode() : 0);
      }
   }
