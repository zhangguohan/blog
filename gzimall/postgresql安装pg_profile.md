## postgresql安装pg_profile

### 下载并安装pg_profile

```
[root@centos7-01 tmp]$ chowm postgres:postgres -R /usr/local/pg11/share/postgresql/extension/
[root@centos7-01 tmp]$ su - postgres
[postgres@centos7-01 tmp]$ tar -zxvf pg_profile--0.2.1.tar.gz -C /usr/local/pg11/share/postgresql/extension/
```

### 编辑postgresql添加如下

```
[postgres@centos7-01 data]$ vi postgresql.conf


track_activities = on
track_counts = on
track_io_timing = on
track_functions = all                   # none, pl, all

shared_preload_libraries = 'pg_stat_statements'
pg_stat_statements.max = 1000
pg_stat_statements.track = 'top'
pg_stat_statements.save = off

pg_profile.topn = 20
pg_profile.retention = 7
```

### 创建pg_profile扩展及相关依赖

```
[postgres@centos7-01 tmp]$psql

postgres=#   CREATE EXTENSION dblink;
postgres=#   CREATE EXTENSION pg_stat_statements;
postgres=#   CREATE SCHEMA profile;
postgres=#   CREATE EXTENSION pg_profile SCHEMA profile;
```

### 添加计划任务定时生成快照

```
[postgres@centos7-01 data]$ crontab -e

*/30 * * * *  /usr/local/pg11/bin/psql -c 'SELECT profile.snapshot()' > /tmp/pg_awr.log  2>&1
```

### 查快生成快照

```
[postgres@centos7-01 data]$ psql 
psql (13.0, server 11.1)
Type "help" for help.

postgres=# select profile.show_samples();
           show_samples           
----------------------------------
 (1,"2020-12-09 18:45:07-05",,,)
 (2,"2020-12-09 18:47:02-05",,,)
 (3,"2020-12-09 19:00:02-05",,,)
 (4,"2020-12-09 19:30:01-05",,,)
 (5,"2020-12-09 20:00:01-05",,,)
 (6,"2020-12-09 20:30:02-05",,,)
 (7,"2020-12-09 21:00:02-05",,,)
 (8,"2020-12-09 21:30:02-05",,,)
 (9,"2020-12-09 22:00:02-05",,,)

postgres=# \q
```

### 生成报告：

```
[postgres@centos7-01 data]$ psql -qtc "select profile.get_report(1,7)"  --output awr_report_postgres_1_7.html
[postgres@centos7-01 data]$ 
```

如果时间没有设置，可以设置来当前会话时区为北京时间,也可以修改postgresql.conf指定默计时区

postgres=# set timezone="Asia/Shanghai";
SET
postgres=# select profile.show_samples();

```
       show_samples           
```

------

(1,"2020-12-10 07:45:07+08",,,)
(2,"2020-12-10 07:47:02+08",,,)
(3,"2020-12-10 08:00:02+08",,,)
(4,"2020-12-10 08:30:01+08",,,)
(5,"2020-12-10 09:00:01+08",,,)
(6,"2020-12-10 09:30:02+08",,,)
(7,"2020-12-10 10:00:02+08",,,)
(8,"2020-12-10 10:30:02+08",,,)
(9,"2020-12-10 11:00:02+08",,,)
(10,"2020-12-10 11:30:01+08",,,)
(11,"2020-12-10 12:00:02+08",,,)
(12,"2020-12-10 12:30:01+08",,,)
(13,"2020-12-10 13:00:01+08",,,)
(14,"2020-12-10 13:30:01+08",,,)
(15,"2020-12-10 14:00:01+08",,,)
(16,"2020-12-10 14:30:01+08",,,)
(17,"2020-12-10 15:00:01+08",,,)
(18,"2020-12-10 15:21:54+08",,,)
(19,"2020-12-10 15:30:01+08",,,)