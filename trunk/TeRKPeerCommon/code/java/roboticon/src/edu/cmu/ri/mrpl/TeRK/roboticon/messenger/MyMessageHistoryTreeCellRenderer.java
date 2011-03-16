package edu.cmu.ri.mrpl.TeRK.roboticon.messenger;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PropertyResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.tree.DefaultTreeCellRenderer;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoModel;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.Roboticon;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessage;
import org.apache.log4j.Logger;

public class MyMessageHistoryTreeCellRenderer extends DefaultTreeCellRenderer
   {
   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(RoboticonMessengerView.class.getName());

   private static final long serialVersionUID = 6787381871394635518L;

   private static final Font FONT = new Font("Verdana", 0, 11);

   private static final Font ITALIC_FONT = FONT.deriveFont(Font.ITALIC);

   private static final Font BOLD_FONT = FONT.deriveFont(Font.BOLD);

   private static final SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
   private static final SimpleDateFormat sdt = new SimpleDateFormat("hh:mm a");

   private static final Logger LOG = Logger.getLogger(MyMessageHistoryTreeCellRenderer.class);

   private static ImageIcon attachmentIcon = null;

   private PeerInfoModel peerInfoModel = null;

   private Color treeBackgroundColor;

   public MyMessageHistoryTreeCellRenderer()
      {
      if (attachmentIcon == null)
         {
         java.net.URL imageURL = MyMessageHistoryTreeCellRenderer.class.getResource("attachmentIcon.png");
         attachmentIcon = new ImageIcon(imageURL);
         }
      }

   public MyMessageHistoryTreeCellRenderer(PeerInfoModel peerInfoModel)
      {
      this();
      this.peerInfoModel = peerInfoModel;
      }

   public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                 boolean sel, boolean expanded, boolean leaf, int row,
                                                 boolean hasFocus)
      {
      JComponent cellContents = null;
      JLabel nodeLabel = new JLabel();
      treeBackgroundColor = tree.getBackground();

      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                                         row, hasFocus);
      if (value != null)
         {
         if (this.isSubject(value))
            {
            SubjectNode subjectNode = (SubjectNode)value;

            if (subjectNode.roboticonMessage != null)
               {
               boolean attachmentInTree = subjectNode.hasAttachment();

               HeadingPanel headingPanel = new HeadingPanel(true, attachmentInTree);

               headingPanel.setMessage(subjectNode.roboticonMessage);

               cellContents = headingPanel;
               }
            else
               {
               nodeLabel.setText(subjectNode.subject);
               nodeLabel.setFont(BOLD_FONT);
               cellContents = nodeLabel;
               }

            if (LOG.isTraceEnabled())
               {
               LOG.trace("Rendering subject node: " + subjectNode.subject
                         + ", Number of children: "
                         + subjectNode.getChildCount());
               }
            }
         else
            {
            MessageNode messageNode = (MessageNode)value;
            CellPanel cell = new CellPanel();
            cell.setMessage(messageNode.roboticonMessage);

            if (sel)
               {
               cell.select();
               }
            else
               {
               cell.unselect();
               }

            cellContents = cell;
            }
         }
      else
         {
         nodeLabel.setText("<empty>");
         nodeLabel.setFont(FONT);
         cellContents = nodeLabel;
         }
      return cellContents;
      }

   private boolean isSubject(Object value)
      {
      boolean subjectFlag = false;
      try
         {
         SubjectNode subjectNode = (SubjectNode)value;
         subjectFlag = true;
         }
      catch (ClassCastException e)
         {// Do Nothing
         }
      return subjectFlag;
      }

   /*public Component getReplyButton(Component cell) {
       Component c = null;
       if(cell instanceof CellPanel){
           if(cell != null){
               c = ((CellPanel)cell).replyButton;
           }
       }
       return c;
   }*/

   private class CellPanel extends JPanel
      {
      public CellPanel()
         {
         initComponents();
         }

      private void initComponents()
         {
         headingPanel = new HeadingPanel(false, false);
         messagePanel = new javax.swing.JPanel();
         attachmentPanel = new javax.swing.JPanel();
         attachmentLabel = new javax.swing.JLabel();
         attachmentText = new javax.swing.JTextArea();
         messageText = new javax.swing.JTextArea();

         lineBorderUnselected = BorderFactory.createLineBorder(treeBackgroundColor, 3);
         lineBorderSelected = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 3);
         lineBorder = BorderFactory.createLineBorder(Color.black);

         unselectedBorder = BorderFactory.createCompoundBorder(lineBorderUnselected, lineBorder);
         selectedBorder = BorderFactory.createCompoundBorder(lineBorderSelected, lineBorder);

         //this.setBorder(messageBorder);
         this.setBorder(unselectedBorder);
         this.setBackground(Color.WHITE);

         messageText.setFont(FONT);
         messageText.setForeground(Color.BLACK);
         messageText.setOpaque(false);
         messageText.setColumns(25);
         messageText.setLineWrap(true);
         messageText.setWrapStyleWord(true);
         messageText.setAlignmentY(LEFT_ALIGNMENT);

         messagePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
         messagePanel.setAlignmentY(java.awt.Component.LEFT_ALIGNMENT);
         messagePanel.setOpaque(false);
         messagePanel.add(messageText);

         attachmentPanel.setLayout(new BoxLayout(attachmentPanel, BoxLayout.LINE_AXIS));
         attachmentPanel.setOpaque(false);
         attachmentPanel.setAlignmentY(LEFT_ALIGNMENT);

         attachmentLabel.setFont(FONT);
         attachmentLabel.setForeground(Color.BLACK);
         attachmentLabel.setOpaque(false);
         attachmentLabel.setIcon(attachmentIcon);
         attachmentLabel.setText(":");
         attachmentLabel.setAlignmentY(LEFT_ALIGNMENT);
         attachmentLabel.setAlignmentX(TOP_ALIGNMENT);

         attachmentText.setFont(FONT);
         attachmentText.setForeground(Color.DARK_GRAY);
         attachmentText.setOpaque(false);
         attachmentText.setColumns(20);
         attachmentText.setLineWrap(true);
         attachmentText.setWrapStyleWord(true);
         attachmentText.setAlignmentY(LEFT_ALIGNMENT);
         attachmentText.setAlignmentX(TOP_ALIGNMENT);

         attachmentPanel.add(attachmentLabel);
         attachmentPanel.add(attachmentText);

         this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
         this.add(headingPanel);
         this.add(messagePanel);
         this.add(attachmentPanel);
         }

      public void select()
         {
         this.setBorder(selectedBorder);
         }

      public void unselect()
         {
         this.setBorder(unselectedBorder);
         }

      public void setMessage(RoboticonMessage message)
         {
         headingPanel.setMessage(message);

         messageText.setText(message.theMessage.text);

         messageText.validate();
         messagePanel.doLayout();

         if (message.roboticons.size() == 0)
            {
            attachmentLabel.setVisible(false);
            attachmentText.setVisible(false);
            }
         else
            {
            for (Roboticon r : message.roboticons)
               {
               String name = r.filename;
               name = name.substring(0, name.indexOf(".xml"));
               attachmentText.append(name + "\n");
               }
            }
         attachmentText.setSize(attachmentText.getPreferredSize());
         }

      private HeadingPanel headingPanel;
      private javax.swing.JPanel messagePanel;
      private javax.swing.JPanel attachmentPanel;

      private javax.swing.JLabel attachmentLabel;
      private javax.swing.JTextArea attachmentText;

      private javax.swing.JTextArea messageText;

      private Border lineBorder;
      private Border lineBorderUnselected;
      private Border lineBorderSelected;
      private Border unselectedBorder;
      private Border selectedBorder;
      }

   private class HeadingPanel extends JPanel
      {
      public HeadingPanel(boolean isSubject, boolean attachmentInTree)
         {
         this.attachmentInTree = attachmentInTree;
         this.isSubject = isSubject;
         initComponents();
         }

      private void initComponents()
         {
         headingPanel = new javax.swing.JPanel();
         subject = new javax.swing.JLabel();

         sender = new javax.swing.JLabel();
         iconLabel = new javax.swing.JLabel();

         timestampDate = new javax.swing.JLabel();
         timestampTime = new javax.swing.JLabel();

         this.setBackground(Color.WHITE);

         headingPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
         headingPanel.setAlignmentY(LEFT_ALIGNMENT);
         headingPanel.setBackground(new Color(238, 238, 238));

         if (isSubject)
            {
            headingPanel.setOpaque(false);
            }

         subject.setFont(FONT);
         subject.setForeground(Color.BLACK);
         subject.setOpaque(false);

         sender.setFont(BOLD_FONT);
         sender.setForeground(Color.ORANGE);
         sender.setOpaque(false);

         timestampDate.setFont(ITALIC_FONT);
         timestampDate.setForeground(new Color(102, 102, 102));
         timestampDate.setOpaque(false);

         timestampTime.setFont(ITALIC_FONT);
         timestampTime.setForeground(new Color(102, 102, 102));
         timestampTime.setOpaque(false);

         headingPanel.add(iconLabel);
         headingPanel.add(sender);
         headingPanel.add(Box.createRigidArea(new Dimension(4, 1)));
         headingPanel.add(subject);
         headingPanel.add(Box.createRigidArea(new Dimension(4, 1)));
         headingPanel.add(timestampDate);
         headingPanel.add(Box.createRigidArea(new Dimension(4, 1)));
         headingPanel.add(timestampTime);

         attachmentIconLabel = new JLabel(attachmentIcon);

         if (isSubject && attachmentInTree)
            {
            headingPanel.add(Box.createRigidArea(new Dimension(4, 1)));
            headingPanel.add(attachmentIconLabel);
            }

         this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
         this.add(headingPanel);
         }

      public void setMessage(RoboticonMessage message)
         {
         subject.setText(message.theMessage.subject);
         sender.setText(message.senderUserId);
         if (peerInfoModel != null)
            {
            sender.setForeground(
                  peerInfoModel.getUserColor(message.senderUserId));
            Icon userIcon = peerInfoModel.getUserIcon(message.senderUserId, 16, 16);
            if (userIcon != null)
               {
               iconLabel.setIcon(userIcon);
               }
            else
               {
               iconLabel.setVisible(false);
               }
            }
         timestampDate.setText(sdf.format(message.timestamp));

         if (timestampDate.getText().equals(sdf.format(new Date())))
            {
            timestampDate.setText("Today");
            }

         timestampTime.setText(sdt.format(message.timestamp));
         }

      private boolean isSubject;
      private boolean attachmentInTree;
      private javax.swing.JPanel headingPanel;
      private JLabel attachmentIconLabel;
      private javax.swing.JLabel subject;
      private javax.swing.JLabel sender;
      private javax.swing.JLabel iconLabel;
      private javax.swing.JLabel timestampDate;
      private javax.swing.JLabel timestampTime;
      }
   }
