@echo off
setlocal EnableDelayedExpansion

:: Define your specific deployment variables
set JAR_FILE=healthcheck-0.0.1-SNAPSHOT.jar
set REMOTE_USER=azureuser
set REMOTE_HOST=52.184.66.220
set PROJECT_NAME=healthcheck
set SSH_KEY=C:\Users\kalje\.ssh\kaljessy.pem
set SERVICE_SCRIPT=create_java_service.sh

:: Build environment variables string with proper escaping
:: Use delayed expansion to properly handle special characters
set "ENV_VARS=HC_ENCRYPTION_KEY='!HC_ENCRYPTION_KEY!'"
if "!HC_ENCRYPTION_KEY!"=="" (
    echo Error: HC_ENCRYPTION_KEY is not set
    exit /b 1
)


:: Call the generic deployment script with your parameters
call deploy_java_app.bat ^
    %JAR_FILE% ^
    %REMOTE_USER% ^
    %REMOTE_HOST% ^
    %PROJECT_NAME% ^
    %SSH_KEY% ^
    %SERVICE_SCRIPT% ^
    "!ENV_VARS!" ^
    %1
