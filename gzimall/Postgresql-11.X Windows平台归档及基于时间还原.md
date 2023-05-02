# Postgresql-11.X Windows平台归档及基于时间还原





一、添加用备份流复制用户

```
psql#CREATE ROLE replication WITH REPLICATION PASSWORD '6220104' LOGIN; //新建用于复制用户
```

二、修改pg_hba.conf

```
# TYPE  DATABASE        USER            ADDRESS                 METHOD

# IPv4 local connections:
host    all             all             127.0.0.1/32            md5
# IPv6 local connections:
host    all             all             ::1/128                 md5
# Allow replication connections from localhost, by a user with the
# replication privilege.
host    replication     all             127.0.0.1/32            trust
host    replication     all             ::1/128                 md5
```

三、开启归档日志及复制（postgresql.conf）

```
#------------------------------------------------------------------------------
# WRITE-AHEAD LOG
#------------------------------------------------------------------------------

# - Settings -

wal_level = replica            

# - Archiving -

archive_mode = on        
                
archive_command = 'copy "%p" "D:\\archivelogs\\%f"'    (指定归档日志目录)

# - Sending Servers -

# Set these on the master and on any standby that will send replication data.

max_wal_senders = 10        

# - Standby Servers -

# These settings are ignored on a master server.

hot_standby = on            

        
```

- 修改pg_hba.conf、postgrsql.conf需要重启postgresql服务

四、执行在线基备份

```
C:\Users\Administrator>D:\PostgreSQL\11\bin\pg_basebackup -D D:\backup202010121000 -Ft -z -P -v -w -h 127.0.0.1   -U replication
pg_basebackup: 开始基础备份，等待检查点完成
pg_basebackup: 已完成检查点
pg_basebackup: 预写日志起始于时间点: 0/7000060, 基于时间轴2
pg_basebackup: 启动后台 WAL 接收进程
pg_basebackup: 已创建临时复制槽"pg_basebackup_1740"
33586/33586 kB (100%), 1/1 表空间
pg_basebackup: 预写日志结束点: 0/7000130
pg_basebackup: 等待后台进程结束流操作...
pg_basebackup: 基础备份已完成
```

五、基于时间还原

1、将base备份解压成data目录
2、将pg_wal文件复制到data目录下pg_wal目录
3、添加NETWORK SERVICE用户为data目录权限读写操作

4、从share目录一个recovery.conf.sample到data目录 修改为recovery.conf

```
# expect them to be interchangeable.
#
restore_command = 'copy  "D:\\archivelogs\\%f" "%p"'    

recovery_target_time = '2020-10-12 14:31:00 '    # e.g. '2004-07-14 22:39:00 EST'
#
```

六、启动Postgresql服务查日志

```
2020-10-12 15:09:13.674 HKT [6004] LOG:  database system was interrupted; last known up at 2020-10-12 14:29:47 HKT
2020-10-12 15:09:17.322 HKT [6004] LOG:  starting point-in-time recovery to 2020-10-12 14:31:00+08
2020-10-12 15:09:17.456 HKT [6004] LOG:  restored log file "000000010000000000000009" from archive
2020-10-12 15:09:17.680 HKT [6004] LOG:  redo starts at 0/9000060
2020-10-12 15:09:17.686 HKT [6004] LOG:  consistent recovery state reached at 0/9000130
2020-10-12 15:09:17.716 HKT [5624] LOG:  database system is ready to accept read only connections
2020-10-12 15:09:17.812 HKT [6004] LOG:  restored log file "00000001000000000000000A" from archive
2020-10-12 15:09:18.774 HKT [6004] LOG:  recovery stopping before commit of transaction 600, time 2020-10-12 14:31:15.358086+08
2020-10-12 15:09:18.774 HKT [6004] LOG:  recovery has paused
2020-10-12 15:09:18.774 HKT [6004] HINT:  Execute pg_wal_replay_resume() to continue.
```

七、执行pg_wal_replay_resume() 确认时间点

```
psql# select pg_wal_replay_resume() ;
```

八、查看日志

```
2020-10-12 15:15:44.593 HKT [6004] LOG:  redo done at 0/A44C248
2020-10-12 15:15:44.593 HKT [6004] LOG:  last completed transaction was at log time 2020-10-12 14:30:51.34594+08
2020-10-12 15:15:44.669 HKT [6004] LOG:  restored log file "00000002.history" from archive
2020-10-12 15:15:44.753 HKT [6004] LOG:  restored log file "00000003.history" from archive
2020-10-12 15:15:44.822 HKT [6004] LOG:  restored log file "00000004.history" from archive
2020-10-12 15:15:44.869 HKT [6004] LOG:  selected new timeline ID: 5
2020-10-12 15:15:45.101 HKT [6004] LOG:  archive recovery complete
2020-10-12 15:15:45.449 HKT [5624] LOG:  database system is ready to accept connections
```

至此，Postgresql数据库已经回退到指定时间状态。