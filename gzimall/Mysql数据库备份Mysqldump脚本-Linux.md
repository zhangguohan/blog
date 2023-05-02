## Mysql数据库备份Mysqldump脚本-Linux

## 一、编写Mysql导出脚本

```
[root@rhel6 sbin]# vi mysqlbackup.sh

#If any of your tables run on InnoDB engine
#directory to store backups in
DST=/backup/dbback
# A regex, passed to egrep -v, for which databases to ignore
IGNREG='^snort$'
# The MySQL username and password
DBUSER=root
DBPASS=62201042
# Any backups older than this will be deleted first
KEEPDAYS=7

DATE=$(date  +%Y-%m-%d)

cd /usr/local/mysql

find ${DST} -type f -mtime +${KEEPDAYS} -exec rm -f {} \;
rmdir $DST/* 2>/dev/null

mkdir -p ${DST}/${DATE}
for db in $(echo 'show databases;' | /usr/local/mysql/bin/mysql -s -u ${DBUSER} -p${DBPASS} | egrep -v ${IGNREG}) ; do
        echo -n "Backing up ${db}... "
        /usr/local/mysql/bin/mysqldump --opt  --single-transaction -u ${DBUSER} -p${DBPASS} $db | gzip -c > ${DST}/${DATE}/${db}.sql.gz
        echo "Done."
done

exit 0
```

### 注意如下

- 需要手动创建DST备份目录及用户权限，
- 需要用户名及密码及默认保留天数DBUSER 、DBPASS、 KEEPDAYS
- Mysql数据库及可执行文件的按装目录

## 二、导入计划任务

```
[root@localhost ~]# crontab -e

30 01 * * * /mysqlbackup.sh
```

## 三、执行备份结果

[root@rhel6 sbin]# cd /backup/dbback/2014-06-07/
[root@rhel6 2014-06-07]# ll
总用量 176
-rw-r--r-- 1 root root 353 6月 7 11:38 information_schema.sql.gz
-rw-r--r-- 1 root root 161767 6月 7 11:38 mysql.sql.gz
-rw-r--r-- 1 root root 354 6月 7 11:38 performance_schema.sql.gz
-rw-r--r-- 1 root root 649 6月 7 11:38 tankdb.sql.gz
-rw-r--r-- 1 root root 432 6月 7 11:38 test.sql.gz
[root@rhel6 2014-06-07]#