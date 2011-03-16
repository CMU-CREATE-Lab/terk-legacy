#!/bin/sh

if [ -f prototyping-playground.jar ] 
then
   rm prototyping-playground.jar
fi

if [ -d ./bin ]
then
   rm -rf ./bin/
fi

exit 0