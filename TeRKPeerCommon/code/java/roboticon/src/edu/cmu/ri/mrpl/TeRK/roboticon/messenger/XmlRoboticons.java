package edu.cmu.ri.mrpl.TeRK.roboticon.messenger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import edu.cmu.ri.createlab.xml.XmlObject;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.Roboticon;
import org.jdom.Element;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class XmlRoboticons extends XmlObject
   {
   static final String ELEMENT_NAME = "roboticons";

   private final List<XmlRoboticon> xmlRoboticons = new ArrayList<XmlRoboticon>();

   XmlRoboticons(final Element element)
      {
      super(element);

      final List roboticonElements = getElement().getChildren(XmlRoboticon.ELEMENT_NAME);
      if ((roboticonElements != null) && (!roboticonElements.isEmpty()))
         {
         for (final ListIterator listIterator = roboticonElements.listIterator(); listIterator.hasNext();)
            {
            final Element roboticonElement = (Element)listIterator.next();
            final XmlRoboticon xmlRoboticon = new XmlRoboticon(roboticonElement);
            xmlRoboticons.add(xmlRoboticon);
            }
         }
      }

   XmlRoboticons()
      {
      getElement().setName(ELEMENT_NAME);
      }

   XmlRoboticons(final List<Roboticon> roboticons)
      {
      getElement().setName(ELEMENT_NAME);
      if ((roboticons != null) && (!roboticons.isEmpty()))
         {
         for (final Roboticon roboticon : roboticons)
            {
            getElement().addContent(new XmlRoboticon(roboticon).toElement());
            }
         }
      }

   /** Returns an unmodifiable list of {@link Roboticon}s. */
   List<Roboticon> getRoboticons()
      {
      final List<Roboticon> roboticons = new ArrayList<Roboticon>(xmlRoboticons.size());
      for (final XmlRoboticon xmlRoboticon : xmlRoboticons)
         {
         roboticons.add(xmlRoboticon.toRoboticon());
         }

      return Collections.unmodifiableList(roboticons);
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

      final XmlRoboticons that = (XmlRoboticons)o;

      if (xmlRoboticons != null ? !xmlRoboticons.equals(that.xmlRoboticons) : that.xmlRoboticons != null)
         {
         return false;
         }

      return true;
      }

   public int hashCode()
      {
      return (xmlRoboticons != null ? xmlRoboticons.hashCode() : 0);
      }
   }