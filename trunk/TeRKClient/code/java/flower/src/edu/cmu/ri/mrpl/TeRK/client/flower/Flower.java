package edu.cmu.ri.mrpl.TeRK.client.flower;

import java.util.Hashtable;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.ExpressOMatic;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions.Condition;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions.conditionals.AbstractConditional;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions.conditionals.AnalogInputsConditional;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions.Expression;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.sequence.SequenceStep;
import edu.cmu.ri.mrpl.TeRK.client.flower.components.AbstractComponentWrapper;
import edu.cmu.ri.mrpl.TeRK.client.flower.components.CellWrapper;
import edu.cmu.ri.mrpl.TeRK.client.flower.components.ConditionalWrapper;
import edu.cmu.ri.mrpl.TeRK.client.flower.render.AbstractFlowerRenderer;
import edu.cmu.ri.mrpl.TeRK.client.flower.render.CenterRenderer;
import edu.cmu.ri.mrpl.TeRK.client.flower.render.FlowerRenderingConstants;
import edu.cmu.ri.mrpl.TeRK.client.flower.render.IRRenderer;
import edu.cmu.ri.mrpl.TeRK.client.flower.render.PetalRenderer;
import edu.cmu.ri.mrpl.TeRK.client.flower.render.PotRenderer;
import edu.cmu.ri.mrpl.TeRK.client.flower.render.StemRenderer;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.DefaultCell;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.ServoCell;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class Flower extends ExpressOMatic implements FlowerRenderingConstants
   {
   // **** Default Objects ****

   private static final Logger LOG = Logger.getLogger(Flower.class);

   /** The application name (appears in the title bar) */
   private static final String APPLICATION_NAME = "Flower Power";

   /** Properties file used to setup Ice for this application */
   private static final String ICE_DIRECT_CONNECT_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/client/flower/Flower.direct-connect.ice.properties";
   private static final String ICE_RELAY_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/client/flower/Flower.relay.ice.properties";

   // **** Flower Objects ****

   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               new Flower(APPLICATION_NAME, ICE_RELAY_PROPERTIES_FILE, ICE_DIRECT_CONNECT_PROPERTIES_FILE,
                          new int[]{AFTER6PM_CONDITION, FLOWERTOUCHED_CONDITION});
               }
            });
      }

   private Flower(final String applicationName,
                  final String relayCommunicatorIcePropertiesFile,
                  final String directConnectCommunicatorIcePropertiesFile,
                  int[] conditionsArray)
      {
      super(applicationName, relayCommunicatorIcePropertiesFile, directConnectCommunicatorIcePropertiesFile,
            conditionsArray);
      }

   // ALL GUI CODE BELOW ===================================================================

   private Hashtable<AbstractComponentWrapper, AbstractFlowerRenderer> componentRenderers;
   private JPanel flowerPanel;

   protected JPanel mainPanel()
      {
      flowerPanel = new JPanel(null);
      flowerPanel.setBackground(BACKGROUND_COLOR);

      // Draw IR Sensors
      final IRRenderer ir1 = new IRRenderer(2);
      final IRRenderer ir2 = new IRRenderer(4);
      final IRRenderer ir3 = new IRRenderer(6);

      flowerPanel.add(ir1);
      flowerPanel.add(ir2);
      flowerPanel.add(ir3);

      // Draw Petals
      final PetalRenderer petal2 = new PetalRenderer(1);
      final PetalRenderer petal3 = new PetalRenderer(2);
      final PetalRenderer petal4 = new PetalRenderer(3);
      final PetalRenderer petal5 = new PetalRenderer(4);
      final PetalRenderer petal6 = new PetalRenderer(5);
      final PetalRenderer petal1 = new PetalRenderer(6);

      flowerPanel.add(petal1);
      flowerPanel.add(petal2);
      flowerPanel.add(petal3);
      flowerPanel.add(petal4);
      flowerPanel.add(petal5);
      flowerPanel.add(petal6);

      // Draw Flower Center
      final CenterRenderer center = new CenterRenderer();
      flowerPanel.add(center);

      // Draw Flower Pot
      final PotRenderer pot = new PotRenderer();
      flowerPanel.add(pot);

      // Draw Stem
      final StemRenderer stem = new StemRenderer();
      flowerPanel.add(stem);

      componentRenderers =
            new Hashtable<AbstractComponentWrapper, AbstractFlowerRenderer>();

      componentRenderers.put(new CellWrapper(new ServoCell(), 1), petal1);
      componentRenderers.put(new CellWrapper(new ServoCell(), 2), petal2);
      componentRenderers.put(new CellWrapper(new ServoCell(), 3), petal3);
      componentRenderers.put(new CellWrapper(new ServoCell(), 4), petal4);
      componentRenderers.put(new CellWrapper(new ServoCell(), 5), petal5);
      componentRenderers.put(new CellWrapper(new ServoCell(), 6), petal6);

      componentRenderers.put(new CellWrapper(new ServoCell(), 0), stem);

      componentRenderers.put(new ConditionalWrapper(new AnalogInputsConditional(), 0), ir1);
      componentRenderers.put(new ConditionalWrapper(new AnalogInputsConditional(), 1), ir2);
      componentRenderers.put(new ConditionalWrapper(new AnalogInputsConditional(), 2), ir3);

      flowerPanel.setPreferredSize(getPreferredSize());

      return flowerPanel;
      }

   protected void togglePlaying(boolean isPlaying)
      {
      AbstractFlowerRenderer r;
      for (AbstractComponentWrapper w : componentRenderers.keySet())
         {
         r = componentRenderers.get(w);
         r.setSelected(false);
         if (w instanceof CellWrapper)
            {
            r.setText(isPlaying ? "?" : "");
            }
         else
            {
            r.setText("");
            }
         }

      SequenceStep step = getSelectedStep();
      if (step == null)
         {
         return;
         }

      selectExpressionParts(step.getExpression());
      flowerPanel.repaint();
      }

   protected void stepPlayed(SequenceStep step)
      {
      for (AbstractFlowerRenderer r : componentRenderers.values())
         {
         r.setSelected(false);
         }

      if (step == null)
         {
         return;
         }

      selectExpressionParts(step.getExpression());
      flowerPanel.repaint();
      }

   protected void stepSelected(SequenceStep step)
      {
      for (AbstractFlowerRenderer r : componentRenderers.values())
         {
         r.setSelected(false);
         r.setText("");
         }

      if (step == null)
         {
         return;
         }

      selectExpressionParts(step.getExpression());
      flowerPanel.repaint();
      }

   protected void conditionValuesChecked(Condition condition, Object[] values)
      {
      for (AbstractFlowerRenderer r : componentRenderers.values())
         {
         r.setSelected(false);
         }

      if (condition == null)
         {
         return;
         }

      selectConditionParts(condition);
      flowerPanel.repaint();
      }

   private void selectExpressionParts(Expression e)
      {
      for (DefaultCell c : e.getComponentCells())
         {
         AbstractFlowerRenderer r = componentRenderers.get(new CellWrapper(c));

         if (r != null)
            {
            if (c.getValues() != null && c.getValueKey() != null && c.getValues().get(c.getValueKey()) != null)
               {
               r.setText(c.getValues().get(c.getValueKey()).toString());
               }
            r.setSelected(true);
            }
         }
      }

   private void selectConditionParts(Condition c)
      {
      for (AbstractConditional cl : c.getConditionals())
         {
         AbstractFlowerRenderer r = componentRenderers.get(new ConditionalWrapper(cl));

         if (r != null)
            {
            if (cl.getLastCheckedValue() != null)
               {
               r.setText(cl.getLastCheckedValue().toString());
               }
            r.setSelected(true);
            }
         }
      }
   }