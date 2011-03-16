INTRODUCTION
------------

This module makes creating TeRK builds easy.  It can build any subset of the modules listed below in the "ABOUT"
section (assuming dependencies are satisfied).

========================================================================================================================

ABOUT
-----

Here's a brief description of what each module contains:

   TeRKAgents - TeRK agent implementations

   TeRKBuild - This module, used for easily building all the other modules.

   TeRKClient - TeRK client implementations (command line, diff-drive GUI, etc.)

   TeRKPeerCommon - Code shared by all TeRK users (e.g. Slice definitions, service and relay communication classes, etc)

   TeRKRobot - Simple robot simulators

========================================================================================================================

REQUIRED SETUP INSTRUCTIONS
---------------------------

* Make sure there are no spaces in the path to the root of your development directory!!!  Spaces cause weird,
  non-intuitive errors when building the Ice stuff.

* Install Sun JDK 1.6.x.  This stuff won't run on JDK 1.5.x or earlier.  Make sure the JAVA_HOME environment variable is
  defined.

* Install Ice (see the IceInstallation.txt file), making sure you download and install the EXACT version of Ice the
  instructions tell you to use.  Also make sure the ICE_HOME environment variable is set and that Ice's bin directory is
  on your path.  For Linux, make sure you also set the LD_LIBRARY_HOME environment variable.

  You don't need to install Ice-E unless you want to be able to run the Ice-E Slice translator.  The only people who
  will typically have a need for this are developers who need to edit Slice code that will also be used to generate code
  for Qwerks.  If you ever edit such code, you should make sure that your changes are compatible with Ice-E.  See the
  section at the end of the IceInstallation.txt document for instructions on installing Ice-E.

* Install Ant 1.7.0 or later (http://ant.apache.org/manual/install.html).  Make sure the ANT_HOME environment variable
  is set. Also make sure that Ant's bin directory is in your path.

* If necessary, copy the JUnit jar from the TeRKBuild module (you should find it under /TeRKBuild/lib/junit/) to the lib
  directory of your Ant installation (ANT_HOME/lib). This just enables Ant to execute our unit tests during the build.

========================================================================================================================