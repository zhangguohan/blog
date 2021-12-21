
  ## 心得体会  

  今天学习了openGauss定义游标，为了处理SQL语句，存储过程进程分配一段内存区域来保存上下文联系，游标是指向上下文区域的句柄或指针。借助游标，存储过程可以控制上下文区域的变化。

## 课后作业  

## 1.创建游标，且使用select子句指定游标返回的行，分别使用FETCH抓取数据，MOVE重定位游标


### 1.1 创建游标，且使用select子句指定游标返回的行，分别使用FETCH抓取数据

```
omm=# CREATE TABLE test
omm-# (
omm(# id integer,
omm(# );
omm(# name character(10)
CREATE TABLE
omm=# 
omm=# insert into test values(1,'tank');
INSERT 0 1
omm=# insert into test values(2,'pank');
INSERT 0 1
omm=# start transaction;
START TRANSACTION
omm=# CURSOR cursor1 FOR SELECT * FROM test;
DECLARE CURSOR
omm=# select * from pg_cursors;
  name   |               statement                | is_holdable | is_binary | is_scrollable |        creation_time         
---------+----------------------------------------+-------------+-----------+---------------+------------------------------
 cursor1 | CURSOR cursor1 FOR SELECT * FROM test; | f           | f         | t             | 2021-12-17 15:07:01.68463+08
(1 row)

omm=# FETCH FORWARD 2 FROM cursor1;
omm=#  id |    name    
----+------------
  1 | tank      
  2 | pank      
(2 rows)



```
### 1.2 MOVE重定位游标

omm=# MOVE FORWARD 1 FROM cursor1;
MOVE 0
omm=# FETCH 4 FROM cursor1;
 id | name 
----+------
(0 rows)

omm=# CLOSE cursor1;
CLOSE CURSOR
omm=# end;
COMMIT
omm=#



## 2.在系统视图pg_cursors中查看游标
```
omm=# select * from pg_cursors;
  name   |               statement                | is_holdable | is_binary | is_scrollable |        creation_time         
---------+----------------------------------------+-------------+-----------+---------------+------------------------------
 cursor1 | CURSOR cursor1 FOR SELECT * FROM test; | f           | f         | t             | 2021-12-17 15:07:01.68463+08
(1 row)
```


## 3.创建一个使用游标的存储过程
```
create or replace procedure test_cursor_1
as
    id    int;
    name varchar(10);

    cursor c1_all is  select * from test;
begin
    if not c1_all%isopen then
        open c1_all;
    end if;
    loop
        fetch c1_all into id,name ;
                RAISE INFO 'name %' ,name;
        exit when c1_all%notfound;
    end loop;
    if c1_all%isopen then
        close c1_all;
    end if;
end;
omm-# /

CREATE PROCEDURE

omm$#call test_cursor_1();
omm=# 
INFO:  name tank
INFO:  name pank
INFO:  name pank
 test_cursor_1 
---------------
 
```

## 4.清理数据

```
omm=# drop procedure test_cursor_1;
DROP PROCEDURE

```