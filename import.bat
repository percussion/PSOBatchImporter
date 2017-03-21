@echo off
set RHYTHMYX_HOME=C:\Rhythmyx
set ANT_HOME=%RHYTHMYX_HOME%\Patch\InstallToolkit
set JAVA_HOME=%RHYTHMYX_HOME%\JRE

%ANT_HOME%\bin\ant -f run.xml