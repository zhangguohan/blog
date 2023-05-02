# PostgreSQL9.6 基于时间点恢复(PITR)测试

### 一、新建归档目录及备份目录

```
[root@node01 ~]# mkdir /archivelog/
[root@node01 ~]# chown postgres -R /archivelog/
[root@node01 ~]# mkdir /home/postgres/backup

[root@node01 ~]# chown postgres  /home/postgres/backup
```

### 二、配置开启流复制备份

```
[postgres@node01 data]$ vi pg_hba.conf 

# Allow replication connections from localhost, by a user with the
# replication privilege.
#local   replication     postgres                                trust
host    replication     all        localhost           trust
#host    replication     postgres        ::1/128                 trust
"pg_hba.conf" 93L, 4458C         

[postgres@node01 data]$ vi postgresql.conf

# - Settings -

wal_level = logical            # minimal, replica, or logical
                    # (change requires restart)



# - Sending Server(s) -

# Set these on the master and on any standby that will send replication data.

max_wal_senders = 5        # max number of walsender processes
                # (change requires restart)
#wal_keep_segments = 0        # in logfile segments, 16MB each; 0 disables
#wal_sender_timeout = 60s    # in milliseconds; 0 disables


# These settings are ignored on a master server.

hot_standby = on            # "on" allows queries during recovery
                    # (change requires restart)


[postgres@node01 data]$ psql -d ghan
psql (9.6beta2)
Type "help" for help.

ghan=# 
ghan=# CREATE ROLE replication WITH REPLICATION PASSWORD '6220104' LOGIN;
```

### 三、生成测试表

```
ghan=# create table t2_test(id int) tablespace tank;



ghan=# \timing on
Timing is on.
ghan=# select count(*) from t_test;
  count  
---------
 4010000
(1 row)

Time: 639.881 ms
ghan=# \! date
2016年 07月 08日 星期五 08:39:27 CST ----> 时间点 
ghan=# 
```

### 四、模拟生成基备份：

```
[postgres@node01 backup]$ pg_basebackup -D /home/postgres/backup -Ft -z -P -v -w -h localhost  -U replication 
183658/183658 kB (100%), 2/2 tablespaces                                         
NOTICE:  pg_stop_backup complete, all required WAL segments have been archived
pg_basebackup: base backup completed
[postgres@node01 backup]$ 
```

### 五、增加测试数据库在不同时间点

```
ghan=# insert into t_test values(generate_series(1,7000));
INSERT 0 7000
ghan=# select count(*) from t_test;
  count  
---------
 4052000
(1 row)

ghan=# \! date
2016年 07月 08日 星期五 08:44:48 CST  ---》时间点
ghan=# 

ghan=# truncate table t_test;
TRUNCATE TABLE
ghan=# \! date
2016年 07月 08日 星期五 08:46:42 CST
ghan=# select count(*) from t_test;
 count 
-------
     0
(1 row)

ghan=# 
```

### 六、停止数据库用

#### /etc/init.d/pg9.6 stop

### 七、解压生成基于备份到指定目录指定时间点还原

```
[postgres@node01 ~]  tar -zxvf 16397.tar.gz  -C backup/
 
[postgres@node01 ~]  tar -zxvf base.tar.gz  -C backup/
[postgres@node01 ~]  cd backup
[postgres@node01 ~] cp /usr/local/pg9.6/share/postgresql/recovery.conf.sample  recovery.conf
[postgres@node01 ~] vi recovery.conf

restore_command = 'cp /archivelog/%f %p'        # e.g. 'cp /mnt/server/archivedir/%f %p'

recovery_target_time = '2016-07-08 08:44:48'    # e.g. '2004-07-14 22:39:00 EST' 
```

### 八、查看还原进度

```
[postgres@node01 ~]$ pg_ctl -D backup start -l ok.log
server starting
[postgres@node01 ~]$ tail -f ok.log 
LOG:  database system was interrupted; last known up at 2016-07-08 08:42:39 CST
LOG:  starting point-in-time recovery to 2016-07-08 08:44:48+08
LOG:  restored log file "000000010000000000000028" from archive
LOG:  redo starts at 0/28000028
LOG:  consistent recovery state reached at 0/280000F8
LOG:  database system is ready to accept read only connections
LOG:  restored log file "000000010000000000000029" from archive
LOG:  recovery stopping before commit of transaction 1703, time 2016-07-08 08:46:40.179655+08
LOG:  recovery has paused
HINT:  Execute pg_xlog_replay_resume() to continue.
```

### 九、执行日志回滚查看还原结果

```
[postgres@node01 ~]$ psql  -d ghan
psql (9.6beta2)
Type "help" for help.

ghan=# select pg_xlog_replay_resume();
 pg_xlog_replay_resume 
-----------------------
 
(1 row)

ghan=# select count(*) from t_test;
  count  
---------
 4052000
(1 row)

ghan=#  
```

### 十、查看详细日志：

```
[postgres@node01 ~]$ more ok.log 
LOG:  database system was interrupted; last known up at 2016-07-08 08:42:39 CST
LOG:  starting point-in-time recovery to 2016-07-08 08:44:48+08
LOG:  restored log file "000000010000000000000028" from archive
LOG:  redo starts at 0/28000028
LOG:  consistent recovery state reached at 0/280000F8
LOG:  database system is ready to accept read only connections
LOG:  restored log file "000000010000000000000029" from archive
LOG:  recovery stopping before commit of transaction 1703, time 2016-07-08 08:46:40.179655+08
LOG:  recovery has paused
HINT:  Execute pg_xlog_replay_resume() to continue.
ERROR:  canceling statement due to user request
STATEMENT:  select count(*) from t_test;
LOG:  redo done at 0/29295F60
LOG:  last completed transaction was at log time 2016-07-08 08:44:39.85587+08
LOG:  restored log file "00000002.history" from archive
LOG:  restored log file "00000003.history" from archive
LOG:  restored log file "00000004.history" from archive
cp: cannot stat `/archivelog/00000005.history': No such file or directory
LOG:  selected new timeline ID: 5
cp: cannot stat `/archivelog/00000001.history': No such file or directory
LOG:  archive recovery complete
LOG:  MultiXact member wraparound protections are now enabled
LOG:  database system is ready to accept connections
LOG:  autovacuum launcher started
[postgres@node01 ~]$ 
```