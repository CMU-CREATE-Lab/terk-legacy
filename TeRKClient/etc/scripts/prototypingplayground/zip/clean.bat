@echo OFF
IF EXIST prototyping-playground.jar del prototyping-playground.jar
IF EXIST .\bin\*.class del .\bin\*.class
IF EXIST .\bin rmdir .\bin