
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
````
##### 5.1、修改从节点/etc/pgbackrest.conf 
[root@sdw01 ~]# more /etc/pgbackrest.conf 
[demo]
db1-host=db-primary
db1-path=/usr/local/pg10/data
recovery-option=standby_mode=on

[global]
backup-host=backup

````

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


#### 6、Streaming Replication


##### 6.1 db-primary -->Create replication user

````

sudo -u postgres psql -c " create user replicator password 'jw8s0F4' replication";

````


##### 6.2 db-primary --> Create pg_hba.conf entry for replication user

````
host    replication     all             108.88.3.0/24           md5
host    replication     all             10.0.0.0/24           md5

sudo  /etc/init.d/postgresql restart // 重启生效


````


##### 6.3  db-standby:/etc/pgbackrest.conf -->Set primary_conninfo
````
[demo]
db-path=/usr/local/pg10/data
recovery-option=standby_mode=on
recovery-option=primary_conninfo=host=10.0.0.1 port=5432 user=replicator password=jw8s0F4

[global]
backup-host=backup
````

##### 6.4 db-standby -> Stop PostgreSQL and restore the demo standby cluster
````
#sudo /etc/init.d/postgresql stop

#sudo -u postgres pgbackrest --stanza=demo --delta restore

[root@sdw02 ~]# cat /usr/local/pg10/data/recovery.conf 
primary_conninfo = 'host=10.0.0.1 port=5432 user=replicator password=jw8s0F4'
standby_mode = 'on'
restore_command = '/usr/bin/pgbackrest --stanza=demo archive-get %f "%p"'

````
#### 6.5 db-standby -> Start PostgreSQL
````
#sudo /etc/init.d/postgresql start
````

#### 6.6 Test explamper
````
db-primary --> Create a new table on the primary
sudo -u postgres psql -c " \
       begin; \
       create table stream_table (message text); \
       insert into stream_table values ('Important Data'); \
       commit; \
       select *, current_timestamp from stream_table";
    message     |              now              
----------------+-------------------------------
 Important Data | 2017-11-21 23:20:54.465412+00
(1 row)
db-standby -> Query table on the standby
sudo -u postgres psql -c " \
       select *, current_timestamp from stream_table"
    message     |              now              
----------------+-------------------------------
 Important Data | 2017-11-21 23:20:54.707766+00
 
 
 ````
 
 ### 7、Backup from a Standby
 
 
 #### 7.1 backup-node  --> /etc/pgbackrest.conf
 [root@sdw01 ~]# more /etc/pgbackrest.conf 
[demo]
db1-host=db-primary
db1-path=/usr/local/pg10/data
db1-user=postgres

db2-host=db-standby
db2-path=/usr/local/pg10/data
db2-user=postgres

[global]
repo-path=/var/lib/pgbackrest
retention-full=2
start-fast=y
[root@sdw01 ~]# 

#### 7.2 backup -> Backup the demo cluster from db-standby
````
[root@sdw01 ~]# sudo -u backrest pgbackrest --stanza=demo --log-level-console=detail backup
2017-11-27 10:44:35.529 P00   INFO: backup command begin 1.25: --db1-host=db-primary --db1-path=/usr/local/pg10/data --db1-user=postgres --db2-host=db-standby --db2-path=/usr/local/pg10/data --db2-user=postgres --log-level-console=detail --repo-path=/var/lib/pgbackrest --retention-full=2 --stanza=demo --start-fast
2017-11-27 10:44:39.142 P00   INFO: last backup label = 20171127-050238F_20171127-104304I, version = 1.25
2017-11-27 10:44:39.616 P00   INFO: execute non-exclusive pg_start_backup() with label "pgBackRest backup started at 2017-11-27 10:44:35": backup begins after the requested immediate checkpoint completes
2017-11-27 10:44:40.438 P00   INFO: backup start archive = 000000080000000000000050, lsn = 0/50000028
2017-11-27 10:44:44.342 P01   INFO: backup file db-primary:/usr/local/pg10/data/base/16386/16387 (2.4MB, 99%) checksum b0e2924509d189f986bfab22c1a762ce203bce95
2017-11-27 10:44:44.507 P01   INFO: backup file db-primary:/usr/local/pg10/data/pg_xact/0000 (8KB, 99%) checksum 6ec018edfe9c646c4e5eb8c738ebe409e03c9eaa
2017-11-27 10:44:44.626 P01   INFO: backup file db-primary:/usr/local/pg10/data/global/pg_control (8KB, 99%) checksum b47c4bc8b162a72258e0481dc25a0da0c3a6be9a
2017-11-27 10:44:44.778 P01   INFO: backup file db-primary:/usr/local/pg10/data/pg_logical/replorigin_checkpoint (8B, 100%) checksum 347fc8f2df71bd4436e38bd1516ccd7ea0d46532
2017-11-27 10:44:44.861 P00   INFO: incr backup size = 2.4MB
2017-11-27 10:44:44.862 P00   INFO: execute non-exclusive pg_stop_backup() and wait for all WAL segments to archive
2017-11-27 10:44:45.276 P00   INFO: backup stop archive = 000000080000000000000050, lsn = 0/500000F8
2017-11-27 10:44:45.414 P00 DETAIL: wrote 'pg_data/backup_label' file returned from pg_stop_backup()
2017-11-27 10:44:50.025 P00   INFO: new backup label = 20171127-050238F_20171127-104435I
2017-11-27 10:44:53.642 P00   INFO: backup command end: completed successfully
2017-11-27 10:44:53.644 P00   INFO: expire command begin 1.25: --db1-host=db-primary --db2-host=db-standby --log-level-console=detail --repo-path=/var/lib/pgbackrest --retention-archive=2 --retention-full=2 --stanza=demo
2017-11-27 10:44:53.674 P00   INFO: expire command end: completed successfully
[root@sdw01 ~]# 

pgBackRest 将自动选择从节点进行备份操作

````

##### 参考官方链接：http://pgbackrest.org/user-guide.html 

