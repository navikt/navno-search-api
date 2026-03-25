FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25
COPY build/libs/navno-search-api.jar /app.jar
ENV TZ="Europe/Oslo"
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]