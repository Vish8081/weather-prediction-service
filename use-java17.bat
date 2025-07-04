@echo off
REM Set Java 17 environment
set "JAVA_HOME=C:\Users\visverma7\AppData\Local\Programs\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo ✅ Using Java version:
java -version

echo.
echo 🚧 Building the application...
call .\mvnw clean package -DskipTests

echo.
echo 🚀 Running the application...
java -jar target\weather-prediction-service-0.0.1-SNAPSHOT.jar

pause
