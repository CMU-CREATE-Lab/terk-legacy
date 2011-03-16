using System;

namespace TeRK.components
{
    public struct VideoFrame
    {
        private byte[] _imageData;
        private int _width;
        private int _height;

        public byte[] ImageData
        {
            get { return _imageData; }
            set
            {
                _imageData = new byte[value.Length];
                Array.Copy(value, _imageData, value.Length);
            }
        }

        public int Width
        {
            get { return _width; }
            set { _width = value; }
        }

        public int Height
        {
            get { return _height; }
            set { _height = value; }
        }
    }
}
