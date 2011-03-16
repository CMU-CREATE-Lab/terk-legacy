//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//     DSS Runtime Version: 2.0.730.3
//     CLR Runtime Version: 2.0.50727.1434
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.IO;
using Microsoft.Ccr.Adapters.WinForms;
using Microsoft.Ccr.Core;
using Microsoft.Dss.Core.Attributes;
using Microsoft.Dss.ServiceModel.Dssp;
using Microsoft.Dss.ServiceModel.DsspServiceBase;

namespace Robotics.VideoStreamViewer
{   
    /// <summary>
    /// Implementation class for VideoStreamViewer
    /// </summary>
    [DisplayName("VideoStreamViewer")]
    [Description("The VideoStreamViewer Service")]
    [Contract(Contract.Identifier)]
    public class VideoStreamViewerService : DsspServiceBase
    {
        VideoStreamViewerForm _form;
        VideoStreamViewerFormEventsPort _eventsPort = new VideoStreamViewerFormEventsPort();
        /// <summary>
        /// _state
        /// </summary>
        [ServiceState()]
        private VideoStreamViewerState _state = new VideoStreamViewerState();
        
        /// <summary>
        /// _main Port
        /// </summary>
        [ServicePort("/videostreamviewer", AllowMultipleInstances=false)]
        private VideoStreamViewerOperations _mainPort = new VideoStreamViewerOperations();
        
        /// <summary>
        /// Default Service Constructor
        /// </summary>
        public VideoStreamViewerService(DsspServiceCreationPort creationPort) : 
                base(creationPort)
        {
        }
        
        /// <summary>
        /// Service Start
        /// </summary>
        protected override void Start()
        {
			base.Start();

            MainPortInterleave.CombineWith(
                Arbiter.Interleave(
                    new TeardownReceiverGroup(),
                    new ExclusiveReceiverGroup
                    (
                        Arbiter.Receive<OnLoad>(true, _eventsPort, OnLoadHandler),
                        Arbiter.Receive<OnClosed>(true, _eventsPort, OnClosedHandler)
                    ),
                    new ConcurrentReceiverGroup()
                )
            );

            WinFormsServicePort.Post(
                new RunForm(
                    delegate()
                    {
                        return new VideoStreamViewerForm(_eventsPort);
                    }
                    )
            );            
			// Add service specific initialization here.
        }

        private void Invoke(System.Windows.Forms.MethodInvoker mi)
        {
            WinFormsServicePort.Post(new FormInvoke(mi));
        }

        private void drawFrameToForm(Image frame)
        {
            Invoke(delegate()
            {
                _form.drawFrame(frame);
                _form.Invalidate();
            });
        }

        void OnLoadHandler(OnLoad onLoad)
        {
            LogInfo("Video Viewer Loaded!");
            _form = onLoad.Form;
        }

        void OnClosedHandler(OnClosed onClosed)
        {
            _form = null;
        }

        [ServiceHandler(ServiceHandlerBehavior.Concurrent)]
        public virtual IEnumerator<ITask> GetHandler(Get get)
        {
            get.ResponsePort.Post(_state);
            yield break;
        }

        [ServiceHandler(ServiceHandlerBehavior.Concurrent)]
        public virtual IEnumerator<ITask> DrawFrameHandler(DrawFrame drawFrame)
        {
            if(drawFrame.Body.ImageData == null || drawFrame.Body.ImageData.Length == 0){
                LogInfo("Incoming drawframe image is null");
                drawFrame.ResponsePort.Post(DefaultUpdateResponseType.Instance);
                yield break;
            }
            Stream imageStream = new MemoryStream(drawFrame.Body.ImageData);
            this.drawFrameToForm(new Bitmap(imageStream));
            drawFrame.ResponsePort.Post(DefaultUpdateResponseType.Instance);
            yield break;
        }

        [ServiceHandler(ServiceHandlerBehavior.Exclusive)]
        public virtual IEnumerator<ITask> ReplaceHandler(Replace replace)
        {
            _state = replace.Body;
            replace.ResponsePort.Post(DefaultReplaceResponseType.Instance);
            yield break;
        }

        [ServiceHandler(ServiceHandlerBehavior.Teardown)]
        public void DropHandler(DsspDefaultDrop drop)
        {
            if (_form != null)
            {
                WinFormsServicePort.FormInvoke(
                    delegate()
                    {
                        if (!_form.IsDisposed)
                        {
                            _form.Dispose();
                        }
                    }
                );
            }

            base.DefaultDropHandler(drop);
        }

    }
}