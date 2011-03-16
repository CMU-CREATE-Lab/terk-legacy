import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import finch.Finch;

/**
 * @author Alex Styler (styler@cmu.edu)
 */
public class FinchDiagnosticTool
   {
   // Enum of posisble test results
   public enum Result
      {
         PASS, FAIL
      }

   // Enum for user console input
   public enum Input
      {
         YES, NO
      }

   final static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

   public static void main(final String[] args) throws IOException, InterruptedException
      {
      List<Test> tests = new ArrayList<Test>();
      tests.add(new MotorTest());
      tests.add(new EncoderTest());

      final Finch finch = new Finch();
      System.out.println("Starting Finch diagnostic tests...");
      System.out.println("Set the finch upright and press ENTER to begin");

      while (!in.ready())
         {
         Thread.sleep(100);
         }

      for (Test test : tests)
         {
         System.out.println(test.getName() + ":");
         System.out.println(test.getDescription());
         test.runTest(finch);
         System.out.println(test.getResult());
         System.out.println();
         }

      System.out.println("Finch Diagnostic Results:");
      for (Test test : tests)
         {
         System.out.println(test.getName() + ": " + test.getResult());
         }

      finch.quit();
      }

   private static Input getUserInput(String prompt) throws IOException, InterruptedException
      {
      char input = 0;
      String line;

      if (in.ready())

         {
         in.readLine();
         }

      System.out.println(prompt);

      while (input != 'Y' && input != 'N' && input != 'y' && input != 'n')
         {
         System.out.print("(y/n): ");
         line = in.readLine();
         if (line != null && line.length() > 0)
            {
            input = line.charAt(0);
            }
         }

      if (input == 'N' || input == 'n')
         {
         return Input.NO;
         }
      else
         {
         return Input.YES;
         }
      }

   private static abstract class Test
      {
      protected String name = "Test";
      protected String description = "Placeholder test";
      protected Result result = null;

      public abstract Result runTest(Finch finch) throws InterruptedException, IOException;

      public String getName()
         {
         return name;
         }

      public String getDescription()
         {
         return description;
         }

      public Result getResult()
         {
         return result;
         }
      }

   private static class MotorTest extends Test
      {
      public MotorTest()
         {
         name = "Motor Test";
         description = "Runs motors and has user verify status.";
         }

      public Result runTest(Finch finch) throws InterruptedException, IOException
         {
         result = Result.PASS;
         finch.setWheelVelocities(10.0, 10.0);
         Thread.sleep(1000);
         if (getUserInput("Are the wheels spinning?") == Input.NO)
            {
            result = Result.FAIL;
            }
         finch.setWheelVelocities(0.0, 0.0);
         Thread.sleep(1000);
         return result;
         }
      }

   private static class EncoderTest extends Test
      {
      public EncoderTest()
         {
         name = "Encoder Test";
         description = "Runs motors forward and backward and checks encoder values.";
         }

      public Result runTest(Finch finch) throws InterruptedException, IOException
         {
         result = Result.PASS;
         double initialLeft = finch.getLeftWheelDistance();
         double initialRight = finch.getRightWheelDistance();
         finch.setWheelVelocities(10.0, 10.0);
         Thread.sleep(1000);
         if (finch.getLeftWheelDistance() <= initialLeft)
            {
            result = Result.FAIL;
            }
         if (finch.getRightWheelDistance() <= initialRight)
            {
            result = Result.FAIL;
            }
         finch.setWheelVelocities(0.0, 0.0);
         Thread.sleep(1000);

         initialLeft = finch.getLeftWheelDistance();
         initialRight = finch.getRightWheelDistance();

         finch.setWheelVelocities(-10.0, -10.0);
         Thread.sleep(1000);
         if (finch.getLeftWheelDistance() >= initialLeft)
            {
            result = Result.FAIL;
            }
         if (finch.getRightWheelDistance() >= initialRight)
            {
            result = Result.FAIL;
            }

         finch.setWheelVelocities(0.0, 0.0);
         Thread.sleep(1000);
         return result;
         }
      }
   }




