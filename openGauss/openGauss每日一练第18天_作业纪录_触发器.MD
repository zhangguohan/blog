
  ## 心得体会  

  今天学习了openGauss触发器，触发器是对应用动作的响应机制，当应用对一个对象发起DML操作时，就会产生一个触发事件（Event），如果该对象上拥有该事件对应的触发器，那么就会检查触发器的触发条件（Condition）是否满足，如果满足触发条件，那么就会执行触发动作（Action）。

## 课后作业  

## 1.创建游创建源表和触发表，在源表上创建insert触发器，创建操作触发表的触发器函数


### 1.1 创建源表和触发表，创建操作触发表的触发器函数

```
omm=# CREATE TABLE test_src_tbl(id1 INT, id2 INT, name char(20)) ; 
CREATE TABLE
omm=# CREATE TABLE test__des_tbl(id1 INT, id2 INT, name char(20));
CREATE TABLE
omm=# CREATE OR REPLACE FUNCTION tri_insert_func() RETURNS TRIGGER AS
omm-# $$
omm$# DECLARE
omm$# BEGIN
omm$# INSERT INTO test__des_tbl VALUES(NEW.id1, NEW.id2, NEW.name);
omm$# RETURN NEW;
omm$# END
omm$# $$ LANGUAGE PLPGSQL;
CREATE FUNCTION


```


### 1.2  在源表上创建insert触发器
```
omm=# CREATE TRIGGER insert_trigger
omm-# BEFORE INSERT ON test_src_tbl
omm-# FOR EACH ROW
omm-# EXECUTE PROCEDURE tri_insert_func();
CREATE TRIGGER
omm=# 

```

## 2.在源表上执行insert操作，查看触发操作是否生效；禁用触发器后，再次查看触发操作是否生效

### 2.1 在源表上执行insert操作，查看触发操作是否生效
```
omm=# insert into test_src_tbl values(1,33,'tank');
INSERT 0 1
omm=# select * from test__des_tbl;
 id1 | id2 |         name         
-----+-----+----------------------
   1 |  33 | tank                
(1 row)

                      
omm=# select * from test_src_tbl;
 id1 | id2 |         name         
-----+-----+----------------------
   1 |  33 | tank                
(1 row)




```


### 2.2 禁用触发器后，再次查看触发操作是否生效


```
omm=# ALTER TABLE test_src_tbl DISABLE TRIGGER insert_trigger;
ALTER TABLE


omm=# insert into test_src_tbl values(3,33,'tank');
INSERT 0 1
omm=# select * from test__des_tbl;
 id1 | id2 |         name         
-----+-----+----------------------
   1 |  33 | tank                
(1 row)

omm=#  select * from test_src_tbl;
 id1 | id2 |         name         
-----+-----+----------------------
   1 |  33 | tank                
   3 |  33 | tank                
(2 rows)

omm=# 

```

## 3.使用系统表PG_TRIGGER和\dS+查看触发器
```
omm=# select * from PG_TRIGGER;
 tgrelid |     tgname     | tgfoid | tgtype | tgenabled | tgisinternal | tgconstrrelid | tgconstrindid | tgconstraint | tgdefe
rrable | tginitdeferred | tgnargs | tgattr | tgargs | tgqual | tgowner 
---------+----------------+--------+--------+-----------+--------------+---------------+---------------+--------------+-------
-------+----------------+---------+--------+--------+--------+---------
   16389 | insert_trigger |  16395 |      7 | D         | f            |             0 |             0 |            0 | f     
       | f              |       0 |        | \x     |        |      10
(1 row)




omm=# \dS+  test_src_tbl
                        Table "public.test_src_tbl"
 Column |     Type      | Modifiers | Storage  | Stats target | Description 
--------+---------------+-----------+----------+--------------+-------------
 id1    | integer       |           | plain    |              | 
 id2    | integer       |           | plain    |              | 
 name   | character(20) |           | extended |              | 
Disabled triggers:
    insert_trigger_renamed BEFORE INSERT ON test_src_tbl FOR EACH ROW EXECUTE PROCEDURE tri_insert_func()
Has OIDs: no
Options: orientation=row, compression=no

omm=#




```




## 4.重命名触发器

```
omm=# ALTER TRIGGER insert_trigger ON test_src_tbl RENAME TO insert_trigger_renamed;
ALTER TRIGGER
omm=# 

```


## 5.删除触发器

```
omm=# DROP TRIGGER insert_trigger_renamed ON test_src_tbl;
DROP TRIGGER
omm=# 

```