package edu.cmu.ri.mrpl.TeRK.client.dance.bufferio;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import edu.cmu.ri.createlab.TeRK.motor.BackEMFMotorBuffer;

public class MotorBufferIO
   {
   public static class MotorBufferData
      {
      private final BackEMFMotorBuffer[] _buffers;
      private final boolean[] _mask;

      public MotorBufferData(final boolean[] mask, final BackEMFMotorBuffer[] buffers)
         {
         _mask = mask == null ? new boolean[0] : mask.clone();
         _buffers = buffers == null ? new BackEMFMotorBuffer[0] : buffers.clone();
         }

      public boolean[] getMask()
         {
         return _mask.clone();
         }

      public BackEMFMotorBuffer[] getBuffers()
         {
         return _buffers.clone();
         }
      }

   private static final byte[] SIGNATURE = {(byte)0xDA, (byte)'N', (byte)0xCE, (byte)'\r', (byte)'\n', (byte)'r', (byte)'o', (byte)'b', (byte)'o', (byte)'\n', (byte)'D', (byte)'A', (byte)'N', (byte)'C', (byte)'E', (byte)'\r'};

   public static void write(final DataOutput out, final MotorBufferData data) throws IOException
      {
      //acquire info
      final boolean[] mask = data.getMask();
      final BackEMFMotorBuffer[] buffers = data.getBuffers();

      //write signature
      out.write(SIGNATURE);

      //write buffer data
      out.writeInt(mask.length);//write mask
      for (int i = 0; i < mask.length; i += 8)
         {
         int bitmask = 0;
         for (int j = 0; j < 8; j++)
            {
            if ((i + j < mask.length) && (mask[i + j]))
               {
               bitmask |= (1 << j);
               }
            }
         out.writeByte(bitmask);
         }

      for (int i = 0; i < mask.length; i++)
         {//write motor buffer data
         if (mask[i])
            {
            out.writeInt(buffers[i].size());
            for (final int j : buffers[i].getValues())
               {
               out.writeInt(j);
               }
            }
         else
            {
            out.writeInt(0);
            }
         }
      }

   public static MotorBufferData read(final DataInput in) throws IOException
      {
      //verify signature
      final byte[] sig = new byte[SIGNATURE.length];
      in.readFully(sig);
      for (int i = 0; i < sig.length; i++)
         {
         if (sig[i] != SIGNATURE[i])
            {
            throw new IOException("Unrecognized Format");
            }
         }

      //read buffer data
      final int length = in.readInt();
      final boolean[] mask = new boolean[length];//read mask
      for (int i = 0; i < mask.length; i += 8)
         {
         final int bitmask = in.readByte();
         for (int j = 0; j < 8; j++)
            {
            if ((i + j) < mask.length)
               {
               mask[i + j] = ((bitmask & (1 << j)) != 0);
               }
            }
         }

      final BackEMFMotorBuffer[] buffers = new BackEMFMotorBuffer[length];
      for (int i = 0; i < buffers.length; i++)
         {
         final int[] nextbuffer = new int[in.readInt()];
         for (int j = 0; j < nextbuffer.length; j++)
            {
            nextbuffer[j] = in.readInt();
            }
         buffers[i] = new BackEMFMotorBuffer(nextbuffer);
         }

      return new MotorBufferData(mask, buffers);
      }

   private MotorBufferIO()
      {
      // private to prevent instantiation
      }
   }
