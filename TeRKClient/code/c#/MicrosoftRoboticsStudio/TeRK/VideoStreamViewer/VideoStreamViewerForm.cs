using System;
using System.Drawing;
using System.Windows.Forms;
using Microsoft.Ccr.Core;

namespace Robotics.VideoStreamViewer
{
    internal partial class VideoStreamViewerForm : Form
    {
        Image currentFrame;
        VideoStreamViewerFormEventsPort _eventsPort;
       
        public VideoStreamViewerForm(VideoStreamViewerFormEventsPort eventsPort)
        {
            this._eventsPort = eventsPort;
            InitializeComponent();
            this.Closed += new System.EventHandler(VideoStreamViewerForm_Closed);
            this.Paint += new PaintEventHandler(VideoStreamViewerForm_Paint);
            this.DoubleBuffered = true;
        }

        public void drawFrame(Image frame)
        {
            currentFrame = frame;
            
            this.Invalidate();
            this.Update();
        }

        private void VideoStreamViewerForm_Paint(object sender, PaintEventArgs e)
        {
            if (currentFrame != null)
            {
                e.Graphics.DrawImage(currentFrame, new Rectangle(0, 0, this.Width, this.Height));
            }
        }

        private void VideoStreamViewerForm_Load(object sender, EventArgs e)
        {
            _eventsPort.Post(new OnLoad(this));
        }

        private void VideoStreamViewerForm_Closed(object sender, EventArgs e)
        {
            _eventsPort.Post(new OnClosed(this));
        }


    }

    class VideoStreamViewerFormEventsPort : PortSet<OnLoad, OnClosed>
    {
    }

    internal class VideoStreamViewerFormEvent
    {
        public VideoStreamViewerFormEvent(VideoStreamViewerForm form)
        {
            this._form = form;
        }

        private VideoStreamViewerForm _form;

        public VideoStreamViewerForm Form
        {
            get { return _form; }
            set { _form = value; }
        }
    }

    class OnLoad : VideoStreamViewerFormEvent
    {
        public OnLoad(VideoStreamViewerForm form)
            : base(form)
        {
        }
    }

    class OnClosed : VideoStreamViewerFormEvent
    {
        public OnClosed(VideoStreamViewerForm form)
            : base(form)
        {
        }
    }

}

