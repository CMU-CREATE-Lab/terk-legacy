package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components;

/**
 * This class is used as the "user object" in the cells that make up the ports on the Qwerk board.
 * It is used to store some additional information about the cells that is not part of the normal
 * cell data. For example, it stores the type of connection
 */
public class QwerkPortInfo
   {

   /// The name of this port
   protected String mName;

   /// The kind of class that can be connected to this port
   protected Class mConnectionClass;

   /**
    * Create a QwerkPortInfo object.
    * @param name The name to display for this cell.
    * @param connectionClass The kinds of objects which are allowed to connect (in a Qwerk sense) to this port.
    */
   public QwerkPortInfo(final String name, final Class connectionClass)
      {
      super();

      mName = name;
      mConnectionClass = connectionClass;
      }

   /**
    * Get the name of this Qwerk port.
    * @return The name of the Qwerk port.
    */
   public String toString()
      {
      return mName;
      }

   /**
    * Get the kinds of objects which are allowed to connect to this port.
    * For example, a port may only allow MotorCells or ServoCells.
    * @return The allowed class.
    */
   public Class connectionClass()
      {
      return mConnectionClass;
      }
   }
