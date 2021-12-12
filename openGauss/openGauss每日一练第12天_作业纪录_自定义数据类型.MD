
  ## 心得体会  

  今天学习了openGauss自定义数据类型、枚举类型、自定义数据类型的属性的修改。

## 课后作业  

## 1.创建一个复合类型，重命名复合类型，为复合类型增加属性、删除属性


### 1.1 创建一个复合类型
```
omm=# CREATE TYPE test AS (f1 int, f2 text);
CREATE TYPE
omm=# 

omm=# INSERT INTO  t1_test  values(1,(3,'demo3'));
INSERT 0 1
omm=# SELECT (b).f2 FROM t1_test;
  f2   
-------
 demo3
(2 rows)


```
### 1.2 重命名复合类型
```


omm=# ALTER TYPE test RENAME TO test1;      
ALTER TYPE

```

### 1.3 增加属性
```
omm=# ALTER TYPE test4  ADD ATTRIBUTE f3 int;
ALTER TYPE

```


### 1.4 删除属性

omm=# ALTER TYPE test4 drop ATTRIBUTE f1;
ALTER TYPE


## 2.创建一个枚举类型，新增标签值，重命名标签值

### 2.1 创建一个枚举类型
```
omm=# CREATE TYPE week AS ENUM ('MA', 'TU', 'WED');
CREATE TYPE
omm=# 

omm=# create table t2 (id int, dt week);
CREATE TABLE
omm=# insert into t2 values(1,'MA');
INSERT 0 1
omm=# insert into t2 values(1,'MAdd');
ERROR:  invalid input value for enum week: "MAdd"
LINE 1: insert into t2 values(1,'MAdd');
                                ^
CONTEXT:  referenced column: dt
omm=# select * from t2;
omm=#  id | dt 
----+----
  1 | MA
(1 row)


```


### 2.2 新增标签值

```
omm=# ALTER TYPE week ADD VALUE IF NOT EXISTS 'FUO' BEFORE 'WED';
ALTER TYPE
omm=# select * from pg_enum;                
 enumtypid | enumsortorder | enumlabel 
-----------+---------------+-----------
     16399 |             1 | MA
     16399 |             2 | TU
     16399 |             3 | WED
     16399 |           2.5 | FUO
(4 rows)

omm=# 


```



### 2.3 重命名标签值

```
omm=# ALTER TYPE week RENAME VALUE 'MA' TO 'KK';
ALTER TYPE
omm=# select * from pg_enum;
 enumtypid | enumsortorder | enumlabel 
-----------+---------------+-----------
     16399 |             2 | TU
     16399 |             3 | WED
     16399 |           2.5 | FUO
     16399 |             1 | KK
(4 rows)

omm=# insert into t2 values(1,'MA');
ERROR:  invalid input value for enum week: "MA"
LINE 1: insert into t2 values(1,'MA');
                                ^
CONTEXT:  referenced column: dt
omm=# 

```


## 3.使用新创建的类型创建表

```
omm=# CREATE TABLE t2_test (a int, b test, c week);
CREATE TABLE
omm=# insert into t2_test values(1,(1,'t'),'WED');
INSERT 0 1
omm=# insert into t2_test values(1,(1,'t'),'WEDd');
ERROR:  invalid input value for enum week: "WEDd"
LINE 1: insert into t2_test values(1,(1,'t'),'WEDd');
                                             ^
CONTEXT:  referenced column: c
omm=# select * from t2_test;
 a |   b   |  c  
---+-------+-----
 1 | (1,t) | WED
(1 row)

omm=# 



```


### 4.删除类型
```
omm=# drop table t2_test;
omm=# DROP TABLE

omm=# drop type test;
DROP TYPE
omm=# 

``` 