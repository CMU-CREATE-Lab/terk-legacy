#!/bin/sh

./clean.sh
mkdir bin
javac -g -cp .:./bin:./terk.jar -sourcepath .:./bin -d ./bin PrototypingPlayground.java
jar cf prototyping-playground.jar -C ./bin .

