
  ## 心得体会  

  今天学习了使用gs_dump导出数据、导出数据库定义、导出数据库数据、导出指定表、导出指定schema数据。

## 课后作业  

## 1.创建数据库tpcc，在数据库tpcc中创建模式schema1，在模式schema1中建表products

### 1.1 创建数据库tpcc
```
omm=# create database tpcc;
CREATE DATABASE
omm=# 


```

### 1.2 在数据库tpcc中创建模式schema1

```
tpcc=# create schema schema1;
CREATE SCHEMA
tpcc=# 


```

### 1.3在模式schema1中建表products

```
                                   ^
tpcc=# create table schema1.tank(id int,name char(20));
CREATE TABLE
tpcc=# insert into schema1.tank values(1, 'tank');
insert into schema1.tank values(2, 'zhang');

insert into schema1.tank values(3, 'guank');INSERT 0 1

tpcc=# select * from schema1.tank;
 id |         name         
----+----------------------
  1 | tank                
  2 | zhang               
  3 | guank               
(3 rows)

tpcc=# 




```



## 2.使用gs_dump工具以文本格式导出数据库tpcc的全量数据

```
omm@c61f169bc7d9:~$ gs_dump -f /home/omm/tpcc_database_all.sql tpcc -F p
gs_dump[port='5432'][tpcc][2021-12-14 13:32:54]: The total objects number is 391.
gs_dump[port='5432'][tpcc][2021-12-14 13:32:54]: [100.00%] 391 objects have been dumped.
gs_dump[port='5432'][tpcc][2021-12-14 13:32:54]: dump database tpcc successfully
gs_dump[port='5432'][tpcc][2021-12-14 13:32:54]: total time: 1767  ms
omm@c61f169bc7d9:~$ more /home/omm/tpcc_database_all.sql 





```

## 3.使用gs_dump工具以文本格式导出模式schema1的定义

```
omm@c61f169bc7d9:~$ gs_dump -f /home/omm/tpcc_schema_define.sql  tpcc -n schema1 -s -F p
gs_dump[port='5432'][tpcc][2021-12-14 13:33:42]: The total objects number is 381.
gs_dump[port='5432'][tpcc][2021-12-14 13:33:42]: [100.00%] 381 objects have been dumped.
gs_dump[port='5432'][tpcc][2021-12-14 13:33:42]: dump database tpcc successfully
gs_dump[port='5432'][tpcc][2021-12-14 13:33:42]: total time: 1626  ms
omm@c61f169bc7d9:~$ more /home/omm/tpcc_schema_define.sql
--
-- openGauss database dump
--

SET statement_timeout = 0;
SET xmloption = content;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: schema1; Type: SCHEMA; Schema: -; Owner: omm
--

CREATE SCHEMA schema1;


ALTER SCHEMA schema1 OWNER TO omm;

SET search_path = schema1;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: tank; Type: TABLE; Schema: schema1; Owner: omm; Tablespace: 
--

CREATE TABLE tank (
    id integer,
    name character(20)
)
WITH (orientation=row, compression=no);


ALTER TABLE schema1.tank OWNER TO omm;

--
-- openGauss database dump complete
--



```


## 4.使用gs_dump工具以文本格式导出数据库tpcc的数据，不包含定义

```
omm@c61f169bc7d9:~$  gs_dump -f /home/omm/tpcc_database_data.sql tpcc -a -F p 

gs_dump[port='5432'][tpcc][2021-12-14 13:35:20]: dump database tpcc successfully
gs_dump[port='5432'][tpcc][2021-12-14 13:35:20]: total time: 1552  ms
omm@c61f169bc7d9:~$ 
omm@c61f169bc7d9:~$ more /home/omm/tpcc_database_data.sql 
--
-- openGauss database dump
--

SET statement_timeout = 0;
SET xmloption = content;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = schema1;

--
-- Data for Name: tank; Type: TABLE DATA; Schema: schema1; Owner: omm
--

COPY tank (id, name) FROM stdin;
1	tank                
2	zhang               
3	guank               
\.
;

--
-- openGauss database dump complete
--

omm@c61f169bc7d9:~$ 


```

## 5.删除表、模式和数据库
```
omm=# \c tpcc
Non-SSL connection (SSL connection is recommended when requiring high-security)
You are now connected to database "tpcc" as user "omm".
tpcc=# drop table schema1.tank;
DROP TABLE

tpcc=# drop schema schema1;
DROP SCHEMA

omm=# drop database tpcc;
DROP DATABASE
omm=# 


```