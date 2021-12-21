
  ## 心得体会  

  今天学习了openGauss事务控制，事务是用户定义的一个数据库操作序列，这些操作要么全做要么全不做，是一个不可分割的工作单位。

## 课后作业  

## 1.以默认方式启动事务1，修改事务隔离级别，查看transaction_isolation


### 1.2以默认方式启动事务1，修改事务隔离级别

```

omm=# START TRANSACTION;
START TRANSACTION
omm=# SET LOCAL TRANSACTION ISOLATION LEVEL READ COMMITTED READ ONLY;
SET
omm=# show transaction_isolation;
 transaction_isolation 
-----------------------
 read committed
(1 row)

omm=# 
omm=# END;
COMMIT
omm=# 


```
### 1.2 查看transaction_isolation

omm=# show transaction_isolation;
 transaction_isolation 
-----------------------
 read committed
(1 row)

omm=# 



## 2.以读写方式启动事务2，创建新表，修改事务为只读事务，查看transaction_read_only，并向表中插入记录
```
omm=# create table test(id int);
CREATE TABLE
omm=# SET LOCAL TRANSACTION ISOLATION LEVEL READ COMMITTED READ ONLY;
SET
omm=# show transaction_read_only;
 transaction_read_only 
-----------------------
 on
(1 row)

omm=# insert into test values(44);
ERROR:  cannot execute INSERT in a read-only transaction
omm=# commit;
ROLLBACK
omm=# \dt
No relations found.
omm=# 
```


## 3.启动事务3，对表进行增删改查，并用到创建savepoint，回滚savepoint和删除savepoint


### 3.1 启动事务3，对表进行增删改查
```


omm=# drop table test1;
DROP TABLE
omm=# CREATE TABLE test1(a int);
CREATE TABLE
omm=# START TRANSACTION;
START TRANSACTION
omm=# INSERT INTO test1 VALUES (1);
INSERT 0 1
omm=#

```
### 3.2 并用到创建savepoint，回滚savepoint和删除savepoint
```


omm=# SAVEPOINT my_savepoint;
SAVEPOINT
omm=# INSERT INTO test1 VALUES (2);
INSERT 0 1
omm=# omm=# delete from test1;

DELETE 2
omm=# ROLLBACK TO SAVEPOINT my_savepoint;
ROLLBACK
omm=# select * from test1;
 a 
---
 1
(1 row)

omm=# 

omm=# RELEASE SAVEPOINT my_savepoint;
RELEASE
omm=#

```

## 4.清理数据

```
omm=# commit;
ROLLBACK
omm=# drop table test1;
DROP TABLE
omm=# 

```