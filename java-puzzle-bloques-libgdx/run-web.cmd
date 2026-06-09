@echo off
setlocal
cd /d "%~dp0"

rem Gradle 8.10 no soporta Java 25+. Forzar JDK 17 (Temurin).
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot"
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo No se encontro JDK 17 en:
    echo   %JAVA_HOME%
    echo Instala Eclipse Temurin 17 o ajusta JAVA_HOME en run-web.cmd
    exit /b 1
)
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Usando Java:
"%JAVA_HOME%\bin\java.exe" -version
echo.
echo Compilando y sirviendo CodeQuest en http://localhost:8082 ...
call gradlew.bat teavm:gdx_teavm_web_js_run
endlocal
