package edu.cmu.ri.createlab.TeRK.audio;

import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManagerWrapper;

public abstract class BaseAudioServiceImpl extends PropertyManagerWrapper implements AudioService
   {
   public BaseAudioServiceImpl(final PropertyManager propertyManager)
      {
      super(propertyManager);
      }

   public final String getTypeId()
      {
      return TYPE_ID;
      }
   }