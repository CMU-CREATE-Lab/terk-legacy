@echo OFF
IF EXIST MyFirstRobot250.jar del MyFirstRobot250.jar
IF EXIST .\bin\*.class del .\bin\*.class
IF EXIST .\bin rmdir .\bin
mkdir bin
javac -g -cp .;./bin;./terk.jar -sourcepath .;./bin -d ./bin MyFirstRobot250.java
jar cf MyFirstRobot250.jar -C ./bin .