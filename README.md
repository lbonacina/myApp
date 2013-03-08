myApp
========================

What is it?
-----------

Skeleton for web applications, implements common features like :

    - authentication / authorization with Apache Shiro
    - User/Role management interface (WIP)
    - sample REST WS for users (TODO)
    - sample JS client that uses the REST WS (TODO)
    - simple template for JSF pages
    - validation with Hibernate Validator (also Messages & i18n)

Some extended feautures :

    - extend logs with MDC for logging info about logged user doing the operations
    - password management (encryption, deadline, history, reset) (some TODO)
    - superadmin role to be accessed by dev/1st level operators
    - tech log, access log, access both log from admin (or superadmin) role (TODO)
    - access to mail server (as demo) (TODO)
    - export Excel reports with JXLS (develop some sample interface structure to encapsulate reports generation) (TODO)
    - setup JPA Caching, also a way to analize caching stats (as superadmin) (WIP both for JPA/Hibernate & Shiro cache)
    - i18n (TODO)
    - history management with Hibernate Envers (TODO)
    - create custom JSF components like commandButton with built in check for rendered permissions
      (for example use "form:id" as a permission with Shiro, extend existing facelets tag lib) (TODO)
    - session listener to log session termination (TODO)
    - track users logged in (at least the users with an active session) (TODO)


ideas :

    - some kind of access log monitoring strategy that allows for log access (failed(successful login, logout and session expiration)
      to System.out, Log file, Database
    - evalutate the possibility of seperating user information like name, phone, email, from authentication/authorization informations like username, pwd history and so on

uses :

    - Primefaces & Omnifaces as JSF lib
    - Myfaces CODI as CDI extension for ViewAccessScope
    - Apache Shiro (integrated with JPA for Realms & Infinispan for permissions caching)
    - Spring Data JPA for repository (DAO) layer
    - querydsl as an alternative to JPA criteria queries (integrate with Spring Data JPA)
    - Stateless EJB for Service/Transaction layer
    - Arquillian + Drone + Selenium for Webapp test (WIP)

myApp will also be used as testbed to try out new technologies.


System requirements
-------------------

All you need to build this project is Java 6.0 (Java SDK 1.6) or better, Maven 3.0 or better.
The application this project produces is designed to be run on JBoss AS 7.1.


Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](../README.html/#mavenconfiguration) before testing the quickstarts.


Start JBoss Enterprise Application Platform 6 or JBoss AS 7.1
-------------------------

1. Open a command line and navigate to the root of the JBoss server directory.
2. The following shows the command line to start the server with the web profile:

        For Linux:   JBOSS_HOME/bin/standalone.sh
        For Windows: JBOSS_HOME\bin\standalone.bat


Build and Deploy the Quickstart
-------------------------

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Build and Deploy the Quickstarts](../README.html/#buildanddeploy) for complete instructions and additional options._

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type this command to build and deploy the archive:

        mvn clean package jboss-as:deploy

4. This will deploy `target/myApp.war` to the running instance of the server.


Access the application 
---------------------
 
The application will be running at the following URL: <http://localhost:8080/myApp/>.


Undeploy the Archive
--------------------

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. When you are finished testing, type this command to undeploy the archive:

        mvn jboss-as:undeploy


Run the Arquillian tests
----------------------------

This quickstart provides Arquillian tests. By default, these tests are configured to be skipped as Arquillian tests require the use of a container. 

_NOTE: The following commands assume you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Run the Arquillian Tests](../README.html/#arquilliantests) for complete instructions and additional options._

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type the following command to run the test goal with the following profile activated:

        mvn clean test -Parq-jbossas-remote 


Run the Quickstart in JBoss Developer Studio or Eclipse
-------------------------------------
You can also start the server and deploy the quickstarts from Eclipse using JBoss tools. For more information, see [Use JBoss Developer Studio or Eclipse to Run the Quickstarts](../README.html/#useeclipse) 


Debug the Application
------------------------------------

If you want to debug the source code or look at the Javadocs of any library in the project, run either of the following commands to pull them into your local repository. The IDE should then detect them.

        mvn dependency:sources
        mvn dependency:resolve -Dclassifier=javadoc

