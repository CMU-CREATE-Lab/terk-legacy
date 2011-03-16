package edu.cmu.ri.mrpl.TeRK.roboticon.manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.PropertyResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonDirectory.RoboticonType;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.SortedRoboticonManagerModel.SortOrder;
import edu.cmu.ri.mrpl.swing.DragAndDropJList;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class RoboticonManagerView implements RoboticonManagerListener
   {
   private static final Logger LOG = Logger.getLogger(RoboticonManagerView.class);//  @jve:decl-index=0:

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(RoboticonManagerView.class.getName());//  @jve:decl-index=0:
   private static final Font FONT = GUIConstants.FONT_SMALL;
   private static final Font BOLD_FONT = FONT.deriveFont(Font.BOLD);
   private final SortedRoboticonManagerModel privateSortedRoboticonModel;
   private final SortedRoboticonManagerModel publicSortedRoboticonModel;
   private final DragAndDropJList privateList;
   private final DragAndDropJList publicList;

   private RoboticonListPanel privateListPanel = null;
   private RoboticonListPanel publicListPanel = null;

   private JPanel listPanel = null;//  @jve:decl-index=0:visual-constraint="179,19"

   public RoboticonManagerView(final RoboticonManagerModel roboticonManagerModel,
                               final RoboticonManagerModel publicRoboticonManagerModel)
      {
      privateSortedRoboticonModel = new SortedRoboticonManagerModel(roboticonManagerModel, SortOrder.TYPE);

      privateList = new DragAndDropJList(privateSortedRoboticonModel);
      privateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      privateList.setLayoutOrientation(DragAndDropJList.VERTICAL);
      privateList.setCellRenderer(new MyListCellRenderer());
      privateList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      privateList.setDragEnabled(true);
      privateList.setSelectionBackground(Color.orange);
      privateList.setSelectionForeground(Color.white);

      publicSortedRoboticonModel = new SortedRoboticonManagerModel(publicRoboticonManagerModel, SortOrder.NAME);

      publicList = new DragAndDropJList(publicSortedRoboticonModel);
      publicList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      publicList.setLayoutOrientation(DragAndDropJList.VERTICAL);
      publicList.setCellRenderer(new MyListCellRenderer());
      publicList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      publicList.setDragEnabled(true);
      publicList.setSelectionBackground(Color.orange);
      publicList.setSelectionForeground(Color.white);
      getListPanel();
      }

   public Component getRoboticonListComponent()
      {
      return listPanel;
      }

   public void contentsChanged()
      {
      LOG.trace("RoboticonManagerView.contentsChanged()");
      }

   public void setEnabled(final boolean isEnabled)
      {
      privateList.setEnabled(isEnabled);
      //privateListPanel.setIsSupported(isEnabled);    //not actually doing anything?
      //publicListPanel.setIsSupported(isEnabled);     //not actually doing anything?
      //listPanel.setIsSupported(isEnabled);           //not actually doing anything?
      publicList.setEnabled(isEnabled);
      }

   private final class MyListCellRenderer extends JLabel implements ListCellRenderer
      {
      private static final long serialVersionUID = 1769903297941249743L;

      private MyListCellRenderer()
         {
         setOpaque(true);
         setBorder(BorderFactory.createEmptyBorder());
         setHorizontalAlignment(JLabel.LEFT);
         setVerticalTextPosition(JLabel.CENTER);
         setFont(FONT);
         }

      public Component getListCellRendererComponent(final JList list,
                                                    final Object value,
                                                    final int index,
                                                    final boolean isSelected,
                                                    final boolean cellHasFocus)
         {
         final RoboticonFile roboticonFile = (RoboticonFile)value;

         JLabel label = new JLabel();
         label.setOpaque(true);
         label.setBorder(BorderFactory.createEmptyBorder());
         label.setHorizontalAlignment(JLabel.LEFT);
         label.setVerticalTextPosition(JLabel.CENTER);
         label.setFont(FONT);
         label.setEnabled(list.isEnabled());
         label.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
         label.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());

         //BOLD text if sequence
         if (roboticonFile.roboticonType.equals(RoboticonType.SEQUENCE))
            {
            label.setFont(BOLD_FONT);
            }
         String fileName = roboticonFile == null ? "unknown" : roboticonFile.getName();
         if (fileName.lastIndexOf('.') != -1)
            {
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            }
         label.setText(fileName);
         String toolTip = null;
         if (roboticonFile != null)
            {
            toolTip = roboticonFile.senderId;
            if (toolTip.length() == 0)
               {
               toolTip = roboticonFile.getAbsolutePath();
               }
            }
         label.setToolTipText(toolTip);
         return label;
         }
      }

   /**
    * This method initializes privateListPanel
    *
    * @return javax.swing.JPanel
    */
   private RoboticonListPanel getPrivateListPanel()
      {
      if (privateListPanel == null)
         {
         privateListPanel = new RoboticonListPanel(
               RESOURCES.getString("label.roboticons"),
               privateList,
               new String[]{SortOrder.NAME.toString(), SortOrder.TYPE.toString(), SortOrder.DATE.toString()}, 1,
               new Dimension(200, 250));
         privateListPanel.addSortOrderListener(
               new SortOrderListenerIf()
               {
               public void sortOrderChanged(SortOrder newOrder)
                  {
                  privateSortedRoboticonModel.setSortOrder(newOrder);
                  }
               });
         }
      return privateListPanel;
      }

   /**
    * This method initializes publicListPanel
    *
    * @return javax.swing.JPanel
    */
   private RoboticonListPanel getPublicListPanel()
      {
      if (publicListPanel == null)
         {
         publicListPanel = new RoboticonListPanel(
               RESOURCES.getString("label.shared-roboticons"),
               publicList,
               new String[]{SortOrder.NAME.toString(), SortOrder.TYPE.toString(), SortOrder.DATE.toString(), SortOrder.OWNER.toString()}, 0,
               new Dimension(200, 250));
         publicListPanel.addSortOrderListener(
               new SortOrderListenerIf()
               {
               public void sortOrderChanged(SortOrder newOrder)
                  {
                  publicSortedRoboticonModel.setSortOrder(newOrder);
                  }
               });
         }
      return publicListPanel;
      }

   /**
    * This method initializes listPanel
    *
    * @return javax.swing.JPanel
    */
   private JPanel getListPanel()
      {
      if (listPanel == null)
         {
         listPanel = new JPanel();
         listPanel.setLayout(new BorderLayout());
         listPanel.setPreferredSize(new Dimension(225, 400));
         listPanel.add(getPublicListPanel().getPanel(), BorderLayout.SOUTH);
         listPanel.add(Box.createRigidArea(new Dimension(5, 5)));
         listPanel.add(getPrivateListPanel().getPanel(), BorderLayout.NORTH);
         listPanel.add(Box.createRigidArea(new Dimension(5, 5)));
         listPanel.setBackground(Color.orange);
         }
      return listPanel;
      }

   public SortedRoboticonManagerModel getPublicSortedRoboticonModel()
      {
      return publicSortedRoboticonModel;
      }

   public JList getPrivateListComponent()
      {
      return privateList;
      }

   public JList getPublicListComponent()
      {
      return publicList;
      }
   }
