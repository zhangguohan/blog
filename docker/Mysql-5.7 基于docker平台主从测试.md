

https://blog.csdn.net/ctypyb2002/article/details/87972946 


### Mysql-5.7 基于Docker容器主从复制测试

#### 新建数据库本地相关存储目录
~~~
[root@localhost conf]# mkdir -p /storage/mysql/mysql-master01/conf


[root@localhost conf]# mkdir -p /storage/mysql/mysql-master01/data

[root@localhost conf]# chmod 777 -R /storage/mysql/mysql-master01 
~~~


#### 新建Master数据库配置文件

~~~
[mysqld]
pid-file	= /var/run/mysqld/mysqld.pid
socket		= /var/run/mysqld/mysqld.sock
datadir		= /var/lib/mysql
#log-error	= /var/log/mysql/error.log
# By default we only accept connections from localhost
#bind-address	= 127.0.0.1,108.88.8.34
# Disabling symbolic-links is recommended to prevent assorted security risks
symbolic-links=0
lower_case_table_names=1

character-set-server = utf8mb4
collation-server = utf8mb4_bin

skip-external-locking
key_buffer_size = 16M
max_allowed_packet = 1M
table_open_cache = 64
sort_buffer_size = 512K
net_buffer_length = 8K
read_buffer_size = 256K
read_rnd_buffer_size = 512K
myisam_sort_buffer_size = 8M

server-id = 1                    #服务id，必须唯一

log_bin = mysql-bin
binlog_format = row
sync_binlog = 1 
expire_logs_days =7
binlog_cache_size = 128m
max_binlog_cache_size = 256m 
max_binlog_size = 256m 
master_info_repository=TABLE 
relay_log_info_repository=TABLE 
gtid_mode = on
enforce_gtid_consistency = on
binlog_ignore_db=mysql 
binlog_ignore_db=information_schema 
binlog_ignore_db=performation_schema
binlog_ignore_db=sys



relay_log = mysql-relay-bin 
relay_log_purge = on 
relay_log_recovery = on 
max_relay_log_size = 1G

[mysqldump]
quick
max_allowed_packet = 16M

[mysql]
no-auto-rehash

[myisamchk]
key_buffer_size = 20M
sort_buffer_size = 20M
read_buffer = 2M
write_buffer = 2M

[mysqlhotcopy]
interactive-timeout



############# 
~~~~


#### 新建Master数据库实例使用本地存储卷
~~~
[root@localhost ~]# docker run --name mysql5.7-master  -v /storage/mysql/mysql-master01/conf:/etc/mysql/mysql.conf.d -v /storage/mysql/mysql-master01/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=8732  -p 3306:3306 -d mysql:5.7 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
b6981e0664bd092b99b631abebae8a542bb51e5e9210055e15f02bc753b02b33
[root@localhost ~]# 

~~~

#### 配置Mater服务器用于同步用户


[root@localhost conf]# docker exec -it  mysql5.7-master mysql -uroot -p8732
~~~
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 4
Server version: 5.7.26-log MySQL Community Server (GPL)

Copyright (c) 2000, 2019, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> CREATE USER 'repl_2_1'@'172.17.0.3' IDENTIFIED BY 'repl_2_1';
Query OK, 0 rows affected (0.00 sec)

mysql> GRANT REPLICATION SLAVE ON *.* TO 'repl_2_1'@'172.17.0.3' IDENTIFIED BY 'repl_2_1';
Query OK, 0 rows affected, 1 warning (0.00 sec)

mysql> flush privileges;
Query OK, 0 rows affected (0.00 sec)
~~~




#### Master 配置semisync_master模块

[root@localhost conf]# docker exec -it mysql5.7-master  mysql -uroot -p8732
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 5
Server version: 5.7.26-log MySQL Community Server (GPL)

Copyright (c) 2000, 2019, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> mysql> show global variables like '%semi%';
Empty set (0.01 sec)

mysql> install plugin rpl_semi_sync_master soname 'semisync_master.so';
Query OK, 0 rows affected (0.01 sec)

mysql> show global variables like '%semi%';
+-------------------------------------------+------------+
| Variable_name                             | Value      |
+-------------------------------------------+------------+
| rpl_semi_sync_master_enabled              | OFF        |
| rpl_semi_sync_master_timeout              | 10000      |
| rpl_semi_sync_master_trace_level          | 32         |
| rpl_semi_sync_master_wait_for_slave_count | 1          |
| rpl_semi_sync_master_wait_no_slave        | ON         |
| rpl_semi_sync_master_wait_point           | AFTER_SYNC |
+-------------------------------------------+------------+
6 rows in set (0.00 sec)

mysql> set global rpl_semi_sync_master_enabled = on;
Query OK, 0 rows affected (0.01 sec)

mysql> set global rpl_semi_sync_master_timeout = 2000;
Query OK, 0 rows affected (0.00 sec)

mysql>

#### Master DB配置mysqld.cnf文件
~~~~
########################## # semisync replication ##########################
rpl_semi_sync_master_enabled = on 
rpl_semi_sync_master_timeout = 2000
~~~~





### Master 导出提定数据库备份


~~~
root@b6981e0664bd:/# mysqldump -uroot -p --lock-tables --events --set-gtid-purged=OFF --triggers --routines --flush-logs --master-data=2 --databases tank>/tmp/db2019065.sql
Enter password: 
root@b6981e0664bd:/# ll
~~~



#### 复制Master mysqld.cnf 修改server-id 号 
~~~~
[mysqld]
pid-file	= /var/run/mysqld/mysqld.pid
socket		= /var/run/mysqld/mysqld.sock
datadir		= /var/lib/mysql
#log-error	= /var/log/mysql/error.log
# By default we only accept connections from localhost
#bind-address	= 127.0.0.1,108.88.8.34
# Disabling symbolic-links is recommended to prevent assorted security risks
symbolic-links=0
lower_case_table_names=1

character-set-server = utf8mb4
collation-server = utf8mb4_bin

skip-external-locking
key_buffer_size = 16M
max_allowed_packet = 1M
table_open_cache = 64
sort_buffer_size = 512K
net_buffer_length = 8K
read_buffer_size = 256K
read_rnd_buffer_size = 512K
myisam_sort_buffer_size = 8M

server-id = 2                    #服务id，必须唯一

log_bin = mysql-bin
binlog_format = row
sync_binlog = 1 
expire_logs_days =7
binlog_cache_size = 128m
max_binlog_cache_size = 256m 
max_binlog_size = 256m 
master_info_repository=TABLE 
relay_log_info_repository=TABLE 
gtid_mode = on
enforce_gtid_consistency = on
binlog_ignore_db=mysql 
binlog_ignore_db=information_schema 
binlog_ignore_db=performation_schema
binlog_ignore_db=sys



relay_log = mysql-relay-bin 
relay_log_purge = on 
relay_log_recovery = on 
max_relay_log_size = 1G

[mysqldump]
quick
max_allowed_packet = 16M

[mysql]
no-auto-rehash

[myisamchk]
key_buffer_size = 20M
sort_buffer_size = 20M
read_buffer = 2M
write_buffer = 2M

[mysqlhotcopy]
interactive-timeout

~~~~




#### 启动主从复制
~~~
[root@localhost conf]# docker run --name mysql5.7-slave01  -v /storage/mysql/mysql-slave01/conf:/etc/mysql/mysql.conf.d -v /storage/mysql/mysql-slave01/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=8732  -p 3308:3306 -d mysql:5.7 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
4909fceea47abf1cf1c1a55a3d486e6037589b595a29200b8c1e547b50467a1e
[root@localhost conf]# docker exec -it mysql5.7-slave01 /bin/bash
root@4909fceea47a:/# ll
bash: ll: command not found
root@4909fceea47a:/# ps -ef
bash: ps: command not found
root@4909fceea47a:/# exit
exit
[root@localhost conf]# docker exec -it mysql5.7-slave01 /bin/bash
root@4909fceea47a:/# mysql -uroot -p8732
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 2
Server version: 5.7.26-log MySQL Community Server (GPL)

Copyright (c) 2000, 2019, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> create database tank;
Query OK, 1 row affected (0.00 sec)

mysql> 


~~~

#### 还原数据库到slaveDB
~~~
[root@localhost conf]# docker exec -it mysql5.7-slave01  /bin/bash
root@4909fceea47a:/# mysql -uroot -p8732 tank < /etc/mysql/mysql.conf.d/db201906.sql 
db201906.sql   db2019065.sql  
root@4909fceea47a:/# mysql -uroot -p8732 tank < /etc/mysql/mysql.conf.d/db2019065.sql 
mysql: [Warning] Using a password on the command line interface can be insecure.
root@4909fceea47a:/# 

~~~


#### Slave DB 配置semisync
~~~
mysql>  show global variables like '%semi%';
Empty set (0.01 sec)

mysql> install plugin rpl_semi_sync_slave soname 'semisync_slave.so';
Query OK, 0 rows affected (0.00 sec)

mysql>  show global variables like '%semi%';
+---------------------------------+-------+
| Variable_name                   | Value |
+---------------------------------+-------+
| rpl_semi_sync_slave_enabled     | OFF   |
| rpl_semi_sync_slave_trace_level | 32    |
+---------------------------------+-------+
2 rows in set (0.01 sec)

mysql> set global rpl_semi_sync_slave_enabled = on;

~~~


#### Slave DB配置mysqld.cnf文件
~~~~
########################## # semisync replication ##########################
rpl_semi_sync_master_enabled = on
rpl_semi_sync_master_timeout = 2000
rpl_semi_sync_slave_enabled = on
~~~~



#### 启动SLAVE 半同步复制


~~~~
[root@localhost conf]# grep -i "change master" db2019065.sql 
-- CHANGE MASTER TO MASTER_LOG_FILE='mysql-bin.000008', MASTER_LOG_POS=194;
[root@localhost conf]#



[root@localhost conf]# docker exec -it mysql5.7-slave01  mysql -uroot -p8732
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 7
Server version: 5.7.26-log MySQL Community Server (GPL)

Copyright (c) 2000, 2019, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> show slave status\G
Empty set (0.00 sec)

mysql> stop slave;
Query OK, 0 rows affected, 1 warning (0.00 sec)

mysql> change master to master_host='172.17.0.2', 
    -> master_user='repl_2_1',
    -> master_password='repl_2_1',
    -> master_port=3306, 
    -> master_log_file='mysql-bin.000008', 
    -> master_log_pos=194, 
    -> master_connect_retry=10,
    -> master_retry_count=86400;
Query OK, 0 rows affected, 2 warnings (0.01 sec)


mysql> start slave;
Query OK, 0 rows affected (0.00 sec)

mysql> show slave status\G
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: 172.17.0.2
                  Master_User: repl_2_1
                  Master_Port: 3306
                Connect_Retry: 10
              Master_Log_File: mysql-bin.000008
          Read_Master_Log_Pos: 950
               Relay_Log_File: mysql-relay-bin.000003
                Relay_Log_Pos: 360
        Relay_Master_Log_File: mysql-bin.000008
             Slave_IO_Running: Yes
            Slave_SQL_Running: Yes
              Replicate_Do_DB: 
          Replicate_Ignore_DB: 
           Replicate_Do_Table: 
       Replicate_Ignore_Table: 
      Replicate_Wild_Do_Table: 
  Replicate_Wild_Ignore_Table: 
                   Last_Errno: 0
                   Last_Error: 
                 Skip_Counter: 0
          Exec_Master_Log_Pos: 950
              Relay_Log_Space: 1489
              Until_Condition: None
               Until_Log_File: 
                Until_Log_Pos: 0
           Master_SSL_Allowed: No
           Master_SSL_CA_File: 
           Master_SSL_CA_Path: 
              Master_SSL_Cert: 
            Master_SSL_Cipher: 
               Master_SSL_Key: 
        Seconds_Behind_Master: 0
Master_SSL_Verify_Server_Cert: No
                Last_IO_Errno: 0
                Last_IO_Error: 
               Last_SQL_Errno: 0
               Last_SQL_Error: 
  Replicate_Ignore_Server_Ids: 
             Master_Server_Id: 1
                  Master_UUID: 627b7b85-8629-11e9-a990-0242ac110002
             Master_Info_File: mysql.slave_master_info
                    SQL_Delay: 0
          SQL_Remaining_Delay: NULL
      Slave_SQL_Running_State: Slave has read all relay log; waiting for more updates
           Master_Retry_Count: 86400
                  Master_Bind: 
      Last_IO_Error_Timestamp: 
     Last_SQL_Error_Timestamp: 
               Master_SSL_Crl: 
           Master_SSL_Crlpath: 
           Retrieved_Gtid_Set: 627b7b85-8629-11e9-a990-0242ac110002:9-11
            Executed_Gtid_Set: 627b7b85-8629-11e9-a990-0242ac110002:9-11,
88f3f957-86ec-11e9-a383-0242ac110003:1-20
                Auto_Position: 0
         Replicate_Rewrite_DB: 
                 Channel_Name: 
           Master_TLS_Version: 
1 row in set (0.00 sec)
~~~



#### Master DB 查看
~~~
[root@localhost data]# docker exec -it mysql5.7-master mysql -uroot -p8732
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 17
Server version: 5.7.26-log MySQL Community Server (GPL)

Copyright (c) 2000, 2019, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> show global status like '%semi%';
+--------------------------------------------+-------+
| Variable_name                              | Value |
+--------------------------------------------+-------+
| Rpl_semi_sync_master_clients               | 1     |
| Rpl_semi_sync_master_net_avg_wait_time     | 0     |
| Rpl_semi_sync_master_net_wait_time         | 0     |
| Rpl_semi_sync_master_net_waits             | 0     |
| Rpl_semi_sync_master_no_times              | 1     |
| Rpl_semi_sync_master_no_tx                 | 11    |
| Rpl_semi_sync_master_status                | ON    |
| Rpl_semi_sync_master_timefunc_failures     | 0     |
| Rpl_semi_sync_master_tx_avg_wait_time      | 0     |
| Rpl_semi_sync_master_tx_wait_time          | 0     |
| Rpl_semi_sync_master_tx_waits              | 0     |
| Rpl_semi_sync_master_wait_pos_backtraverse | 0     |
| Rpl_semi_sync_master_wait_sessions         | 0     |
| Rpl_semi_sync_master_yes_tx                | 0     |
+--------------------------------------------+-------+
14 rows in set (0.00 sec)

mysql> 

~~~