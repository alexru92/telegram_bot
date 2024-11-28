FROM openjdk:17-jdk-slim
VOLUME /tmp
LABEL authors="alex-ey"
COPY target/ApartmentsSearchWithTelegramReporting-0.0.1-SNAPSHOT.jar app.jar
COPY apartments.txt apartments.txt
COPY chatIds.txt chatIds.txt
CMD ["java", "-Xmx512m", "-Xms512m", "-XX:MaxRAMPercentage=75", "-XX:+ExitOnOutOfMemoryError", "-jar", "app.jar"]