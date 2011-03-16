package edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.AbstractFileHandler;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.DefaultCell;

/**
 * Based on code from RobotUniversalRemote
 *
 * Reads in and saves XML files compatible with RUR Expressions
 *
 * @author Mel Ludowise
 *
 */
public class ExpressionFileHandler extends AbstractFileHandler<Expression>
   {
   private static final ExpressionFileHandler instance = new ExpressionFileHandler();

   private static final String RUR_VERSION = "1.0";

   private static final String CLASS_KEY = "class";
   private static final String DEVICE_ID_KEY = "device_id";
   private static final String COMPONENTS_KEY = "components";

   private ExpressionFileHandler()
      {
      }

   public static ExpressionFileHandler getInstance()
      {
      return instance;
      }

   // Do something about name

   public Expression openFile(File filename)
      {
      Expression e = super.openFile(filename);
      String name = filename.getName();
      name = name.substring(0, name.length() - 4);
      e.setName(name);
      return e;
      }

   /**
    * Returns one object which contains enough information to reconstruct the current layout.
    * Used for serialization.
    */
   public Hashtable getRepresentation(Expression expression)
      {

      // In the future, it would be nice to store the model from the JGraph directly
      //  but due to difficulties with that, this will store enough info to
      //  reconstruct the current configuration.

      // Need to store:
      //  1. Position of Qwerk board
      //  2. Information about each component
      //    a. Position
      //    b. Type
      //    c. Port connection

      final Hashtable storage = new Hashtable();

      final ArrayList<Hashtable> components = new ArrayList<Hashtable>();
      Hashtable componentHash;

      // Overall info
      storage.put("rur_version", RUR_VERSION);

      // Get info about each component and put it in the component list

      final ArrayList<DefaultCell> cells = expression.getComponentCells();

      for (DefaultCell cell : cells)
         {
         // This cell represents a component
         componentHash = new Hashtable();

         componentHash.put(DEVICE_ID_KEY, cell.getDeviceId());
         componentHash.put(CLASS_KEY, cell.getClass());

         // For expressions
         // Put all the values of the cells into the oneComponent hashtable.
         Hashtable values = cell.getValues();
         if (values != null)
            {
            componentHash.putAll(values);
            }

         // Add this component to our list
         components.add(componentHash);
         }

      // Add the component list to our storage
      storage.put(COMPONENTS_KEY, components);

      return storage;
      }

   /**
    * Reconstructs the canvas from a representation of a Qwerk board.
    * Used for de-serialization.
    */
   public Expression setRepresentation(final Hashtable hash)
      {
      if (hash == null)
         {
         return null;
         }

      Expression expression = new Expression();

      // This object will be used for instanceof checks
      Object tempObject;

      // Get the ArrayList of componentHashes
      tempObject = hash.get(COMPONENTS_KEY);
      if (tempObject == null || !(tempObject instanceof ArrayList))
         {
         return null;
         }
      final ArrayList components = (ArrayList)tempObject;

      for (Object o : components)
         {

         // Check to make sure o is a Hashtable
         if (!(o instanceof Hashtable))
            {
            continue;
            }
         final Hashtable componentHash = (Hashtable)o;

         // Get deviceId
         tempObject = componentHash.get(DEVICE_ID_KEY);
         final int deviceId = (tempObject != null && tempObject instanceof Integer)
                              ? (Integer)tempObject
                              : -1;

         // Get Class of this component
         tempObject = componentHash.get(CLASS_KEY);
         if (tempObject == null || !(tempObject instanceof Class))
            {
            continue;
            }
         final Class componentType = (Class)tempObject;

         DefaultCell newCell;
         try
            {
            // Create a new cell class of the correct type
            newCell = (DefaultCell)componentType.newInstance();
            newCell.setValues(componentHash);
            }
         catch (Exception ex)
            {
            System.out.println("Error " + this.getClass() + ": " + ex.getMessage());
            ex.printStackTrace();
            return null;
            }

         newCell.setDeviceId(deviceId);

         // Insert the new component in the graph
         expression.addComponentCell(newCell);
         }

      return expression;
      }
   }
