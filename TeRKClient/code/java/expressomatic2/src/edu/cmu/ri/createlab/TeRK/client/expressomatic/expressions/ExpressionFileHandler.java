package edu.cmu.ri.createlab.TeRK.client.expressomatic.expressions;

import java.io.File;
import java.util.Hashtable;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.AbstractFileHandler;
import edu.cmu.ri.createlab.TeRK.expression.XmlExpression;
import edu.cmu.ri.mrpl.util.security.Base64Coder;

/**
 * Based on code from RobotUniversalRemote
 *
 * Reads in and saves XML files compadible with RUR Expressions
 *
 * @author Mel Ludowise
 *
 */

public class ExpressionFileHandler extends AbstractFileHandler<XmlExpression>
   {
   private static final ExpressionFileHandler instance = new ExpressionFileHandler();

   private static final String RUR_VERSION = "1.0";

   private static final String EXPRESSION_XML_KEY = "expressionXML";
   private static final String EXPRESSION_NAME_KEY = "expressionName";

   private ExpressionFileHandler()
      {
      }

   public static ExpressionFileHandler getInstance()
      {
      return instance;
      }

   // Do something about name

   public XmlExpression openFile(File filename)
      {
      try
         {
         XmlExpression e = XmlExpression.create(filename);
         String name = filename.getName();
         name = name.substring(0, name.length() - 4);
         e.setName(name);
         return e;
         }
      catch (Exception x)
         {
         return null;
         }
      }

   /**
    * Returns one object which contains enough information to reconstruct the current layout.
    * Used for serialization.
    */
   public Hashtable getRepresentation(XmlExpression expression)
      {
      final Hashtable storage = new Hashtable();

      // Overall info
      storage.put("rur_version", RUR_VERSION);

      storage.put(EXPRESSION_NAME_KEY, expression.getName());

      storage.put(EXPRESSION_XML_KEY, Base64Coder.encode(expression.toXmlDocumentStringFormatted()));

      return storage;
      }

   /**
    * Reconstructs the canvas from a representation of a Qwerk board.
    * Used for de-serialization.
    */
   public XmlExpression setRepresentation(final Hashtable hash)
      {
      if (hash == null)
         {
         return null;
         }

      // This object will be used for instanceof checks
      Object tempObject;
      String name;

      tempObject = hash.get(EXPRESSION_NAME_KEY);
      if (tempObject == null || !(tempObject instanceof String))
         {
         return null;
         }
      name = (String)tempObject;

      tempObject = hash.get(EXPRESSION_XML_KEY);
      if (tempObject == null || !(tempObject instanceof String))
         {
         return null;
         }

      try
         {
         XmlExpression x = XmlExpression.create(Base64Coder.decode((String)tempObject));
         x.setName(name);
         return x;
         }
      catch (Exception x)
         {
         }
      return null;
      }
   }
