# Oracle 手动更新指定用户统计信息

```sql
SQL> exec dbms_stats.gather_schema_stats(ownname => 'WDGGS',options => 'GATHER AUTO', estimate_percent => dbms_stats.auto_sample_size,method_opt => 'for all columns size repeat',degree =>15,cascade=>TRUE)

PL/SQL 过程已成功完成。

SQL>
```



