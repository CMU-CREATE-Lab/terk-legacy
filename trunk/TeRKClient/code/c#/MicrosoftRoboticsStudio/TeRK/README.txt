USAGE NOTES
-----------

** This document (briefly) describes the steps you need to get started with a Qwerk
   in Microsoft Robotics Studio (MSRS).

** You can either use the precompiled libraries and get started with VPL or your 
   own services right away, or you can compile the QwerkService yourself (allowing
   you to modify it to suit your needs).

** [MRS_HOME] refers to your installation directory of robotics studio, usually:

      C:\Users\MyUser\Micrsoft Robotics Dev Studio 2008\

** This document assumes the following:

   * You have already downloaded the TeRK source code from the TeRK web site.

   * You have installed Microsoft Robotics Developer Studio 2008 CTP.
	
      * Users of Microsoft Robotics Studio (1.5) will have to upgrade.

--------------------------------------------------------------------------------


COMPILING THE QwerkService
-----

** To compile the QwerkService you will need to have installed Ant, Ice-3.1.1, 
   Java, and Visual Studio and then followed all installation and setup 
   instructions (setting environment variables, etc..) to normally compile the 
   source code. The root directory of your TeRK source code is referred to below 
   as [TERK_HOME].

1. Open a Dss Command Prompt (Start -> Programs -> Microsoft Robotics Developer Studio...
   -> DSS Command Prompt).  From here, navigate to the
   [TERK_HOME]\TeRKClient\code\c#\MicrosoftRoboticsStudio\TeRK\ directory and run the
   command:

   DssProjectMigration -p "."

   * This will update the build targets and events to point to your MRDS installation
     instead of the installation used on our development machines.

2. Open a fresh, standard command prompt and navigate to the
   [TERK_HOME]\TeRKPeerCommon directory.

3. Run this from the command prompt:

   ant clean dist.c#

   * If you run into errors be sure you have .NET installed and the compiler 
     directory included in your PATH environment variable (as of this writing, 
     C:\Windows\Microsoft.NET\Framework\v2.0.50727).  Also, make sure you set your 
     ANT_HOME and ICE_HOME environment variables accoridingly and added them to your
     PATH variable.

4. Copy the following 5 .dll files to
   [TERK_HOME]\TeRKClient\code\c#\MicrosoftRoboticsStudio\TeRK\QwerkService:

     [TERK_HOME]\TeRKPeerCommon\dist\c#\MRPLPeer.dll
     [TERK_HOME]\TeRKPeerCommon\dist\c#\TeRKPeerCommon.dll
     [TERK_HOME]\TeRKPeerCommon\dist\c#\TeRKPeerCommonCommandLine.dll
     [ICE_HOME]\bin\glacier2cs.dll
     [ICE_HOME]\bin\icecs.dll

5. Open the QwerkService solution and edit the references to those libraries in 
   the QwerkService project to point to the fresh copies in the 
   [TERK_HOME]\TeRKClient\code\c#\MicrosoftRoboticsStudio\TeRK\QwerkService
   directory.

6. Compile the solution in Visual Studio.  The libraries should output directly 
   into your [MRS_HOME]\bin\ directory.

7. Open the VideoStreamViewer solution in Visual Studio and compile it.  These should
   also output directly into your [MRS_HOME]\bin\directory
  
--------------------------------------------------------------------------------


RUNNING THE QwerkService
-----

1. If you are using the pre-compiled libraries, copy the following files to the
   [MRS_HOME]\bin\ directory:

   [TERK_HOME]\TeRKClient\code\c#\MicrosoftRoboticsStudio\TeRK\PreCompiled\* (all files)
   [ICE_HOME]\bin\icecs.dll
   [ICE_HOME]\bin\glacier2cs.dll
  
   * If you compiled the libraries yourself, they should already be in the 
     [MRS_HOME]\bin\ directory, so skip to Step 2.

2. Copy the Ice properties files directly into the 
   [MRS_HOME]\ directory (the base of the installation):

   [TERK_HOME]\TeRKClient\code\c#\MicrosoftRoboticsStudio\TeRK\QwerkService\
      QwerkServiceDirect.ice.properties 
   [TERK_HOME]\TeRKClient\code\c#\MicrosoftRoboticsStudio\TeRK\QwerkService\
      QwerkServiceRelay.ice.properties 


3. Open up the sample VPL application:
   [TERK_HOME]\TeRKClient\code\c#\MicrosoftRoboticsStudio\TeRK\SampleApplications\
      Simple\
   in the Visual Programming Language software that came bundled with Robotics Studio.

4. Edit the 3 data fields to contain your unique Terk Login, Terk Password, and
   Qwerk Login (your website login and Qwerk ID from the Manage Your Robots page)

5. Turn on the Qwerk, make sure it is set on relay mode, and wait for it to connect to the relay

6. Run the application hit the Login button.  If successful the dialog should 
   close.  If not, you'll see an error message in the dialog text.  
   
   * If it says "Failed to register with the relay...", be sure you have an 
     internet connection and your Terk Login and Password are correct. If
     you still run into trouble, try pinging the relay server in a command
     prompt to make sure it's running.

   * If it says "Failed to connect to Qwerk ..., peer.PeerUnavailableException" 
     be sure you put in the correct Qwerk ID (should look like 123456) and that
     your Qwerk is powered on and connected to the relay server.

7. After you are logged in, manipulate the desktop joystick by dragging the axes
   in the circle with your mouse. The qwerk should react by spinning it's motor0 
   and motor1 ports according to your input.  Enjoy!