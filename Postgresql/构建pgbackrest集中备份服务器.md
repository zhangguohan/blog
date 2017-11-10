## 构建Pgbackrest集中备份服务器


### 一、安全配置集中备份服务器

#### 1.1 安装备份服务器软件依赖包
````
  yum -y install perl-DBD-Pg perl-JSON perl-Thread-Queue  perl-JSON-PP.noarch perl-Digest-SHA.x86_64
````

#### 1.2 新建一个备份用户
````
  useradd backrest
  passwd backrest
````

#### 1.3 安装备份软件
```` 

cp -r /root/pgbackrest-release-1.25/lib/pgBackRest        /usr/share/perl5

sudo find /usr/share/perl5/pgBackRest -type f -exec chmod 644 {} +

sudo find /usr/share/perl5/pgBackRest -type d -exec chmod 755 {} +

sudo cp /root/pgbackrest-release-1.25/bin/pgbackrest /usr/bin/pgbackrest

sudo chmod 755 /usr/bin/pgbackrest

sudo mkdir -m 770 /var/log/pgbackrest

sudo chown backrest:backrest /var/log/pgbackrest

sudo touch /etc/pgbackrest.conf

sudo chmod 640 /etc/pgbackrest.conf

sudo chown backrest:backrest /etc/pgbackrest.conf

sudo mkdir /var/lib/pgbackrest

sudo chmod 750 /var/lib/pgbackrest

sudo chown backrest:backrest /var/lib/pgbackrest

````
#### 1.4 配置SSH免密信认
````
sudo -u backrest mkdir -m 750 /home/backrest/.ssh
sudo -u backrest ssh-keygen -f /home/backrest/.ssh/id_rsa -t rsa -b 4096 -N ""
````
#### 

 951  vi /etc/hosts
  952  ping db-primary
  953  ping mdw
  954  sudo ssh root@db-primary cat /home/postgres/.ssh/id_rsa.pub |        sudo -u backrest tee -a /home/backrest/.ssh/authorized_keys
  955  sudo -u backrest ssh postgres@db-primary
  956  ssh sdw01
  957  su - postgres