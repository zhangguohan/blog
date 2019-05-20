
## Docker安装mysql-5.6


### docker 中下载 mysql
~~~
docker pull mysql
~~~


### docker 安装Mysql-5.6

~~~
docker run --name mysql5.6 -e MYSQL_ROOT_PASSWORD=8732  -p 3306:3306 -d mysql:5.6 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci 
~~~

### 进入Mysql容器
~~~
docker exec -it  mysql5.6 mysql -uroot -p


[root@localhost tmp]# docker exec -it  mysql5.6 mysql -uroot -p
Enter password: 
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 7
Server version: 5.6.44 MySQL Community Server (GPL)

Copyright (c) 2000, 2019, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.
~~~

### 新建业务数据库
~~~~
mysql> create database nscms

mysql> CREATE USER 'nscms'@'%' IDENTIFIED BY 'nscms';
Query OK, 0 rows affected (0.00 sec)

mysql> grant all privileges on nscms .* to 'nscms'@'%';
Query OK, 0 rows affected (0.00 sec)

mysql>  FLUSH PRIVILEGES;
Query OK, 0 rows affected (0.00 sec)

mysql>

~~~

 