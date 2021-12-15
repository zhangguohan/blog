
  ## 心得体会  

  今天学习了openGauss导入数据、INSERT语句导入、MERGE INTO语句导入、load from语句导入。

## 课后作业  

## 1.创建表1并在表中插入数据，分别指定字段和整行为缺省值

### 1.1 新建一个测试表test
```

omm=# CREATE TABLE test1
(
id integer,
name character(16),
add character(100) default 'gd'
);
insert into test1 values(1, 'tank', 'guangdong');
omm-# omm(# omm(# omm(# omm(# CREATE TABLE
omm=# INSERT 0 1
omm=# insert into test1 values(2, 'tank', 'guangdondsdfdf');
INSERT 0 1
omm=# 
insert into test1 values(3, 'tzahng', 'guangdong');omm=# 
INSERT 0 1
                      
omm=# select * from test1;
 id |       name       |                                                 add                                                  
----+------------------+------------------------------------------------------------------------------------------------------
  1 | tank             | guangdong                                                                                           
  2 | tank             | guangdondsdfdf                                                                                      
  3 | tzahng           | guangdong                                                                                           
(3 rows)

omm=# 

```

### 1.2 指定字段插入数据

```
omm=# insert into test1 values(4,'pao');
INSERT 0 1
s)

omm=# 
omm=# select * from test1;
 id |       name       |                                                 add                                                  
----+------------------+------------------------------------------------------------------------------------------------------
  1 | tank             | guangdong                                                                                           
  2 | tank             | guangdondsdfdf                                                                                      
  3 | tzahng           | guangdong                                                                                           
  4 | pao              | gd                                                                                                  
(4 rows)

omm=# 


```

### 1.3整行为缺省值

```

omm=# select * from test1;
 id |       name       |                                                 add                                                  
----+------------------+------------------------------------------------------------------------------------------------------
omm=#   1 | tank             | guangdong                                                                                           
  2 | tank             | guangdondsdfdf                                                                                      
  3 | tzahng           | guangdong                                                                                           
  4 | pao              | gd                                                                                                  
    |                  | gd                                                                                                  
(5 rows)


```



## 2.创建表2并将表1的数据全部导入表2中

```
omm=# insert into test2 select * from test1;
INSERT 0 5
omm=# select * from test1;
 id |       name       |                                                 add                                                  
----+------------------+------------------------------------------------------------------------------------------------------
  1 | tank             | guangdong                                                                                           
  2 | tank             | guangdondsdfdf                                                                                      
  3 | tzahng           | guangdong                                                                                           
  4 | pao              | gd                                                                                                  
    |                  | gd                                                                                                  
(5 rows)

omm=# select * from test2;
 id |       name       |                                                 add                                                  
----+------------------+------------------------------------------------------------------------------------------------------
  1 | tank             | guangdong                                                                                           
  2 | tank             | guangdondsdfdf                                                                                      
  3 | tzahng           | guangdong                                                                                           
  4 | pao              | gd                                                                                                  
omm=#     |                  | gd                                                                                                  
(5 rows)




```

## 3.创建表3和表4，并合并两个表的数据到表3

```

omm=# select * from test3;
 id |       name       |                                                 add                                                  
----+------------------+------------------------------------------------------------------------------------------------------
  1 | tank             | guangdong                                                                                           
  2 | tank             | guangdondsdfdf                                                                                      
  3 | tzahng           | guangdong                                                                                           
  4 | pao              | gd                                                                                                  
    |                  | gd                                                                                                  
(5 rows)

omm=# select * from test4;
 id |       name       |                                                 add                                                  
----+------------------+------------------------------------------------------------------------------------------------------
  1 | tank             | This test4                                                                                          
  2 | ta               | This test4                                                                                          
(2 rows)

 
omm=# USING test4 t4
omm-# MERGE INTO test3 t3
ON (t3.id = t4.id )
WHEN MATCHED THEN
omm-# omm-# UPDATE SET t3.name= t4.name, t3.add = t4.add
omm-# WHEN NOT MATCHED THEN
omm-# INSERT VALUES (t4.id, t4.name, t4.add) ;
MERGE 2
omm=#  select * from test3;
 id |       name       |                                                 add                                                  
----+------------------+------------------------------------------------------------------------------------------------------
  3 | tzahng           | guangdong                                                                                           
  4 | pao              | gd                                                                                                  
    |                  | gd                                                                                                  
  1 | tank             | This test4                                                                                          
  2 | ta               | This test4                                                                                          
(5 rows)


```


## 4.将表3的数据输出到文件，再将文件中的数据导入到表5

```

omm=# copy test3 to '/home/omm/test3.dat';
COPY 6
omm=# CREATE TABLE test5 (LIKE test3);
CREATE TABLE
omm=# select * from test5;
 id | name | add 
----+------+-----
(0 rows)

omm=# copy test5 from '/home/omm/test3.dat';
COPY 6
omm=# select * from test5;
 id |       name       |                                                 add                                                  
----+------------------+------------------------------------------------------------------------------------------------------
omm=#   4 | pao              | gd                                                                                                  
    |                  | gd                                                                                                  
  1 | tank             | This test4                                                                                          
  2 | ta               | This test4                                                                                          
  6 | tadd             | This test4                                                                                          
  3 | tadddddd         | This test4                                                                                          
(6 rows)





```