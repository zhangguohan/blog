# Postgresql数据库备份pg_dump脚本-Linux

## 一、编写备份脚本

```
DATE=$(date  +%Y-%m-%d)

for db in $( /usr/local/pgsql/bin/psql  -c "SELECT datname FROM pg_database;" |grep -v "datname" |grep -v "(" |grep -v "\-" |grep -v "template*") ; do
        echo -n "Backing up ${db}... "
        /usr/local/pgsql/bin/pg_dump -Ft $db > /backup94/$db-$DATE.dump
        echo "Done."

     find /backup94/ -type f -mtime +5 -exec rm -f {} \;
done
```

\## 注意如下：

- 需要修改psql及pg_dump具体的路径
- 需要修改具体的备份保存路径及权限
- 默认保留5天以内的备份

## 二、添加计划任务

```
[postgres@localhost ~]$ crontab -l
02 01 * * * /home/postgres/backup94.sh
```

## 三、执行效果如下

```
[postgres@localhost backup94]$ ll
total 20823040
-rwxr-xr-x 1 postgres postgres    4298752 May 23 01:07 pms-2020-05-23.dump
-rwxr-xr-x 1 postgres postgres    4299264 May 24 01:07 pms-2020-05-24.dump
-rwxr-xr-x 1 postgres postgres    4299264 May 25 01:07 pms-2020-05-25.dump
-rwxr-xr-x 1 postgres postgres    4299264 May 26 01:07 pms-2020-05-26.dump
-rwxr-xr-x 1 postgres postgres    4305920 May 27 01:07 pms-2020-05-27.dump
-rwxr-xr-x 1 postgres postgres    4307968 May 28 01:07 pms-2020-05-28.dump
-rwxr-xr-x 1 postgres postgres    4309504 May 29 01:07 pms-2020-05-29.dump
-rwxr-xr-x 1 postgres postgres       5632 May 23 01:05 postgres-2020-05-23.dump
-rwxr-xr-x 1 postgres postgres       5632 May 24 01:05 postgres-2020-05-24.dump
-rwxr-xr-x 1 postgres postgres       5632 May 25 01:05 postgres-2020-05-25.dump
-rwxr-xr-x 1 postgres postgres       5632 May 26 01:05 postgres-2020-05-26.dump
-rwxr-xr-x 1 postgres postgres       5632 May 27 01:05 postgres-2020-05-27.dump
-rwxr-xr-x 1 postgres postgres       5632 May 28 01:05 postgres-2020-05-28.dump
```