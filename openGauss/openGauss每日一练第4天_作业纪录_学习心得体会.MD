# openGauss每日一练第4天_作业纪录_学习心得体会

  **心得体会：**  今天学习了openGauss创建角色、修改角色属性、更改角色权限和删除角色。

## 课后作业

1、创建角色role1为系统管理员, role2指定生效日期, role3具有LOGIN属性
```
omm=# create role role1 sysadmin identified by 'tank123455#3';
NOTICE:  The encrypted password contains MD5 ciphertext, which is not secure.
CREATE ROLE

omm=# create role role2   identified by 'tank123455#3'   VALID BEGIN '2021-12-10' VALID UNTIL '2022-12-30';
NOTICE:  The encrypted password contains MD5 ciphertext, which is not secure.
CREATE ROLE
omm=# 

omm=# create role role3 login sysadmin identified by 'tank123455#3'; 
NOTICE:  The encrypted password contains MD5 ciphertext, which is not secure.
CREATE ROLE
omm=# \du
                                                              List of roles
 Role name |                                                    Attributes                                                    
| Member of 
-----------+------------------------------------------------------------------------------------------------------------------
+-----------
 gaussdb   | Sysadmin                                                                                                         
| {}
 manager20 | Sysadmin                                                                                                         
| {}
 omm       | Sysadmin, Create role, Create DB, Replication, Administer audit, Monitoradmin, Operatoradmin, Policyadmin, UseFT 
| {}
 role1     | Cannot login, Sysadmin                                                                                           
| {}
 role2     | Cannot login                                                                                                    +
| {}
           | Role valid begin 2021-12-10 00:00:00+08                                                                         +
| 
 role3     | Sysadmin                                                                                                         
| {}

           | Role valid until 2022-12-30 00:00:00+08                                                                          
| 
omm=# 




```

2.重命名role1

```
omm=# alter role role1 rename to tank;
NOTICE:  MD5 password cleared because of role rename
ALTER ROLE
omm=# 


```

3.修改role2密码

```

omm=# ALTER ROLE role2 IDENTIFIED BY 'tS!22222qqzd3' replace 'tank123455#3';
NOTICE:  The encrypted password contains MD5 ciphertext, which is not secure.
ALTER ROLE
omm=# 


```

4.将omm权限授权给role3,再回收role3的权限


```
omm=# grant omm to role3 with admin option;
GRANT ROLE
omm=# revoke all privilege from role3;
ALTER ROLE
omm=# 



```


5.删除所有创建角色过程中使用\du或\du+查看角色信息

```

omm=# drop role tank;
DROP ROLE
omm=# drop role role2;
DROP ROLE
omm=# drop role role3;
DROP ROLE
omm=# \du
                                                              List of roles
 Role name |                                                    Attributes      
                                              | Member of 
-----------+--------------------------------------------------------------------
----------------------------------------------+-----------
 gaussdb   | Sysadmin                                                           
                                              | {}
 manager20 | Sysadmin                                                           
                                              | {}
 omm       | Sysadmin, Create role, Create DB, Replication, Administer audit, Mo
nitoradmin, Operatoradmin, Policyadmin, UseFT | {}

omm=# \du+
                                                                     List of rol
es
 Role name |                                                    Attributes      
                                              | Member of | Description 
-----------+--------------------------------------------------------------------
----------------------------------------------+-----------+-------------
 gaussdb   | Sysadmin                                                           
                                              | {}        | 
 manager20 | Sysadmin                                                           
                                              | {}        | 
 omm       | Sysadmin, Create role, Create DB, Replication, Administer audit, Mo
nitoradmin, Operatoradmin, Policyadmin, UseFT | {}        | 

omm=# 



```








