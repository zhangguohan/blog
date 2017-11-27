
# 添加 Replication从节点模式下使用pgbackrest备份从库 




### 1、新建一个hot-standby节点

````
 wget https://ftp.postgresql.org/pub/source/v10.0/postgresql-10.0.tar.bz2
 tar -jxvf postgresql-10.0.tar.bz2 
 cd postgresql-10.0

 ./configure --prefix=/usr/local/pg10
 make world
 make world-install
 make install-world
 su - postgres
 /usr/local/pg10/bin/initdb -D /usr/local/pg10/data/ 
````

### 2、安装pgbackrest
````
$sudo wget -q -O - \
       https://github.com/pgbackrest/pgbackrest/archive/release/1.26.tar.gz | \
       sudo tar zx -C /root
       
sudo cp -r /root/pgbackrest-release-1.26/lib/pgBackRest \
       /usr/share/perl5
sudo find /usr/share/perl5/pgBackRest -type f -exec chmod 644 {} +
sudo find /usr/share/perl5/pgBackRest -type d -exec chmod 755 {} +
sudo cp /root/pgbackrest-release-1.26/bin/pgbackrest /usr/bin/pgbackrest
sudo chmod 755 /usr/bin/pgbackrest
sudo mkdir -m 770 /var/log/pgbackrest
sudo chown postgres:postgres /var/log/pgbackrest
sudo touch /etc/pgbackrest.conf
sudo chmod 640 /etc/pgbackrest.conf
sudo chown postgres:postgres /etc/pgbackrest.conf       

````

### 3、在db-primary  Build and Install C Library

````
sudo sh -c 'cd /root/pgbackrest-release-1.26/libc && \
       perl Makefile.PL INSTALLMAN1DIR=none INSTALLMAN3DIR=none'
sudo make -C /root/pgbackrest-release-1.26/libc test
sudo make -C /root/pgbackrest-release-1.26/libc install

````


#### 4、Setup Trusted SSH

##### 4.1 db-standby > Create db-standby host key pair
````
sudo -u postgres mkdir -m 750 -p /home/postgres/.ssh
sudo -u postgres ssh-keygen -f /home/postgres/.ssh/id_rsa -t rsa -b 4096 -N ""
````
##### 4.2 backup > Copy db-standby public key to backup
````
sudo ssh root@db-standby cat /home/postgres/.ssh/id_rsa.pub |  sudo -u backrest tee -a /home/backrest/.ssh/authorized_keys
````
##### 4.3 db-standby > Copy backup public key to db-standby
````
sudo ssh root@backup cat /home/backrest/.ssh/id_rsa.pub | sudo -u postgres tee -a /home/postgres/.ssh/authorized_keys
````
##### 4.4 backup >Test connection from backup to db-standby
````
sudo -u backrest ssh postgres@db-standby
````

##### 4.5 db-standby > Test connection from db-standby to backup
````
sudo -u postgres ssh backrest@backup
````


#### 5、开启从节点host-standby模式

##### 5.1、修改从节点/etc/pgbackrest.conf 
[root@sdw01 ~]# more /etc/pgbackrest.conf 
[demo]
db1-host=db-primary
db1-path=/usr/local/pg10/data
recovery-option=standby_mode=on

[global]
backup-host=backup

`````

#### 5.2从备份节点生成一个standby从节点
````
sudo -u postgres pgbackrest --stanza=demo --delta restore
sudo -u postgres cat /usr/local/pg10/data/recovery.conf
standby_mode = 'on'
restore_command = '/usr/bin/pgbackrest --stanza=demo archive-get %f "%p"'

````

##### 5.3 db-standby postgresql.conf  Enable hot_standby
````
hot_standby = on
log_filename = 'postgresql.log'
log_line_prefix = ''
````

##### 5.4 启动db-standby 
````
# /etc/init.d/postgresql start

````
