package edu.cmu.ri.mrpl.TeRK.roboticon.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.SortedRoboticonManagerModel.SortOrder;

public class RoboticonListPanel
   {

   public static final String FONT_NAME = "Verdana";
   public static final Font FONT_SMALL = new Font(FONT_NAME, 0, 11);

   private Dimension roboticonListDimension = null;//  @jve:decl-index=0:
   private JPanel privateRoboticonsPanel = null;//  @jve:decl-index=0:visual-constraint="211,41"
   private JScrollPane privateRoboticonScrollPanel = null;//  @jve:decl-index=0:visual-constraint="403,121"
   private JList privateRoboticonList = null;
   private JPanel privateRoboticonSortPanel = null;//  @jve:decl-index=0:visual-constraint="408,77"
   private JLabel myRoboticonsLabel = null;//  @jve:decl-index=0:visual-constraint="69,185"
   private JLabel sortLabel1 = null;//  @jve:decl-index=0:visual-constraint="66,134"
   private JComboBox privateSortOptions = null;//  @jve:decl-index=0:visual-constraint="69,236"

   private String panelTitle = "Roboticons";//  @jve:decl-index=0:

   private List<SortOrderListenerIf> sortListeners = new ArrayList<SortOrderListenerIf>(2);//  @jve:decl-index=0:

   public RoboticonListPanel(String panelTitle)
      {
      if (panelTitle != null)
         {
         this.panelTitle = panelTitle;
         }
      this.getPrivateRoboticonsPanel(null, 0);
      }

   /**
    * Constructor
    * @param panelTitle the title of the roboticon panel
    * @param list {@link JList} used to display roboticons
    * @param sortOptions String array of names of the available sort options
    * @param selectedOption the index of the initially selected sort option
    * @param listDimensions the dimensions (height and width) of the roboticon panel
    */
   public RoboticonListPanel
   (String panelTitle, JList list, String[] sortOptions, int selectedOption, Dimension listDimensions)
      {
      this.privateRoboticonList = list;
      this.roboticonListDimension = listDimensions;
      this.panelTitle = panelTitle;
      this.getPrivateRoboticonsPanel(sortOptions, selectedOption);
      privateRoboticonList.addKeyListener(new java.awt.event.KeyAdapter()
      {
      public void keyReleased(java.awt.event.KeyEvent e)
         {
         int keyCode = e.getKeyCode();
         if (keyCode == KeyEvent.VK_DELETE)
            {
            DeletePrivateRoboticon();
            }
         }
      });
      }

   public JPanel getPanel()
      {
      return privateRoboticonsPanel;
      }

   /**
    * This method initializes privateRoboticonsPanel
    *
    * @return javax.swing.JPanel
    */
   private JPanel getPrivateRoboticonsPanel(String[] options, int selectedIndex)
      {
      if (privateRoboticonsPanel == null)
         {
         privateRoboticonsPanel = new JPanel();
         privateRoboticonsPanel.setLayout(new BoxLayout(privateRoboticonsPanel, BoxLayout.Y_AXIS));
         privateRoboticonsPanel.add(getPrivateRoboticonSortPanel(options, selectedIndex));
         //privateRoboticonsPanel.add(Box.createRigidArea(new Dimension(5, 5)));
         privateRoboticonsPanel.add(getPrivateRoboticonScrollPanel());
         //privateRoboticonsPanel.add(Box.createRigidArea(new Dimension(5, 5)));
         }
      return privateRoboticonsPanel;
      }

   private Dimension getRoboticonListDimension()
      {
      if (this.roboticonListDimension == null)
         {
         this.roboticonListDimension = new Dimension(200, 150);
         }
      return this.roboticonListDimension;
      }

   /**
    * This method initializes privateRoboticonScrollPanel
    *
    * @return javax.swing.JScrollPane
    */
   private JScrollPane getPrivateRoboticonScrollPanel()
      {
      if (privateRoboticonScrollPanel == null)
         {
         privateRoboticonScrollPanel = new JScrollPane(
               JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
               JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
         privateRoboticonScrollPanel.setBackground(new Color(255, 255, 204));
         privateRoboticonScrollPanel.setPreferredSize(getRoboticonListDimension());
         privateRoboticonScrollPanel.setViewportView(getPrivateRoboticonList());
         }
      return privateRoboticonScrollPanel;
      }

   /**
    * This method initializes privateRoboticonList
    *
    * @return javax.swing.JList
    */
   private JList getPrivateRoboticonList()
      {
      if (privateRoboticonList == null)
         {
         privateRoboticonList = new JList();
         privateRoboticonList.setPreferredSize(new Dimension(200, 150));
         privateRoboticonList.setBackground(new Color(255, 255, 204));
         }
      return privateRoboticonList;
      }

   /**
    * This method initializes jPanel
    *
    * @return javax.swing.JPanel
    */
   private JPanel getPrivateRoboticonSortPanel(String[] options, int selectedIndex)
      {
      if (privateRoboticonSortPanel == null)
         {
         privateRoboticonSortPanel = new JPanel();
         privateRoboticonSortPanel.setLayout(new BorderLayout(5, 5));
         privateRoboticonSortPanel.setSize(new Dimension(128, 38));
         privateRoboticonSortPanel.add(getMyRoboticonsLabel(), BorderLayout.PAGE_START);
         privateRoboticonSortPanel.add(getSortLabel1(), BorderLayout.LINE_START);
         privateRoboticonSortPanel.add(
               getPrivateSortOptions(options, selectedIndex), BorderLayout.CENTER);
         privateRoboticonSortPanel.setBackground(Color.orange);
         }
      return privateRoboticonSortPanel;
      }

   /**
    * This method initializes sortLabel
    *
    * @return javax.swing.JLabel
    */
   private JLabel getMyRoboticonsLabel()
      {
      if (myRoboticonsLabel == null)
         {
         myRoboticonsLabel = new JLabel();
         myRoboticonsLabel.setText(panelTitle);//this.panelTitle);
         myRoboticonsLabel.setFont(GUIConstants.FONT_SMALL);
         myRoboticonsLabel.setSize(new Dimension(100, 24));
         myRoboticonsLabel.setBackground(Color.orange);
         }
      return myRoboticonsLabel;
      }

   /**
    * This method initializes sortLabel1
    *
    * @return javax.swing.JLabel
    */
   private JLabel getSortLabel1()
      {
      if (sortLabel1 == null)
         {
         sortLabel1 = new JLabel();
         sortLabel1.setText("Sort By:");
         sortLabel1.setFont(GUIConstants.FONT_SMALL);
         sortLabel1.setSize(new Dimension(100, 28));
         sortLabel1.setBackground(Color.orange);
         }
      return sortLabel1;
      }

   /**
    * This method initializes privateSortOptions
    *
    * @return javax.swing.JComboBox
    */
   private JComboBox getPrivateSortOptions(String[] options, int selected)
      {
      if (privateSortOptions == null)
         {
         privateSortOptions = new JComboBox(options);
         privateSortOptions.setPreferredSize(new Dimension(100, 20));
         privateSortOptions.setSize(new Dimension(100, 20));
         privateSortOptions.setFont(GUIConstants.FONT_SMALL);
         privateSortOptions.setBackground(Color.white);
         privateSortOptions.addItemListener(
               new ItemListener()
               {
               public void itemStateChanged(ItemEvent e)
                  {
                  if (e.getStateChange() == ItemEvent.SELECTED)
                     {
                     informSortSelectionListeners();
                     }
                  }
               });
         }
      int selectedIndex = selected;
      if (options == null || (selectedIndex >= 0 && selectedIndex >= options.length))
         {
         selectedIndex = 0;
         }
      privateSortOptions.setSelectedIndex(selectedIndex);
      return privateSortOptions;
      }

   public void setEnabled(final boolean isEnabled)
      {
      privateRoboticonScrollPanel.setEnabled(isEnabled);
      }

   public void addSortOrderListener(SortOrderListenerIf listener)
      {
      if (listener != null && !sortListeners.contains(listener))
         {
         sortListeners.add(listener);
         }
      }

   public boolean removeSortOrderListener(SortOrderListenerIf listener)
      {
      boolean result = false;
      if (listener != null && !sortListeners.isEmpty())
         {
         result = sortListeners.remove(listener);
         }
      return result;
      }

   private void informSortSelectionListeners()
      {
      String selectedStr = (String)privateSortOptions.getSelectedItem();
      SortOrder selectedOrder = SortOrder.fromString(selectedStr);
      for (SortOrderListenerIf listener : sortListeners)
         {
         listener.sortOrderChanged(selectedOrder);
         }
      }

   private void DeletePrivateRoboticon()
      {
      RoboticonFile selectedRoboticon = (RoboticonFile)privateRoboticonList.getSelectedValue();
      String filePath = selectedRoboticon.toString();
      String fileName = selectedRoboticon.getName();
      int index = fileName.lastIndexOf(".xml");
      if (index > 0)
         {
         fileName = fileName.substring(0, index);
         }
      int delete = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + fileName + "?", "Delete Roboticon", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if (delete == JOptionPane.YES_OPTION)
         {
         //delete roboticon
         boolean success = (new File(filePath)).delete();
         if (!success)
            {
            // Deletion failed
            JOptionPane.showMessageDialog(null, "Failed to delete " + fileName + ".", "Delete Failed", JOptionPane.ERROR_MESSAGE);
            }
         }
      }
   }
