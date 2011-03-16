package edu.cmu.ri.mrpl.TeRK.roboticon.messenger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import edu.cmu.ri.createlab.xml.XmlHelper;
import edu.cmu.ri.createlab.xml.XmlObject;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessage;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class XmlRoboticonMessengerHistory extends XmlObject implements RoboticonMessengerHistory
   {
   static final String ELEMENT_NAME = "roboticon-messenger-history";
   private static final String DOCTYPE_PUBLIC_ID = "-//CREATE Lab//TeRK//Roboticon Messenger History//EN";
   private static final String DOCTYPE_SYSTEM_ID = "http://www.createlab.ri.cmu.edu/dtd/terk/roboticon-messenger-history.dtd";
   private static final DocType DOC_TYPE = new DocType(ELEMENT_NAME, DOCTYPE_PUBLIC_ID, DOCTYPE_SYSTEM_ID);

   public static XmlRoboticonMessengerHistory create(final String xml) throws IOException, JDOMException
      {
      final Document document = XmlHelper.createDocument(xml);
      document.setDocType((DocType)DOC_TYPE.clone());
      final Element element = XmlHelper.createElement(XmlHelper.writeDocumentToString(document));
      return new XmlRoboticonMessengerHistory(element);
      }

   public static XmlRoboticonMessengerHistory create(final InputStream inputStream) throws IOException, JDOMException
      {
      return new XmlRoboticonMessengerHistory(XmlHelper.createElement(inputStream));
      }

   /** Creates an empty RoboticonMessengerHistory. */
   public static XmlRoboticonMessengerHistory create()
      {
      return new XmlRoboticonMessengerHistory();
      }

   private final List<RoboticonMessage> roboticonMessages = new ArrayList<RoboticonMessage>();

   private XmlRoboticonMessengerHistory()
      {
      getElement().setName(ELEMENT_NAME);
      }

   private XmlRoboticonMessengerHistory(final Element element)
      {
      super(element);

      final List roboticonMessageElements = getElement().getChildren(XmlRoboticonMessage.ELEMENT_NAME);
      if ((roboticonMessageElements != null) && (!roboticonMessageElements.isEmpty()))
         {
         for (final ListIterator listIterator = roboticonMessageElements.listIterator(); listIterator.hasNext();)
            {
            final Element roboticonMessageElement = (Element)listIterator.next();
            final XmlRoboticonMessage xmlRoboticonMessage = new XmlRoboticonMessage(roboticonMessageElement);
            roboticonMessages.add(xmlRoboticonMessage.toRoboticonMessage());
            }
         }
      }

   public void addRoboticonMessage(final RoboticonMessage roboticonMessage)
      {
      if (roboticonMessage != null)
         {
         getElement().addContent(new XmlRoboticonMessage(roboticonMessage).toElement());
         roboticonMessages.add(roboticonMessage);
         }
      }

   public List<RoboticonMessage> getRoboticonMessages()
      {
      return Collections.unmodifiableList(roboticonMessages);
      }

   public Document getDocument()
      {
      if (getElement().getDocument() != null)
         {
         return getElement().getDocument();
         }
      return new Document(getElement(), (DocType)DOC_TYPE.clone());
      }

   public void clear()
      {
      getElement().removeContent();
      roboticonMessages.clear();
      }
   }
