#!/bin/sh

DIR=`pwd`
OPF_DIR=/opt/openflame/base

if [ "`ps aux |grep java |grep $OPF_DIR`"  !=  "" ] ;  then
 echo "Stop openflame and restart memcached"
 /etc/init.d/openflame stop
 sleep 10
fi

 /etc/init.d/memcached restart

if [ "`ps aux |grep java |grep $OPF_DIR`"  !=  "" ] ;  then
 echo "Incorrect shutdown openflame process, please kill process  manualy and restart upgrade"
 exit 0
fi

rm -rf $OPF_DIR/webapps/openflame.war
cp $DIR/webapps/openflame.war $OPF_DIR/webapps/openflame.war

/etc/init.d/openflame start