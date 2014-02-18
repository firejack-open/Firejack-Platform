#!/bin/sh

DIR=`pwd`

ARCH=`uname -i`

IF=$(echo `ip r |grep default| awk {'print $5'}`)
SERVER_IP=$(ip a ls dev $IF|grep inet|grep global|grep -v $IF:$|grep -v inet6|grep -v secondary|awk {'print $2'}|awk -F '/' {'print $1'})
DEFAULT_SERVER_PORT=8080;

OPF_DEFAULT_ADMIN_USERNAME="admin"

DEFAULT_MEMCACHED_PORT="11211"
DEFAULT_MEMCACHED_CACHE_SIZE="10"

MEMCACHED_CACHE_SIZE=$DEFAULT_MEMCACHED_CACHE_SIZE
MEMCACHED_SERVER_NAME="localhost"
MEMCACHED_PORT=$DEFAULT_MEMCACHED_PORT


INSTALL_BASE_DIR=/opt/firejack
OPF_DIR=$INSTALL_BASE_DIR/base
FILESTORE_DIR=$OPF_DIR/filestore

SOURCES_DIR=$INSTALL_BASE_DIR/downloads/


INFO_MESAGGE_COLOR='\E[32;40m'
ALERT_MESSAGE_COLOR='\E[31;40m'

MASTER_NODE_INSTALL=1
SLAVE_NODE_INSTALL=0
REMOTE_NODE_INSTALL=0


if [ -d $INSTALL_BASE_DIR ]; then
        echo -e $ALERT_MESSAGE_COLOR"$INSTALL_BASE_DIR directory already exists"
        echo -e $INFO_MESAGGE_COLOR"Firejack seems already installed on your server"
        echo -e $INFO_MESAGGE_COLOR"If you are sure it is not installed you can remove $INSTALL_BASE_DIR manually and run setup again"
        tput sgr0;
        exit
fi              
        
if [ "$ARCH" = "x86_64" ]; then
        JDK_URL=http://dev.opf.firejack.net/downloads/installs/jdk/jdk-6u31-linux-x64.bin
        JDK_INTBIN=jdk-6u31-linux-x64.bin
else
        JDK_URL=http://dev.opf.firejack.net/downloads/installs/jdk/jdk-6u31-linux-i586.bin
        JDK_INTBIN=jdk-6u31-linux-i586.bin
fi

if [ -x "/sbin/runuser" ]; then
    SU="/sbin/runuser"
else
    SU="su"
fi

TOMCAT_VERSION=6.0.32
MAVEN_VERSION=3.0.5
TOMCAT_URL=http://archive.apache.org/dist/tomcat/tomcat-6/v6.0.32/bin/apache-tomcat-6.0.32.tar.gz
MAVEN_URL=http://www.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz


function yesno()
{

	echo -en $INFO_MESAGGE_COLOR
	message=$1

	while true 
	do
		read -p "$message" item
		case "$item" in
			y|Y) return 0
			;;
			n|N) return 1
			;;
			*) continue
			;;
		esac
	done
	tput sgr0
}

function get_new_mysql_root_password()
{

	MYSQL_ROOT_PASSWORD=0
	MYSQL_ROOT_PASSWORD_CONFIRMED=1
	while :
	do
		echo -en $INFO_MESAGGE_COLOR

		read -s -p "Please Enter the MYSQL root password: " MYSQL_ROOT_PASSWORD
		tput sgr0
		echo
		echo -en $INFO_MESAGGE_COLOR
		read -s -p "Please Confirm the MYSQL root password: " MYSQL_ROOT_PASSWORD_CONFIRMED
		tput sgr0
		echo
		if [ "$MYSQL_ROOT_PASSWORD" == "$MYSQL_ROOT_PASSWORD_CONFIRMED" ]; then
			break
		else
			echo -en $ALERT_MESSAGE_COLOR
			echo "Passwords do not match"
			tput sgr0
			echo
		fi
	done
}

function get_current_mysql_root_password()
{

	local host=$1

	echo -en $INFO_MESAGGE_COLOR
	read -s -p "Please Enter MYSQL root password: " MYSQL_ROOT_PASSWORD
	tput sgr0
}

function check_mysql_connection()
{
	local host=$1
	local username=$2
	local password=$3
	local ret=1

#	mysql -h $host -u $username -p$password  -e ";" 1>/dev/null 2>&1
	mysql --connect_timeout=10 -h $host -u $username -p$password  -e ";"
	ret=$?

	echo $ret
}


function check_tcp_connection()
{
	local servername=$1
	local port=$2
	local ret=1
	con=`echo > /dev/tcp/$servername/$port`
	ret=$?

	echo $ret
}
	


function get_servername()
{
	local resultvar=$1
	local message=$2
	local SERVER_NAME=""
	echo -en $INFO_MESAGGE_COLOR
        read -p "$message" SERVER_NAME
	tput sgr0

	eval $resultvar="$SERVER_NAME"
}

function valid_ip()
{
    local  ip=$1
    local  ret=1

    if [[ $ip =~ ^[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}$ ]]; then
        OIFS=$IFS
        IFS='.'
        ip=($ip)
        IFS=$OIFS
        [[ ${ip[0]} -le 255 && ${ip[1]} -le 255 \
            && ${ip[2]} -le 255 && ${ip[3]} -le 255 ]]
        ret=$?
    fi
    return $ret
}

function valid_servername()
{
	local  servername=$1
	local  ret=1

	if [ `echo -n $servername|wc -c` -eq 0 ]; then
		return $ret
	fi

	if [ "`nslookup $servername| grep -i "can't find"`" == "" ]; then
		ret=0
	fi

    	return $ret
}

function valid_email()
{
	local email=$1
	local regex="^[a-z0-9!#\$%&'*+/=?^_\`{|}~-]+(\.[a-z0-9!#$%&'*+/=?^_\`{|}~-]+)*@([a-z0-9]([a-z0-9-]*[a-z0-9])?\.)+[a-z0-9]([a-z0-9-]*[a-z0-9])?\$"

	if [[ $email =~ $regex ]] ; then
		return 0
	else
		return 1
	fi
}

function valid_username()
{
	local username=$1
	local regex="^[A-Za-z0-9]{5,10}$"
	
	if [[ $username =~ $regex ]] ; then
		return 0
	else
		return 1
	fi
}

function valid_password()
{
	local password=$1
	#local regex="^\w{7,}[A-Za-z0-9]$"
	local regex="^[A-Za-z0-9]{6,}$"

	if [[ $password =~ $regex ]] ; then
		return 0
	else
		return 1
	fi
}


function get_opf_admin()
{
while :
do
	echo -en $INFO_MESAGGE_COLOR
	read -p "Please enter Firejack admin username (5-10 letters or/and digits) [$OPF_DEFAULT_ADMIN_USERNAME]: " OPF_ADMIN_USERNAME
	tput sgr0
	if [ `echo -n $OPF_ADMIN_USERNAME|wc -c` -eq 0 ]; then
		OPF_ADMIN_USERNAME=$OPF_DEFAULT_ADMIN_USERNAME
	fi
	if valid_username $OPF_ADMIN_USERNAME; then
		break
	else
		echo -en $ALERT_MESSAGE_COLOR
		echo "Usename is not correct, it should be 5-10 letters and/or, please try again"
		tput sgr0
	fi
done

while :
do
    echo -en $INFO_MESAGGE_COLOR
    read -s -p "Please enter Firejack admin password(at least 6 symbolis, letters and/or digits): " OPF_ADMIN_PASSWORD
	tput sgr0
	echo
    echo -en $INFO_MESAGGE_COLOR
    read -s -p "Please confirm Firejack admin password: " OPF_ADMIN_PASSWORD_CONFIRMED
    tput sgr0
    echo
    if [ "$OPF_ADMIN_PASSWORD" == "$OPF_ADMIN_PASSWORD_CONFIRMED" ]; then
        if valid_password $OPF_ADMIN_PASSWORD; then
            break
        else
            echo -en $ALERT_MESSAGE_COLOR
            echo "Password is not correct, it must be at least 6 symbols and must contain only letters and/or digits, please try again"
            tput sgr0
        fi
    else
        echo -en $ALERT_MESSAGE_COLOR
        echo "Passwords do not match"
        tput sgr0
        echo
    fi
done

if [ $MASTER_NODE_INSTALL -gt 0 ]; then
	while :
	do
		echo -en $INFO_MESAGGE_COLOR
		read -p "Please enter Firejack admin email: " OPF_ADMIN_EMAIL
		tput sgr0
		if valid_email $OPF_ADMIN_EMAIL; then
			break
		else
			echo -en $ALERT_MESSAGE_COLOR
			echo "Email address is not correct, please neter correct email address"
			tput sgr0
		fi
	done
fi
}

function repo() {
if [ "`yum repolist all|grep remi`" = "" -a ! -e /etc/yum.repos.d/firejack.repo ]; then
cat << EOF > /etc/yum.repos.d/firejack.repo
[remi]
name=Les RPM de remi pour Enterprise Linux \$releasever - \$basearch
mirrorlist=http://rpms.famillecollet.com/enterprise/\$releasever/remi/mirror
enabled=1
gpgcheck=1
gpgkey=http://rpms.famillecollet.com/RPM-GPG-KEY-remi
EOF
fi

if [ "`yum repolist all|grep 'base '`" = "" ]; then
		local version=`cat /etc/redhat-release`;
		local keyversion;
		if [[ $version =~ .*6\.3.* ]]; then
			version="6.3";
			keyversion="6";
		elif [[ $version =~ .*6\.2.* ]] || [[ $version =~ .*6\.1.* ]] || [[ $version =~ .*6\.0.* ]]; then
			version="6";
			keyversion="6";
		elif [[ $version =~ .*5\.8.* ]]; then
			version="5.8";
			keyversion="5";
		else
			version="\$releasever";
		fi
cat << EOF > /etc/yum.repos.d/firejack.repo
[remi]
name=Les RPM de remi pour Enterprise Linux \$releasever - \$basearch
mirrorlist=http://rpms.famillecollet.com/enterprise/\$releasever/remi/mirror
enabled=1
gpgcheck=1
gpgkey=http://rpms.famillecollet.com/RPM-GPG-KEY-remi

[base]
name=CentOS- $version - Base
mirrorlist=http://mirrorlist.centos.org/?release=$version&arch=\$basearch&repo=os
gpgcheck=1
gpgkey=http://mirror.centos.org/centos/RPM-GPG-KEY-CentOS-$keyversion

#released updates
[updates]
name=CentOS- $version - Updates
mirrorlist=http://mirrorlist.centos.org/?release=$version&arch=\$basearch&repo=updates
gpgcheck=1
gpgkey=http://mirror.centos.org/centos/RPM-GPG-KEY-CentOS-$keyversion
EOF
fi
}
	

if yesno "Install Firejack Master Server Node? (y/n):"; then
# MASTER specific install
MASTER_NODE_INSTALL=1

while :
do
        get_servername OPF_SERVER_NAME "Please Enter Firejack Servername [$SERVER_IP]: "
        if [ `echo -n $OPF_SERVER_NAME|wc -c` -eq 0 ]; then
                OPF_SERVER_NAME=$SERVER_IP
        fi
        if valid_ip $OPF_SERVER_NAME; then
                break
        elif valid_servername $OPF_SERVER_NAME; then
                break
        else
                echo -en $ALERT_MESSAGE_COLOR
                echo "Servername is not correct, it must be correct FQDN or IP address, please try again"
                tput sgr0
        fi
done

while :
do
	echo -en $INFO_MESAGGE_COLOR
	read -p "Please Enter Firejack Server port [$DEFAULT_SERVER_PORT]: " SERVER_PORT
	tput sgr0
	if [ `echo -n $SERVER_PORT|wc -c` -eq 0 ]; then
		SERVER_PORT=$DEFAULT_SERVER_PORT
	fi
	if [[ $SERVER_PORT =~ ^[0-9]{1,5}$ ]] && [ $SERVER_PORT -gt 0 ] && [ $SERVER_PORT -lt 65535 ]; then
		break
	else
		echo -en $ALERT_MESSAGE_COLOR
		echo "Port number is not correct, please try again"
		tput sgr0
	fi
done

while :
do
        get_servername LOCAL_SERVER_NAME "Please Enter local Servername [$SERVER_IP]: "
        if [ `echo -n $LOCAL_SERVER_NAME|wc -c` -eq 0 ]; then
                LOCAL_SERVER_NAME=$SERVER_IP
        fi
        if valid_ip $LOCAL_SERVER_NAME; then
                break
        elif valid_servername $LOCAL_SERVER_NAME; then
                break
        else
                echo -en $ALERT_MESSAGE_COLOR
                echo "Servername is not correct, it must be correct FQDN or IP address, please try again"
                tput sgr0
        fi
done

get_opf_admin

# MySQL
if yesno "Would you like to install MySQL on this host? (y/n):"; then
	# check if mysql server is installed
	if [ "`rpm -qa|grep mysql-server`" = "" ] && [ "`pidof mysqld`" = "" ]; then
		repo
		get_new_mysql_root_password
		echo -e $INFO_MESAGGE_COLOR"Installing mysql server"; tput sgr0
		yum -y install mysql-server
		/etc/init.d/mysqld start
		chkconfig mysqld on
		mysqladmin -u root password "$MYSQL_ROOT_PASSWORD"
		mysql -uroot -p"$MYSQL_ROOT_PASSWORD" mysql -e"UPDATE user SET password=password('$MYSQL_ROOT_PASSWORD') WHERE user = 'root'; FLUSH PRIVILEGES;"
	else
		echo -e $INFO_MESAGGE_COLOR"Mysql server is already installed and running on your server"; tput sgr0
		while :
		do
			get_current_mysql_root_password
			echo
			if [ "$(check_mysql_connection localhost root $MYSQL_ROOT_PASSWORD)" == "0" ]; then
				break
			else
				echo -en $ALERT_MESSAGE_COLOR
				echo "Can't connect, please retry: "
				tput sgr0
			fi
		done
	fi
	MYSQL_SERVER_NAME=$LOCAL_SERVER_NAME
	mysql -uroot -p"$MYSQL_ROOT_PASSWORD" mysql -e"CREATE USER 'root'@'%' IDENTIFIED BY '$MYSQL_ROOT_PASSWORD'; GRANT ALL ON *.* TO 'root'@'%'; GRANT SELECT ON mysql.* TO 'root'@'%'; "
else

	while :
	do

		while :
		do
			get_servername MYSQL_SERVER_NAME "Please Enter the IP or Servername to a remote database: "

			if valid_ip $MYSQL_SERVER_NAME; then
				break
			elif valid_servername $MYSQL_SERVER_NAME; then
				break
			else
				echo -en $ALERT_MESSAGE_COLOR
				echo "IP address or Servername is not correct, please try again"
				tput sgr0
			fi
		done
		get_current_mysql_root_password
		echo
		if [ "$(check_mysql_connection $MYSQL_SERVER_NAME root $MYSQL_ROOT_PASSWORD)" == "0" ]; then
			break
		else
			echo -en $ALERT_MESSAGE_COLOR
			echo "Can't connect to server, please retry: "
			tput sgr0
		fi
		mysql -h"$MYSQL_SERVER_NAME" -uroot -p"$MYSQL_ROOT_PASSWORD" mysql -e"CREATE USER 'root'@'%' IDENTIFIED BY '$MYSQL_ROOT_PASSWORD'; GRANT ALL ON *.* TO 'root'@'%'; GRANT SELECT ON mysql.* TO 'root'@'%'; "
	done
fi

# memcached
if yesno "Do you want to use the local host for caching? (y/n):"; then
	# check if memcached is installed
	if [ "`rpm -qa|grep memcached`" = "" ] && [ "`pidof memcached`" = "" ]; then
		repo
		echo -e $INFO_MESAGGE_COLOR "Installing memcached"; tput sgr0
		yum -y install memcached
		chkconfig memcached on
		/etc/init.d/memcached start
	else
		echo -e $INFO_MESAGGE_COLOR "memcached already installed on your server"; tput sgr0
	fi
	MEMCACHED_SERVER_NAME=$LOCAL_SERVER_NAME
else
	while :
	do
		while :
		do
			get_servername MEMCACHED_SERVER_NAME "Please Enter the IP address or Servername for your cache server: "
			if valid_ip $MEMCACHED_SERVER_NAME; then
				break
			elif valid_servername $MEMCACHED_SERVER_NAME; then
				break
			else
				echo -en $ALERT_MESSAGE_COLOR
				echo "IP address or Servername is not correct, please try again"
				tput sgr0
			fi
		done
		
		while :
		do
			echo -en $INFO_MESAGGE_COLOR
			read -p "Please enter the port number for your caching server [$DEFAULT_MEMCACHED_PORT]: " MEMCACHED_PORT
			tput sgr0
			if [ `echo -n $MEMCACHED_PORT|wc -c` -eq 0 ]; then
				MEMCACHED_PORT=$DEFAULT_MEMCACHED_PORT
			fi
			if [[ $MEMCACHED_PORT =~ ^[0-9]{1,5}$ ]] && [ $MEMCACHED_PORT -gt 0 ] && [ $MEMCACHED_PORT -lt 65535 ]; then
				break
			else
				echo -en $ALERT_MESSAGE_COLOR
				echo "Port number is not correct, please try again"
				tput sgr0
			fi
		done

		if [ "$(check_tcp_connection $MEMCACHED_SERVER_NAME $MEMCACHED_PORT)" == "0" ]; then
			break
		else
			echo -en $ALERT_MESSAGE_COLOR
			echo "Can not connect to the remote caching server"
			tput sgr0
		fi
	done
fi
# END MASTER specific install
# END MASTER specific install

elif yesno "Install Firejack Slave Node? (y/n):"; then
	MASTER_NODE_INSTALL=0
	SLAVE_NODE_INSTALL=1

	while :
	do
        	while :
		do
                        get_servername OPF_MASTER_SERVER_NAME "Please Enter the Master OPF IP address or Servername: "
                        if  valid_ip $OPF_MASTER_SERVER_NAME; then
				break
			elif valid_servername $OPF_MASTER_SERVER_NAME ; then
				break
			else
				echo -en $ALERT_MESSAGE_COLOR
                                echo "IP address or Servername is not correct, please try again"
                                tput sgr0
			fi
		done

		while :
		do
			echo -en $INFO_MESAGGE_COLOR
			read -p "Please Enter the Master server port: [$DEFAULT_SERVER_PORT]: " SERVER_PORT
			tput sgr0
			if [ `echo -n $SERVER_PORT|wc -c` -eq 0 ]; then
				SERVER_PORT=$DEFAULT_SERVER_PORT
			fi
			if [[ $SERVER_PORT =~ ^[0-9]{1,5}$ ]] && [ $SERVER_PORT -gt 0 ] && [ $SERVER_PORT -lt 65535 ]; then
				break
			else
				echo -en $ALERT_MESSAGE_COLOR
				echo "Port number is not correct, please try again"
				tput sgr0
			fi
		done

		if [ "$(check_tcp_connection $OPF_MASTER_SERVER_NAME $SERVER_PORT)" == "0" ]; then
			break
		else
			echo -en $ALERT_MESSAGE_COLOR
			echo "WARNING: Can not connect to OPF Master server"
			echo "Master OPF is not working now, before start slave OPF you must be sure the Master OPF is working"
			tput sgr0
			break
		fi
	done

	get_opf_admin
# END of Slave specific install
# END of Slave specific install
elif yesno "Install Firejack Remote Server Node? (y/n):"; then
	MASTER_NODE_INSTALL=0
	REMOTE_NODE_INSTALL=1
	exit 0
else
	echo -en $ALERT_MESSAGE_COLOR
	echo "Firejack setup has been canceled"
	tput sgr0
	exit 0
fi


groupadd -f -r firejack
if [ ! `id -u firejack 2> /dev/null` ]; then
        useradd -g firejack -d $INSTALL_BASE_DIR  firejack
fi              
mkdir -p $INSTALL_BASE_DIR
mkdir -p $SOURCES_DIR
			

# Install JDK
cd $SOURCES_DIR
/bin/rm -rf jdk*
/bin/rm -rf $INSTALL_BASE_DIR/jdk*
echo -e $INFO_MESAGGE_COLOR"Downloading Sun JDK";tput sgr0
wget $JDK_URL
echo -e $INFO_MESAGGE_COLOR"Unpacking JDK";tput sgr0
sh ./$JDK_INTBIN -noregister
mv jdk1.6.0_31 $INSTALL_BASE_DIR/
ln -s $INSTALL_BASE_DIR/jdk1.6.0_31 $INSTALL_BASE_DIR/jdk
echo "JAVA_HOME=$INSTALL_BASE_DIR/jdk" > /etc/profile.d/firejack.sh
echo "export JAVA_HOME" >> /etc/profile.d/firejack.sh
chmod 755 /etc/profile.d/firejack.sh


# Install maven
echo -e $INFO_MESAGGE_COLOR"Downloading Apache Maven";tput sgr0
cd $SOURCES_DIR
/bin/rm -rf apache-maven*
/bin/rm -rf $INSTALL_BASE_DIR/*maven*
wget $MAVEN_URL
echo -e $INFO_MESAGGE_COLOR"Unpacking Apache Maven";tput sgr0
tar xzf apache-maven-$MAVEN_VERSION-bin.tar.gz
mv apache-maven-$MAVEN_VERSION $INSTALL_BASE_DIR/
ln -s $INSTALL_BASE_DIR/apache-maven-$MAVEN_VERSION $INSTALL_BASE_DIR/maven

# Install tomcat
echo -e $INFO_MESAGGE_COLOR"Downloading Apache Tomcat";tput sgr0
cd $SOURCES_DIR
/bin/rm -rf apache-tomcat*
/bin/rm -rf $INSTALL_BASE_DIR/*tomcat*
wget $TOMCAT_URL
echo -e $INFO_MESAGGE_COLOR"Unpacking Apache Tomcat";tput sgr0
tar xzf apache-tomcat-$TOMCAT_VERSION.tar.gz
mv apache-tomcat-$TOMCAT_VERSION $INSTALL_BASE_DIR/
ln -s $INSTALL_BASE_DIR/apache-tomcat-$TOMCAT_VERSION $INSTALL_BASE_DIR/tomcat

cd $DIR
mkdir -p $OPF_DIR
if [ $MASTER_NODE_INSTALL -gt 0 ]; then
mkdir -p $FILESTORE_DIR
fi
mkdir -p $OPF_DIR/logs
mkdir -p $INSTALL_BASE_DIR/apache-tomcat-$TOMCAT_VERSION/temp
cp -a * $OPF_DIR/
cp $DIR/environment.example.xml $OPF_DIR/environment.xml
cp $DIR/environment.properties.example $OPF_DIR/environment.properties

sed -i -e "s%\[ADMIN_NAME\]%$OPF_ADMIN_USERNAME%g" $OPF_DIR/environment.properties
sed -i -e "s%\[ADMIN_PASSWORD\]%$OPF_ADMIN_PASSWORD%g" $OPF_DIR/environment.properties
sed -i -e "s%\[ADMIN_EMAIL\]%$OPF_ADMIN_EMAIL%g" $OPF_DIR/environment.properties

sed -i -e "s%\[MEMCACHED_PORT\]%$MEMCACHED_PORT%g" $OPF_DIR/environment.properties
sed -i -e "s%\[MEMCACHED_CACHE_SIZE\]%$MEMCACHED_CACHE_SIZE%g" $OPF_DIR/environment.properties

if [ $MASTER_NODE_INSTALL -eq 1 ]; then
	sed -i -e "s%\[FILESTORE_DIRECTORY\]%$FILESTORE_DIR%g" $OPF_DIR/environment.xml
	sed -i -e "s/\[DB_PASSWORD\]/$MYSQL_ROOT_PASSWORD/g" $OPF_DIR/environment.xml
	sed -i -e "s/\[DB_NAME\]/opf_main/g" $OPF_DIR/environment.xml
	sed -i -e "s/\[DB_USERNAME\]/root/g" $OPF_DIR/environment.xml
	sed -i -e "s/\[DB_SERVER_NAME\]/$MYSQL_SERVER_NAME/g" $OPF_DIR/environment.xml

	sed -i -e "s%\[MEMCACHED_URL\]%$MEMCACHED_SERVER_NAME%g" $OPF_DIR/environment.properties

	sed -i -e "s/\[SERVER_NAME\]/$OPF_SERVER_NAME/g" $OPF_DIR/environment.xml
	sed -i -e "s/\[SERVER_PORT\]/$SERVER_PORT/g" $OPF_DIR/environment.xml
fi

if [ $SLAVE_NODE_INSTALL -eq 1 ]; then
	sed -i -e "s%\#opf.master.url=\[OPF_MASTER_URL\]%opf.master.url=http:\/\/$OPF_MASTER_SERVER_NAME\:$SERVER_PORT\/platform%g" $OPF_DIR/environment.properties

	sed -i -e "s%\[MEMCACHED_URL\]%$OPF_MASTER_SERVER_NAME%g" $OPF_DIR/environment.properties

	/bin/rm -rf $OPF_DIR/environment.xml
fi


chown -R firejack:firejack $INSTALL_BASE_DIR

cat << EOF > /etc/init.d/firejack
#!/bin/sh
#
# Tomcat Server
#
# chkconfig: 345 96 30
# description: Java servlet container

JAVA_HOME="$INSTALL_BASE_DIR/jdk"
CATALINA_BASE="$OPF_DIR"
FIREJACK_CONFIG="$OPF_DIR"
CATALINA_HOME="$INSTALL_BASE_DIR/tomcat"
CATALINA_TMPDIR="$INSTALL_BASE_DIR/tomcat/temp"
CLASSPATH="\$CATALINA_HOME/bin/bootstrap.jar"
M2_HOME="$INSTALL_BASE_DIR/maven"
OPF_ENVIRONMENT_FILE="$OPF_DIR/environment.xml"
OPF_INSTALL_FILE="$OPF_DIR/environment.properties"
DEBUG_OPTS=" -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8787"

export JAVA_HOME CATALINA_BASE CATALINA_HOME CLASSPATH M2_HOME FIREJACK_CONFIG
export OPF_ENVIRONMENT_FILE OPF_ENVIRONMENT_FILE
export PATH=\$PATH:\$M2_HOME/bin:\$JAVA_HOME/bin
export CATALINA_OPTS="-server -Xms512m -Xmx1024m -XX:PermSize=512m -XX:MaxPermSize=512m"

start()
{
        echo "Starting Firejack..."
		cd $OPF_DIR
        $SU firejack -c "\$CATALINA_HOME/bin/catalina.sh start" >> \$CATALINA_HOME/logs/firejack.log 2>&1
}

debug()
{
        echo "Starting debug Firejack..."
        export CATALINA_OPTS=\$CATALINA_OPTS" -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8787"
		cd $OPF_DIR
        $SU firejack -c "\$CATALINA_HOME/bin/catalina.sh start" >> \$CATALINA_HOME/logs/firejack.log 2>&1
}

stop()
{
        echo "Stopping Firejack..."
		$SU firejack -c "\$CATALINA_HOME/bin/catalina.sh stop" >> \$CATALINA_HOME/logs/firejack.log 2>&1

        sleep 10

        while :
        do
        	if [ "\`pgrep -G firejack java\`" = "" ]; then
        		break;
        	else
        	    echo "Try to kill process \`pgrep -G firejack java\`"
                kill -9 \`pgrep -G firejack java\`;
                sleep 5
        	fi
        done
}

restart()
{
        stop
        start
}

selfrestart() {
        \`\$CATALINA_HOME/bin/catalina.sh stop\` >> \$CATALINA_HOME/logs/restart.log 2>&1
        sleep 10
        while :
        do
        	if [ "\`pgrep -G firejack java\`" = "" ]; then
        		break;
        	else
        	    echo "Try to kill process \`pgrep -G firejack java\`"
                kill -9 \`pgrep -G firejack java\`;
                sleep 5
        	fi
        done
        \`\$CATALINA_HOME/bin/catalina.sh start\` >> \$CATALINA_HOME/logs/restart.log 2>&1
}

case "\$1" in
'start')
        start
        ;;
'stop')
        stop
        ;;
'restart')
        restart
        ;;
'selfrestart')
        selfrestart
        ;;
'debug')
        debug
        ;;
*)
        echo "Please supply an argument [start|stop|restart|debug]"
esac
EOF
chmod 755 /etc/init.d/firejack
chkconfig --add firejack
chkconfig firejack on
echo -en '\E[37;44m'"Please use /etc/init.d/firejack to start|stop|restart firejack service"; tput sgr0
echo
