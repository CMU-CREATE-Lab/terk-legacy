package edu.cmu.ri.mrpl.TeRK.color;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class ColorPalette extends JPanel
   {
   /** An unmodifiable {@link Collection} of {@link Color}s in the palette. */
   @SuppressWarnings({"PublicStaticCollectionField"})
   public static final Collection<Color> COLORS;

   static
      {
      final Collection<Color> colorMap = new ArrayList<Color>();
      for (int r = 0; r <= 255; r += 51)
         {
         for (int g = 0; g <= 255; g += 51)
            {
            for (int b = 0; b <= 255; b += 51)
               {
               final Color color = new Color(r, g, b);
               colorMap.add(color);
               }
            }
         }

      COLORS = Collections.unmodifiableCollection(colorMap);
      }

   private final Collection<ColorPaletteButton> buttons = new ArrayList<ColorPaletteButton>();
   private final boolean isHorizontal;

   /** Creates a color palette with a vertical layout. */
   public ColorPalette(final ColorChangeActionListenerStrategy strategy)
      {
      this(strategy, false);
      }

   /**
    * Creates a color palette which is horizontal if <code>isHorizontal</code> is <code>true</code> and vertical
    * otherwise.
    */
   public ColorPalette(final ColorChangeActionListenerStrategy strategy, final boolean isHorizontal)
      {
      this.isHorizontal = isHorizontal;
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

      for (final Color color : COLORS)
         {
         final ColorPaletteButton button = new ColorPaletteButton(color);
         if (color.getRed() + color.getGreen() + color.getBlue() == 0)
            {
            button.setSelected(true);
            }
         buttons.add(button);
         }

      final ButtonGroup buttonGroup = new ButtonGroup();
      final JPanel buttonPanel = new JPanel(new SpringLayout());

      for (final ColorPaletteButton button : buttons)
         {
         button.addActionListener(new ColorSettingAction(button.getColor(), strategy));
         buttonGroup.add(button);
         buttonPanel.add(button);
         }

      final int numRows = isHorizontal ? 12 : 18;
      final int numCols = isHorizontal ? 18 : 12;
      SpringLayoutUtilities.makeCompactGrid(buttonPanel,
                                            numRows, numCols, // rows, cols
                                            2, 2, // initX, initY
                                            2, 2);// xPad, yPad;

      add(Box.createGlue());
      add(buttonPanel);
      add(Box.createGlue());
      }

   public void setEnabled(final boolean isEnabled)
      {
      for (final ColorPaletteButton button : buttons)
         {
         button.setEnabled(isEnabled);
         }
      }

   private final class ColorSettingAction implements ActionListener
      {
      private final Color color;
      private final ColorChangeActionListenerStrategy strategy;

      private ColorSettingAction(final Color color, final ColorChangeActionListenerStrategy strategy)
         {
         this.color = color;
         this.strategy = strategy;
         }

      public void actionPerformed(final ActionEvent e)
         {
         strategy.actionPerformed(color);
         }
      }
   }
