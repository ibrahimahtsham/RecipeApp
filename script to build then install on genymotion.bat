@echo off
REM Build the debug APK using Gradle
echo Building the debug APK...
call ./gradlew assembleDebug

REM Check if the build was successful
if %ERRORLEVEL% NEQ 0 (
    echo Gradle build failed. Exiting.
    exit /b %ERRORLEVEL%
)

REM Install the APK using ADB
echo Installing the APK on the connected device...
adb install C:\Users\Siamax\Desktop\RecipeApp\app\build\outputs\apk\debug\app-debug.apk

REM Check if the installation was successful
if %ERRORLEVEL% NEQ 0 (
    echo APK installation failed. Exiting.
    exit /b %ERRORLEVEL%
)

echo APK installed successfully.
