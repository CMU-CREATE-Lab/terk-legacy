PROTOTYPING PLAYGROUND QUICKSTART
---------------------------------

Purpose
-------

This document describes how to install, compile, run, and edit the
PrototypingPlayground program.

Installation
------------

* Create a directory for the PrototypingPlayground program and its associated
  files.

* Unzip the terk-client-prototyping-playground.zip file into the directory you
  just created.  Doing so will create a new directory called
  terk-client-prototyping-playground.

* Mac OS and Linux users will need to make the shell scripts executable.  To
  do so, open a command prompt and set the current directory to the
  terk-client-prototyping-playground directory created when you unzipped the
  zip file.  Now run the following command:

      chmod 755 *.sh

* Finally, you will need to have Sun's Java SE JDK 5.0 installed.  If it's not
  already installed, you can download it at:

      http://java.sun.com/javase/downloads/index.jsp

  Make sure you download the JDK, not the JRE.  Follow Sun's instructions for
  installing it on your machine.

Compiling the Prototyping Playground Program
--------------------------------------------

* Open a command prompt and set the current directory to the directory you
  created during installation.

* Type "compile" (Windows) or "./compile.sh" (Mac OS or Linux) at the command
  prompt to compile the program.  It will take a few seconds to compile, and
  will print out any syntax errors that may occur.

Running the Prototyping Playground Program
------------------------------------------

* If there are no compilation errors, type "run" (Windows) or "./run.sh" (Mac OS
  or Linux) to run the program.

* Once the program is running, click on the "Connect" button.  A connection
  wizard will appear to guide you through connecting to your robot.

* Once connected, click the "Finish" button.

* Once you are connected to a robot, you can press the various buttons to see
  how the robot behaves/responds.

Editing the Prototyping Playground Code
---------------------------------------

* When you open the PrototypingPlayground.java file in your editor/IDE, you'll
  see that each button calls an ActionListener inner class.  You can modify them
  or add your own to try out different things with your robot.

* Compile the program as described in the "Compiling the Prototyping Playground
  Program" section above.

* Run the program as described in the "Running the Prototyping Playground
  Program" section above.

================================================================================