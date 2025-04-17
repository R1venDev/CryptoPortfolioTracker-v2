#FROM adoptopenjdk/openjdk11:latest
FROM gradle:7.1.0-jdk11
RUN mkdir /opt/app
COPY . /opt/app/
WORKDIR /opt/app/
CMD ["gradle", "--info", "run"]
#CMD ["java", "-cp ./:lib/*.jar", "com.cryptoportfolio.App"]