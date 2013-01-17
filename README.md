# Batch Upload Client

Sample code to batch upload experiment files to Ingenuity for generating variant analyses.

bash/curl scripts are under src/main/resources/scripts

Sample java program is under scr/main/java/sample/Main

Note: 

  * You will need to update the scripts with your oauth access_token, client_id and client_secret.  
  * Copy sample zip files to src/main/resources/scripts and src/main/resources/isatab

https://developer.ingenuity.com/datastream/developers/myapps.html

To build and execute:

    mvn -Dexec.mainClass=sample.Main compile exec:java

To generate library dependencies (dependency:copy) and execute

    mvn package    
    cd target
    java -jar batch-client.jar