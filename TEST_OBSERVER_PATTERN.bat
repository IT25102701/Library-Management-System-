@echo off
cd /d "%~dp0Backend"
echo =====================================================
echo   SmartLibrary Connect - Observer Pattern Test
echo =====================================================
echo.
gradlew.bat test --tests com.sliit.smartlibrary.designpattern.observer.LibraryNotificationSubjectTest
pause
