package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.tree.MutableTreeNode;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

public class QwerkCell extends DefaultGraphCell
   {

   public static final long serialVersionUID = 1;

   private static final double SIZE_MULTIPLIER = 1.75;

   private static final double PORT_HEIGHT = 20 * SIZE_MULTIPLIER;
   private static final double PORT_WIDTH = 11 * SIZE_MULTIPLIER;
   private static final double PORT_PAD = 2.0 * SIZE_MULTIPLIER;
   private static final double BOARD_WIDTH = 230 * SIZE_MULTIPLIER;
   private static final double BOARD_HEIGHT = 220 * SIZE_MULTIPLIER;
   private static final double BOARD_COVER_WIDTH = 170 * SIZE_MULTIPLIER;
   private static final double BOARD_COVER_HEIGHT = 170 * SIZE_MULTIPLIER;

   private static final int NUMBER_LEDS = 1;
   private static final String[] LED_LABELS = {"On-Board LEDs"};
   private static final int[] LED_DEVICE_IDS = {0};

   private static final int NUMBER_MOTORS = 4;
   private static final String[] MOTOR_LABELS = {"3", "2", "1", "0"};
   private static final int[] MOTOR_DEVICE_IDS = {3, 2, 1, 0};

   private static final int NUMBER_SERVOS = 16;
   private static final String[] SERVO_LABELS = {"15", "14", "13", "12", "11", "10", "9", "8",
                                                 "7", "6", "5", "4", "3", "2", "1", "0"};
   private static final int[] SERVO_DEVICE_IDS = {15, 14, 13, 12, 11, 10, 9, 8,
                                                  7, 6, 5, 4, 3, 2, 1, 0};

   // number of analog ins
   private static final int NUMBER_ANALOGS = 8;
   private static final String[] ANALOG_LABELS = {"0", "1", "2", "3", "4", "5", "6", "7"};
   private static final int[] ANALOG_DEVICE_IDS = {0, 1, 2, 3, 4, 5, 6, 7};

   // number of digital ins ** NOTE: DIGITAL INS AND OUT NEED TO BE SEPARATED
   private static final int NUMBER_DIGITALS_IN = 4;
   private static final String[] DIGITAL_LABELS_IN = {"I3", "I2", "I1", "I0"};
   private static final int[] DIGITAL_DEVICE_IDS_IN = {3, 2, 1, 0};

   // number of digital outs ** NOTE: DIGITAL INS AND OUT NEED TO BE SEPARATED
   private static final int NUMBER_DIGITALS_OUT = 4;
   private static final String[] DIGITAL_LABELS_OUT = {"O3", "O2", "O1", "O0"};
   private static final int[] DIGITAL_DEVICE_IDS_OUT = {3, 2, 1, 0};

   // Coordinates of the cover
   private static final double COVER_START_X = 30 * SIZE_MULTIPLIER;
   private static final double COVER_START_Y = 0 * SIZE_MULTIPLIER;

   // Starting coordinates of the LED port
   private static final double LED_START_X = COVER_START_X;
   private static final double LED_START_Y = COVER_START_Y + BOARD_COVER_HEIGHT + (2 * PORT_PAD);
   private static final double LED_WIDTH = BOARD_COVER_WIDTH / 2;
   private static final double LED_HEIGHT = 20 * SIZE_MULTIPLIER;

   // Starting coordinates of the analog inputs
   private static final double ANALOG_START_X = 0 * SIZE_MULTIPLIER;
   private static final double ANALOG_START_Y = 20 * SIZE_MULTIPLIER;
   // Starting coordinates of the servos
   private static final double SERVO_START_X = 10 * SIZE_MULTIPLIER;
   private static final double SERVO_START_Y = (BOARD_HEIGHT - PORT_HEIGHT);
   // Starting coordinates of the motors
   // Note: This calculates the size of the motor pads to ensure proper placement on the board
   private static final double MOTOR_START_X = BOARD_WIDTH - PORT_HEIGHT;
   private static final double MOTOR_START_Y = BOARD_HEIGHT -
                                               (NUMBER_MOTORS * (PORT_WIDTH + PORT_PAD)) -
                                               (PORT_HEIGHT + PORT_PAD);
   // Starting coordinates of the digitals
   private static final double DIGITAL_START_X = (BOARD_WIDTH - PORT_HEIGHT);
   private static final double DIGITAL_START_Y = 20 * SIZE_MULTIPLIER;

   // camera
   private static final int NUMBER_CAMERA = 1;
   private static final String[] CAMERA_LABELS = {"Camera"};
   private static final int[] CAMERA_DEVICE_IDS = {0};
   private static final double CAMERA_START_X = LED_START_X + LED_WIDTH + 10;//0 * SIZE_MULTIPLIER;
   private static final double CAMERA_START_Y = COVER_START_Y + BOARD_COVER_HEIGHT + (2 * PORT_PAD);//160 * SIZE_MULTIPLIER;
   private static final double CAMERA_HEIGHT = LED_HEIGHT;
   private static final double CAMERA_WIDTH = BOARD_COVER_WIDTH / 4 - 10;

   // Starting coordinates of the speaker
   private static final int NUMBER_AUDIO = 1;
   private static final String[] AUDIO_LABELS = {"Speaker"};
   private static final int[] AUDIO_DEVICE_IDS = {0};
   private static final double AUDIO_START_X = CAMERA_START_X + CAMERA_WIDTH + 10;//0 * SIZE_MULTIPLIER;
   private static final double AUDIO_START_Y = COVER_START_Y + BOARD_COVER_HEIGHT + (2 * PORT_PAD);//160 * SIZE_MULTIPLIER;
   private static final double AUD_HEIGHT = LED_HEIGHT;
   private static final double AUD_WIDTH = BOARD_COVER_WIDTH / 4 - 10;

   private DefaultGraphCell backgroundCell;

   /**
    * Construct a new Qwerk cell.
    * @param p The starting point (upper-left corner) of the board.
    */
   public QwerkCell(final Point2D p)
      {
      super();

      initChildren(p);
      }

   /**
    * Get the cell which represents the starting point of the Qwerk board.
    * Use this method for position information.
    * @return The graph cell which represents the starting point of the Qwerk board.
    */
   public Point2D getBaseCellPosition()
      {
      final Rectangle2D rect = GraphConstants.getBounds(backgroundCell.getAttributes());
      return new Point2D.Double(rect.getMinX(), rect.getMinY());
      }

   /**
    * Setup the children of the Qwerk board cell.
    * @param boardPosition The starting point (upper-left corner) of the board.
    */
   private void initChildren(final Point2D boardPosition)
      {
      final double boardX = boardPosition.getX();
      final double boardY = boardPosition.getY();
      final ArrayList<DefaultGraphCell> boardCells = new ArrayList<DefaultGraphCell>();
      // Create the background cell
      backgroundCell = new DefaultGraphCell();

      GraphConstants.setBounds(backgroundCell.getAttributes(),
                               new Rectangle2D.Double(boardX, boardY,
                                                      BOARD_WIDTH, BOARD_HEIGHT));
      GraphConstants.setGradientColor(backgroundCell.getAttributes(), Color.orange);
      GraphConstants.setOpaque(backgroundCell.getAttributes(), true);
      GraphConstants.setBorder(backgroundCell.getAttributes(), BorderFactory.createLineBorder(Color.BLACK, 2));
      GraphConstants.setSizeable(backgroundCell.getAttributes(), false);
      boardCells.add(backgroundCell);

      // Create the cover cell
      // We store the reference to this one so that we can keep track of where
      //  to put the control panel (goes over the cover cell).
      final DefaultGraphCell coverCell = new DefaultGraphCell("Qwerk Board");
      GraphConstants.setBounds(coverCell.getAttributes(),
                               new Rectangle2D.Double(boardX + COVER_START_X, boardY + COVER_START_Y,
                                                      BOARD_COVER_WIDTH, BOARD_COVER_HEIGHT));
      GraphConstants.setGradientColor(coverCell.getAttributes(), Color.GRAY);
      GraphConstants.setOpaque(coverCell.getAttributes(), true);
      GraphConstants.setBorder(coverCell.getAttributes(), BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
      GraphConstants.setSizeable(coverCell.getAttributes(), false);
      boardCells.add(coverCell);

      // Add LED 'port'
      initQwerkPorts(NUMBER_LEDS,
                     boardX + LED_START_X, boardY + LED_START_Y,
                     LED_WIDTH, LED_HEIGHT,
                     true,
                     LEDCell.getColor(),
                     LED_LABELS,
                     LED_DEVICE_IDS,
                     LEDCell.class,
                     boardCells);

      // Add servo ports
      initQwerkPorts(NUMBER_SERVOS,
                     boardX + SERVO_START_X, boardY + SERVO_START_Y,
                     PORT_WIDTH, PORT_HEIGHT,
                     true,
                     ServoCell.getColor(),
                     SERVO_LABELS,
                     SERVO_DEVICE_IDS,
                     ServoCell.class,
                     boardCells);

      // Add digital out
      initQwerkPorts(NUMBER_DIGITALS_OUT,
                     boardX + DIGITAL_START_X, boardY + DIGITAL_START_Y,
                     PORT_WIDTH, PORT_HEIGHT,
                     false,
                     DigitalOutCell.getColor(),
                     DIGITAL_LABELS_OUT,
                     DIGITAL_DEVICE_IDS_OUT,
                     DigitalOutCell.class,
                     boardCells);

      // Add digital in
      initQwerkPorts(NUMBER_DIGITALS_IN,
                     boardX + DIGITAL_START_X, boardY + DIGITAL_START_Y + 100,
                     PORT_WIDTH, PORT_HEIGHT,
                     false,
                     DigitalInCell.getColor(),
                     DIGITAL_LABELS_IN,
                     DIGITAL_DEVICE_IDS_IN,
                     DigitalInCell.class,
                     boardCells);

      // Add motor ports
      initQwerkPorts(NUMBER_MOTORS,
                     boardX + MOTOR_START_X, boardY + MOTOR_START_Y,
                     PORT_WIDTH, PORT_HEIGHT,
                     false,
                     MotorCell.getColor(),
                     MOTOR_LABELS,
                     MOTOR_DEVICE_IDS,
                     MotorCell.class,
                     boardCells);

      // Add analog input ports
      initQwerkPorts(NUMBER_ANALOGS,
                     boardX + ANALOG_START_X, boardY + ANALOG_START_Y,
                     PORT_WIDTH, PORT_HEIGHT,
                     false,
                     AnalogInputCell.getColor(),
                     ANALOG_LABELS,
                     ANALOG_DEVICE_IDS,
                     AnalogInputCell.class,
                     boardCells);

      // Add camera port
      initQwerkPorts(NUMBER_CAMERA,
                     boardX + CAMERA_START_X,
                     boardY + CAMERA_START_Y,
                     CAMERA_HEIGHT,
                     CAMERA_WIDTH,
                     false,
                     CameraCell.getColor(),
                     CAMERA_LABELS,
                     CAMERA_DEVICE_IDS,
                     CameraCell.class,
                     boardCells);

      // Add audio port
      initQwerkPorts(NUMBER_AUDIO,
                     boardX + AUDIO_START_X, boardY + AUDIO_START_Y,
                     AUD_HEIGHT, AUD_WIDTH,
                     false,
                     AudioCell.getColor(),
                     AUDIO_LABELS,
                     AUDIO_DEVICE_IDS,
                     AudioCell.class,
                     boardCells);

      // Get the cells
      for (Object oneChild : boardCells)
         {
         add((MutableTreeNode)oneChild);
         }

      // Setup some parameters
      GraphConstants.setChildrenSelectable(getAttributes(), false);
      GraphConstants.setSizeable(getAttributes(), false);
      }

   /**
    * Initialize a Qwerk board port.
    * This helper method creates Qwerk board cells that are grouped under the
    * parent cell on the canvas.
    * @param numberPorts The number of ports to add.
    * @param startX The starting X location of these ports.
    * @param startY The starting Y location of these ports.
    * @param sizeWidth The width of these ports.
    * @param sizeHeight The height of these ports.
    * @param horizontal Set to true to spread the ports in the X direction, false to spread in the Y direction.
    * @param color The color of these ports.
    * @param labels The labels for these ports.
    * @param deviceIds The device IDs for these ports.
    * @param connectionClass The type of classes that can connect to this port.
    * @param list The list of cells to add this port to.
    */
   private void initQwerkPorts(final int numberPorts,
                               final double startX, final double startY,
                               final double sizeWidth, final double sizeHeight,
                               final boolean horizontal,
                               final Color color,
                               final String[] labels,
                               final int[] deviceIds,
                               final Class connectionClass,
                               final ArrayList<DefaultGraphCell> list)
      {
      Rectangle2D.Double newBounds;

      for (int i = 0; i < numberPorts; i++)
         {
         if (horizontal)
            {
            // Vary the spacing in the x direction
            newBounds = new Rectangle2D.Double(
                  startX + ((sizeWidth + PORT_PAD) * i),
                  startY,
                  sizeWidth,
                  sizeHeight);
            }
         else
            {
            // Vary the spacing in the y direction (vertical)
            newBounds = new Rectangle2D.Double(
                  startX,
                  startY + ((sizeWidth + PORT_PAD) * i),
                  sizeHeight,
                  sizeWidth);
            }

         final QwerkBoardPortCell portCell = new QwerkBoardPortCell(new QwerkPortInfo(labels[i], connectionClass));
         portCell.setDeviceId(deviceIds[i]);
         GraphConstants.setBounds(portCell.getAttributes(), newBounds);
         GraphConstants.setGradientColor(portCell.getAttributes(), color);
         GraphConstants.setOpaque(portCell.getAttributes(), true);
         GraphConstants.setBorder(portCell.getAttributes(), BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
         GraphConstants.setSizeable(portCell.getAttributes(), false);

         // Add a port, then add the whole thing to our qwerk board group
         portCell.add(new DefaultPort());

         list.add(portCell);
         }
      }

   /**
    * Determine the Qwerk port cell that is at the specified point.
    * If no cell is found, this method returns null. This method will also return null if
    * the type is not as specified in the cellType parameter.
    * @param p The point to check at.
    * @param cellType The class of the cell we're copying to the canvas (e.g., a motor or servo).
    * @return The child cell found, or null if nothing found.
    */
   public DefaultGraphCell cellAtPointWithType(final Point2D p, final Class cellType)
      {
      final DefaultGraphCell found = cellAtPoint(p);

      if (found != null)
         {
         final Object userObj = found.getUserObject();

         if (((QwerkPortInfo)userObj).connectionClass() != cellType)
            {
            return null;
            }
         }

      return found;
      }

   /**
    * Determine the Qwerk port cell that is at the specified point.
    * If no cell is found, this method returns null.
    * @param p The point to check at.
    * @return The child cell found, or null if nothing found.
    */
   public DefaultGraphCell cellAtPoint(final Point2D p)
      {
      final Vector<DefaultGraphCell> children = (Vector<DefaultGraphCell>)getChildren();
      Rectangle2D childLocation;

      for (DefaultGraphCell cell : children)
         {
         childLocation = GraphConstants.getBounds(cell.getAttributes());

         // Has to be a valid size cell and has to contain the point,
         if (childLocation != null && childLocation.contains(p))
            {
            final Object userObj = cell.getUserObject();

            // Has to be a non-null user object, the user object has to be of the QwerkPortInfo type
            //  the user object has to allow classes of this new type, and the port can not be already
            //  connected
            if (userObj != null &&
                userObj.getClass() == QwerkPortInfo.class &&
                ((DefaultPort)cell.getChildAt(0)).getEdges().size() == 0)
               {

               // The user must have been dropping their new cell on this port cell
               return cell;
               }
            }
         }

      return null;
      }

   /**
    * Finds the DefaultGraphCell which represents a port on the Qwerk board based on a device ID and connection type.
    * @param deviceId The device ID of the port.
    * @param cellType The class of DefaultCells that are allowed to be connected to this port.
    * @return The found port cell.
    */
   public QwerkBoardPortCell cellWithDeviceIdAndType(final int deviceId, final Class cellType)
      {
      final Vector<DefaultGraphCell> children = (Vector<DefaultGraphCell>)getChildren();

      for (DefaultGraphCell cell : children)
         {

         final Object userObj = cell.getUserObject();

         // Has to be a non-null user object, the user object has to be of the QwerkPortInfo type
         //  the user object has to allow classes of this type, and the deviceId has to match
         if (userObj != null &&
             userObj.getClass() == QwerkPortInfo.class &&
             ((QwerkPortInfo)userObj).connectionClass() == cellType &&
             ((QwerkBoardPortCell)cell).getDeviceId() == deviceId)
            {

            // This is the port cell we're looking for
            return (QwerkBoardPortCell)cell;
            }
         }

      // Not found
      return null;
      }
   }
