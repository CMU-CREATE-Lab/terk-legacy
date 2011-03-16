package edu.cmu.ri.mrpl.TeRK.client.flower.render;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;

public interface FlowerRenderingConstants
   {

   // Global constants
   public final static int LINE_WIDTH = 1;

   public final static Font FLOWER_FONT = new Font("Verdana", 0, 12);

   public final static Color BACKGROUND_COLOR = Color.WHITE;
   public final static Color SELECT_COLOR = new Color(0xFFCCCC);
   public final static Color SELECT_FONT_COLOR = Color.RED;
   public final static Color LINE_COLOR = new Color(0.5f, 0.5f, 0.5f);

   // Petal constants
   public final static int PETAL_MINOR_WIDTH = 30;
   public final static int PETAL_MAJOR_WIDTH = 60;
   public final static int PETAL_HEIGHT = 80;

   public final static Color PETAL_FILL_COLOR = new Color(0.9f, 0.9f, 0.9f);
   public final static Color PETAL_SELECT_COLOR = SELECT_COLOR;
   public final static Color PETAL_FONT_COLOR = new Color(0.0f, 0.0f, 0.0f);
   public final static Color PETAL_SELECT_FONT_COLOR = SELECT_FONT_COLOR;
   public final static Color PETAL_LINE_COLOR = LINE_COLOR;

   // Flower center constants
   public final static int FLOWER_CENTER_RADIUS = 25;
   public final static Point FLOWER_CENTER_POSITION =
         new Point(PETAL_HEIGHT + PETAL_MINOR_WIDTH, PETAL_HEIGHT + PETAL_MINOR_WIDTH);

   public final static Color CENTER_FILL_COLOR = new Color(1.0f, 0.75f, 0.0f);
   public final static Color CENTER_FONT_COLOR = new Color(0.0f, 0.0f, 0.0f);
   public final static Color CENTER_SELECT_FONT_COLOR = SELECT_FONT_COLOR;
   public final static Color CENTER_SELECT_COLOR = SELECT_COLOR;
   public final static Color CENTER_LINE_COLOR = LINE_COLOR;

   // Stem constants
   public final static int STEM_TOTAL_HEIGHT = 200;
   public final static int STEM_PART_WIDTH = 25;
   public final static Point STEM_JOINT_POSITION = new Point(60, STEM_TOTAL_HEIGHT / 2);

   public final static Color STEM_FILL_COLOR = new Color(0.0f, 0.64f, 0.24f);
   public final static Color STEM_FONT_COLOR = new Color(1.0f, 1.0f, 1.0f);
   public final static Color STEM_SELECT_FONT_COLOR = SELECT_FONT_COLOR;
   public final static Color STEM_SELECT_COLOR = SELECT_COLOR;
   public final static Color STEM_LINE_COLOR = LINE_COLOR;

   // Flower pot constants
   public final static int POT_BASE_WIDTH = 80;
   public final static int POT_TOP_WIDTH = 100;
   public final static int POT_HEIGHT = 50;
   public final static int POT_HEIGHT_LEVEL = 170;

   public final static Color POT_FILL_COLOR = new Color(0.9f, 0.75f, 0.6f);
   public final static Color POT_SELECT_COLOR = SELECT_COLOR;
   public final static Color POT_SELECT_FONT_COLOR = SELECT_FONT_COLOR;
   public final static Color POT_LINE_COLOR = LINE_COLOR;

   // IR constants
   public final static int IR_WIDTH = 30;
   public final static int IR_HEIGHT = 15;
   public final static int IR_PLACEMENT_HEIGHT = PETAL_MINOR_WIDTH + 10;

   public final static Color IR_FILL_COLOR = new Color(0.25f, 0.25f, 0.25f);
   public final static Color IR_FONT_COLOR = new Color(1.0f, 1.0f, 1.0f);
   public final static Color IR_SELECT_FONT_COLOR = SELECT_FONT_COLOR;
   public final static Color IR_SELECT_COLOR = SELECT_COLOR;
   public final static Color IR_LINE_COLOR = LINE_COLOR;

   // Global Constants
   public final static Dimension FLOWER_SIZE = new Dimension(
         (int)(2 * FLOWER_CENTER_POSITION.getX()),
         (int)FLOWER_CENTER_POSITION.getY() + POT_HEIGHT_LEVEL + POT_HEIGHT + 5
   );
   }
