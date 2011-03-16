package edu.cmu.ri.createlab.TeRK.serial;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface SerialPort
   {
   /** Opens the serial port using the given {@link SerialIOConfiguration}. */
   void open(final SerialIOConfiguration config) throws SerialIOException;

   /** Returns <code>true</code> if the serial port is currently open, <code>false</code> otherwise. */
   boolean isOpen();

   /** Returns <code>true</code> if data is available for reading from the serial port; <code>false</code> otherwise. */
   boolean isDataAvailable() throws SerialIOException;

   /**
    * Reads from the serial port and returns the data read in a byte array.  Stops reading when either of the following
    * conditions is satisfied:
    * <ul>
    *    <li>There is no more data available for reading</li>
    *    <li>The maximum number of bytes has been read (specified by the <code>maxNumberOfBytesToRead</code> parameter)</li>
    * </ul>
    * This method does nothing and simply returns an empty arrary if <code>maxNumberOfBytesToRead</code> is less than 1.
    */
   byte[] read(final int maxNumberOfBytesToRead) throws SerialIOException;

   /**
    *  Reads from the serial port and returns the data read in a byte array.  Stops reading when either of the
    * following conditions is satisfied:
    * <ul>
    *    <li>The maximum number of bytes has been read (specified by the <code>maxNumberOfBytesToRead</code> parameter)</li>
    *    <li>The command times out because the specified maximum number of bytes is not read within the number of
    *        milliseconds specified by <code>timeoutMilliseconds</code></li>
    * </ul>
    * This method does nothing and simply returns an empty array if <code>maxNumberOfBytesToRead</code> is less than 1.
    * Setting <code>timeoutMilliseconds</code> to a value less than 1 causes the method to wait indefinitely and only
    * returns when one of the other conditions becomes true.
    */
   byte[] read(final int maxNumberOfBytesToRead, final int timeoutMilliseconds) throws SerialIOException;

   /**
    * Reads from the serial port and returns the data read in a byte array.  Stops reading when any of the following
    * conditions is satisfied:
    * <ul>
    *    <li>The delimiter character is read</li>
    *    <li>There is no more data available for reading</li>
    *    <li>The maximum number of bytes has been read (specified by the <code>maxNumberOfBytesToRead</code> parameter)</li>
    * </ul>
    * This method does nothing and simply returns an empty arrary if <code>maxNumberOfBytesToRead</code> is less than 1.
    */
   byte[] read(final int maxNumberOfBytesToRead, final ASCIICharacter delimiterCharacter) throws SerialIOException;

   /**
    * Reads from the serial port and returns the data read in a byte array.  Stops reading when any of the following
    * conditions is satisfied:
    * <ul>
    *    <li>The delimiter character is read</li>
    *    <li>The maximum number of bytes has been read (specified by the <code>maxNumberOfBytesToRead</code> parameter)</li>
    *    <li>The command times out because neither the specified delimiter character nor the specified number of bytes
    *        is read within the number of milliseconds specified by <code>timeoutMilliseconds</code></li>
    * </ul>
    * This method does nothing and simply returns an empty arrary if <code>maxNumberOfBytesToRead</code> is less than 1.
    * Setting <code>timeoutMilliseconds</code> to a value less than 1 causes the method to wait indefinitely and only
    * returns when one of the other conditions becomes true.
    */
   byte[] read(final int maxNumberOfBytesToRead, final ASCIICharacter delimiterCharacter, final int timeoutMilliseconds) throws SerialIOException;

   /** Writes the given string to the serial port. */
   void write(final String data) throws SerialIOException;

   /** Writes the given byte array to the serial port. */
   void write(final byte[] data) throws SerialIOException;

   /** Closes the serial port. */
   void close();
   }
