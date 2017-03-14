sudo cp -rf conf/settings.xml /usr/share/maven/conf/settings.xml
sudo echo "export CATALINA_BASE=/var/lib/tomcat7" >> /usr/share/tomcat7/bin/setenv.sh
sudo cp -rf conf/tomcat-users.xml /var/lib/tomcat7/conf/tomcat-users.xml
