@echo off
set JAVA_HOME=C:\Program Files\Java\jre6
set RHYTHMYX_HOME=C:\Rhythmyx
set ANT_HOME=%RHYTHMYX_HOME%\Patch\InstallToolkit

%ANT_HOME%\bin\ant -f run.xml