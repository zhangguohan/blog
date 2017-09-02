
## Greenplum install On Centos 7 ##

0、Centos7.3.1611 --最小化安装

   DB:GreenPlum 5.0.0


注意事项：

1、一定要git clone完整版本

2、各节点环境一致（hosts\page\sysctl\limitfile）  //如果hosts文件各节点不一样，建立镜象节点无法完成

3、确保各节点间SSH

4、关闭selinux、防火墙设置主机名

    hostnamectl set-hostname mdw
    hostnamectl set-hostname sdw01
    systemctl stop firewalld.service 
    systemctl disable firewalld.service


5、各节点安装软件一致
     
     yum install curl-devel bzip2-devel python-devel openssl-devel -y
     yum install perl-ExtUtils-Embed -y
     yum install libxml2-devel -y 
     yum install openldap-devel -y
     yum install pam pam-devel -y
     yum install perl-devel  -y
     yum install -y python-setuptools
     yum install gcc* -y 
     yum install net-tools -y //如果没有安装会出现无法获取镜象节点状态信息
     yum install git -y
     yum install wget -y
     yum install unzip -y
     yum install zlib* -y 
     yum install readline* -y
     yum install curl curl-devel -y 
     yum install libevent-devel -y
     yum install bison -y
     yum install flex* -y
     yum install apr* -y
     
     # 安装pip软件
    easy_install pip/easy_install -i https://mirrors.aliyun.com/pypi/simple pip  或者：
    wget https://mirrors.aliyun.com/pypi/packages/11/b6/abcb525026a4be042b486df43905d6893fb04f05aac21c32c638e939e447/pip-9.0.1.tar.gz
    cd pip-9.0.1
    python setup.py install
    mkdir /root/.pip/pip.conf

    [root@linux03 yaml-0.1.7]# more /root/.pip/pip.conf 
    [global]
    index-url = http://mirrors.aliyun.com/pypi/simple/
    [install]
    trusted-host=mirrors.aliyun.com


    [root@linux03 yaml-0.1.7]# yum install ftp://mirror.switch.ch/pool/4/mirror/centos/7.3.1611/cloud/x86_64/openstack-kilo/common/pyparsing-2.0.3-1.el7.noarch.rpm
    
    #使用pip安装相关模块
    pip install psi paramiko
    pip  install psutil
    pip install lockfile
    pip  install  epydoc
  
    #安装yaml相关模块
     wget http://pyyaml.org/download/libyaml/yaml-0.1.7.zip
     unzip yaml-0.1.7.zip 
     cd yaml-0.1.7
     ./configure --prefix=/usr
      make
    make install
   


    #安装gpora优化器依懒包
    git clone https://github.com/greenplum-db/gp-xerces.git
    git clone https://github.com/greenplum-db/gporca.git
 
    cd gp-xerces/
  
    mkdir build
    cd build
    ../configure --prefix=/usr/
    make
    make install
    cd ../
    #安装cmake
    https://cmake.org/files/v3.8/cmake-3.8.0-rc2.tar.gz
    wget -c https://cmake.org/files/v3.8/cmake-3.8.0-rc2.tar.gz
    tar -zxvf cmake-3.8.0-rc2.tar.gz 
    cd cmake-3.8.0-rc2/ 
    ./bootstrap && make && make install

    cd gporca/

    cd /usr/local/src/gporca/
    mkdir build/
    cd build/
    history 
    cmake ../
    make
    make install
    ctest -j7


    [root@mdw ld.so.conf.d]# cd /etc/ld.so.conf.d/
  [root@mdw ld.so.conf.d]# echo "/usr/local/lib" >> usrlocallib.conf






[root@mdw package]# more /etc/fstab 

1、如果是使用XFS文件系统
       
        #
        # /etc/fstab
        # Created by anaconda on Mon Mar 27 08:34:13 2017
        #
        # Accessible filesystems, by reference, are maintained under '/dev/disk'
        # See man pages fstab(5), findfs(8), mount(8) and/or blkid(8) for more info
        #
      UUID=395ee60c-6201-4569-826d-3c0e3597a3f2 /                       xfs nodev,noatime,nobarrier,inode64,allocsize=16m 0 0
      UUID=92ad7aa5-17b7-40d6-87d8-72f954b5f702 /boot                   xfs     defaults        0 0
      UUID=a22e1a9f-eb3a-48ce-98a9-f65d9c412d43 swap                    swap    defaults        0 0
      [root@mdw package]#

     Set the following parameters in the /etc/sysctl.conf file and reboot: 


2、设制OS内核参数
    
    # For more information, see sysctl.conf(5) and sysctl.d(5).

    kernel.shmmax = 500000000000
    kernel.shmmni = 4096
    kernel.shmall = 4000000000
    kernel.sem = 250 512000 100 2048
    kernel.sysrq = 1
    kernel.core_uses_pid = 1
    kernel.msgmnb = 65536
    kernel.msgmax = 65536
    kernel.msgmni = 2048
    net.ipv4.tcp_syncookies = 1
    net.ipv4.ip_forward = 0
    net.ipv4.conf.default.accept_source_route = 0
    net.ipv4.tcp_tw_recycle = 1
    net.ipv4.tcp_max_syn_backlog = 4096
    net.ipv4.conf.all.arp_filter = 1
    net.ipv4.ip_local_port_range = 1025 65535
    net.core.netdev_max_backlog = 10000
    net.core.rmem_max = 2097152
    net.core.wmem_max = 2097152
    vm.overcommit_memory = 2



    Set the following parameters in the /etc/security/limits.conf file:

     * soft nofile 65536
     * hard nofile 65536
     * soft nproc 131072
     * hard nproc 131072

     For RedHat Enterprise Linux 7.x and CentOS 7.x, parameter values in the /etc/security/limits.d/20-nproc.conf file override the values in the limits.conf


    以上在所有节点进行安装



* **Git clone Greenplum 数据库源码包**

    git clone    https://github.com/greenplum-db/gpdb.git 
    cd gpdb/
    ./configure --with-perl --with-python --with-libxml --enable-mapreduce --enable-orca --prefix=/usr/local/gpdb-5.0.0
     make
     make install


* **配置Greenplum数据库**

     添加gpadmin用户及密码
    [root@mdw ~]# useradd gpadmin
    [root@mdw ~]# passwd gpadmin


    [root@mdw ~]# chown -R gpadmin:gpadmin /usr/local/gpdb-5.0.0/
    [root@mdw ~]# cd /home/gpadmin/
    [root@mdw gpadmin]# touch all_hosts
    [root@mdw gpadmin]# vi /etc/hosts

     [root@mdw gpadmin]# scp /etc/hosts sdw01:/etc/
   
     [root@mdw gpadmin]# scp /etc/hosts sdw02:/etc/

     [root@mdw gpadmin]# more /home/gpadmin/all_hosts 
     mdw
     sdw01
     sdw02


打通root用户所有节点SSH免密码


[root@mdw .ssh]#source /usr/local/gpdb-5.0.0/greenplum_path.sh 

[root@mdw .ssh]# gpssh-exkeys -f /home/gpadmin/all_hosts
[STEP 1 of 5] create local ID and authorize on local host
  ... /root/.ssh/id_rsa file exists ... key generation skipped

[STEP 2 of 5] keyscan all hosts and update known_hosts file

[STEP 3 of 5] authorize current user on remote hosts
  ... send to sdw01
  ... send to sdw02

[STEP 4 of 5] determine common authentication file content

[STEP 5 of 5] copy authentication files to all remote hosts
  ... finished key exchange with sdw01
  ... finished key exchange with sdw02

[INFO] completed successfully
[root@mdw .ssh]# 




[root@mdw .ssh]#  gpssh -f /home/gpadmin/all_segs '/usr/sbin/useradd gpadmin -d /home/gpadmin -s /bin/bash'
[sdw02]
[sdw01]
[root@mdw .ssh]# gpssh -f /home/gpadmin/all_segs ' echo gpadmin | passwd --stdin gpadmin'
[sdw02] Changing password for user gpadmin.
[sdw02] passwd: all authentication tokens updated successfully.
[sdw01] Changing password for user gpadmin.
[sdw01] passwd: all authentication tokens updated successfully.
[root@mdw .ssh]#



打通gpadmin用户所有节点SSH免密码



[gpadmin@mdw ~]$ vi .bashrc 
[gpadmin@mdw ~]$ source /usr/local/gpdb-5.0.0/greenplum_path.sh 
[gpadmin@mdw ~]$ gpssh-exkeys -f /home/gpadmin/all_hosts
[STEP 1 of 5] create local ID and authorize on local host

[STEP 2 of 5] keyscan all hosts and update known_hosts file

[STEP 3 of 5] authorize current user on remote hosts
  ... send to sdw01
  ***
  *** Enter password for sdw01: 
  ... send to sdw02

[STEP 4 of 5] determine common authentication file content

[STEP 5 of 5] copy authentication files to all remote hosts
  ... finished key exchange with sdw01
  ... finished key exchange with sdw02

[INFO] completed successfully



新建存储目录

[root@mdw ~]# mkdir /gpmaster
[root@mdw ~]# chown gpadmin:gpadmin -R /gpmaster/
[root@mdw ~]# 
[root@mdw ~]# source /usr/local/gpdb-5.0.0/greenplum_path.sh 
[root@mdw ~]#  gpssh -f /home/gpadmin/all_segs 'mkdir /gpdata'
[sdw02]
[sdw01]
[root@mdw ~]#  gpssh -f /home/gpadmin/all_segs 'chown gpadmin:gpadmin -R /gpdata'
[sdw02]
[sdw01]
[root@mdw ~]#

cd /usr/local
gtar -cvf /usr/local/gp.tar gpdb-*.*.*.*

[root@mdw local]# gpscp -f /home/gpadmin/all_segs /usr/local/gp.tar =:/usr/local
[root@mdw local]# 


[root@mdw local]# gpssh -f /home/gpadmin/all_segs
=> gtar --directory /usr/local -xvf /usr/local/gp.tar




=> chown -R gpadmin:gpadmin /usr/local/gpdb*
[sdw01]
[sdw02]
=> 



[gpadmin@mdw ~]$ more .bashrc 
# .bashrc

# Source global definitions
if [ -f /etc/bashrc ]; then
	. /etc/bashrc
fi


source /usr/local/gpdb-5.0.0/greenplum_path.sh 

# User specific aliases and functions

MASTER_DATA_DIRECTORY=/gpmaster
export MASTER_DATA_DIRECTORY
[gpadmin@mdw ~]$ 



对表：

[gpadmin@mdw ~]$ gpssh -f /home/gpadmin/all_hosts -v date
[WARN] Reference default values as $MASTER_DATA_DIRECTORY/gpssh.conf could not be found
Using delaybeforesend 0.05 and prompt_validation_timeout 1.0

[Reset ...]
[INFO] login sdw02
[INFO] login mdw
[INFO] login sdw01
[sdw02] Sat Mar 18 08:24:22 UTC 2017
[  mdw] Sat Mar 18 08:24:22 UTC 2017
[sdw01] Sat Mar 18 08:24:22 UTC 2017
[INFO] completed successfully

[Cleanup...]
[gpadmin@mdw ~]$ 


新建初始化配置文件：


cd /home/gpadmin

mkdir gpconfigs 


[gpadmin@mdw ~]$ cp /usr/local/gpdb-5.0.0/docs/cli_help/gpconfigs/gpinitsystem_config  gpconfigs/
[gpadmin@mdw ~]$ 

修改:.bashrc 
 

source /usr/local/gpdb-5.0.0/greenplum_path.sh
MASTER_DATA_DIRECTORY=/gpmaster50/gpseg-1
PGDATABASE=tank
PGPORT=5432 
export MASTER_DATA_DIRECTORY  PGDATABASE PGPORT 



vi /home/gpadmin/gpconfigs/gpinitsystem_config 


# FILE NAME: gpinitsystem_config

# Configuration file needed by the gpinitsystem

################################################
#### REQUIRED PARAMETERS
################################################

#### Name of this Greenplum system enclosed in quotes.
ARRAY_NAME="EMC Greenplum DW"

#### Naming convention for utility-generated data directories.
SEG_PREFIX=gpseg

#### Base number by which primary segment port numbers 
#### are calculated.
PORT_BASE=40000

#### File system location(s) where primary segment data directories 
#### will be created. The number of locations in the list dictate
#### the number of primary segments that will get created per
#### physical host (if multiple addresses for a host are listed in 
#### the hostfile, the number of segments will be spread evenly across
#### the specified interface addresses).
declare -a DATA_DIRECTORY=(/gpdata)

#### OS-configured hostname or IP address of the master host.
MASTER_HOSTNAME=mdw

#### File system location where the master data directory 
#### will be created.
MASTER_DIRECTORY=/gpmaster

#### Port number for the master instance. 
MASTER_PORT=5432

#### Shell utility used to connect to remote hosts.
TRUSTED_SHELL=ssh

#### Maximum log file segments between automatic WAL checkpoints.
CHECK_POINT_SEGMENTS=8

#### Default server-side character set encoding.
ENCODING=UNICODE


################################################
#### OTHER OPTIONAL PARAMETERS
################################################

#### Create a database of this name after initialization.
DATABASE_NAME=tank

#### Specify the location of the host address file here instead of
#### with the the -h option of gpinitsystem.
MACHINE_LIST_FILE=/home/gpadmin/all_segs






[gpadmin@mdw gpAdminLogs]$ gpinitsystem -c /home/gpadmin/gpconfigs/gpinitsystem_config 
20170319:10:32:47:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Checking configuration parameters, please wait...
20170319:10:32:47:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Reading Greenplum configuration file /home/gpadmin/gpconfigs/gpinitsystem_config
20170319:10:32:47:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Locale has not been set in /home/gpadmin/gpconfigs/gpinitsystem_config, will set to default value
20170319:10:32:47:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Locale set to en_US.utf8
20170319:10:32:47:006441 gpinitsystem:mdw:gpadmin-[INFO]:-MASTER_MAX_CONNECT not set, will set to default value 250
20170319:10:32:47:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Checking configuration parameters, Completed
20170319:10:32:47:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Commencing multi-home checks, please wait...
..
20170319:10:32:48:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Configuring build for standard array
20170319:10:32:48:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Commencing multi-home checks, Completed
20170319:10:32:48:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Building primary segment instance array, please wait...
..
20170319:10:32:49:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Checking Master host
20170319:10:32:49:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Checking new segment hosts, please wait...
..
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Checking new segment hosts, Completed
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Greenplum Database Creation Parameters
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:---------------------------------------
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Master Configuration
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:---------------------------------------
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Master instance name       = EMC Greenplum DW
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Master hostname            = mdw
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Master port                = 5432
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Master instance dir        = /gpmaster/gpseg-1
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Master LOCALE              = en_US.utf8
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Greenplum segment prefix   = gpseg
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Master Database            = tank
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Master connections         = 250
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Master buffers             = 128000kB
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Segment connections        = 750
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Segment buffers            = 128000kB
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Checkpoint segments        = 8
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Encoding                   = UNICODE
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Postgres param file        = Off
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Initdb to be used          = /usr/local/gpdb-5.0.0/bin/initdb
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-GP_LIBRARY_PATH is         = /usr/local/gpdb-5.0.0/lib
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Ulimit check               = Passed
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Array host connect type    = Single hostname per node
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Master IP address [1]      = ::1
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Master IP address [2]      = 10.218.94.67
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Master IP address [3]      = fe80::217:faff:fe00:9370
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Standby Master             = Not Configured
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Primary segment #          = 1
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Total Database segments    = 2
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Trusted shell              = ssh
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Number segment hosts       = 2
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Mirroring config           = OFF
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:----------------------------------------
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Greenplum Primary Segment Configuration
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:----------------------------------------
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-sdw01 	/gpdata/gpseg0 	40000 	2 	0
20170319:10:32:51:006441 gpinitsystem:mdw:gpadmin-[INFO]:-sdw02 	/gpdata/gpseg1 	40000 	3 	1

Continue with Greenplum creation Yy|Nn (default=N):
> y
20170319:10:32:53:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Building the Master instance database, please wait...
20170319:10:32:58:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Starting the Master in admin mode
20170319:10:33:08:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Commencing parallel build of primary segment instances
20170319:10:33:08:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Spawning parallel processes    batch [1], please wait...
..
20170319:10:33:08:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Waiting for parallel processes batch [1], please wait...
..............
20170319:10:33:22:006441 gpinitsystem:mdw:gpadmin-[INFO]:------------------------------------------------
20170319:10:33:22:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Parallel process exit status
20170319:10:33:22:006441 gpinitsystem:mdw:gpadmin-[INFO]:------------------------------------------------
20170319:10:33:22:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Total processes marked as completed           = 2
20170319:10:33:22:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Total processes marked as killed              = 0
20170319:10:33:22:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Total processes marked as failed              = 0
20170319:10:33:22:006441 gpinitsystem:mdw:gpadmin-[INFO]:------------------------------------------------
20170319:10:33:23:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Deleting distributed backout files
20170319:10:33:23:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Removing back out file
20170319:10:33:23:006441 gpinitsystem:mdw:gpadmin-[INFO]:-No errors generated from parallel processes
20170319:10:33:23:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Restarting the Greenplum instance in production mode
20170319:10:33:23:016871 gpstop:mdw:gpadmin-[INFO]:-Starting gpstop with args: -a -l /home/gpadmin/gpAdminLogs -i -m -d /gpmaster/gpseg-1
20170319:10:33:23:016871 gpstop:mdw:gpadmin-[INFO]:-Gathering information and validating the environment...
20170319:10:33:23:016871 gpstop:mdw:gpadmin-[INFO]:-Obtaining Greenplum Master catalog information
20170319:10:33:23:016871 gpstop:mdw:gpadmin-[INFO]:-Obtaining Segment details from master...
20170319:10:33:23:016871 gpstop:mdw:gpadmin-[INFO]:-Greenplum Version: 'postgres (Greenplum Database) 5.0.0-alpha.0+dev.212.gb533bcb build dev'
20170319:10:33:23:016871 gpstop:mdw:gpadmin-[INFO]:-There are 0 connections to the database
20170319:10:33:23:016871 gpstop:mdw:gpadmin-[INFO]:-Commencing Master instance shutdown with mode='immediate'
20170319:10:33:23:016871 gpstop:mdw:gpadmin-[INFO]:-Master host=mdw
20170319:10:33:23:016871 gpstop:mdw:gpadmin-[INFO]:-Commencing Master instance shutdown with mode=immediate
20170319:10:33:23:016871 gpstop:mdw:gpadmin-[INFO]:-Master segment instance directory=/gpmaster/gpseg-1
20170319:10:33:24:016871 gpstop:mdw:gpadmin-[INFO]:-Attempting forceful termination of any leftover master process
20170319:10:33:24:016871 gpstop:mdw:gpadmin-[INFO]:-Terminating processes for segment /gpmaster/gpseg-1
20170319:10:33:25:016958 gpstart:mdw:gpadmin-[INFO]:-Starting gpstart with args: -a -l /home/gpadmin/gpAdminLogs -d /gpmaster/gpseg-1
20170319:10:33:25:016958 gpstart:mdw:gpadmin-[INFO]:-Gathering information and validating the environment...
20170319:10:33:25:016958 gpstart:mdw:gpadmin-[INFO]:-Greenplum Binary Version: 'postgres (Greenplum Database) 5.0.0-alpha.0+dev.212.gb533bcb build dev'
20170319:10:33:25:016958 gpstart:mdw:gpadmin-[INFO]:-Greenplum Catalog Version: '301703131'
20170319:10:33:25:016958 gpstart:mdw:gpadmin-[INFO]:-Starting Master instance in admin mode
20170319:10:33:26:016958 gpstart:mdw:gpadmin-[INFO]:-Obtaining Greenplum Master catalog information
20170319:10:33:26:016958 gpstart:mdw:gpadmin-[INFO]:-Obtaining Segment details from master...
20170319:10:33:26:016958 gpstart:mdw:gpadmin-[INFO]:-Setting new master era
20170319:10:33:26:016958 gpstart:mdw:gpadmin-[INFO]:-Master Started...
20170319:10:33:26:016958 gpstart:mdw:gpadmin-[INFO]:-Shutting down master
20170319:10:33:27:016958 gpstart:mdw:gpadmin-[INFO]:-Commencing parallel segment instance startup, please wait...
.. 
20170319:10:33:29:016958 gpstart:mdw:gpadmin-[INFO]:-Process results...
20170319:10:33:29:016958 gpstart:mdw:gpadmin-[INFO]:-----------------------------------------------------
20170319:10:33:29:016958 gpstart:mdw:gpadmin-[INFO]:-   Successful segment starts                                            = 2
20170319:10:33:29:016958 gpstart:mdw:gpadmin-[INFO]:-   Failed segment starts                                                = 0
20170319:10:33:29:016958 gpstart:mdw:gpadmin-[INFO]:-   Skipped segment starts (segments are marked down in configuration)   = 0
20170319:10:33:29:016958 gpstart:mdw:gpadmin-[INFO]:-----------------------------------------------------
20170319:10:33:29:016958 gpstart:mdw:gpadmin-[INFO]:-
20170319:10:33:29:016958 gpstart:mdw:gpadmin-[INFO]:-Successfully started 2 of 2 segment instances 
20170319:10:33:29:016958 gpstart:mdw:gpadmin-[INFO]:-----------------------------------------------------
20170319:10:33:29:016958 gpstart:mdw:gpadmin-[INFO]:-Starting Master instance mdw directory /gpmaster/gpseg-1 
20170319:10:33:31:016958 gpstart:mdw:gpadmin-[INFO]:-Command pg_ctl reports Master mdw instance active
20170319:10:33:31:016958 gpstart:mdw:gpadmin-[INFO]:-No standby master configured.  skipping...
20170319:10:33:31:016958 gpstart:mdw:gpadmin-[INFO]:-Database successfully started
20170319:10:33:31:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Completed restart of Greenplum instance in production mode

20170319:10:33:57:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Scanning utility log file for any warning messages
20170319:10:33:57:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Log file scan check passed
20170319:10:33:57:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Greenplum Database instance successfully created
20170319:10:33:57:006441 gpinitsystem:mdw:gpadmin-[INFO]:-------------------------------------------------------
20170319:10:33:57:006441 gpinitsystem:mdw:gpadmin-[INFO]:-To complete the environment configuration, please 
20170319:10:33:57:006441 gpinitsystem:mdw:gpadmin-[INFO]:-update gpadmin .bashrc file with the following
20170319:10:33:57:006441 gpinitsystem:mdw:gpadmin-[INFO]:-1. Ensure that the greenplum_path.sh file is sourced
20170319:10:33:57:006441 gpinitsystem:mdw:gpadmin-[INFO]:-2. Add "export MASTER_DATA_DIRECTORY=/gpmaster/gpseg-1"
20170319:10:33:57:006441 gpinitsystem:mdw:gpadmin-[INFO]:-   to access the Greenplum scripts for this instance:
20170319:10:33:57:006441 gpinitsystem:mdw:gpadmin-[INFO]:-   or, use -d /gpmaster/gpseg-1 option for the Greenplum scripts
20170319:10:33:57:006441 gpinitsystem:mdw:gpadmin-[INFO]:-   Example gpstate -d /gpmaster/gpseg-1
20170319:10:33:57:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Script log file = /home/gpadmin/gpAdminLogs/gpinitsystem_20170319.log
20170319:10:33:57:006441 gpinitsystem:mdw:gpadmin-[INFO]:-To remove instance, run gpdeletesystem utility
20170319:10:33:57:006441 gpinitsystem:mdw:gpadmin-[INFO]:-To initialize a Standby Master Segment for this Greenplum instance
20170319:10:33:57:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Review options for gpinitstandby
20170319:10:33:58:006441 gpinitsystem:mdw:gpadmin-[INFO]:-------------------------------------------------------
20170319:10:33:58:006441 gpinitsystem:mdw:gpadmin-[INFO]:-The Master /gpmaster/gpseg-1/pg_hba.conf post gpinitsystem
20170319:10:33:58:006441 gpinitsystem:mdw:gpadmin-[INFO]:-has been configured to allow all hosts within this new
20170319:10:33:58:006441 gpinitsystem:mdw:gpadmin-[INFO]:-array to intercommunicate. Any hosts external to this
20170319:10:33:58:006441 gpinitsystem:mdw:gpadmin-[INFO]:-new array must be explicitly added to this file
20170319:10:33:58:006441 gpinitsystem:mdw:gpadmin-[INFO]:-Refer to the Greenplum Admin support guide which is
20170319:10:33:58:006441 gpinitsystem:mdw:gpadmin-[INFO]:-located in the /usr/local/gpdb-5.0.0/docs directory
20170319:10:33:58:006441 gpinitsystem:mdw:gpadmin-[INFO]:-------------------------------------------------------
[gpadmin@mdw gpAdminLogs]$ 
[gpadmin@mdw gpAdminLogs]$


[gpadmin@mdw gpAdminLogs]$ psql -d tank
psql (8.3.23)
Type "help" for help.

tank=# select version();
                                                                                               version                                                             
                                   
-------------------------------------------------------------------------------------------------------------------------------------------------------------------
-----------------------------------
 PostgreSQL 8.3.23 (Greenplum Database 5.0.0-alpha.0+dev.212.gb533bcb build dev) on x86_64-pc-linux-gnu, compiled by GCC gcc (GCC) 4.8.5 20150623 (Red Hat 4.8.5-11
) compiled on Mar 19 2017 10:21:20
(1 row)

tank=# 


[gpadmin@mdw gpAdminLogs]$ psql -d tank
psql (8.3.23)
Type "help" for help.

tank=# CREATE TABLE uname (id int,name char(20));
NOTICE:  Table doesn't have 'DISTRIBUTED BY' clause -- Using column named 'id' as the Greenplum Database data distribution key for this table.
HINT:  The 'DISTRIBUTED BY' clause determines the distribution of data. Make sure column(s) chosen are the optimal data distribution key to minimize skew.
CREATE TABLE
tank=# insert into uname values(generate_series(1,100000),'张国
tank'# 
tank'# ');;
ERROR:  invalid byte sequence for encoding "UTF8": 0xd5c5
HINT:  This error can also happen if the byte sequence does not match the encoding expected by the server, which is controlled by "client_encoding".
tank=# insert into uname values(generate_series(1,100000),'张国汉');
INSERT 0 100000
tank=# SELECT gp_segment_id, count(1) FROM uname GROUP BY gp_segment_id;
 gp_segment_id | count 
---------------+-------
             1 | 49999
             0 | 50001
(2 rows)

tank=# insert into uname values(generate_series(1,100000),'张国汉');
INSERT 0 100000
tank=# SELECT gp_segment_id, count(1) FROM uname GROUP BY gp_segment_id;
 gp_segment_id | count  
---------------+--------
             0 | 100002
             1 |  99998
(2 rows)







