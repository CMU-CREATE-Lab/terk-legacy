package edu.cmu.ri.mrpl.TeRK.peerinformation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PropertyResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import edu.cmu.ri.mrpl.swing.ColorUtils;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class PeerInfoView implements PeerInfoListener
   {
   private static final Logger LOG = Logger.getLogger(PeerInfoView.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(PeerInfoView.class.getName());

   private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("MMM d 'at' h:mm:ss aaa");

   private static final Font FONT = new Font("Verdana", 0, 11);

   private final JPanel listPanel = new JPanel();
   private final JList list;
   private final JScrollPane listScroller;
   private final MyListCellRenderer listCellRenderer = new MyListCellRenderer();
   private final PeerInfoModel peerInfoModel;

   /** Creates the view using a vertical list. */
   public PeerInfoView(final PeerInfoModel peerInfoModel)
      {
      this(peerInfoModel, false);
      }

   /** Creates the view using a horizontal list if <code>useHorizontalList</code> is <code>true</code>; vertical otherwise. */
   public PeerInfoView(final PeerInfoModel peerInfoModel, final boolean useHorizontalList)
      {
      this.peerInfoModel = peerInfoModel;
      list = new JList(peerInfoModel);
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      list.setLayoutOrientation(useHorizontalList ? JList.HORIZONTAL_WRAP : JList.VERTICAL);
      list.setVisibleRowCount(useHorizontalList ? 1 : 10);
      list.setCellRenderer(listCellRenderer);
      //list.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); //not needed until tabs can be closed/opened

      listScroller = new JScrollPane(list);
      listScroller.setPreferredSize(new Dimension(200, 200));

      /*final JPanel connectedUsersPanel = new JPanel();
  connectedUsersPanel.setLayout(new BoxLayout(connectedUsersPanel, BoxLayout.X_AXIS));
  //final JLabel connectedUsersLabel = new JLabel(RESOURCES.getString("label.connected-users"));
  //connectedUsersLabel.setFont(FONT);
  //connectedUsersPanel.add(connectedUsersLabel);
  connectedUsersPanel.add(Box.createGlue()); //is this doing anything?*/

      listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
      // listPanel.add(connectedUsersPanel);            // nothing is ever added to this...
      //mlistPanel.add(Box.createRigidArea(new Dimension(5, 5)));
      listPanel.add(listScroller);
      }

   public Component getListComponent()
      {
      return listPanel;
      }

   public void setEnabled(final boolean isEnabled)
      {
      list.setEnabled(isEnabled);
      listScroller.setEnabled(isEnabled);
      }

   public void peerConnected(final PeerInfo peerInfo)
      {
      if (LOG.isTraceEnabled())
         {
         LOG.trace("PeerInfoView.peerConnected(" + peerInfo.userId + "," + peerInfo.connectionTimestamp + ")");
         }
      }

   public void peerUpdated(final PeerInfo peerInfo)
      {
      if (LOG.isTraceEnabled())
         {
         LOG.trace("PeerInfoView.peerUpdated(" + peerInfo.userId + "," + peerInfo.connectionTimestamp + ")");
         }
      }

   public void peerDisconnected(final PeerInfo peerInfo)
      {
      if (LOG.isTraceEnabled())
         {
         LOG.trace("PeerInfoView.peerDisconnected(" + peerInfo.userId + "," + peerInfo.connectionTimestamp + ")");
         }
      }

   public void peersChanged()
      {
      LOG.trace("PeerInfoView.peersChanged()");
      }

   private final class MyListCellRenderer extends JPanel implements ListCellRenderer
      {
      private final JLabel nameAndColorLabel = new JLabel();
      private final JLabel iconLabel = new JLabel();
      //private final ColorIcon colorIcon = new ColorIcon(); //not currently being used

      private MyListCellRenderer()
         {
         setOpaque(true);
         setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 6));

         nameAndColorLabel.setOpaque(true);
         nameAndColorLabel.setBorder(BorderFactory.createEmptyBorder());
         nameAndColorLabel.setHorizontalAlignment(JLabel.LEFT);
         nameAndColorLabel.setVerticalAlignment(JLabel.CENTER);
         nameAndColorLabel.setVerticalTextPosition(JLabel.CENTER);
         nameAndColorLabel.setFont(FONT);

         iconLabel.setOpaque(true);
         iconLabel.setBorder(BorderFactory.createEmptyBorder());
         iconLabel.setHorizontalAlignment(JLabel.RIGHT);
         iconLabel.setVerticalAlignment(JLabel.CENTER);
         iconLabel.setVerticalTextPosition(JLabel.CENTER);
         iconLabel.setIconTextGap(0);

         setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
         add(iconLabel);
         add(Box.createHorizontalStrut(4));
         add(nameAndColorLabel);
         }

      public Component getListCellRendererComponent(final JList list,
                                                    final Object value,
                                                    final int index,
                                                    final boolean isSelected,
                                                    final boolean cellHasFocus)
         {
         final PeerInfo peerInfo = (PeerInfo)value;
         nameAndColorLabel.setText(peerInfo.userId);
         iconLabel.setIcon(peerInfoModel.getUserIcon(peerInfo.userId, 1));

         setForeground(list.getForeground());
         setBackground(list.getBackground());
         nameAndColorLabel.setForeground(list.getForeground());
         nameAndColorLabel.setBackground(list.getBackground());
         iconLabel.setForeground(list.getForeground());
         iconLabel.setBackground(list.getBackground());
         iconLabel.setEnabled(peerInfo.isConnected);

         // get the peer's color
         final String hexColor = peerInfo.attributes.get("hexColor");
         Color color = Color.BLACK;
         if (hexColor != null)
            {
            try
               {
               color = ColorUtils.getColor(hexColor);
               }
            catch (Exception e)
               {
               LOG.warn("Invalid hex color [" + hexColor + "], defaulting to black", e);
               color = Color.BLACK;
               }
            }
         //colorIcon.setColor(color);   //not currently being used
         if (peerInfo.isConnected)
            {
            setToolTipText(RESOURCES.getString("tooltip.online-since") + " " + DATE_FORMATTER.format(new Date(peerInfo.connectionTimestamp)));
            nameAndColorLabel.setForeground(color);
            }
         else
            {
            nameAndColorLabel.setForeground(Color.LIGHT_GRAY);
            setToolTipText(RESOURCES.getString("tooltip.offline"));
            }
         setEnabled(list.isEnabled());

         return this;
         }

      private final class ColorIcon implements Icon
         {
         private static final int SIZE = 16;
         private Color color = Color.BLACK;

         public void paintIcon(final Component c, final Graphics g, final int x, final int y)
            {
            if (g != null)
               {
               g.setColor(Color.WHITE);
               g.fillOval(0, 0, SIZE, SIZE);
               g.setColor(color);
               g.fillOval(2, 2, SIZE - 4, SIZE - 4);
               }
            }

         public int getIconWidth()
            {
            return SIZE;
            }

         public int getIconHeight()
            {
            return SIZE;
            }

         public void setColor(final Color color)
            {
            this.color = color;
            }
         }
      }
   }
