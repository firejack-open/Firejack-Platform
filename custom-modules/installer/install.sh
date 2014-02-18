#!/bin/sh
#
# ------------------------------------------------------
#  OpenFlame Startup Script for Unix
# ------------------------------------------------------
#
# ------------------------------------------------------
# Before you run OpenFlame specify the location of the
# JDK1.6 installation directory (JAVA_HOME),
# OPF_ENV_PATH,
# CATALINA_HOME
# which will be used for installation process
# ------------------------------------------------------


export OPF_ENV_PATH=${OPF_ENV_PATH%/}

OPF_ENVIRONMENT_FILE=environment.xml
OPF_INSTALL_FILE=environment.properties

if [ ! -d "$JAVA_HOME" ] ; then
    echo "You have to set JAVA_HOME"
    exit 1
fi

if [ ! -d "$CATALINA_HOME" ]; then
    echo "You have to set CATALINA_HOME variable"
    exit 1
else
    if [ ! -d "$CATALINA_HOME/temp" ] || [ ! -e "$CATALINA_HOME/bin/bootstrap.jar" ] ; then
        echo "Invalid CATALINA_HOME"
        exit 1
    fi
fi

#-------------------------------------------------------
# Check environment.xml and environment.properties
#-------------------------------------------------------


if [ -d "$OPF_ENV_PATH" ] && [ -e "$OPF_ENV_PATH/$OPF_ENVIRONMENT_FILE" ]; then
    OPF_ENVIRONMENT_FILE=$OPF_ENV_PATH/$OPF_ENVIRONMENT_FILE
fi

if [ ! -e "$OPF_ENVIRONMENT_FILE" ]; then
    echo "Can't find environment.xml. It must be located in current directory or at OPF_ENV_PATH "
    exit
fi

if [ -d "$OPF_ENV_PATH" ] && [ -e "$OPF_ENV_PATH/$OPF_INSTALL_FILE" ]; then
    OPF_INSTALL_FILE=$OPF_ENV_PATH/$OPF_INSTALL_FILE
fi

if [ ! -e "$OPF_INSTALL_FILE" ]; then
    echo "Can't find environment.properties. It must be located in current directory or at OPF_ENV_PATH "
    exit
fi

#-------------------------------------------------------
# Set CATALINA_BASE
#-------------------------------------------------------


CATALINA_BASE=$(pwd)

if [ ! -w "$CATALINA_BASE" ]; then
    echo "Don't have write permission on current directory"
    exit 1
fi

export CATALINA_BASE
export CATALINA_TMPDIR=$CATALINA_HOME/temp
export CLASSPATH=$CATALINA_HOME/bin/bootstrap.jar

if [ ! -d logs ]; then
        mkdir logs
fi

echo "Running OpenFlame .."

export CATALINA_OPTS="$CATALINA_OPTS -server -Xms512m -Xmx1024m -XX:MaxPermSize=512m -Dproject.name=net.firejack.platform"

nohup $CATALINA_HOME/bin/catalina.sh start > logs/openflame.log &