package edu.cmu.ri.createlab.TeRK.client.visualprogrammer;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
enum GridDirection
   {
      NORTH("North", 0, 1),
      EAST("East", 1, 0),
      SOUTH("South", 0, -1),
      WEST("West", -1, 0);

   private final String direction;
   private final int deltaX;
   private final int deltaY;

   private GridDirection(final String direction, final int deltaX, final int deltaY)
      {
      this.direction = direction;
      this.deltaX = deltaX;
      this.deltaY = deltaY;
      }

   String getDirection()
      {
      return direction;
      }

   public int getDeltaX()
      {
      return deltaX;
      }

   public int getDeltaY()
      {
      return deltaY;
      }
   }
