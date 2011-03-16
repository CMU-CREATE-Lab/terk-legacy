/*
 * MorseCodeFrame.java
 *
 * Created on Jan 12, 2009, 6:36:27 PM
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 *
 * @author Erik Pasternak
 *
 * Most of this code was created automatically by Java NetBeans IDE.
 */
public class MorseCodeFrame extends JFrame implements ActionListener
   {

   //These were added by me to let me differentiate between the button and the
   //tail being pressed.
   final static public int BUTTON = 0;
   final static public int TAIL = 1;

   /** Creates new form MorseCodeFrame */
   public MorseCodeFrame()
      {
      initComponents();
      new Timer(10, this).start();
      }

   //This adds a letter to the message box by looking up the morse code in
   //the morse code box to find the appropriate letter.
   public void addLetter()
      {
      //Get the current string of morse code
      String currLetter = morseText.getText();
      //if it's not empty
      if (currLetter.length() > 0)
         {
         //clear the morse code box
         morseText.setText("");
         //trim what we have to make sure there's no whitespace
         currLetter = currLetter.trim();
         //set a flag that we haven't found it yet
         boolean letterFound = false;
         //Go through the lookup array for morse code strings
         for (int i = 0; i < MorseCodeBeeper.MorseLookup.length; i++)
            {
            //If we find it add it to the message and break
            if (currLetter.compareTo(MorseCodeBeeper.MorseLookup[i]) == 0)
               {
               messageText.append(MorseCodeBeeper.asciiLookup[i]);
               letterFound = true;
               break;
               }
            }
         //if we didn't find it give some output
         if (!letterFound)
            {
            System.out.printf("%s was not recognized and will be ignored\n", currLetter);
            }
         }
      }

   /** This method should be called everytime a change in the state
    * of the beep occurs. This can either be caused by the finch
    * crossing its threshhold for beeping or from the beep button on
    * the GUI being pressed/unpressed.
    */

   public void beepStateChanged(boolean pressed, int pressSrc)
      {

      //We want the gui button to take precedence, since it is not pressed by
      //default.
      if (this.buttonPressed)
         {
         pressed = true;
         }
      if (pressed)
         {
         pressCount++;
         nPressCount = 0;
         }
      else
         {
         nPressCount++;
         pressCount = 0;
         }
      //keeping a count lets us filter out the bouncing in the tail but introduces
      //about 60-100 ms of delay
      if (pressed != prevBeepPressed && ((pressCount > 3 || nPressCount > 3) || pressSrc == BUTTON))
         {
         //Set up some variables for figuring out what to do because of the
         //transition
         int thresh1;
         int thresh2;
         int threshsp1;
         //We still want to ignore very short things because they are usually
         //the tail bouncing
         int ignoreThresh = 50; //dotLength/4;
         double oldTime = startTime;
         //Find out how long since we last changed states
         double timeDiff = System.currentTimeMillis() - oldTime;
         //if it wasn't too short restart the timer
         if (timeDiff > ignoreThresh)
            {
            startTime = System.currentTimeMillis();
            }
         //These set up how closely we follow the standard morse restrictions
         //on timing. The not strict threshholds mostly apply to the wait time
         //after the button was released.
         if (strictTimingBox.isEnabled())
            {
            thresh1 = oneThreeThresh;
            threshsp1 = oneThreeThresh;
            thresh2 = threeSevenThresh;
            }
         else
            {
            thresh1 = oneThreeThresh;
            threshsp1 = notStrict13Thresh;
            thresh2 = notStrict37Thresh;
            }
         //a dash is three dots
         //a space between dots and dashes is one dot
         //a space between letters is 3 dots
         //a space between words is 7 dots

         //If we just let go of the button
         if (prevBeepPressed && !pressed)
            {
            //if it was too short a time amount ignore it.
            if (timeDiff < ignoreThresh)
               {
               }
            //if it was a dot length output a dot
            else if (timeDiff < thresh1)
               {
               morseText.setText(morseText.getText() + ".");
               }
            //for a dash length output a dash
            else if (timeDiff < thresh2)
               {
               morseText.setText(morseText.getText() + "-");
               }
            }
         //if we just pressed the button
         else if (!prevBeepPressed && pressed)
            {
            //Don't do anything, we only add things when we let go of the button.
            if (timeDiff < threshsp1)
               {
               }
            else if (timeDiff < thresh2)
               {
               }
            else
               {
               }
            }
         //if we did something change the previous state
         if (timeDiff > ignoreThresh)
            {
            prevBeepPressed = pressed;
            }
         }
      }

   //This is an interrupt that fires every 10ms and performs updates on the output
   public void actionPerformed(ActionEvent e)
      {
      int thresh1;
      int thresh2;
      int threshsp1;
      //We'd like to know how long since our current state started
      double timeDiff = System.currentTimeMillis() - startTime;
      //Again, we set different threshholds depending on how strict we're being
      if (strictTimingBox.isEnabled())
         {
         thresh1 = oneThreeThresh;
         threshsp1 = oneThreeThresh;
         thresh2 = threeSevenThresh;
         }
      else
         {
         thresh1 = oneThreeThresh;
         threshsp1 = notStrict13Thresh;
         thresh2 = notStrict37Thresh;
         }
      //If we just started the program set the start time
      if (startTime == 0)
         {
         startTime = System.currentTimeMillis();
         }
      //if the button is not currently being pressed
      if (!prevBeepPressed)
         {
         //And we're below the time for a space between dots
         if (timeDiff < threshsp1)
            {
            //just update the output bar
            typeTimingBar.setValue((int)(100 * timeDiff / thresh1));
            typeTimingBar.setString("dot-space");
            }
         //if we've reached a letter space timing
         else if (timeDiff < thresh2)
            {
            //update the output bar
            typeTimingBar.setValue((int)(100 * (timeDiff - thresh1) / (thresh2 - thresh1)));
            typeTimingBar.setString("letter-space");
            //and add the letter if it exists
            addLetter();
            }
         //if we're above that it's a word space
         else
            {
            //if we haven't already add a space to the message
            typeTimingBar.setValue(100);
            typeTimingBar.setString("word-space");
            if (!messageText.getText().endsWith(" "))
               {
               messageText.append(" ");
               }
            }
         }
      //If it is currently pressed
      else
         {
         //We just update the output appropriately
         if (timeDiff < thresh1)
            {
            typeTimingBar.setValue((int)(100 * timeDiff / thresh1));
            typeTimingBar.setString("dot");
            }
         else if (timeDiff < thresh2)
            {
            typeTimingBar.setValue((int)(100 * (timeDiff - thresh1) / (thresh2 - thresh1)));
            typeTimingBar.setString("dash");
            }
         else if (timeDiff < backspaceThresh)
            {
            typeTimingBar.setValue((int)(100 * timeDiff - thresh2) / (backspaceThresh - thresh2));
            typeTimingBar.setString("nothing");
            }
         //If they hold it down long enough start deleting chars from the message
         else if (timeDiff > backspaceThresh)
            {
            typeTimingBar.setValue(100);
            typeTimingBar.setString("backspace");
            if (backspaceCount == 0)
               {
               int msgLength = messageText.getText().length() - 2;
               if (msgLength >= 0)
                  {
                  messageText.setText(messageText.getText().substring(0, msgLength));
                  }
               }
            //and clear any morse they might have input
            morseText.setText("");
            backspaceCount = (++backspaceCount) % 10;
            }
         }
      }

   /////The code below here is autogenerated by NetBeans///////////////////////////

   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">
   private void initComponents()
      {

      startTime = 0;
      prevBeepPressed = false;
      dotLength = 800;
      oneThreeThresh = 2 * dotLength;
      threeSevenThresh = 6 * dotLength;
      notStrict13Thresh = 6 * dotLength;
      notStrict37Thresh = 12 * dotLength;
      backspaceThresh = 20 * dotLength;
      backspaceCount = 0;
      pressCount = 0;
      nPressCount = 0;

      jScrollPane1 = new javax.swing.JScrollPane();
      messageText = new javax.swing.JTextArea();
      morseText = new javax.swing.JTextField();
      beepButton = new javax.swing.JButton();
      jScrollPane2 = new javax.swing.JScrollPane();
      directionsText = new javax.swing.JTextArea();
      jLabel1 = new javax.swing.JLabel();
      jLabel2 = new javax.swing.JLabel();
      strictTimingBox = new javax.swing.JCheckBox();
      dotLengthSlider = new javax.swing.JSlider();
      dotLengthLabel = new javax.swing.JLabel();
      freqLabel = new javax.swing.JLabel();
      freqSlider = new javax.swing.JSlider();
      typeTimingBar = new javax.swing.JProgressBar();

      setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

      messageText.setColumns(20);
      messageText.setEditable(false);
      messageText.setRows(5);
      jScrollPane1.setViewportView(messageText);
      messageText.getAccessibleContext().setAccessibleName("morseMessageBox");

      morseText.setEditable(false);
      morseText.setFont(new java.awt.Font("Bookman Old Style", 1, 18));
      morseText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
      morseText.setText(".-.---.");
      morseText.setName("MorseLetter"); // NOI18N

      beepButton.setBackground(new java.awt.Color(40, 0, 200));
      beepButton.setLabel("Beep");
      beepButton.setName("BeepIndicator"); // NOI18N
      beepButton.addMouseListener(new java.awt.event.MouseAdapter()
      {
      public void mousePressed(java.awt.event.MouseEvent evt)
         {
         beepButtonMousePressed(evt);
         }

      public void mouseReleased(java.awt.event.MouseEvent evt)
         {
         beepButtonMouseReleased(evt);
         }
      });
      /*
      beepButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            beepButtonActionPerformed(evt);
         }
      });
      beepButton.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
          public void propertyChange(java.beans.PropertyChangeEvent evt) {
              beepButtonPropertyChange(evt);
          }
      });
      beepButton.addChangeListener(new javax.swing.event.ChangeListener() {
          public void stateChanged(javax.swing.event.ChangeEvent evt) {
              beepButtonStateChanged(evt);
          }
      });
      */

      jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

      directionsText.setColumns(20);
      directionsText.setEditable(false);
      directionsText.setFont(new java.awt.Font("Arial", 0, 10));
      directionsText.setLineWrap(true);
      directionsText.setRows(5);
      directionsText.setText("Use the finch or the beep button to input morse code. What you're inputting will appear in the bottom box as dots and dashes and the translated message will appear in the center left box. Hold down beep to backspace. Strict Timing will enforce standard morse timing on input.");
      directionsText.setWrapStyleWord(true);
      jScrollPane2.setViewportView(directionsText);

      jLabel1.setText("Message");

      jLabel2.setText("Current Character");

      strictTimingBox.setText("Strict Timing");
      strictTimingBox.addActionListener(new java.awt.event.ActionListener()
      {
      public void actionPerformed(java.awt.event.ActionEvent evt)
         {
         strictTimingBoxActionPerformed(evt);
         }
      });

      dotLengthSlider.setMajorTickSpacing(50);
      dotLengthSlider.setMaximum(1000);
      dotLengthSlider.setMinimum(100);
      dotLengthSlider.setPaintTicks(true);
      dotLengthSlider.setValue(dotLength);
      dotLengthSlider.addPropertyChangeListener(new java.beans.PropertyChangeListener()
      {
      public void propertyChange(java.beans.PropertyChangeEvent evt)
         {
         dotLengthSliderPropertyChange(evt);
         }
      });
      dotLengthSlider.addChangeListener(new javax.swing.event.ChangeListener()
      {
      public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
         dotLengthSliderStateChanged(evt);
         }
      });

      dotLengthLabel.setText("Dot Length: 100ms");

      freqLabel.setText("Beep Frequency: 700Hz");

      freqSlider.setMaximum(10000);
      freqSlider.setMinimum(20);
      freqSlider.addPropertyChangeListener(new java.beans.PropertyChangeListener()
      {
      public void propertyChange(java.beans.PropertyChangeEvent evt)
         {
         freqSliderPropertyChange(evt);
         }
      });
      freqSlider.addChangeListener(new javax.swing.event.ChangeListener()
      {
      public void stateChanged(javax.swing.event.ChangeEvent evt)
         {
         freqSliderStateChanged(evt);
         }
      });

      typeTimingBar.setName("letter space"); // NOI18N
      typeTimingBar.setString("letter space");
      typeTimingBar.setStringPainted(true);

      org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                  .add(layout.createSequentialGroup()
                             .addContainerGap()
                             .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                                        .add(layout.createSequentialGroup()
                                                   .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                                                              .add(morseText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                                                              .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 224, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                              .add(jLabel1)
                                                              .add(jLabel2))
                                                   .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                   .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                              .add(org.jdesktop.layout.GroupLayout.CENTER, dotLengthLabel)
                                                              .add(org.jdesktop.layout.GroupLayout.CENTER, dotLengthSlider, 0, 0, Short.MAX_VALUE)
                                                              .add(org.jdesktop.layout.GroupLayout.CENTER, freqLabel)
                                                              .add(org.jdesktop.layout.GroupLayout.CENTER, freqSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                              .add(layout.createSequentialGroup()
                                                                         .add(beepButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                                         .add(strictTimingBox))
                                                              .add(typeTimingBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                             .addContainerGap())
      );
      layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                  .add(layout.createSequentialGroup()
                             .addContainerGap()
                             .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                             .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                             .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(layout.createSequentialGroup()
                                                   .add(2, 2, 2)
                                                   .add(jLabel1)
                                                   .add(2, 2, 2)
                                                   .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 106, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .add(layout.createSequentialGroup()
                                                   .add(dotLengthLabel)
                                                   .add(1, 1, 1)
                                                   .add(dotLengthSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                   .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                   .add(freqLabel)
                                                   .add(1, 1, 1)
                                                   .add(freqSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                             .add(10, 10, 10)
                             .add(jLabel2)
                             .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                             .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                        .add(layout.createSequentialGroup()
                                                   .add(morseText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                   .add(24, 24, 24))
                                        .add(layout.createSequentialGroup()
                                                   .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                              .add(beepButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                                                              .add(strictTimingBox))
                                                   .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                   .add(typeTimingBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                   .addContainerGap())))
      );

      morseText.getAccessibleContext().setAccessibleName("morseMorseBox");
      dotLengthSlider.getAccessibleContext().setAccessibleName("dotLengthSlider");
      dotLengthLabel.getAccessibleContext().setAccessibleName("Tick Length: 100ms");

      pack();
      }// </editor-fold>

   private void dotLengthSliderPropertyChange(java.beans.PropertyChangeEvent evt)
      {
      dotLengthLabel.setText("Dot Length: " + this.dotLengthSlider.getValue() + "ms");
      }

   private void dotLengthSliderStateChanged(javax.swing.event.ChangeEvent evt)
      {
      dotLengthLabel.setText("Dot Length: " + this.dotLengthSlider.getValue() + "ms");
      dotLength = dotLengthSlider.getValue();
      oneThreeThresh = 2 * dotLength;
      threeSevenThresh = 6 * dotLength;
      notStrict13Thresh = 6 * dotLength;
      notStrict37Thresh = 12 * dotLength;
      backspaceThresh = 20 * dotLength;
      }

   private void strictTimingBoxActionPerformed(java.awt.event.ActionEvent evt)
      {
      // TODO add your handling code here:
      }

   private void freqSliderPropertyChange(java.beans.PropertyChangeEvent evt)
      {
      freqLabel.setText("Beep Frequency: " + freqSlider.getValue() + "Hz");
      }

   private void freqSliderStateChanged(javax.swing.event.ChangeEvent evt)
      {
      freqLabel.setText("Beep Frequency: " + freqSlider.getValue() + "Hz");
      }

   private void beepButtonMousePressed(java.awt.event.MouseEvent evt)
      {
      beepStateChanged(true, BUTTON);
      this.buttonPressed = true;
      }

   private void beepButtonMouseReleased(java.awt.event.MouseEvent evt)
      {
      beepStateChanged(false, BUTTON);
      this.buttonPressed = false;
      }

   public int getFreq()
      {
      return freqSlider.getValue();
      }

   /**
    * @param args the command line arguments
    */
   public static void main(String args[])
      {
      java.awt.EventQueue.invokeLater(new Runnable()
      {
      public void run()
         {
         new MorseCodeFrame().setVisible(true);
         }
      });
      }

   // Variables declaration - do not modify
   private javax.swing.JButton beepButton;
   private javax.swing.JTextArea directionsText;
   private javax.swing.JLabel dotLengthLabel;
   private javax.swing.JSlider dotLengthSlider;
   private javax.swing.JLabel freqLabel;
   private javax.swing.JSlider freqSlider;
   private javax.swing.JLabel jLabel1;
   private javax.swing.JLabel jLabel2;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JScrollPane jScrollPane2;
   private javax.swing.JTextArea messageText;
   private javax.swing.JTextField morseText;
   private javax.swing.JCheckBox strictTimingBox;
   private javax.swing.JProgressBar typeTimingBar;
   // End of auto-generated variables declaration

   //These are extra variables I've added in to help track things.
   private double startTime;
   private boolean prevBeepPressed;
   //private boolean prevPrevBeepPressed;
   private int dotLength;
   private int oneThreeThresh;
   private int threeSevenThresh;
   private int notStrict13Thresh;
   private int notStrict37Thresh;
   private int backspaceThresh;
   private int backspaceCount;
   private int pressCount;
   private int nPressCount;
   //this keeps track of whether or not the gui button is being pressed
   private boolean buttonPressed = false;
   }
