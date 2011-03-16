package edu.cmu.ri.mrpl.TeRK.client.diffdrive.udp;

import java.net.InetAddress;

public interface UDPHelper
   {
   public InetAddress[] getIPAddresses();

   public byte[] getUser(); //must be 64-bytes

   public byte[] getKey();  //must be 64-bytes
   }
