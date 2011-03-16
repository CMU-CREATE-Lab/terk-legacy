package edu.cmu.ri.createlab.TeRK.serial;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public enum ASCIICharacter
   {
   // descriptions are from Wikipedia (http://en.wikipedia.org/wiki/ASCII)
      /** Null character (\0) */
      ASCII_000((byte)0, "Null character", "\\0"),
      /** Start of Header */
      ASCII_001((byte)1, "Start of Header"),
      /** Start of Text */
      ASCII_002((byte)2, "Start of Text"),
      /** End of Text */
      ASCII_003((byte)3, "End of Text"),
      /** End of Transmission */
      ASCII_004((byte)4, "End of Transmission"),
      /** Enquiry */
      ASCII_005((byte)5, "Enquiry"),
      /** Acknowledgment */
      ASCII_006((byte)6, "Acknowledgment"),
      /** Bell (\a) */
      ASCII_007((byte)7, "Bell", "\\a"),
      /** Backspace (\b) */
      ASCII_008((byte)8, "Backspace", "\\b"),
      /** Horizontal Tab (\t) */
      ASCII_009((byte)9, "Horizontal Tab", "\\t"),
      /** Line feed (\n) */
      ASCII_010((byte)10, "Line feed", "\\n"),
      /** Vertical Tab (\v) */
      ASCII_011((byte)11, "Vertical Tab", "\\v"),
      /** Form feed (\f) */
      ASCII_012((byte)12, "Form feed", "\\f"),
      /** Carriage return (\r) */
      ASCII_013((byte)13, "Carriage return", "\\r"),
      /** Shift Out */
      ASCII_014((byte)14, "Shift Out"),
      /** Shift In */
      ASCII_015((byte)15, "Shift In"),
      /** Data Link Escape */
      ASCII_016((byte)16, "Data Link Escape"),
      /** Device Control 1 (often XON) */
      ASCII_017((byte)17, "XmlDevice Control 1 (often XON)"),
      /** Device Control 2 */
      ASCII_018((byte)18, "XmlDevice Control 2"),
      /** Device Control 3 (often XOFF) */
      ASCII_019((byte)19, "XmlDevice Control 3 (often XOFF)"),
      /** Device Control 4 */
      ASCII_020((byte)20, "XmlDevice Control 4"),
      /** Negative Acknowledgement */
      ASCII_021((byte)21, "Negative Acknowledgement"),
      /** Synchronous Idle */
      ASCII_022((byte)22, "Synchronous Idle"),
      /** End of Transmission Block */
      ASCII_023((byte)23, "End of Transmission Block"),
      /** Cancel */
      ASCII_024((byte)24, "Cancel"),
      /** End of Medium */
      ASCII_025((byte)25, "End of Medium"),
      /** Substitute */
      ASCII_026((byte)26, "Substitute"),
      /** Escape */
      ASCII_027((byte)27, "Escape"),
      /** File Separator */
      ASCII_028((byte)28, "File Separator"),
      /** Group Separator */
      ASCII_029((byte)29, "Group Separator"),
      /** Record Separator */
      ASCII_030((byte)30, "Record Separator"),
      /** Unit Separator */
      ASCII_031((byte)31, "Unit Separator"),
      /** Space */
      ASCII_032((byte)32, "Space"),
      /** ! */
      ASCII_033((byte)33, "!"),
      /** " */
      ASCII_034((byte)34, "\""),
      /** # */
      ASCII_035((byte)35, "#"),
      /** $ */
      ASCII_036((byte)36, "$"),
      /** % */
      ASCII_037((byte)37, "%"),
      /** & */
      ASCII_038((byte)38, "&"),
      /** ' */
      ASCII_039((byte)39, "'"),
      /** ( */
      ASCII_040((byte)40, "("),
      /** ) */
      ASCII_041((byte)41, ")"),
      /** * */
      ASCII_042((byte)42, "*"),
      /** + */
      ASCII_043((byte)43, "+"),
      /** , */
      ASCII_044((byte)44, ","),
      /** - */
      ASCII_045((byte)45, "-"),
      /** . */
      ASCII_046((byte)46, "."),
      /** / */
      ASCII_047((byte)47, "/"),
      /** 0 */
      ASCII_048((byte)48, "0"),
      /** 1 */
      ASCII_049((byte)49, "1"),
      /** 2 */
      ASCII_050((byte)50, "2"),
      /** 3 */
      ASCII_051((byte)51, "3"),
      /** 4 */
      ASCII_052((byte)52, "4"),
      /** 5 */
      ASCII_053((byte)53, "5"),
      /** 6 */
      ASCII_054((byte)54, "6"),
      /** 7 */
      ASCII_055((byte)55, "7"),
      /** 8 */
      ASCII_056((byte)56, "8"),
      /** 9 */
      ASCII_057((byte)57, "9"),
      /** : */
      ASCII_058((byte)58, ":"),
      /** ; */
      ASCII_059((byte)59, ";"),
      /** < */
      ASCII_060((byte)60, "<"),
      /** = */
      ASCII_061((byte)61, "="),
      /** > */
      ASCII_062((byte)62, ">"),
      /** ? */
      ASCII_063((byte)63, "?"),
      /** @ */
      ASCII_064((byte)64, "@"),
      /** A */
      ASCII_065((byte)65, "A"),
      /** B */
      ASCII_066((byte)66, "B"),
      /** C */
      ASCII_067((byte)67, "C"),
      /** D */
      ASCII_068((byte)68, "D"),
      /** E */
      ASCII_069((byte)69, "E"),
      /** F */
      ASCII_070((byte)70, "F"),
      /** G */
      ASCII_071((byte)71, "G"),
      /** H */
      ASCII_072((byte)72, "H"),
      /** I */
      ASCII_073((byte)73, "I"),
      /** J */
      ASCII_074((byte)74, "J"),
      /** K */
      ASCII_075((byte)75, "K"),
      /** L */
      ASCII_076((byte)76, "L"),
      /** M */
      ASCII_077((byte)77, "M"),
      /** N */
      ASCII_078((byte)78, "N"),
      /** O */
      ASCII_079((byte)79, "O"),
      /** P */
      ASCII_080((byte)80, "P"),
      /** Q */
      ASCII_081((byte)81, "Q"),
      /** R */
      ASCII_082((byte)82, "R"),
      /** S */
      ASCII_083((byte)83, "S"),
      /** T */
      ASCII_084((byte)84, "T"),
      /** U */
      ASCII_085((byte)85, "U"),
      /** V */
      ASCII_086((byte)86, "V"),
      /** W */
      ASCII_087((byte)87, "W"),
      /** X */
      ASCII_088((byte)88, "X"),
      /** Y */
      ASCII_089((byte)89, "Y"),
      /** Z */
      ASCII_090((byte)90, "Z"),
      /** [ */
      ASCII_091((byte)91, "["),
      /** \ */
      ASCII_092((byte)92, "\\"),
      /** ] */
      ASCII_093((byte)93, "]"),
      /** ^ */
      ASCII_094((byte)94, "^"),
      /** _ */
      ASCII_095((byte)95, "_"),
      /** ` */
      ASCII_096((byte)96, "`"),
      /** a */
      ASCII_097((byte)97, "a"),
      /** b */
      ASCII_098((byte)98, "b"),
      /** c */
      ASCII_099((byte)99, "c"),
      /** d */
      ASCII_100((byte)100, "d"),
      /** e */
      ASCII_101((byte)101, "e"),
      /** f */
      ASCII_102((byte)102, "f"),
      /** g */
      ASCII_103((byte)103, "g"),
      /** h */
      ASCII_104((byte)104, "h"),
      /** i */
      ASCII_105((byte)105, "i"),
      /** j */
      ASCII_106((byte)106, "j"),
      /** k */
      ASCII_107((byte)107, "k"),
      /** l */
      ASCII_108((byte)108, "l"),
      /** m */
      ASCII_109((byte)109, "m"),
      /** n */
      ASCII_110((byte)110, "n"),
      /** o */
      ASCII_111((byte)111, "o"),
      /** p */
      ASCII_112((byte)112, "p"),
      /** q */
      ASCII_113((byte)113, "q"),
      /** r */
      ASCII_114((byte)114, "r"),
      /** s */
      ASCII_115((byte)115, "s"),
      /** t */
      ASCII_116((byte)116, "t"),
      /** u */
      ASCII_117((byte)117, "u"),
      /** v */
      ASCII_118((byte)118, "v"),
      /** w */
      ASCII_119((byte)119, "w"),
      /** x */
      ASCII_120((byte)120, "x"),
      /** y */
      ASCII_121((byte)121, "y"),
      /** z */
      ASCII_122((byte)122, "z"),
      /** { */
      ASCII_123((byte)123, "{"),
      /** | */
      ASCII_124((byte)124, "|"),
      /** } */
      ASCII_125((byte)125, "}"),
      /** ~ */
      ASCII_126((byte)126, "~"),
      /** Delete */
      ASCII_127((byte)127, "Delete");

   private final byte code;
   private final String name;
   private final String escapeCode;

   ASCIICharacter(final byte code, final String name)
      {
      this(code, name, null);
      }

   ASCIICharacter(final byte code, final String name, final String escapeCode)
      {
      this.code = code;
      this.name = name;
      this.escapeCode = escapeCode;
      }

   public byte getCode()
      {
      return code;
      }

   public String getName()
      {
      return name;
      }

   public String getEscapeCode()
      {
      return escapeCode;
      }

   public String toString()
      {
      if (escapeCode != null)
         {
         return name + " (" + escapeCode + ")";
         }
      return name;
      }
   }
