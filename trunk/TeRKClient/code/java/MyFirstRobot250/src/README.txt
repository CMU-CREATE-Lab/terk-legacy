ROBOT CLIENT QUICKSTART
----------------------

Purpose
-------

This document describes how to install, compile, and run the MyFirstRobot250 program.
It also briefly describes important other files in this folder.

Installation
------------

* Create a directory for the MyFirstRobot250 program and its associated files.

* Unzip the terk-client-MyFirstRobot250.zip file into the directory you just
  created.

* You will need to have Sun's Java SE JDK 5.0 installed.  If it's not already
  installed, you can download it at:

      http://java.sun.com/javase/downloads/index.jsp

  Follow their instructions for installing it on your machine.

Compiling and Running the MyFirstRobot250 Program in JCreator (RECOMMENDED METHOD)
----------------------------------------------------------------------------------
JCreator is a freeware Java IDE.  A project file, MyFirstRobot250.jcp, is available
in the MyFirstRobot250 folder with all of the correct configuration settings to
quickly begin compiling and running the MyFirstRobot250 program.

* Install JCreator LE from http://www.jcreator.com/download.htm

* Open the file MyFirstRobot250.jcp.  The JCreator IDE should start.

* In the File View of the IDE, open MyFirstRobot250.java for editing by double clicking on it.

* Compile the project by either hitting F7 or going to Build->'Compile Project' in the top menu

* Run the project by going to Build->'Execute File' in the top menu.  Note that you must execute
  the file, NOT the project.

* Running the program will open a graphical interface.  To connect to a robot,
  click on the "Connect" button.  A connection dialog will pop up and you will have
  to select whether you are connecting to the robot via the CMU-based relay (relay mode)
  or if you will connect over a local network by entering the robot's IP address (direct connect).
  For more information about these two modes of operating your qwerk, visit
  http://www.terk.ri.cmu.edu/projects/qwerk-overview.php.  Make your selection and click 'Next':
    - If you entered direct connect mode, you will now be prompted to enter the IP address of the robot.
      Do so and click 'Connect', then click 'Finish'.  You should now be connected to the robot.
    - If you entered relay mode, you will be prompted to enter your TeRK login and password.
      Enter your login, click the 'Login' button, and then click 'Next' to go to the next page in the
      connection wizard.  You will see a list of available robots.  Highlight the appropriate robot
      by clicking on it once.  Then click the 'Connect' and the 'Finish' buttons.  You should now
      be connected to the robot.

* Once you are connected to a robot, you can begin displaying an image stream from the robot's onboard
  camera by clicking on "Start Video".  Once started, you can pause video at any time by hitting
  "Pause Video", and capture images from the video stream by clicking "Save Picture".  To run your
  program, hit the 'Play' button.  To stop your program, hit 'Stop'.

* Several other example files are available and visible in the file view.  They are Headturner.java,
  Photovore.java, hearingTest.java, and soundTest.java.  You can compile and run these as you did
  for MyFirstRobot250.java.


Compiling and Running the MyFirstRobot250 Program from Command Line
-------------------------------------------------------------------

* Open a command prompt and set the current directory to the directory you
  created during installation.

* Type "compile" (no quotes) at the command prompt to compile the program.  It
  will take a few seconds to compile, and will print out any syntax errors that
  may occur.

* If there are no compilation errors, type "run" (no quotes) to run the program.

* Running the program will open a graphical interface.  To connect to a robot,
  click on the "Connect" button.  A connection dialog will pop up and you will have
  to select whether you are connecting to the robot via the CMU-based relay (relay mode)
  or if you will connect over a local network by entering the robot's IP address (direct connect).
  For more information about these two modes of operating your qwerk, visit
  http://www.terk.ri.cmu.edu/projects/qwerk-overview.php.  Make your selection and click 'Next':
    - If you entered direct connect mode, you will now be prompted to enter the IP address of the robot.
      Do so and click 'Connect', then click 'Finish'.  You should now be connected to the robot.
    - If you entered relay mode, you will be prompted to enter your TeRK login and password.
      Enter your login, click the 'Login' button, and then click 'Next' to go to the next page in the
      connection wizard.  You will see a list of available robots.  Highlight the appropriate robot
      by clicking on it once.  Then click the 'Connect' and the 'Finish' buttons.  You should now
      be connected to the robot.

* Once you are connected to a robot, you can begin displaying an image stream from the robot's onboard
  camera by clicking on "Start Video".  Once started, you can pause video at any time by hitting
  "Pause Video", and capture images from the video stream by clicking "Save Picture".  To run your
  program, hit the 'Play' button.  To stop your program, hit 'Stop'.


Important Locations in this Folder
----------------------------------

API Reference.pdf:  This file contains descriptions and sample usages of all methods used for
controlling the robot, accessing robot sensor data, and using the GUI.  Read this document
before writing any programs for controlling the robot!!

MyFirstRobot250.java:  Skeleton starter file.

Examples:  Example programs which demonstrate some of the available robot control methods.
All examples are commented and contain a header with a description of the program's operation.

MyFirstRobot250.jcp:  Jcreator project file.  Double click to open JCreator and the MyFirstRobot250 project.

MyFirstRobot250.jcw and .jcu:  Other Jcreator files.

compile.bat:  Batch file for compiling MyFirstRobot250.java from the command line
run.bat:      Batch file for running MyFirstRobot250.java from the command line
clean.bat:    Batch file for removing any files generated by using compile or run.bat

RobotClient Folder:  Folder containing classes for accessing and controlling the robot and GUI
- RobotClient.java:  Contains all methods for controlling and accessing the robot, as well as the GUI
- RobotClientGUI.java:  Specifies the properties of the GUI.
- SimpleRobotClient.java:  Wrapper class for RobotClient.java - can be used interchangeably with RobotClient.java

RSSReaders:  Folder containing classes for reading RSS feeds from the internet:
- RSSReader.java:  Generic class for reading feeds, check file for available methods
- WeatherReader.java:  Class for reading weather data for any US city, courtesy of www.wunderground.com feeds

TTS:  Folder containing class for generatic speech from text
- TTS.java:  Generic class for generating text to speech, check file for available methods

sounds Folder:  Stores sounds that can be played by the Qwerk's speakers.  A good place to generate such
files is to visit http://www.research.att.com/~ttsweb/tts/demo.php

================================================================================



