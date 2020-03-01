# Production

This application has 2 spring profiles: default (used for development) and "prod" (used for production). In development
mode, it uses an [H2](https://www.h2database.com/html/main.html) in-memory database. In production mode, it uses a
[MySQL](https://www.mysql.com/) database. Compiling to a war file and deploying to Tomcat automatically uses the "prod"
profile.

## First Time Production Setup

Install Java, Tomcat, and MySQL.

Create empty database and user.

    mysql --user=root --password=********
    create database trashdb;
    create user trashuser identified by 'swordfish';
    grant all on trashdb.* to trashuser;
    exit

Create empty tables.

    cd ..,/trash
    mvn clean package
    java -jar -Dspring.profiles.active=prod -Dspring.jpa.hibernate.ddl-auto=create target/trash.war
    If error, ctrl-c to shutdown and run again.
    Browse to http://localhost:8080/ to test.
    ctrl-c to shutdown

Deploy to Tomcat

    cd ..,/trash
    mvn clean package
    sudo mv target/trash.war /var/lib/tomcat8/webapps/

## Second Time Production Upgrade

Delete old version and deploy new version.

    sudo service tomcat8 stop
    sudo rm /var/lib/tomcat8/webapps/trash.war
    sudo rm -rf /var/lib/tomcat8/webapps/trash/
    sudo service tomcat8 start
    mvn clean package
    sudo mv target/trash.war /var/lib/tomcat8/webapps/

Dump MySQL with the following command:

    mysqldump --user=trashuser --password=swordfish --skip-extended-insert --complete-insert --no-create-info --skip-add-locks trashdb > import.sql

Delete Database

    mysql --user=trashuser --password=swordfish
    drop database trashdb;
    create database trashdb;
    exit
