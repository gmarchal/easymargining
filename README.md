# easymargining

## 1 / Installation procedure

Unzip open/gamma-eurex/pme-18.3_eurex-repository.zip on your drive.

## 1.1 / Maven configuration : To add on settings.xml
## Add the profile below in profiles tag
	<profile>
      <id>eurex-pme-profile</id>
        <repositories>
          <repository>
            <id>eurex-pme</id>
            <url>file://[Path of pme-1.8.3_eurex-repository]</url>
          </repository>
        </repositories>
    </profile>
    <profile>
        <id>easymargining</id>
        <repositories>
            <repository>
                <id>easymargining</id>
                <url>file://[Path of easymargin-local-repository]</url>
            </repository>
        </repositories>
        <pluginRepositories>
            <pluginRepository>
                <id>easymargining-plugins</id>
                <url>file://[Path of easymargin-local-repository]</url>
            </pluginRepository>
        </pluginRepositories>
    	</profile>
## Add the activeProfile below in activeProfiles tag
    <activeProfile>eurex-pme-profile</activeProfile>

## 1.2 Lombok
https://projectlombok.org/

## Python for Client tests
https://www.continuum.io/downloads

## Git utils tools
https://desktop.github.com/


##MongoDB Install
- Download MongoDB
- tar xvzf mongodb-linux-x86_64-rhel62-3.2.1.tgz
- Installation description on : http://www.tokiwinter.com/mongodb-installation-basic-configuration/

# Windows CMD
c:\Homeware\easymargining>mongod --port 27017 --dbpath "c:\\Homeware\\MongoDB\\data\\db"
C:\Users\Gilles Marchal>mongo -port 27017

# MongDb Config
vi /home/esmuat01/conf/mongodb.conf
fork=true
auth=true
bind_ip = 0.0.0.0
port = 1234
quiet = true
dbpath = /home/esmuat01/lib/mongodb
logpath = /home/esmuat01/log/mongodb/mongod.log
logappend = true
journal = true

# Script MongoDb
./mongo --port 1234
use esmdb
db.createUser(
   {
     user: "esmdbusr",
     pwd: "esmdbpwd",
     roles: [ "readWrite", "dbAdmin" ]
   }
)

# Installation NodeJs & bower 
npm install -g bower
bower install

# Template 
bower install startbootstrap-sb-admin-2

#Passport installation
npm install passport
npm install passport-http-oauth


#Xeditable AngularJs Component
https://vitalets.github.io/angular-xeditable/#editable-row
# Angular Datatables
http://l-lin.github.io/angular-datatables/#/gettingStarted


#Template :

http://themeforest.net/item/material-design-admin-with-angularjs/full_screen_preview/13582227

http://themeforest.net/item/angulr-bootstrap-admin-web-app-with-angularjs/8437259?s_phrase=&s_rank=2