@echo OFF
IF EXIST prototyping-playground.jar del prototyping-playground.jar
IF EXIST .\bin\*.class del .\bin\*.class
IF EXIST .\bin rmdir .\bin
mkdir bin
javac -g -cp .;./bin;./terk.jar -sourcepath .;./bin -d ./bin PrototypingPlayground.java
jar cf prototyping-playground.jar -C ./bin .