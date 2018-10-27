@echo off

cd dcs-client

build

cd ..

del /s /f /q build\*
for /f %f in ('dir /ad /b build\') do rd /s /q build\%f

gradlew clean

rem gradlew clean check test stage
