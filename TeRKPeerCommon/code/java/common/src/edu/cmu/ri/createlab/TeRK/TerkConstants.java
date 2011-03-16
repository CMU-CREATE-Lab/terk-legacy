package edu.cmu.ri.createlab.TeRK;

import java.io.File;

/**
 * <p>
 * <code>TerkConstants</code> defines various constants of the TeRK architecture.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class TerkConstants
   {

   public static final class FilePaths
      {
      public static final String TERK_PATH = System.getProperty("user.home") + File.separator + "TeRK" + File.separator;
      public static final File AUDIO_DIR = new File(TERK_PATH, "Audio");
      public static final File EXPRESSIONS_DIR = new File(TERK_PATH, "Expressions");
      public static final File SEQUENCES_DIR = new File(TERK_PATH, "Sequences");
      public static final File CONDITIONS_DIR = new File(TERK_PATH, "Conditions");
      public static final File EXPRESSIONS_ICONS_DIR = new File(EXPRESSIONS_DIR, "Icons");
      public static final File CONDITIONS_ICONS_DIR = new File(CONDITIONS_DIR, "Icons");
      public static final File SEQUENCES_ICONS_DIR = new File(SEQUENCES_DIR, "Icons");
      public static final File EXPRESSIONS_PUBLIC_DIR = new File(EXPRESSIONS_DIR, "Public");
      public static final File SEQUENCES_PUBLIC_DIR = new File(SEQUENCES_DIR, "Public");

      private FilePaths()
         {
         // private to prevent instantiation
         }
      }

   public static final class PropertyKeys
      {
      public static final String DEVICE_COUNT = "device.count";
      public static final String HARDWARE_TYPE = "hardware.type";
      public static final String HARDWARE_VERSION = "hardware.version";

      private PropertyKeys()
         {
         // private to prevent instantiation
         }
      }

   private TerkConstants()
      {
      // private to prevent instantiation
      }
   }
