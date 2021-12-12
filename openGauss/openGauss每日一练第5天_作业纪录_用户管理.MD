# openGauss每日一练第5天_作业纪录_学习心得体会

  **心得体会：**  学习openGauss创建用户、修改用户属性、更改用户权限和删除用户
用户是用来登录数据库的，通过对用户赋予不同的权限，可以方便地管理用户对数据库的访问及操作
库的访问及操作。

## 课后作业

1、创建用户user1、user2和user3，user1具有CREATEROLE权限，user2具有CREATEDB权限，要求使用两种不同的方法设置密码


```
1、创建用户user1具有CREATEROLE权限
omm=# CREATE USER user1 CREATEROLE  PASSWORD 'abcd@123' ;
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
 omm       | Sysadmin, Create role, Create DB, Replication, Administer audit, Monitoradmin, Operatoradmin, Policyadmin, UseFT 
| {}
 user1     | Create role                                                                                                      
| {}

omm=# 



2、创建用户user2具有CREATEDB权限


omm=# CREATE USER user2 CREATEDB  IDENTIFIED BY 'tank@456' ;
NOTICE:  The encrypted password contains MD5 ciphertext, which is not secure.
CREATE ROLE
omm=# \du
-----------+------------------------------------------------------------------------------------------------------------------
+-----------
 gaussdb   | Sysadmin                                                                                                         
| {}
 omm       | Sysadmin, Create role, Create DB, Replication, Administer audit, Monitoradmin, Operatoradmin, Policyadmin, UseFT 
| {}
 user1     | Create role                                                                                                      
| {}
 user2     | Create DB                                                                                                        
| {}

                                                              List of roles
 Role name |                                                    Attributes                                                    
| Member of 
omm=# 


3、创建用户user3 

omm=# CREATE USER user3   PASSWORD 'tank@123' ;
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
 omm       | Sysadmin, Create role, Create DB, Replication, Administer audit, Monitoradmin, Operatoradmin, Policyadmin, UseFT 
| {}
 user1     | Create role                                                                                                      
| {}
 user2     | Create DB                                                                                                        
| {}
 user3     |                                                                                                                  
| {}

omm=# 


```

2.修改用户user1的密码

```
omm=# ALTER USER user1 IDENTIFIED BY 'Tank@2010' REPLACE 'abcd@123';
NOTICE:  The encrypted password contains MD5 ciphertext, which is not secure.
ALTER ROLE
omm=# 


```

3.重命名用户user2

```

omm=# alter user user2 rename to user4;
NOTICE:  MD5 password cleared because of role rename
ALTER ROLE
omm=# \du
                                                              List of roles
 Role name |                                                    Attributes                                                    
| Member of 
-----------+------------------------------------------------------------------------------------------------------------------
+-----------
 gaussdb   | Sysadmin                                                                                                         
| {}
 omm       | Sysadmin, Create role, Create DB, Replication, Administer audit, Monitoradmin, Operatoradmin, Policyadmin, UseFT 
| {}
 user1     | Create role                                                                                                      
| {}
 user3     |                                                                                                                  
| {}
 user4     | Create DB                                                                                                        
| {}

omm=# 

```

4.将用户user1的权限授权给用户user3，再回收用户user3的权限


```
omm=# grant user1 to user3;
GRANT ROLE
omm=# REVOKE user3 from user1;
WARNING:  role "user1" is not a member of role "user3"
REVOKE ROLE
omm=# 



```


5.删除所有创建角色过程中使用\du或\du+查看角色信息

```
omm=# drop user user1;
drop user user2;
drop user user3;
omm=# drop user user4;
DROP ROLE
omm=# \du
                                                              List of roles
 Role name |                                                    Attributes                                                    
| Member of 
-----------+------------------------------------------------------------------------------------------------------------------
+-----------
 gaussdb   | Sysadmin                                                                                                         
| {}
 omm       | Sysadmin, Create role, Create DB, Replication, Administer audit, Monitoradmin, Operatoradmin, Policyadmin, UseFT 
| {}

omm=#


```








