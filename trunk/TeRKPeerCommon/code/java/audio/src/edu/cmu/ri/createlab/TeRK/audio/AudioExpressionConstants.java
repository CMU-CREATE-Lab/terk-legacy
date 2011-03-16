package edu.cmu.ri.createlab.TeRK.audio;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * <code>AudioExpressionConstants</code> defines various constans for audio expressions.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"PublicStaticCollectionField"})
public final class AudioExpressionConstants
   {
   public static final String OPERATION_NAME_TONE = "playTone";
   public static final String PARAMETER_NAME_FREQUENCY = "frequency";
   public static final String PARAMETER_NAME_AMPLITUDE = "amplitude";
   public static final String PARAMETER_NAME_DURATION = "duration";

   public static final String OPERATION_NAME_CLIP = "playClip";
   public static final String PARAMETER_NAME_FILE = "file";

   public static final String OPERATION_NAME_SPEECH = "speak";
   public static final String PARAMETER_NAME_TEXT = "text";

   public static final Set<String> TONE_PARAMETER_NAMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(PARAMETER_NAME_FREQUENCY, PARAMETER_NAME_AMPLITUDE, PARAMETER_NAME_DURATION)));
   public static final Set<String> CLIP_PARAMETER_NAMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(PARAMETER_NAME_FILE)));
   public static final Set<String> SPEECH_PARAMETER_NAMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(PARAMETER_NAME_TEXT)));
   public static final Map<String, Set<String>> OPERATIONS_TO_PARAMETERS_MAP;

   public static final Map<AudioControlPanel.Mode, String> MODE_NAME_MAP;

   static
      {
      final Map<String, Set<String>> operationsToParametersMap = new HashMap<String, Set<String>>();
      operationsToParametersMap.put(OPERATION_NAME_TONE, TONE_PARAMETER_NAMES);
      operationsToParametersMap.put(OPERATION_NAME_CLIP, CLIP_PARAMETER_NAMES);
      operationsToParametersMap.put(OPERATION_NAME_SPEECH, SPEECH_PARAMETER_NAMES);
      OPERATIONS_TO_PARAMETERS_MAP = Collections.unmodifiableMap(operationsToParametersMap);

      final Map<AudioControlPanel.Mode, String> modeNameMap = new HashMap<AudioControlPanel.Mode, String>();
      modeNameMap.put(AudioControlPanel.Mode.TONE, OPERATION_NAME_TONE);
      modeNameMap.put(AudioControlPanel.Mode.CLIP, OPERATION_NAME_CLIP);
      modeNameMap.put(AudioControlPanel.Mode.SPEECH, OPERATION_NAME_SPEECH);
      MODE_NAME_MAP = Collections.unmodifiableMap(modeNameMap);
      }

   private AudioExpressionConstants()
      {
      // private to prevent instantiation
      }
   }
