package edu.cmu.ri.mrpl.TeRK.roboticon.messenger;

import edu.cmu.ri.createlab.xml.XmlObject;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.Roboticon;
import edu.cmu.ri.mrpl.util.security.Base64Coder;
import org.jdom.CDATA;
import org.jdom.Element;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class XmlRoboticon extends XmlObject
   {
   static final String ELEMENT_NAME = "roboticon";
   private static final String ATTR_TIMESTAMP = "timestamp";
   private static final String ATTR_FILENAME = "filename";

   private final Roboticon roboticon;

   XmlRoboticon(final Element element)
      {
      super(element);
      roboticon = new Roboticon(Long.parseLong(getElement().getAttributeValue(ATTR_TIMESTAMP)),
                                getElement().getAttributeValue(ATTR_FILENAME),
                                Base64Coder.decode(getElement().getTextTrim()),
                                "Unknown");
      }

   XmlRoboticon(final Roboticon roboticon)
      {
      this.roboticon = (Roboticon)roboticon.clone();
      getElement().setName(ELEMENT_NAME);
      getElement().setAttribute(ATTR_TIMESTAMP, String.valueOf(this.roboticon.timestamp));
      getElement().setAttribute(ATTR_FILENAME, this.roboticon.filename);
      getElement().addContent(new CDATA(Base64Coder.encode(this.roboticon.xml)));
      }

   Roboticon toRoboticon()
      {
      // return a clone so we can be sure this object remains immutable
      return (Roboticon)roboticon.clone();
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

      final XmlRoboticon that = (XmlRoboticon)o;

      if (roboticon != null ? !roboticon.equals(that.roboticon) : that.roboticon != null)
         {
         return false;
         }

      return true;
      }

   public int hashCode()
      {
      return (roboticon != null ? roboticon.hashCode() : 0);
      }
   }