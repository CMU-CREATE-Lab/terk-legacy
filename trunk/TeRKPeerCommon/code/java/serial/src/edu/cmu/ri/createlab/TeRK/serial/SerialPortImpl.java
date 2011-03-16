package edu.cmu.ri.createlab.TeRK.serial;

import edu.cmu.ri.mrpl.TeRK.SerialIOBaudRate;
import edu.cmu.ri.mrpl.TeRK.SerialIOCharacterSize;
import edu.cmu.ri.mrpl.TeRK.SerialIOConfig;
import edu.cmu.ri.mrpl.TeRK.SerialIOFlowControl;
import edu.cmu.ri.mrpl.TeRK.SerialIOParity;
import edu.cmu.ri.mrpl.TeRK.SerialIOServicePrx;
import edu.cmu.ri.mrpl.TeRK.SerialIOStopBits;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */

final class SerialPortImpl implements SerialPort
   {
   private final String portName;
   private final SerialIOServicePrx proxy;

   SerialPortImpl(final String portName, final SerialIOServicePrx proxy)
      {
      this.portName = portName;
      this.proxy = proxy;
      }

   public void open(final SerialIOConfiguration config) throws SerialIOException
      {
      try
         {
         proxy.open(createSerialIOConfig(config));
         }
      catch (edu.cmu.ri.mrpl.TeRK.SerialIOException e)
         {
         throw new SerialIOException(e);
         }
      }

   public boolean isOpen()
      {
      return proxy.isOpen(portName);
      }

   public boolean isDataAvailable() throws SerialIOException
      {
      try
         {
         return proxy.isDataAvailable(portName);
         }
      catch (edu.cmu.ri.mrpl.TeRK.SerialIOException e)
         {
         throw new SerialIOException(e);
         }
      }

   public byte[] read(final int maxNumberOfBytesToRead) throws SerialIOException
      {
      try
         {
         return proxy.read(portName, maxNumberOfBytesToRead);
         }
      catch (edu.cmu.ri.mrpl.TeRK.SerialIOException e)
         {
         throw new SerialIOException(e);
         }
      }

   public byte[] read(final int maxNumberOfBytesToRead, final int timeoutMilliseconds) throws SerialIOException
      {
      try
         {
         return proxy.blockingRead(portName, maxNumberOfBytesToRead, timeoutMilliseconds);
         }
      catch (edu.cmu.ri.mrpl.TeRK.SerialIOException e)
         {
         throw new SerialIOException(e);
         }
      }

   public byte[] read(final int maxNumberOfBytesToRead, final ASCIICharacter delimiterCharacter) throws SerialIOException
      {
      try
         {
         return proxy.readUntilDelimiter(portName, maxNumberOfBytesToRead, delimiterCharacter.getCode());
         }
      catch (edu.cmu.ri.mrpl.TeRK.SerialIOException e)
         {
         throw new SerialIOException(e);
         }
      }

   public byte[] read(final int maxNumberOfBytesToRead, final ASCIICharacter delimiterCharacter, final int timeoutMilliseconds) throws SerialIOException
      {
      try
         {
         return proxy.blockingReadUntilDelimiter(portName, maxNumberOfBytesToRead, delimiterCharacter.getCode(), timeoutMilliseconds);
         }
      catch (edu.cmu.ri.mrpl.TeRK.SerialIOException e)
         {
         throw new SerialIOException(e);
         }
      }

   public void write(final String data) throws SerialIOException
      {
      write(data.getBytes());
      }

   public void write(final byte[] data) throws SerialIOException
      {
      try
         {
         proxy.write(portName, data);
         }
      catch (edu.cmu.ri.mrpl.TeRK.SerialIOException e)
         {
         throw new SerialIOException(e);
         }
      }

   public void close()
      {
      proxy.close(portName);
      }

   private SerialIOConfig createSerialIOConfig(final SerialIOConfiguration config)
      {
      return new SerialIOConfig(config.getPortDeviceName(),
                                convertBaudRate(config.getBaudRate()),
                                convertCharacterSize(config.getCharacterSize()),
                                convertParity(config.getParity()),
                                convertStopBits(config.getStopBits()),
                                convertFlowControl(config.getFlowControl()));
      }

   private SerialIOBaudRate convertBaudRate(final BaudRate baudRate)
      {
      switch (baudRate)
         {
         case BAUD_50:
            return SerialIOBaudRate.BAUD50;
         case BAUD_75:
            return SerialIOBaudRate.BAUD75;
         case BAUD_110:
            return SerialIOBaudRate.BAUD110;
         case BAUD_150:
            return SerialIOBaudRate.BAUD150;
         case BAUD_200:
            return SerialIOBaudRate.BAUD200;
         case BAUD_300:
            return SerialIOBaudRate.BAUD300;
         case BAUD_600:
            return SerialIOBaudRate.BAUD600;
         case BAUD_1200:
            return SerialIOBaudRate.BAUD1200;
         case BAUD_1800:
            return SerialIOBaudRate.BAUD1800;
         case BAUD_2400:
            return SerialIOBaudRate.BAUD2400;
         case BAUD_4800:
            return SerialIOBaudRate.BAUD4800;
         case BAUD_9600:
            return SerialIOBaudRate.BAUD9600;
         case BAUD_19200:
            return SerialIOBaudRate.BAUD19200;
         case BAUD_38400:
            return SerialIOBaudRate.BAUD38400;
         case BAUD_57600:
            return SerialIOBaudRate.BAUD57600;
         case BAUD_115200:
            return SerialIOBaudRate.BAUD115200;
         default:
            throw new IllegalArgumentException("Unexpected BaudRate [" + baudRate + "]");
         }
      }

   private SerialIOCharacterSize convertCharacterSize(final CharacterSize characterSize)
      {
      switch (characterSize)
         {
         case FIVE:
            return SerialIOCharacterSize.CHARSIZE5;
         case SIX:
            return SerialIOCharacterSize.CHARSIZE6;
         case SEVEN:
            return SerialIOCharacterSize.CHARSIZE7;
         case EIGHT:
            return SerialIOCharacterSize.CHARSIZE8;
         default:
            throw new IllegalArgumentException("Unexpected CharacterSize [" + characterSize + "]");
         }
      }

   private SerialIOParity convertParity(final Parity parity)
      {
      switch (parity)
         {
         case NONE:
            return SerialIOParity.PARITYNONE;
         case EVEN:
            return SerialIOParity.PARITYEVEN;
         case ODD:
            return SerialIOParity.PARITYODD;

         default:
            throw new IllegalArgumentException("Unexpected Parity [" + parity + "]");
         }
      }

   private SerialIOStopBits convertStopBits(final StopBits stopBits)
      {
      switch (stopBits)
         {
         case ONE:
            return SerialIOStopBits.STOPBITS1;
         case TWO:
            return SerialIOStopBits.STOPBITS2;
         default:
            throw new IllegalArgumentException("Unexpected StopBits [" + stopBits + "]");
         }
      }

   private SerialIOFlowControl convertFlowControl(final FlowControl flowControl)
      {
      switch (flowControl)
         {
         case NONE:
            return SerialIOFlowControl.FLOWCONTROLNONE;
         case HARDWARE:
            return SerialIOFlowControl.FLOWCONTROLHARDWARE;
         case SOFTWARE:
            return SerialIOFlowControl.FLOWCONTROLSOFTWARE;

         default:
            throw new IllegalArgumentException("Unexpected FlowControl [" + flowControl + "]");
         }
      }
   }
