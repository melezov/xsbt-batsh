@echo off

echo Formatting code via scalariform ...
call "%~dp0\sbt.bat" --no-jrebel %* scalariform-format
