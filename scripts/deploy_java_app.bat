@echo off
setlocal

:: Enable error checking
setlocal enabledelayedexpansion

:: Check required parameters
if "%~1"=="" goto usage
if "%~2"=="" goto usage
if "%~3"=="" goto usage
if "%~4"=="" goto usage
if "%~5"=="" goto usage

:: Define variables from parameters
set JAR_FILE=%~1
set REMOTE_USER=%~2
set REMOTE_HOST=%~3
set REMOTE_PATH=/home/%REMOTE_USER%/programs/%~4
set SSH_KEY=%~5
set SERVICE_NAME=%~4
set SERVICE_SCRIPT=%~6
set ENV_VARS=%~7
set ARGUMENT=

:: Check if "--recreate" argument is passed
if "%~8"=="--recreate" set ARGUMENT=--recreate



:: Ensure remote directory exists
echo Checking if remote directory exists...
ssh -i %SSH_KEY% %REMOTE_USER%@%REMOTE_HOST% "[ -d %REMOTE_PATH% ] || mkdir -p %REMOTE_PATH%"
if !errorlevel! neq 0 (
    echo Error: Failed to create remote directory
    exit /b 1
)

:: Transfer files to remote server
echo Transferring %SERVICE_SCRIPT% to %REMOTE_USER%@%REMOTE_HOST%...

scp -C -q -i %SSH_KEY% %SERVICE_SCRIPT% %REMOTE_USER%@%REMOTE_HOST%:%REMOTE_PATH%/
if !errorlevel! neq 0 (
    echo Error: Failed to transfer files
    exit /b 1
)
cd ..
echo Transferring file to %REMOTE_USER%@%REMOTE_HOST%...
scp -C -q -i %SSH_KEY% target/%JAR_FILE% %REMOTE_USER%@%REMOTE_HOST%:%REMOTE_PATH%/
if !errorlevel! neq 0 (
    echo Error: Failed to transfer files
    exit /b 1
)
cd scripts


:: Run the service creation script with arguments
echo Ensuring service exists...
ssh -i %SSH_KEY% %REMOTE_USER%@%REMOTE_HOST% "chmod +x %REMOTE_PATH%/create_java_service.sh && sudo %REMOTE_PATH%/create_java_service.sh %SERVICE_NAME% %REMOTE_USER% %REMOTE_PATH% %JAR_FILE% %ENV_VARS% %ARGUMENT%"
if !errorlevel! neq 0 (
    echo Error: Failed to create/update service
    exit /b 1
)

:: Add a short delay before restart
echo Waiting for 3 seconds before restart...
timeout /t 3 /nobreak > nul

:: Restart the service
echo Restarting the service...
ssh -i %SSH_KEY% %REMOTE_USER%@%REMOTE_HOST% "sudo systemctl restart %SERVICE_NAME%"
if !errorlevel! neq 0 (
    echo Error: Failed to restart service
    exit /b 1
)

echo Deployment complete!
endlocal

goto :eof

:usage
echo Usage: deploy_java_app.bat TAR_FILE REMOTE_USER REMOTE_HOST PROJECT_NAME SSH_KEY [JAR_FILE] [--recreate]
echo Example: deploy_java_app.bat myapp.tar.gz azureuser 52.184.66.220 my-service C:\Users\user\.ssh\key.pem application.jar --recreate
exit /B 1