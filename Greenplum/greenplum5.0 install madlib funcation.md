## Greenplum 5.0 install MADlib ##

### 1、下载Mablib代码

    https://network.pivotal.io/products/pivotal-gpdb#/releases/6876/file_groups/675

### 2、Untar the file

    tar -xvf ossv1.12_pv1.9.9_gpdb5.0-rhel5-x86_64.tar.gz

### 3、 Using gppkg , install the MADlib.


#### [gpadmin@mdw tmp]$ gppkg -i madlib-ossv1.12_pv1.9.9_gpdb5.0-rhel5-x86_64.gppkg 

```
20170909:10:18:14:029213 gppkg:mdw:gpadmin-[INFO]:-Starting gppkg with args: -i madlib-ossv1.12_pv1.9.9_gpdb5.0-rhel5-x86_64.gppkg
20170909:10:18:14:029213 gppkg:mdw:gpadmin-[INFO]:-Installing package madlib-ossv1.12_pv1.9.9_gpdb5.0-rhel5-x86_64.gppkg

20170909:10:18:14:029213 gppkg:mdw:gpadmin-[INFO]:-Validating rpm installation cmdStr='rpm --test -i /usr/local/gpdb-5.0.0/.tmp/madlib-1.12-1.x86_64.rpm --dbpath /usr/local/gpdb-5.0.0/share/packages/database --prefix /usr/local/gpdb-5.0.0'
20170909:10:18:30:029213 gppkg:mdw:gpadmin-[INFO]:-Installing madlib-ossv1.12_pv1.9.9_gpdb5.0-rhel5-x86_64.gppkg locally
20170909:10:18:31:029213 gppkg:mdw:gpadmin-[INFO]:-Validating rpm installation cmdStr='rpm --test -i /usr/local/gpdb-5.0.0/.tmp/madlib-1.12-1.x86_64.rpm --dbpath /usr/local/gpdb-5.0.0/share/packages/database --prefix /usr/local/gpdb-5.0.0'
20170909:10:18:31:029213 gppkg:mdw:gpadmin-[INFO]:-Installing rpms cmdStr='rpm -i /usr/local/gpdb-5.0.0/.tmp/madlib-1.12-1.x86_64.rpm --dbpath /usr/local/gpdb-5.0.0/share/packages/database --prefix=/usr/local/gpdb-5.0.0'
20170909:10:18:32:029213 gppkg:mdw:gpadmin-[INFO]:-Completed local installation of madlib-ossv1.12_pv1.9.9_gpdb5.0-rhel5-x86_64.gppkg.
20170909:10:18:33:029213 gppkg:mdw:gpadmin-[INFO]:-Please run the following command to deploy MADlib

usage:  madpack install [-s schema_name] -p greenplum -c user@host:port/database
Example:
       $ $GPHOME/madlib/bin/madpack install -s madlib -p greenplum -c gpadmin@mdw:5432/testdb
       This will install MADlib objects into a Greenplum database named "testdb"
       running on server "mdw" on port 5432. Installer will try to login as "gpadmin"
       and will prompt for password. The target schema will be "madlib".
       To upgrade to a new version of MADlib from version v1.0 or later, use option "upgrade",
       instead of "install" 
For additional options run:
$ madpack --help

Release notes and additional documentation can be found at http://madlib.apache.org
20170909:10:18:33:029213 gppkg:mdw:gpadmin-[INFO]:-madlib-ossv1.12_pv1.9.9_gpdb5.0-rhel5-x86_64.gppkg successfully installed.
````


#### [gpadmin@mdw tmp]$ $GPHOME/madlib/bin/madpack install -s madlib -p greenplum -c gpadmin@mdw:5432/tank

````
madpack.py : INFO : Detected Greenplum DB version 5.0.0.
madpack.py : INFO : *** Installing MADlib ***
madpack.py : INFO : MADlib tools version    = 1.12 (/usr/local/gpdb-5.0.0/madlib/Versions/1.12/bin/../madpack/madpack.py)
madpack.py : INFO : MADlib database version = None (host=mdw:5432, db=tank, schema=madlib)
madpack.py : INFO : Testing PL/Python environment...
madpack.py : INFO : > Creating language PL/Python...
madpack.py : INFO : > PL/Python environment OK (version: 2.7.5)
madpack.py : INFO : Installing MADlib into MADLIB schema...
madpack.py : INFO : > Creating MADLIB schema
madpack.py : INFO : > Creating MADLIB.MigrationHistory table
madpack.py : INFO : > Writing version info in MigrationHistory table
madpack.py : INFO : > Creating objects for modules:
madpack.py : INFO : > - array_ops
madpack.py : INFO : > - bayes
madpack.py : INFO : > - crf
madpack.py : INFO : > - elastic_net
madpack.py : INFO : > - linalg
madpack.py : INFO : > - pmml
madpack.py : INFO : > - prob
madpack.py : INFO : > - sketch
madpack.py : INFO : > - svec
madpack.py : INFO : > - svm
madpack.py : INFO : > - tsa
madpack.py : INFO : > - stemmer
madpack.py : INFO : > - conjugate_gradient
madpack.py : INFO : > - knn
madpack.py : INFO : > - lda
madpack.py : INFO : > - stats
madpack.py : INFO : > - svec_util
madpack.py : INFO : > - utilities
madpack.py : INFO : > - assoc_rules
madpack.py : INFO : > - convex
madpack.py : INFO : > - glm
madpack.py : INFO : > - graph
madpack.py : INFO : > - linear_systems
madpack.py : INFO : > - recursive_partitioning
madpack.py : INFO : > - regress
madpack.py : INFO : > - sample
madpack.py : INFO : > - summary
madpack.py : INFO : > - kmeans
madpack.py : INFO : > - pca
madpack.py : INFO : > - validation
madpack.py : INFO : MADlib 1.12 installed successfully in MADLIB schema.
````
[gpadmin@mdw tmp]$ 



### 4、Verify the installation. 



#### [gpadmin@mdw tmp]$ $GPHOME/madlib/bin/madpack install-check -p greenplum -c gpadmin@mdw:5432/tank
````
madpack.py : INFO : Detected Greenplum DB version 4.3.
TEST CASE RESULT|Module: array_ops|array_ops.sql_in|PASS|Time: 274 milliseconds
TEST CASE RESULT|Module: bayes|gaussian_naive_bayes.sql_in|PASS|Time: 2261 milliseconds
TEST CASE RESULT|Module: bayes|bayes.sql_in|PASS|Time: 6847 milliseconds
TEST CASE RESULT|Module: crf|crf_train_small.sql_in|PASS|Time: 2463 milliseconds
TEST CASE RESULT|Module: crf|crf_train_large.sql_in|PASS|Time: 3544 milliseconds
TEST CASE RESULT|Module: crf|crf_test_small.sql_in|PASS|Time: 2702 milliseconds
[....]
[....]
TEST CASE RESULT|Module: sample|sample.sql_in|PASS|Time: 382 milliseconds
TEST CASE RESULT|Module: summary|summary.sql_in|PASS|Time: 1531 milliseconds
TEST CASE RESULT|Module: kmeans|kmeans.sql_in|PASS|Time: 5192 milliseconds
TEST CASE RESULT|Module: pca|pca_project.sql_in|PASS|Time: 13731 milliseconds
TEST CASE RESULT|Module: pca|pca.sql_in|PASS|Time: 23499 milliseconds
TEST CASE RESULT|Module: validation|cross_validation.sql_in|PASS|Time: 1377 milliseconds
````

### 5、Connect to the database and check if the schema exists as shown here:

#### [gpadmin@mdw tmp]$ psql -d tank
````
psql (8.3.23)
Type "help" for help.

tank=# \dn madlib
 List of schemas
  Name  |  Owner  
--------+---------
 madlib | gpadmin
(1 row)

tank=# \df madlib.chi2_gof_test
                                     List of functions
 Schema |     Name      |    Result data type     |       Argument data types        | Type 
--------+---------------+-------------------------+----------------------------------+------
 madlib | chi2_gof_test | madlib.chi2_test_result | bigint                           | agg
 madlib | chi2_gof_test | madlib.chi2_test_result | bigint, double precision         | agg
 madlib | chi2_gof_test | madlib.chi2_test_result | bigint, double precision, bigint | agg
(3 rows)

````

### 6、Create the sample table and data set as shown below:
#### tank=# create table testing_madlib ( a bigint , b bigint );
````
NOTICE:  Table doesn't have 'DISTRIBUTED BY' clause -- Using column named 'a' as the Greenplum Database data distribution key for this table.
HINT:  The 'DISTRIBUTED BY' clause determines the distribution of data. Make sure column(s) chosen are the optimal data distribution key to minimize skew.
CREATE TABLE
tank=# insert into testing_madlib values ( generate_series(1,10000),generate_series(1,10000)); 
INSERT 0 10000
tank=# 
tank=# 
tank=#  CREATE TABLE testing_madlib2 (
tank(#    a SERIAL,
tank(#    b VARCHAR,
tank(#    c BIGINT,
tank(#    d DOUBLE PRECISION
tank(#  );
NOTICE:  CREATE TABLE will create implicit sequence "testing_madlib2_a_seq" for serial column "testing_madlib2.a"
NOTICE:  Table doesn't have 'DISTRIBUTED BY' clause -- Using column named 'a' as the Greenplum Database data distribution key for this table.
HINT:  The 'DISTRIBUTED BY' clause determines the distribution of data. Make sure column(s) chosen are the optimal data distribution key to minimize skew.
CREATE TABLE
tank=# 
tank=# 
tank=# INSERT INTO testing_madlib2(b, c, d) VALUES
tank-#   ('A', 34, 12.90),
tank-#    ('B', 65, 90.45),
tank-#     ('C', 78, 67.45),
tank-#    ('D', 32, 10.16);
INSERT 0 4
tank=#  SELECT (madlib.chi2_gof_test(a, b)).* FROM testing_madlib ;
 statistic | p_value |  df  | phi | contingency_coef 
-----------+---------+------+-----+------------------
         0 |       1 | 9999 |   0 |                0
(1 row)

tank=# SELECT (madlib.chi2_gof_test(c, d)).* FROM testing_madlib2;
    statistic     |       p_value        | df |       phi        | contingency_coef  
------------------+----------------------+----+------------------+-------------------
 74.3979432356668 | 4.87681735844096e-16 |  3 | 4.31271211755627 | 0.974155148485107
(1 row)

tank=# 

