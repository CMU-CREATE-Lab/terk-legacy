package edu.cmu.ri.mrpl.TeRK.roboticon.messenger;

import edu.cmu.ri.createlab.xml.XmlObject;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.Message;
import org.jdom.Element;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class XmlMessage extends XmlObject
   {
   static final String ELEMENT_NAME = "message";
   private static final String ATTR_SUBJECT = "subject";

   private final Message message;

   XmlMessage(final Element element)
      {
      super(element);
      message = new Message(getElement().getAttributeValue(ATTR_SUBJECT),
                            getElement().getTextTrim());
      }

   XmlMessage(final Message theMessage)
      {
      message = (Message)theMessage.clone();
      getElement().setName(ELEMENT_NAME);
      getElement().setAttribute(ATTR_SUBJECT, message.subject);
      getElement().addContent(message.text);
      }

   Message toMessage()
      {
      // return a clone so we can be sure this object remains immutable
      return (Message)message.clone();
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

      final XmlMessage that = (XmlMessage)o;

      if (message != null ? !message.equals(that.message) : that.message != null)
         {
         return false;
         }

      return true;
      }

   public int hashCode()
      {
      return (message != null ? message.hashCode() : 0);
      }
   }