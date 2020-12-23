Oracle 性能相关常用脚本(SQL)
在缺乏的可视化工具来监控数据库性能的情形下，常用的脚本就派上用场了，下面提供几个关于Oracle性能相关的脚本供大家参考。以下脚本均在Oracle 10g测试通过，Oracle 11g可能要做相应调整。

 

1、寻找最多BUFFER_GETS开销的SQL 语句

```
--filename: top_sql_by_buffer_gets.sql  
--Identify heavy SQL (Get the SQL with heavy BUFFER_GETS)  
SET LINESIZE 190  
COL sql_text FORMAT a100 WRAP  
SET PAGESIZE 100  
  
SELECT *  
  FROM (  SELECT sql_text,  
                 sql_id,  
                 executions,  
                 disk_reads,  
                 buffer_gets  
            FROM v$sqlarea  
           WHERE DECODE (executions, 0, buffer_gets, buffer_gets / executions) >  
                    (SELECT AVG (DECODE (executions, 0, buffer_gets, buffer_gets / executions))  
                            + STDDEV (DECODE (executions, 0, buffer_gets, buffer_gets / executions))  
                       FROM v$sqlarea)  
                 AND parsing_user_id != 3D  
        ORDER BY 5 DESC) x  /*更正@20140613,原来为order by 4,感谢网友lmalds指正*/  
 WHERE ROWNUM <= 10;


```

  
2、寻找最多DISK_READS开销的SQL 语句

```
--filename:top_sql_disk_reads.sql  
--Identify heavy SQL (Get the SQL with heavy DISK_READS)  
SET LINESIZE 190  
COL sql_text FORMAT a100 WRAP  
SET PAGESIZE 100  
  
SELECT *  
  FROM (  SELECT sql_text,  
                 sql_id,  
                 executions,  
                 disk_reads,  
                 buffer_gets  
            FROM v$sqlarea  
           WHERE DECODE (executions, 0, disk_reads, disk_reads / executions) >  
                    (SELECT AVG (DECODE (executions, 0, disk_reads, disk_reads / executions))  
                            + STDDEV (DECODE (executions, 0, disk_reads, disk_reads / executions))  
                       FROM v$sqlarea)  
                 AND parsing_user_id != 3D  
        ORDER BY 4 DESC) x  /* 更正@20140613,原来为order by 3,谢谢网友lmalds指正*/  
 WHERE ROWNUM <= 10;

```
  
3、寻找最近30分钟导致资源过高开销的事件

```
--filename:top_event_in_30_min.sql  
--Last 30 minutes result those resources that are in high demand on your system.  
SET LINESIZE 180  
COL event FORMAT a60  
COL total_wait_time FORMAT 999999999999999999  
  
  SELECT active_session_history.event,  
         SUM (  
            active_session_history.wait_time  
            + active_session_history.time_waited)  
            total_wait_time  
    FROM v$active_session_history active_session_history  
   WHERE active_session_history.sample_time BETWEEN SYSDATE - 60 / 2880  
                                                AND SYSDATE  
         AND active_session_history.event IS NOT NULL  
GROUP BY active_session_history.event  
ORDER BY 2 DESC;

```
  
4、查找最近30分钟内等待最多的用户

```
--filename:top_wait_by_user.sql  
--What user is waiting the most?  
  
SET LINESIZE 180  
COL event FORMAT a60  
COL total_wait_time FORMAT 999999999999999999  
  
  SELECT ss.sid,  
         NVL (ss.username, 'oracle') AS username,  
         SUM (ash.wait_time + ash.time_waited) total_wait_time  
    FROM v$active_session_history ash, v$session ss  
   WHERE ash.sample_time BETWEEN SYSDATE - 60 / 2880 AND SYSDATE AND ash.session_id = ss.sid  
GROUP BY ss.sid, ss.username  
ORDER BY 3 DESC;


```  
5、查找30分钟消耗最多资源的SQL语句

```

--filename:top_sql_by_wait.sql  
-- What SQL is currently using the most resources?  
SET LINESIZE 180  
COL sql_text FORMAT a90 WRAP  
COL username FORMAT a20 WRAP  
SET PAGESIZE 200  
  
SELECT *  
  FROM (  SELECT sqlarea.sql_text,  
                 dba_users.username,  
                 sqlarea.sql_id,  
                 SUM (active_session_history.wait_time + active_session_history.time_waited)  
                    total_wait_time  
            FROM v$active_session_history active_session_history, v$sqlarea sqlarea, dba_users  
           WHERE     active_session_history.sample_time BETWEEN SYSDATE - 60 / 2880 AND SYSDATE  
                 AND active_session_history.sql_id = sqlarea.sql_id  
                 AND active_session_history.user_id = dba_users.user_id  
        GROUP BY active_session_history.user_id,  
                 sqlarea.sql_text,  
                 sqlarea.sql_id,  
                 dba_users.username  
        ORDER BY 4 DESC) x  
 WHERE ROWNUM <= 11;  

```
6、等待最多的对象

```
--filename:top_object_by_wait.sql  
--What object is currently causing the highest resource waits?  
SET LINESIZE 180  
COLUMN OBJECT_NAME FORMAT a30  
COLUMN EVENT FORMAT a30  
  
  SELECT dba_objects.object_name,  
         dba_objects.object_type,  
         active_session_history.event,  
         SUM (active_session_history.wait_time + active_session_history.time_waited) ttl_wait_time  
    FROM v$active_session_history active_session_history, dba_objects  
   WHERE active_session_history.sample_time BETWEEN SYSDATE - 60 / 2880 AND SYSDATE  
         AND active_session_history.current_obj# = dba_objects.object_id  
GROUP BY dba_objects.object_name, dba_objects.object_type, active_session_history.event  
ORDER BY 4 DESC; 

``` 
7、寻找基于指定时间范围内的历史SQL语句

```
--注该查询受到awr快照相关参数的影响  
-- filename:top_sql_in_spec_time.sql  
--Top SQLs Elaps time and CPU time in a given time range..  
--X.ELAPSED_TIME/1000000 => From Micro second to second  
--X.ELAPSED_TIME/1000000/X.EXECUTIONS_DELTA => How many times the sql ran  
  
SET PAUSE ON  
SET PAUSE 'Press Return To Continue'  
SET LINESIZE 180  
COL sql_text FORMAT a80 WRAP  
  
  SELECT sql_text,  
         dhst.sql_id,  
         ROUND (x.elapsed_time / 1000000 / x.executions_delta, 3) elapsed_time_sec,  
         ROUND (x.cpu_time / 1000000 / x.executions_delta, 3) cpu_time_sec,  
         x.elapsed_time,  
         x.cpu_time,  
         executions_delta AS exec_delta  
    FROM dba_hist_sqltext dhst,  
         (  SELECT dhss.sql_id sql_id,  
                   SUM (dhss.cpu_time_delta) cpu_time,  
                   SUM (dhss.elapsed_time_delta) elapsed_time,  
                   CASE SUM (dhss.executions_delta) WHEN 0 THEN 1 ELSE SUM (dhss.executions_delta) END  
                      AS executions_delta  
              FROM dba_hist_sqlstat dhss  
             WHERE dhss.snap_id IN  
                      (SELECT snap_id  
                         FROM dba_hist_snapshot  
                        WHERE begin_interval_time >= TO_DATE ('&input_start_date', 'YYYYMMDD HH24:MI')  
                              AND end_interval_time <= TO_DATE ('&input_end_date', 'YYYYMMDD HH24:MI'))  
          GROUP BY dhss.sql_id) x  
   WHERE x.sql_id = dhst.sql_id  
ORDER BY elapsed_time_sec DESC;  

```

8、寻找基于指定时间范围内及指定用户的历史SQL语句


```
--注该查询受到awr快照相关参数的影响  
--Author : Robinson  
--Blog   : http://blog.csdn.net/robinson_0612  
  
SELECT DBMS_LOB.SUBSTR (sql_text, 4000, 1) AS sql,  
         ROUND (x.elapsed_time / 1000000, 2) elapsed_time_sec,  
         ROUND (x.cpu_time / 1000000, 2) cpu_time_sec,  
         x.executions_delta AS exec_num,  
         ROUND ( (x.elapsed_time / 1000000) / x.executions_delta, 2) AS exec_time_per_query_sec  
    FROM dba_hist_sqltext dhst,  
         (  SELECT dhss.sql_id sql_id,  
                   SUM (dhss.cpu_time_delta) cpu_time,  
                   SUM (dhss.elapsed_time_delta) elapsed_time,  
                   CASE SUM (dhss.executions_delta) WHEN 0 THEN 1 ELSE SUM (dhss.executions_delta) END  
                      AS executions_delta  
              --DHSS.EXECUTIONS_DELTA = No of queries execution (per hour)  
              FROM dba_hist_sqlstat dhss  
             WHERE dhss.snap_id IN  
                      (SELECT snap_id  
                         FROM dba_hist_snapshot  
                        WHERE begin_interval_time >= TO_DATE ('&input_start_date', 'YYYYMMDD HH24:MI')  
                              AND end_interval_time <= TO_DATE ('&input_end_date', 'YYYYMMDD HH24:MI'))  
                   AND dhss.parsing_schema_name LIKE UPPER ('%&input_username%')  
          GROUP BY dhss.sql_id) x  
   WHERE x.sql_id = dhst.sql_id  
ORDER BY elapsed_time_sec DESC;  


```
9、SQL语句被执行的次数

```
--exe_delta表明在指定时间内增长的次数  
-- filename: sql_exec_num.sql  
-- How many Times a query executed?  
SET LINESIZE 180  
SET VERIFY OFF  
  
  SELECT TO_CHAR (s.begin_interval_time, 'yyyymmdd hh24:mi:ss'),  
         sql.sql_id AS sql_id,  
         sql.executions_delta AS exe_delta,  
         sql.executions_total  
    FROM dba_hist_sqlstat sql, dba_hist_snapshot s  
   WHERE     sql_id = '&input_sql_id'  
         AND s.snap_id = sql.snap_id  
         AND s.begin_interval_time > TO_DATE ('&input_start_date', 'YYYYMMDD HH24:MI')  
         AND s.begin_interval_time < TO_DATE ('&input_end_date', 'YYYYMMDD HH24:MI')  
ORDER BY s.begin_interval_time;  


```

转：http://blog.csdn.net/leshami/article/details/8904804