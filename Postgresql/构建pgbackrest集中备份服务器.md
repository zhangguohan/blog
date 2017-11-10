## 构建Pgbackrest集中备份服务器


### 一、配置集中备份服务器

#### 1.1 备机安装备份服务器软件依赖包
````
  yum -y install perl-DBD-Pg perl-JSON perl-Thread-Queue  perl-JSON-PP.noarch perl-Digest-SHA.x86_64
````

#### 1.2 备机新建一个备份用户
````
  useradd backrest
  passwd backrest
````

#### 1.3 备机安装备份软件
```` 
sudo wget -q -O -  https://github.com/pgbackrest/pgbackrest/archive/release/1.25.tar.gz   sudo tar zx -C /root

cp -r /root/pgbackrest-release-1.25/lib/pgBackRest        /usr/share/perl5

sudo find /usr/share/perl5/pgBackRest -type f -exec chmod 644 {} +

sudo find /usr/share/perl5/pgBackRest -type d -exec chmod 755 {} +

sudo cp /root/pgbackrest-release-1.25/bin/pgbackrest /usr/bin/pgbackrest

sudo chmod 755 /usr/bin/pgbackrest

sudo mkdir -m 770 /var/log/pgbackrest

sudo chown backrest:backrest /var/log/pgbackrest

sudo touch /etc/pgbackrest.conf

sudo chmod 640 /etc/pgbackrest.conf

sudo chown backrest:backrest /etc/pgbackrest.conf

sudo mkdir /var/lib/pgbackrest

sudo chmod 750 /var/lib/pgbackrest

sudo chown backrest:backrest /var/lib/pgbackrest

````
#### 1.4 备机配置SSH生成密钥
````
sudo -u backrest mkdir -m 750 /home/backrest/.ssh
sudo -u backrest ssh-keygen -f /home/backrest/.ssh/id_rsa -t rsa -b 4096 -N ""
````
#### 1.5 备机 添加主节点hostsname
````
  vi /etc/hosts

  ping db-primary

  sudo ssh root@db-primary cat /home/postgres/.ssh/id_rsa.pub |   sudo -u backrest tee -a /home/backrest/.ssh/authorized_keys  // 将主节点公钥复制到备节点

  sudo -u backrest ssh postgres@db-primary //测试连接主节点

````

#### 1.6 主节点复制备节点公钥到主节点
````
    sudo ssh root@backup cat /home/backrest/.ssh/id_rsa.pub | sudo -u postgres tee -a /home/postgres/.ssh/authorized_keys // 将备节点公钥复制到主节点

    sudo -u postgres ssh backrest@backup //测试连接备节点
````


#### 1.7 配置备节点pgbackrest配置文件
````
[root@sdw01 ~]#  more /etc/pgbackrest.conf 
[demo]
db1-host=db-primary
db1-path=/usr/local/pg10/data
db1-user=postgres

[global]
repo-path=/var/lib/pgbackrest
retention-full=2
start-fast=y
[root@sdw01 ~]# 

````

#### 1.8 配置主节点pgbackrest配置文件

````
[root@mdw ~]# more /etc/pgbackrest.conf 
[demo]
db-path=/usr/local/pg10/data
[global]
backup-host=backup
backup-user=backrest
log-level-file=detail
[root@mdw ~]#

````

#### 1.9 备份节点生成stanza
````
[root@sdw01 ~]#    sudo -u backrest pgbackrest --stanza=demo --db-socket-path=/tmp   --log-level-console=info  stanza-create
2017-11-10 03:30:01.791 P00   INFO: stanza-create command begin 1.25: --db1-host=db-primary --db1-path=/usr/local/pg10/data --db1-socket-path=/tmp --db1-user=postgres --log-level-console=info --repo-path=/var/lib/pgbackrest --stanza=demo
2017-11-10 03:30:04.275 P00   INFO: stanza-create command end: completed successfully
[root@sdw01 ~]#
````  

#### 1.10 备份节点检查stanza状态
````
[root@sdw01 ~]#  sudo -u backrest pgbackrest --stanza=demo --db-socket-path=/tmp --log-level-console=info  check
2017-11-10 03:31:41.410 P00   INFO: check command begin 1.25: --db1-host=db-primary --db1-path=/usr/local/pg10/data --db1-socket-path=/tmp --db1-user=postgres --log-level-console=info --repo-path=/var/lib/pgbackrest --stanza=demo
2017-11-10 03:31:45.980 P00   INFO: WAL segment 000000060000000000000027 successfully stored in the archive at '/var/lib/pgbackrest/archive/demo/10-1/0000000600000000/000000060000000000000027-ec11aa4b133b216e1e4d080207ff6b8c633c58eb.gz'
2017-11-10 03:31:46.014 P00   INFO: check command end: completed successfully
[root@sdw01 ~]#

````

### 二、备份节点远程备份还原测试 

#### 2.1 备份节点执行全备
````
[root@sdw01 ~]# sudo -u backrest pgbackrest --stanza=demo --db-socket-path=/tmp --log-level-console=info   --type=full backup
2017-11-10 03:33:38.586 P00   INFO: backup command begin 1.25: --db1-host=db-primary --db1-path=/usr/local/pg10/data --db1-socket-path=/tmp --db1-user=postgres --log-level-console=info --repo-path=/var/lib/pgbackrest --retention-full=2 --stanza=demo --start-fast --type=full
2017-11-10 03:33:40.839 P00   INFO: execute non-exclusive pg_start_backup() with label "pgBackRest backup started at 2017-11-10 03:33:38": backup begins after the requested immediate checkpoint completes
2017-11-10 03:33:41.457 P00   INFO: backup start archive = 000000060000000000000029, lsn = 0/29000028

.....
2017-11-10 03:38:01.028 P00   INFO: full backup size = 32.3MB
2017-11-10 03:38:01.028 P00   INFO: execute non-exclusive pg_stop_backup() and wait for all WAL segments to archive
2017-11-10 03:38:01.745 P00   INFO: backup stop archive = 000000060000000000000029, lsn = 0/29000130
2017-11-10 03:38:10.992 P00   INFO: new backup label = 20171110-033338F
2017-11-10 03:38:16.270 P00   INFO: backup command end: completed successfully
2017-11-10 03:38:16.271 P00   INFO: expire command begin 1.25: --db1-host=db-primary --log-level-console=info --repo-path=/var/lib/pgbackrest --retention-archive=2 --retention-full=2 --stanza=demo
2017-11-10 03:38:16.287 P00   INFO: expire full backup set: 20171106-081828F, 20171106-081828F_20171106-172152I
2017-11-10 03:38:19.815 P00   INFO: remove expired backup 20171106-081828F_20171106-172152I
2017-11-10 03:38:19.838 P00   INFO: remove expired backup 20171106-081828F
2017-11-10 03:38:20.475 P00   INFO: expire command end: completed successfully
[root@sdw01 ~]# 

````

#### 2.3 在备节点检查上备份结果集

[root@sdw01 ~]# sudo -u backrest pgbackrest info

````
stanza: demo
    status: ok

    db (current)
        wal archive min/max (10-1): 000000010000000000000007 / 000000060000000000000029

        full backup: 20171109-210931F
            timestamp start/stop: 2017-11-09 21:09:31 / 2017-11-09 21:14:00
            wal start/stop: 000000050000000000000014 / 000000050000000000000016
            database size: 32.3MB, backup size: 32.3MB
            repository size: 3.7MB, repository backup size: 3.7MB

        incr backup: 20171109-210931F_20171110-004318I
            timestamp start/stop: 2017-11-10 00:43:18 / 2017-11-10 00:43:31
            wal start/stop: 000000060000000000000025 / 000000060000000000000025
            database size: 32.3MB, backup size: 24.2KB
            repository size: 3.7MB, repository backup size: 566B
            backup reference list: 20171109-210931F

        full backup: 20171110-033338F
            timestamp start/stop: 2017-11-10 03:33:38 / 2017-11-10 03:38:05
            wal start/stop: 000000060000000000000029 / 000000060000000000000029
            database size: 32.3MB, backup size: 32.3MB
            repository size: 3.7MB, repository backup size: 3.7MB
[root@sdw01 ~]# 
````


#### 2.4模拟主节点故障清理data数据库目录。
````
[postgres@mdw ~]$ /usr/local/pg10/bin/psql 
psql (10.0)
Type "help" for help.

postgres=# \c ghan
You are now connected to database "ghan" as user "postgres".
ghan=# select count(*) from t; 
 count 
-------
 58000
(1 row)

ghan=# 
ghan=# \q
[postgres@mdw ~]$ cd /usr/local/pg10/data
[postgres@mdw data]$ ll
total 68
-rw------- 1 postgres postgres   231 Nov  9 21:13 backup_label.old
drwx------ 6 postgres postgres    54 Nov 10 01:17 base
drwx------ 2 postgres postgres  4096 Nov 10 04:30 global
drwx------ 2 postgres postgres     6 Nov 10 01:17 pg_commit_ts
drwx------ 2 postgres postgres     6 Nov 10 01:17 pg_dynshmem
-rwx------ 1 postgres postgres  4649 Nov  5 22:22 pg_hba.conf
-rwx------ 1 postgres postgres  1636 Nov  5 07:23 pg_ident.conf
drwx------ 4 postgres postgres    68 Nov 10 04:30 pg_logical
drwx------ 4 postgres postgres    36 Nov 10 01:17 pg_multixact
drwx------ 2 postgres postgres    18 Nov 10 04:30 pg_notify
drwx------ 2 postgres postgres     6 Nov 10 01:17 pg_replslot
drwx------ 2 postgres postgres     6 Nov 10 01:17 pg_serial
drwx------ 2 postgres postgres     6 Nov 10 01:17 pg_snapshots
drwx------ 2 postgres postgres     6 Nov 10 04:30 pg_stat
drwx------ 2 postgres postgres    84 Nov 10 04:31 pg_stat_tmp
drwx------ 2 postgres postgres    18 Nov 10 01:19 pg_subtrans
drwx------ 2 postgres postgres     6 Nov 10 01:17 pg_tblspc
drwx------ 2 postgres postgres     6 Nov 10 01:17 pg_twophase
-rwx------ 1 postgres postgres     3 Nov  5 07:23 PG_VERSION
drwx------ 3 postgres postgres  4096 Nov 10 04:30 pg_wal
drwx------ 2 postgres postgres    18 Nov 10 01:17 pg_xact
-rwx------ 1 postgres postgres    88 Nov  5 07:23 postgresql.auto.conf
-rwx------ 1 postgres postgres 22794 Nov  5 22:34 postgresql.conf
-rw------- 1 postgres postgres    57 Nov 10 04:30 postmaster.opts
-rw------- 1 postgres postgres    79 Nov 10 04:30 postmaster.pid
-rw-rw-rw- 1 postgres postgres    99 Nov 10 01:17 recovery.done
[postgres@mdw data]$ rm -rf *
[postgres@mdw data]$ ll
total 0
[postgres@mdw data]$ 
````

#### 2.5 在主节点执行还原命令

````

[root@mdw data]# sudo -u postgres pgbackrest --stanza=demo --delta --log-level-console=info   restore

2017-11-10 04:34:39.380 P01   INFO: restore file /usr/local/pg10/data/base/1/13064 (0B, 100%)
2017-11-10 04:34:39.400 P01   INFO: restore file /usr/local/pg10/data/base/1/13059 (0B, 100%)
2017-11-10 04:34:39.425 P01   INFO: restore file /usr/local/pg10/data/base/1/13054 (0B, 100%)
2017-11-10 04:34:39.493 P01   INFO: restore file /usr/local/pg10/data/base/1/13049 (0B, 100%)
2017-11-10 04:34:39.501 P00   INFO: write /usr/local/pg10/data/recovery.conf
2017-11-10 04:34:40.137 P00   INFO: restore global/pg_control (performed last to ensure aborted restores cannot be started)
2017-11-10 04:34:41.180 P00   INFO: restore command end: completed successfully

````


#### 2.6 启动主节点Postgresql 进行归档日志还原

````
[postgres@mdw ~]$ ./startpg10.sh 
waiting for server to start........ done
server started
[postgres@mdw ~]$ ps -ef |grep ^post
postfix   1698  1554  0 Nov04 ?        00:00:01 qmgr -l -t unix -u
postfix  13544  1554  0 03:10 ?        00:00:00 pickup -l -t unix -u
postgres 14805 14804  0 04:35 pts/0    00:00:00 -bash
postgres 14833     1  0 04:36 pts/0    00:00:00 /usr/local/pg10/bin/postgres -D /usr/local/pg10/data
postgres 14834 14833  0 04:36 ?        00:00:00 postgres: startup process   recovering 00000006000000000000002A
postgres 14839 14833  0 04:36 ?        00:00:00 postgres: checkpointer process   
postgres 14840 14833  0 04:36 ?        00:00:00 postgres: writer process   
postgres 14842 14833  0 04:36 ?        00:00:00 postgres: stats collector process   
postgres 14848 14834 39 04:36 ?        00:00:00 /usr/bin/perl /usr/bin/pgbackrest --log-level-console=info --stanza=demo archive-get 00000007.history pg_wal/RECOVERYHISTORY
postgres 14849 14848  4 04:36 ?        00:00:00 ssh -o LogLevel=error -o Compression=no -o PasswordAuthentication=no backrest@backup /usr/bin/pgbackrest --buffer-size=4194304 --command=archive-get --compress-level=6 --compress-level-network=3 --db1-path=/usr/local/pg10/data --protocol-timeout=1830 --stanza=demo --type=backup remote
postgres 14850 14805  0 04:36 pts/0    00:00:00 ps -ef
postgres 14851 14805  0 04:36 pts/0    00:00:00 grep --color=auto ^post
[postgres@mdw ~]$ 

等归档日志应用完，数据库正常打开数据恢复正常

[postgres@mdw ~]$ /usr/local/pg10/bin/psql 
psql (10.0)
Type "help" for help.

postgres=# \c ghan
You are now connected to database "ghan" as user "postgres".
ghan=# select count(*) from t; 
 count 
-------
 58000
(1 row)

ghan=#
```` 