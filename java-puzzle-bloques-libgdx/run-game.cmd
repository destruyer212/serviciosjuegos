@echo off
cd /d "%~dp0"
echo Compilando core + lwjgl3 (necesario para ver cambios nuevos)...
call mvn -q -pl lwjgl3 -am "-Denforcer.skip=true" -DskipTests install
if errorlevel 1 (
    echo Error al compilar.
    exit /b 1
)
echo Iniciando juego...
call mvn -q -pl lwjgl3 exec:java
