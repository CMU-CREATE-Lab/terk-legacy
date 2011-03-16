package edu.cmu.ri.mrpl.TeRK.client.diffdrive.aviutils;

import java.io.File;
import java.io.IOException;

public interface VideoRecorder
   {
   public boolean isStarted();

   public void startRecording();

   public void stopRecording();

   public void saveToFile(File f) throws IOException;
   }
