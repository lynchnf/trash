
Create database and user.

    mysql --user=root --password=********
    create database trashdb;
    create user trashuser identified by 'swordfish';
    grant all on trashdb.* to trashuser;
    exit

Dump MySQL with the following command:

    mysqldump --user=trashuser --password=swordfish --skip-extended-insert --complete-insert --no-create-info --skip-add-locks trashdb > import.sql

Deploy

    cd ../trash
    mvn clean package
    sudo mv target/trash.war /var/lib/tomcat8//webapps/
