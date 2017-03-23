PSOBatchImporter
=

Downloads
===

[Download the latest stable snapshot:](http://cdn.percussion.com/downloads/open/psobatchimporter/pso-batch-importer-SNAPSHOT.zip)

This version is compatible with Rhythmyx 7.3.0, 7.3.1, and 7.3.2.  Prior to running the importer replace the lib/rxwebservices.jar in the Batch Importer directory with the copy from <Rhythmyx Install>/AppServer/server/rx/deploy/rxapp.ear/rxapp.war/WEB-INF/lib/rxwebservices.jar so that the Web Services client matches your Rhythmyx version. 

To configure the batch importer:
===
* All configuration except for logging is done through a spring config file, located "config/spring.xml".

* The user can be configured by adding an attribute in run.xml

* Logging is in "config/log4j.xml"

To run the batch importer:
====
* Copy the files from this project onto any server that will be able to access the Rhythmyx server (locally or over the network)
* Configure the spring.xml file appropriately
* Ensure that the server has a JRE of version 6 or higher

* Modify the import.bat file so that the JAVA_HOME and RHYTHMYX_HOME variables point to the appropriate locations on your server 

* If not running on a Rhythmyx server you need to install ant at least 1.7 http://ant.apache.org/  and set
the ANT_HOME appropriately

* Run import.bat (any log will appear in the log directory and is rolling based upon date as set in log4j.xml)