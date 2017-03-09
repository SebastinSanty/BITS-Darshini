cp -rf conf/settings.xml /usr/share/maven/conf/settings.xml
echo "export CATALINA_BASE=/var/lib/tomcat7" >> /usr/share/tomcat7/bin/setenv.sh
cp -rf conf/tomcat-users.xml /var/lib/tomcat7/conf/tomcat-users.xml
