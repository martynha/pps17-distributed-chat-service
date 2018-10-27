rem @echo off

SET SRC=".\dist"
SET DEST="..\dcs-webapp-service\src\main\resources\webroot"

IF NOT exist node_modules\ (
    npm install
)

ng build --prod

IF (exist %DEST%\) (
    echo "Removing contents of dest folder"
    del /s /f /q %DEST%\*
    for /f %f in ('dir /ad /b %DEST%\') do rd /s /q %DEST%\%f
) ELSE (
    echo "Creating dest folder"
    mkdir %DEST%
)

robocopy /move /e %SRC% %DEST%

 del /s /f /q %SRC%\*
    for /f %f in ('dir /ad /b %SRC%\') do rd /s /q %SRC%\%f
