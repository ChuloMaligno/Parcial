FROM openjdk:17
COPY "./target/Parcial_empresariales-2.0.jar" "app.jar"
EXPOSE 6565
ENTRYPOINT [ "java", "-jar", "app.jar" ]