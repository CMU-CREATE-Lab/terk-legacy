package edu.cmu.ri.mrpl.TeRK.roboticon.messenger;

import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessage;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface RoboticonMessengerListener
   {
   void messageAdded(final RoboticonMessage roboticonMessage);

   void contentsChanged();

   /**
    * Same as {@link #contentsChanged()}, but may specially render messages based on their relation to the given timestamp
    */
   void contentsChanged(final long timestamp);
   }
