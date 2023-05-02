# PostgreSQL-10 主从同步复制测试

### 一、主节点开启同步复制

#### 1、 新建一个复制账号

```
[postgres@test ~]$ /usr/local/pg10/bin/psql
psql (10.0)
Type "help" for help.



postgres=# CREATE ROLE replication WITH REPLICATION PASSWORD '6220104' LOGIN;  //复制账号
  

postgres=# CREATE ROLE tank SUPERUSER LOGIN PASSWORD '6220104';   //超级管理员
```

#### 2、修改主配置文件及开启流复制

```
[postgres@test pgdata]$ grep -Ev "^$|^[#;]" postgresql.conf 
                    # (change requires restart)
                    # (change requires restart)
                    # (change requires restart)
                    # (change requires restart)
listen_addresses = '*'        # what IP address(es) to listen on;
                    # comma-separated list of addresses;
                    # defaults to 'localhost'; use '*' for all
                    # (change requires restart)
max_connections = 100            # (change requires restart)
                    # (change requires restart)
                    # (change requires restart)
                    # (change requires restart)
                    # (change requires restart)
                    # 0 selects the system default
                    # 0 selects the system default
                    # 0 selects the system default
shared_buffers =2048MB            # min 128kB
                    # (change requires restart)
                    # (change requires restart)
                    # (change requires restart)
work_mem = 128MB                # min 64kB
maintenance_work_mem = 256MB        # min 1MB
dynamic_shared_memory_type = posix    # the default is the first option
                    # supported by the operating system:
                    #   posix
                    #   sysv
                    #   windows
                    #   mmap
                    # use none to disable dynamic shared memory
                    # (change requires restart)
                    # in kB, or -1 for no limit
                    # (change requires restart)
                    # can be used in parallel queries
                    # (change requires restart)
wal_level = replica            # minimal, replica, or logical
                    # (change requires restart)
                    # (turning this off can cause
                    # unrecoverable data corruption)
synchronous_commit = on        # synchronization level;
                    # off, local, remote_write, remote_apply, or on
                    # supported by the operating system:
                    #   open_datasync
                    #   fdatasync (default on Linux)
                    #   fsync
                    #   fsync_writethrough
                    #   open_sync
                    # (change requires restart)
                    # (change requires restart)
max_wal_size = 16GB
min_wal_size = 8GB
                # (change requires restart)
                # placeholders: %p = path of file to archive
                #               %f = file name only
                # e.g. 'test ! -f /mnt/server/archivedir/%f && cp %p /mnt/server/archivedir/%f'
                # number of seconds; 0 disables
max_wal_senders = 10        # max number of walsender processes
                # (change requires restart)
wal_sender_timeout = 10s    # in milliseconds; 0 disables
                # (change requires restart)
                # (change requires restart)
synchronous_standby_names = '*'    # standby servers that provide sync rep
                # method to choose sync standbys, number of sync standbys,
                # and comma-separated list of application_name
                # from standby(s); '*' = all
hot_standby = on            # "off" disallows queries during recovery
                    # (change requires restart)
                    # when reading WAL from archive;
                    # -1 allows indefinite delay
                    # when reading streaming WAL;
                    # -1 allows indefinite delay
                    # 0 disables
                    # query conflicts
                    # communication from master
                    # in milliseconds; 0 disables
                    # retrieve WAL after a failed attempt
                    # (change requires restart)
effective_cache_size = 4GB
                    # JOIN clauses

log_timezone = 'PRC'

timezone = 'PRC'
                    # abbreviations.  Currently, there are
                    #   Default
                    #   Australia (historical usage)
                    #   India
                    # You can create your own file in
                    # share/timezonesets/.
                    # encoding
lc_messages = 'C'            # locale for system error message
                    # strings
lc_monetary = 'C'            # locale for monetary formatting
lc_numeric = 'C'            # locale for number formatting
lc_time = 'C'                # locale for time formatting
default_text_search_config = 'pg_catalog.english'
                    # (change requires restart)
                    # (change requires restart)
                    # (max_pred_locks_per_transaction
                    #  / -max_pred_locks_per_relation) - 1
                    # directory 'conf.d'
[postgres@test pgdata]$ 
```

#### 3、修改访问控制文件

```
[postgres@test pgdata]$ grep -Ev "^$|^[#;]" pg_hba.conf 
local   all             all                                     trust
host    all             all             127.0.0.1/32            trust
host    all             all             14.23.51.58/32          md5
host    all             all             ::1/128                 trust
local   replication     all                                     trust
host    replication     all             14.23.51.58/32          md5
host    replication     all             ::1/128                 trust
[postgres@test pgdata]$ 
```

#### 4、重启pg实例

```
# /etc/init.d/pg10 restart
```

### 二、 备机作业

#### 2.1 初始化生成data基备份

```
[postgres@node02 ~]$ /usr/local/pg10/bin/pg_basebackup -D data   -h 139.159.247.78 -U replication  -X stream //注意其它表空间位置是否存在
```

#### 2.2 编辑recovery.conf文件。

```
# cp /usr/local/pg10/share/postgresql/recovery.conf.sample  recovery.conf

[postgres@node02 backup]$  vi recovery.conf 

#---------------------------------------------------------------------------
# STANDBY SERVER PARAMETERS
#---------------------------------------------------------------------------
#
# standby_mode
#
# When standby_mode is enabled, the PostgreSQL server will work as a
# standby. It will continuously wait for the additional XLOG records, using
# restore_command and/or primary_conninfo.
#
standby_mode = on
#
# primary_conninfo
#
# If set, the PostgreSQL server will try to connect to the primary using this
# connection string and receive XLOG records continuously.
#
primary_conninfo = 'host=139.159.247.78 port=5432  user=replication password=6220104 application_name=ghan' # e.g. 'host=localhost po  
//如果设置application_name=* 将同步所有主节点数据库
rt=5432'
```

#### 2.3启动pg查看日志

```
[postgres@node02 backup]$ /usr/local/pg10/bin/pg_ctl -D data  start -l kkk.log
```

### 三、同步复制状态检查在主节点

```
[postgres@test ~]$ /usr/local/pg10/bin/psql
psql (10.0)
Type "help" for help.

postgres=#  \x
Expanded display is on.
postgres=#  SELECT * FROM pg_stat_replication;
-[ RECORD 1 ]----+------------------------------
pid              | 18242
usesysid         | 16575
usename          | replication
application_name | ghan
client_addr      | 14.23.51.58
client_hostname  | 
client_port      | 52308
backend_start    | 2017-10-31 17:41:04.982858+08
backend_xmin     | 
state            | streaming
sent_lsn         | B/31C17120
write_lsn        | B/31C17120
flush_lsn        | B/31C17120
replay_lsn       | B/31C17120
write_lag        | 
flush_lag        | 
replay_lag       | 
sync_priority    | 1
sync_state       | sync

postgres=# 
```

### 四、关闭备节点测试

```
ghan=# insert into t2_test values(generate_series(1,70));
^CCancel request sent
WARNING:  canceling wait for synchronous replication due to user request
DETAIL:  The transaction has already committed locally, but might not have been replicated to the standby.
INSERT 0 70
ghan=# ^C
ghan=#
 
```

### 五、关闭主节点同步提交修改为异步提交

#### 5.1修改配置文件

```
# vi postgresql.conf 

synchronous_commit = off        # synchronization level;
```

#### 5.2、重启pg实例

```
# /etc/init.d/pg10 restart
```