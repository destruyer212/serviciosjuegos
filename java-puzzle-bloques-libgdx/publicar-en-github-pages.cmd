@echo off
setlocal
cd /d "%~dp0\.."

echo.
echo ============================================================
echo   PUBLICAR CODEQUEST EN GITHUB PAGES
echo ============================================================
echo.
echo PASO 1 (solo la primera vez, en el navegador):
echo   1. Abre: https://github.com/destruyer212/serviciosjuegos/settings/pages
echo   2. En "Build and deployment" ^> Source elige: GitHub Actions
echo   3. Guarda
echo.
echo PASO 2 (cada vez que quieras actualizar el juego web):
echo   Desde la carpeta serviciosjuegos ejecuta estos comandos:
echo.
echo   git add .
echo   git commit -m "Actualizar juego web"
echo   git push
echo.
echo PASO 3:
echo   Espera 2-5 minutos y abre:
echo   https://destruyer212.github.io/serviciosjuegos/
echo.
echo   Para ver el progreso:
echo   https://github.com/destruyer212/serviciosjuegos/actions
echo.
pause
endlocal
