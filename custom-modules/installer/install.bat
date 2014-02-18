@echo off

set OPF_PROPERTIES_FILE=environment.properties
set OPF_ENVIRONMENT_FILE=environment.xml

IF "%OPF_ENV_PATH:~-1%"=="\" SET OPF_ENV_PATH=%OPF_ENV_PATH:~0,-1%

#-------------------------------------------------------
# Check environment.xml and environment.properties
#-------------------------------------------------------
set CATALINA_BASE=%cd%
set CATALINA_TMPDIR=%CATALINA_HOME%\temp
set CLASSPATH=%CATALINA_HOME%\bin\bootstrap.jar


IF "%JAVA_HOME%"=="" (
    @ECHO You have to set JAVA_HOME
    exit 1
)

IF "%CATALINA_HOME%"=="" (
    @ECHO You have to set $CATALINA_HOME
    exit 1
)

IF not exist "%OPF_ENV_PATH%\%OPF_PROPERTIES_FILE%" (
    IF not exist "%CATALINA_BASE%\%OPF_PROPERTIES_FILE%" (
        @ECHO Can't find environment.properties
        exit 1
    )else(
        set OPF_PROPERTIES_FILE=%CATALINA_BASE%\%OPF_PROPERTIES_FILE%
    )
)else(
    set OPF_PROPERTIES_FILE=%CATALINA_BASE%\%OPF_PROPERTIES_FILE%
)


IF not exist "%OPF_ENV_PATH%\%OPF_ENVIRONMENT_FILE%" (
    IF not exist "%CATALINA_BASE%\%OPF_ENVIRONMENT_FILE%" (
        @ECHO Can't find environment.xml
        exit 1
    )else(
        set OPF_PROPERTIES_FILE=%CATALINA_BASE%\%OPF_ENVIRONMENT_FILE%
    )
)else(
    set OPF_PROPERTIES_FILE=%CATALINA_BASE%\%OPF_ENVIRONMENT_FILE%
)


if not exist "logs" mkdir logs

@echo Running OpenFlame  Install ..

set "CATALINA_OPTS=%CATALINA_OPTS% -server -Xms512m -Xmx1024m -XX:MaxPermSize=512m"

cmd /c %CATALINA_HOME%\bin\catalina.bat run > logs\console.log

