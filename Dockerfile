FROM tomcat:9-jre8
COPY ./target/image-search-webapp.war /usr/local/tomcat/webapps/image-search-webapp.war
