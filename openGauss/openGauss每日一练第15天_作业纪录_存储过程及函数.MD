
  ## 心得体会  

  今天学习了openGauss定义存储过程和函数、 带返回值函数、参数函数、函数的调用。

## 课后作业  

## 1.创建带有入参和出参的函数1，调用函数时使用按参数值传递和命名标记法传参

### 1.1 创建带有入参和出参的函数1
```
CREATE FUNCTION func_mcl_sql(num1 IN integer, num2 IN integer, res OUT integer)
 RETURN integer
AS
 BEGIN
res := num1 * num2;
 END;
 /

```

### 1.2 调用函数时使用按参数值传递
```
tank=# CALL func_mcl_sql(2,6,1);
 res 
-----
  12
(1 row)

```

### 1.3 调用函数时命名标记法传参
```
tank=# CALL func_mcl_sql(num1 => 2,num2 => 6,res => 1);
 res 
-----
  12
(1 row)

```


## 2.创建返回类型为record的函数2，重命名函数2

### 2.1 创建返回类型为record的函数2
```
CREATE OR REPLACE FUNCTION func_record_sql2(i int, out result_1 bigint, out result_2
bigint)
returns SETOF RECORD
as $$
begin
result_1 = i + 1 + i;
result_2 = i * 10 * i;
return next;
end;
$$language plpgsql;





tank=# call func_record_sql2(5, 0, 0);
 result_1 | result_2 
----------+----------
       11 |      250
(1 row)

tank=# 


```

### 2.2 重命名函数2

```
tank=# \df
                                                                List of functions
 Schema |       Name       | Result data type |                 Argument data types                 |  Type  | fencedmode | propackage | prokind 
--------+------------------+------------------+-----------------------------------------------------+--------+------------+------------+---------
 public | func_add_sql     | integer          | num1 integer, num2 integer, OUT res integer         | normal | f          | f          | f
 public | func_mcl_sql     | integer          | num1 integer, num2 integer, OUT res integer         | normal | f          | f          | f
 public | func_record_sql2 | SETOF record     | i integer, OUT result_1 bigint, OUT result_2 bigint | normal | f          | f          | f
(3 rows)

tank=#  ALTER FUNCTION func_mcl_sql (INTEGER, INTEGER) RENAME TO func_mcl_tow_number;
ALTER FUNCTION
tank=# \df
                                                                 List of functions
 Schema |        Name         | Result data type |                 Argument data types                 |  Type  | fencedmode | propackage | prokind 
--------+---------------------+------------------+-----------------------------------------------------+--------+------------+------------+---------
 public | func_add_sql        | integer          | num1 integer, num2 integer, OUT res integer         | normal | f          | f          | f
 public | func_mcl_tow_number | integer          | num1 integer, num2 integer, OUT res integer         | normal | f          | f          | f
 public | func_record_sql2    | SETOF record     | i integer, OUT result_1 bigint, OUT result_2 bigint | normal | f          | f          | f
(3 rows)

tank=# 



```


## 3.使用\sf和系统函数查看函数定义

```
tank=# \sf func_mcl_tow_number
CREATE OR REPLACE FUNCTION public.func_mcl_tow_number(num1 IN integer, num2 IN integer, res OUT integer)
 RETURN integer NOT FENCED NOT SHIPPABLE
AS  DECLARE  BEGIN
res := num1 * num2;
 END;
/
tank=# select * from pg_proc where proname = 'func_mcl_tow_number';
       proname       | pronamespace | proowner | prolang | procost | prorows | provariadic | protransform | proisagg | proiswindow | prosecdef | proleakproof | proisstrict | proretset | provolatile | pronargs |
 pronargdefaults | prorettype | proargtypes | proallargtypes | proargmodes |   proargnames   | proargdefaults |       prosrc        | probin | proconfig | proacl | prodefaultargpos | fencedmode | proshippable |
 propackage | prokind |                     proargsrc                     | propackageid | proisprivate | proargtypesext | prodefaultargposext 
---------------------+--------------+----------+---------+---------+---------+-------------+--------------+----------+-------------+-----------+--------------+-------------+-----------+-------------+----------+
-----------------+------------+-------------+----------------+-------------+-----------------+----------------+---------------------+--------+-----------+--------+------------------+------------+--------------+
------------+---------+---------------------------------------------------+--------------+--------------+----------------+---------------------
 func_mcl_tow_number |         2200 |       10 |   11900 |     100 |       0 |           0 | -            | f        | f           | f         | f            | f           | f         | v           |        2 |
               0 |         23 | 23 23       | {23,23,23}     | {i,i,o}     | {num1,num2,res} |                |  DECLARE  BEGIN    +|        |           |        |                  | f          | f            |
 f          | f       | num1 IN integer, num2 IN integer, res OUT integer |            0 | f            |                | 
                     |              |          |         |         |         |             |              |          |             |           |              |             |           |             |          |
                 |            |             |                |             |                 |                | res := num1 * num2;+|        |           |        |                  |            |              |
            |         |                                                   |              |              |                | 
                     |              |          |         |         |         |             |              |          |             |           |              |             |           |             |          |
                 |            |             |                |             |                 |                |  END                |        |           |        |                  |            |              |
            |         |                                                   |              |              |                | 
(1 row)

tank=# 


```

4.删除函数

```
tank=# DROP FUNCTION func_add_sql 
tank-# ;
DROP FUNCTION
tank=# drop function func_mcl_tow_number ;
DROP FUNCTION
tank=# drop function  func_record_sql2 ;
DROP FUNCTION
tank=# 


```


