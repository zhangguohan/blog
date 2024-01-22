# Postgersql-13 Archiving and Point-in-Time Recovery (PITR)

## 1、Create replication user

```
psql#CREATE ROLE replication WITH REPLICATION PASSWORD '6220104' LOGIN; 
```

## 2、Modification pg_hba.conf

```
# TYPE  DATABASE        USER            ADDRESS                 METHOD

# IPv4 local connections:
host    all             all             127.0.0.1/32            md5
# IPv6 local connections:
host    all             all             ::1/128                 md5
# Allow replication connections from localhost, by a user with the
# replication privilege.
host    replication     all             127.0.0.1/32            trust
host    replication     all             ::1/128                 md5
```

## 3、Enable Archiving And Replication（postgresql.conf）

```
#------------------------------------------------------------------------------
# WRITE-AHEAD LOG
#------------------------------------------------------------------------------

# - Settings -

wal_level = replica            

# - Archiving -

archive_mode = on        
                
archive_command = 'test ! -f /backup/archivelogs/%f && cp %p /backup/archivelogs/%f'    

# - Sending Servers -

# Set these on the master and on any standby that will send replication data.

max_wal_senders = 10        

# - Standby Servers -

# These settings are ignored on a master server.

hot_standby = on            

        
```

- Modification pg_hba.conf、postgrsql.conf Reoot postgresql Server

## 4、Exec Create Basebackup

```
[postgres@centos7-01 archivelogs]$ pg_basebackup -D /backup/backup202019 -Ft -z -P -v -w -h 127.0.0.1   -U replication
pg_basebackup: initiating base backup, waiting for checkpoint to complete
pg_basebackup: checkpoint completed
pg_basebackup: write-ahead log start point: 0/3000028 on timeline 1
pg_basebackup: starting background WAL receiver
pg_basebackup: created temporary replication slot "pg_basebackup_1465"
25393/25393 kB (100%), 1/1 tablespace                                         
pg_basebackup: write-ahead log end point: 0/3000100
pg_basebackup: waiting for background process to finish streaming ...
pg_basebackup: syncing data to disk ...
pg_basebackup: renaming backup_manifest.tmp to backup_manifest
pg_basebackup: base backup completed
```

## 5、Recovering Using a Continuous Archive Backup

1、 In to backupdir tar base.tar.gz in to the cluster data directory
2、In to backupdir tar pg_wal.tar.gz file copy file data direcorty into pg_wal
3、copy old data/pg_wal/0*file them into pg_wal/

4、 Modify postgredsql.conf

```
# expect them to be interchangeable.
#
restore_command = 'cp /backup/archivelogs/%f %p'    

recovery_target_time = '2020-10-19 02:48:30 '    # e.g. '2004-07-14 22:39:00 EST'
#
```

5、In to data direcorty create recovery.signal

## 6、Start Postgresql

/usr/local/pg13/bin/pg_ctl -D /usr/local/pg13/data -l logfile start

```
2020-10-19 03:26:06.710 EDT [1695] LOG:  starting PostgreSQL 13.0 on x86_64-pc-linux-gnu, compiled by gcc (GCC) 4.8.5 20150623 (Red Hat 4.8.5-39), 64-bit
2020-10-19 03:26:06.711 EDT [1695] LOG:  listening on IPv4 address "0.0.0.0", port 5432
2020-10-19 03:26:06.711 EDT [1695] LOG:  listening on IPv6 address "::", port 5432
2020-10-19 03:26:06.719 EDT [1695] LOG:  listening on Unix socket "/tmp/.s.PGSQL.5432"
2020-10-19 03:26:06.730 EDT [1696] LOG:  database system was interrupted; last known up at 2020-10-19 02:44:57 EDT
cp: cannot stat ‘/backup/archivelogs/00000002.history’: No such file or directory
2020-10-19 03:26:07.531 EDT [1696] LOG:  starting point-in-time recovery to 2020-10-19 02:48:30-04
2020-10-19 03:26:07.571 EDT [1696] LOG:  restored log file "000000010000000000000003" from archive
2020-10-19 03:26:07.713 EDT [1696] LOG:  redo starts at 0/3000028
2020-10-19 03:26:07.717 EDT [1696] LOG:  consistent recovery state reached at 0/3000100
2020-10-19 03:26:07.717 EDT [1695] LOG:  database system is ready to accept read only connections
2020-10-19 03:26:07.756 EDT [1696] LOG:  restored log file "000000010000000000000004" from archive
2020-10-19 03:26:08.537 EDT [1696] LOG:  recovery stopping before commit of transaction 501, time 2020-10-19 02:49:14.163901-04
2020-10-19 03:26:08.537 EDT [1696] LOG:  pausing at the end of recovery
2020-10-19 03:26:08.537 EDT [1696] HINT:  Execute pg_wal_replay_resume() to promote.
```

## 7、Exec pg_wal_replay_resume()

```
psql# select pg_wal_replay_resume() ;
```

## 8、Check logfile

```
2020-10-19 03:26:08.537 EDT [1696] LOG:  pausing at the end of recovery
2020-10-19 03:26:08.537 EDT [1696] HINT:  Execute pg_wal_replay_resume() to promote.
2020-10-19 03:26:36.051 EDT [1696] LOG:  redo done at 0/458D700
2020-10-19 03:26:36.051 EDT [1696] LOG:  last completed transaction was at log time 2020-10-19 02:48:29.489896-04
cp: cannot stat ‘/backup/archivelogs/00000002.history’: No such file or directory
2020-10-19 03:26:36.070 EDT [1696] LOG:  selected new timeline ID: 2
2020-10-19 03:26:36.329 EDT [1696] LOG:  archive recovery complete
cp: cannot stat ‘/backup/archivelogs/00000001.history’: No such file or directory
2020-10-19 03:26:36.441 EDT [1695] LOG:  database system is ready to accept connections
```

At this point, the Postgresql database has retreated to the specified time state.

标签: none