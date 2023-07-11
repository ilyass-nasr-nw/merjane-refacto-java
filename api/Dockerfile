FROM eclipse-temurin:17-jre

ENV USER_NAME nimbleways
ENV APP_HOME /opt/$USER_NAME/app
ENV SPRING_PROFILES_ACTIVE=default

ENV JVM_HEAP_MIN="-Xms256m" \
    JVM_HEAP_MAX="-Xmx512m"

ENV JAVA_RUN_OPTS="-Dfile.encoding=UTF-8 \
-Djava.net.preferIPv4Stack=true \
-XX:+UseG1GC"

# Add a volume pointing to /tmp, used by Tomcat
VOLUME /tmp

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Create the user that will run the application
RUN adduser --shell /bin/bash --home /opt/$USER_NAME $USER_NAME
RUN mkdir -p $APP_HOME

# Add the application's jar to the container
ADD target/*.jar $APP_HOME/application.jar
RUN chown -R $USER_NAME: $APP_HOME

USER $USER_NAME
WORKDIR $APP_HOME

# Run the jar file
CMD java -Djava.security.egd=file:/dev/./urandom $JVM_HEAP_MIN $JVM_HEAP_MAX $JAVA_RUN_OPTS -jar application.jar