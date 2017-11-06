 
## pgbackrest备份工具备份还原本地测试


### 一、安装依赖包
````
[root@test ~]# yum -y install perl-DBD-Pg perl-JSON perl-Thread-Queue  perl-JSON-PP.noarch perl-Digest-SHA.x86_64

[root@test ~]# sudo wget -q -O -  https://github.com/pgbackrest/pgbackrest/archive/release/1.25.tar.gz   sudo tar zx -C /root


[root@test ~]# sudo cp -r /root/pgbackrest-release-1.25/lib/pgBackRest /usr/share/perl5/
[root@test ~]# sudo find /usr/share/perl5/pgBackRest -type f -exec chmod 644 {} +
[root@test ~]#  find /usr/share/perl5/pgBackRest -type d -exec chmod 755 {} +
[root@test ~]#  cp /root/pgbackrest-release-1.25/bin/pgbackrest /usr/bin/pgbackrest
[root@test ~]# sudo chmod 755 /usr/bin/pgbackrest
[root@test ~]# sudo mkdir -m 770 /var/log/pgbackrest
[root@test ~]# sudo chown postgres:postgres /var/log/pgbackrest
[root@test ~]# sudo touch /etc/pgbackrest.conf
[root@test ~]# sudo chmod 640 /etc/pgbackrest.conf
[root@test ~]# sudo chown postgres:postgres /etc/pgbackrest.conf

````




### 二、测试安装结果
````
[root@test ~]# sudo -u postgres pgbackrest
pgBackRest 1.25 - General help

Usage:
    pgbackrest [options] [command]

Commands:
    archive-get     Get a WAL segment from the archive.
    archive-push    Push a WAL segment to the archive.
    backup          Backup a database cluster.
    check           Check the configuration.
    expire          Expire backups that exceed retention.
    help            Get help.
    info            Retrieve information about backups.
    restore         Restore a database cluster.
    stanza-create   Create the required stanza data.
    stanza-upgrade  Upgrade a stanza.
    start           Allow pgBackRest processes to run.
    stop            Stop pgBackRest processes from running.
    version         Get version.

Use 'pgbackrest help [command]' for more information.
[root@test ~]# 

````
### 三、配置/etc/pgbackrest.conf

````
[demo]
db-path=/usr/local/pg10/data
db-user=postgres
[global]
repo-path=/var/lib/pgbackrest   //备仓库保存日录
retention-full=2
start-fast=y
[root@mdw tmp]# 

````

### 四、Postgresql数据库端修改置配如下

#### 4.1 修改postgresql.conf

````
[root@mdw data]# grep -Ev "^$|^[#;]" postgresql.conf

listen_addresses = '*'		# what IP address(es) to listen on;
				
port = 5432				# (change requires restart)
max_connections = 100			# (change requires restart)


wal_level = replica	

archive_mode = on		
archive_command = 'pgbackrest --stanza=demo archive-push %p'	
				
max_wal_senders = 10	

log_line_prefix = ''		# special values:

````



#### 4.2 修改postgresql.conf

````
[root@mdw data]# grep -Ev "^$|^[#;]" pg_hba.conf 
local   all             all                                     trust
host    all             all             127.0.0.1/32            trust
host    all             all             108.88.3.0/24           md5
host    all             all             ::1/128                 trust
local   replication     all                                     trust
host    replication     all             127.0.0.1/32            trust
host    replication     all             108.88.3.0/24           md5
host    replication     all             ::1/128                 trust
[root@mdw data]# 


````



### 五、初始化pgbackrest环境配置
#### 5.1生成stanza
````
[root@mdw lib]# sudo -u postgres pgbackrest --stanza=demo --db-socket-path=/tmp  --log-level-console=info stanza-create
2017-11-05 22:43:51.064 P00   INFO: stanza-create command begin 1.25: --db1-path=/usr/local/pg10/data --db1-socket-path=/tmp --log-level-console=info --repo-path=/var/lib/pgbackrest --stanza=demo
2017-11-05 22:43:51.622 P00   INFO: stanza-create command end: completed successfully
[root@mdw lib]# 


````
#### 5.2检查stanza是否已经成功生成

````

[root@mdw lib]# sudo -u postgres pgbackrest --stanza=demo --db-socket-path=/tmp --log-level-console=info check
2017-11-05 22:44:23.881 P00   INFO: check command begin 1.25: --db1-path=/usr/local/pg10/data --db1-socket-path=/tmp --log-level-console=info --repo-path=/var/lib/pgbackrest --stanza=demo
2017-11-05 22:44:27.711 P00   INFO: WAL segment 000000010000000000000001 successfully stored in the archive at '/var/lib/pgbackrest/archive/demo/10-1/0000000100000000/000000010000000000000001-660ce3882a753dd5cafe79132a8319dbd2ea5cfc.gz'
2017-11-05 22:44:27.713 P00   INFO: check command end: completed successfully
````

#### 5.3生成备份
````
[root@mdw lib]# 
# sudo -u postgres pgbackrest --stanza=demo --db-socket-path=/tmp --log-level-console=info backup 生成全备

2017-11-05 22:59:25.725 P01   INFO: backup file /usr/local/pg10/data/base/1/13054 (0B, 100%)
2017-11-05 22:59:25.758 P01   INFO: backup file /usr/local/pg10/data/base/1/13049 (0B, 100%)
2017-11-05 22:59:25.851 P00   INFO: full backup size = 22.5MB
2017-11-05 22:59:25.852 P00   INFO: execute non-exclusive pg_stop_backup() and wait for all WAL segments to archive
2017-11-05 22:59:26.263 P00   INFO: backup stop archive = 000000010000000000000003, lsn = 0/3000168
2017-11-05 22:59:29.951 P00   INFO: new backup label = 20171105-225904F
2017-11-05 22:59:31.604 P00   INFO: backup command end: completed successfully
2017-11-05 22:59:31.607 P00   INFO: expire command begin 1.25: --log-level-console=info --repo-path=/var/lib/pgbackrest --retention-archive=2 --retention-full=2 --stanza=demo
2017-11-05 22:59:31.657 P00   INFO: expire command end: completed successfully
````

#### 5.4生成测试数据库及数据

```
[postgres@mdw ~]$ /usr/local/pg10/bin/createdb  ghan
[postgres@mdw ~]$ /usr/local/pg10/bin/psql  ghan
psql (10.0)
Type "help" for help.

ghan=# create table t(id int, name varchar(30));
CREATE TABLE
ghan=# insert into t  values(generate_series(1,7000),'张国汉');
INSERT 0 7000
ghan=# insert into t  values(generate_series(1,17000),'张国汉');
INSERT 0 17000
ghan=# 

```

#### 5.5执行差异备份测试
```
 #sudo -u postgres pgbackrest --stanza=demo --db-socket-path=/tmp --type=diff  --log-level-console=info backup 差异备份

   2017-11-05 23:13:28.975 P00   INFO: diff backup size = 8.4MB
 2017-11-05 23:13:28.976 P00   INFO: execute non-exclusive pg_stop_backup() and wait for all WAL segments to archive
 2017-11-05 23:13:29.685 P00   INFO: backup stop archive = 000000010000000000000005, lsn = 0/50000F8
 2017-11-05 23:13:31.868 P00   INFO: new backup label = 20171105-225904F_20171105-231318D
 2017-11-05 23:13:33.493 P00   INFO: backup command end: completed successfully
 2017-11-05 23:13:33.495 P00   INFO: expire command begin 1.25: --log-level-console=info --repo-path=/var/lib/pgbackrest --retention-archive=2 --retention-full=2 --stanza=demo
 2017-11-05 23:13:33.517 P00   INFO: expire command end: completed successfully


```

#### 5.6查看备份集

[root@mdw ~]# sudo -u postgres pgbackrest info
````
stanza: demo
    status: ok

    db (current)
        wal archive min/max (10-1): 000000010000000000000001 / 000000010000000000000005

        full backup: 20171105-225904F
            timestamp start/stop: 2017-11-05 22:59:04 / 2017-11-05 22:59:28
            wal start/stop: 000000010000000000000003 / 000000010000000000000003
            database size: 22.5MB, backup size: 22.5MB
            repository size: 2.6MB, repository backup size: 2.6MB

        diff backup: 20171105-225904F_20171105-231318D
            timestamp start/stop: 2017-11-05 23:13:18 / 2017-11-05 23:13:30
            wal start/stop: 000000010000000000000005 / 000000010000000000000005
            database size: 30.8MB, backup size: 8.4MB
            repository size: 3.5MB, repository backup size: 989.3KB
            backup reference list: 20171105-225904F
[root@mdw ~]# 
````





### 六、指定还原数据库测试

```
[postgres@test ~]$ /usr/local/pg10/bin/psql  -c "create table test1_table (id int); \
       insert into test1_table (id) values (1);" test1
INSERT 0 1
[postgres@test ~]$ /usr/local/pg10/bin/psql  -c "create table test2_table (id int); \
       insert into test2_table (id) values (1);" test2
INSERT 0 1


postgres=# SELECT oid from pg_database where datname='test1';
  oid  
-------
 16733
(1 row)

postgres=# SELECT oid from pg_database where datname='test2';
  oid  
-------
 16734
(1 row)


[root@test ~]# /etc/init.d/pg10 stop
Stopping PostgreSQL: ok



[postgres@test base]$ du -sh 16734/
7.5M	16734/
[postgres@test base]$ rm -rf 16734/

[postgres@test base]$ pgbackrest --stanza=demo  --delta    --db-include=test2 restore    //虽然只指向了test2数据库，默认会还原postgres\ template0  template1这3个系统库
[postgres@test base]$ ll
total 68
drwx------ 2 postgres postgres  4096 Nov  1 22:27 1
drwx------ 2 postgres postgres  4096 Nov  1 22:27 13210
drwx------ 2 postgres postgres 12288 Nov  2 16:17 13211
drwx------ 2 postgres postgres 12288 Nov  2 16:17 16578
drwx------ 2 postgres postgres 12288 Nov  2 16:18 16594
drwx------ 2 postgres postgres 12288 Nov  2 16:18 16733
drwx------ 2 postgres postgres 12288 Nov  2 16:18 16734
[postgres@test base]$ du -sh 16734/
7.4M	16734/


[root@test ~]# /etc/init.d/pg10 start
Starting PostgreSQL: ok


[postgres@test base]$ /usr/local/pg10/bin/psql -c "select * from test2_table;" test2
 id 
----
  1
(1 row)

[postgres@test base]$ /usr/local/pg10/bin/psql -c "select * from test1_table;" test1            //由于只还原了test2数据库，所以其它test1数据库无法使用
psql: FATAL:  relation mapping file "base/16733/pg_filenode.map" contains invalid data
[postgres@test base]$

`````



### 七、基于时间点恢复测试
````
[postgres@test ~]$ pgbackrest --stanza=demo --db-socket-path=/tmp --type=diff backup
[postgres@test ~]$ /usr/local/pg10/bin/psql -c "begin; \
>        create table important_table (message text); \
>        insert into important_table values ('Important Data'); \
>        commit; \
>        select * from important_table;"
    message     
----------------
 Important Data
(1 row)

[postgres@test ~]$ /usr/local/pg10/bin/psql -Atc "select current_timestamp"
2017-11-02 23:05:00.009758+08

[postgres@test ~]$ /usr/local/pg10/bin/psql -c -c "begin; \
>        drop table important_table; \
>        commit; \
>        select * from important_table;"
psql: FATAL:  database "begin;        drop table important_table;        commit;       " does not exist
[postgres@test ~]$ 
2017-11-02 23:05:00.009758+08
[postgres@test ~]$ 

[root@test ~]# /etc/init.d/pg10 stop
Stopping PostgreSQL: ok
[root@test ~]# su - postgres
Last login: Thu Nov  2 23:07:11 CST 2017 on pts/0
[postgres@test ~]$ pgbackrest --stanza=demo  --delta --type=time "--target=2017-11-02 23:05:00.009758+08" restore
[postgres@test ~]$ cat /data/pgdata/recovery.conf 
restore_command = '/usr/bin/pgbackrest --stanza=demo archive-get %f "%p"'
recovery_target_time = '2017-11-02 23:05:00.009758+08'  //指定还原时间段
[postgres@test ~]$ 

[root@test ~]# /etc/init.d/pg10 start
Starting PostgreSQL: ok
[root@test ~]# su - postgres
Last login: Thu Nov  2 23:10:41 CST 2017 on pts/0
[postgres@test ~]$ /usr/local/pg10/bin/psql  -c "select * from important_table"
    message     
----------------
 Important Data
(1 row)

[postgres@test ~]$


`````

### 八、还原到指定备份集时间段

#### 8.1，现在再次执行操作删除表数据

````
 [postgres@test ~]$ psql -c "begin; \
>        drop table important_table; \
>        commit; \
>        select * from important_table;"
ERROR:  relation "important_table" does not exist
LINE 1: ...ortant_table;        commit;        select * from important_...
                                                             ^
[postgres@test ~]$ psql  -c "select * from important_table"
ERROR:  relation "important_table" does not exist
LINE 1: select * from important_table
                      ^
[postgres@test ~]$ 
````

#### 8.2、执行一次增量备份

````
[postgres@test ~]$  pgbackrest --stanza=demo--db-socket-path=/tmp  --type=incr backup

````

#### 8.3、还原到之前指定时间点测试
````
[root@test ~]# /etc/init.d/pg10 stop
Stopping PostgreSQL: ok
[root@test ~]# 

[postgres@test ~]$ pgbackrest --stanza=demo  --type=time "--target=2017-11-02 23:05:00.009758+08" restore

[root@test ~]# /etc/init.d/pg10 start
Starting PostgreSQL: ok
[root@test ~]#

 
[postgres@test ~]$ psql  -c "select * from important_table";
ERROR:  relation "important_table" does not exist
LINE 1: select * from important_table
                      ^
[postgres@test ~]$               //由于之前已经做了一次增量备份，而在指定还原时间段不在增量备份集中，故只能还原在最后一个备份集中。该表无法还原。


LOG:  database system was interrupted; last known up at 2017-11-03 07:13:50 CST
 LOG:  starting point-in-time recovery to 2017-11-02 23:05:00.009758+08
 LOG:  restored log file "00000005.history" from archive
 LOG:  restored log file "000000050000000C000000B7" from archive
 LOG:  redo starts at C/B7000028
 LOG:  consistent recovery state reached at C/B7000130
 LOG:  database system is ready to accept read only connections
 LOG:  restored log file "000000050000000C000000B8" from archive
 LOG:  redo done at C/B8000060
 LOG:  restored log file "000000050000000C000000B8" from archive
 LOG:  selected new timeline ID: 6
 LOG:  restored log file "00000005.history" from archive
 LOG:  archive recovery complete
 LOG:  database system is ready to accept connections
 ERROR:  relation "important_table" does not exist at character 15
 STATEMENT:  select * from important_table
````

#### 8.4、采用指定备份集还原、指定时间点
````

[postgres@test ~]# /etc/init.d/pg10 stop

[postgres@test ~]$  pgbackrest --stanza=demo   --type=time "--target=2017-11-02 23:05:00.009758+08" --set=20171102-144234F_20171102-230342D restore   //指定前一个备份集，时间点还原测试正常


[postgres@test ~]# /etc/init.d/pg10 start
[postgres@test ~]$ psql  -c "select * from important_table";  // 数据已经正常还原了
    message     
----------------
 Important Data
(1 row)

[postgres@test ~]$ 


 LOG:  listening on IPv6 address "::", port 5432
 LOG:  listening on Unix socket "/tmp/.s.PGSQL.5432"
 LOG:  database system was interrupted; last known up at 2017-11-02 23:03:43 CST
 LOG:  starting point-in-time recovery to 2017-11-02 23:05:00.009758+08
 LOG:  restored log file "00000004.history" from archive
 LOG:  restored log file "000000040000000C000000B4" from archive
 LOG:  redo starts at C/B4000028
 LOG:  consistent recovery state reached at C/B4000130
 LOG:  database system is ready to accept read only connections
 LOG:  restored log file "000000040000000C000000B5" from archive
 LOG:  redo done at C/B50205B0
 LOG:  last completed transaction was at log time 2017-11-02 23:04:17.062501+08
 LOG:  restored log file "000000040000000C000000B5" from archive
 LOG:  restored log file "00000005.history" from archive
 LOG:  restored log file "00000006.history" from archive
 LOG:  selected new timeline ID: 7
 LOG:  restored log file "00000004.history" from archive
 LOG:  archive recovery complete
 LOG:  database system is ready to accept connections
[postgres@test ~]$ 

````