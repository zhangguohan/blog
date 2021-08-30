要求: 机器需要能访问外网

### 配置docker阿里云yum源 (直接执行下面的命令即可)
~~~

cat >>/etc/yum.repos.d/docker.repo<<EOF
[docker-ce-edge]
name=Docker CE Edge - \$basearch
baseurl=https://mirrors.aliyun.com/docker-ce/linux/centos/7/\$basearch/edge
enabled=1
gpgcheck=1
gpgkey=https://mirrors.aliyun.com/docker-ce/linux/centos/gpg
EOF

~~~

### yum 方式安装 docker
~~~
yum -y install docker-ce
~~~

### 查看docker版本
~~~
docker --version  
~~~
### 启动docker
~~~
systemctl enable docker
systemctl start docker
~~~

### 配置阿里云docke仓库加速

~~~

## more /etc/docker/daemon.json 

{
  "registry-mirrors": ["https://a4gh5fxo.mirror.aliyuncs.com"]
}

~~~



### 配置ELP源安装python3

~~~
/etc/yum.repos.d/epel.repo .

yum install python36

~~~



### 配置pip使用清华源


~~~

Linux/Mac os 环境中，配置文件位置在 ~/.pip/pip.conf（如果不存在创建该目录和文件）：
mkdir ~/.pip
打开配置文件 ~/.pip/pip.conf，修改如下：

[global]
index-url = https://pypi.tuna.tsinghua.edu.cn/simple
[install]
trusted-host = https://pypi.tuna.tsinghua.edu.cn

~~~
###查看 镜像地址：
~~~
$ pip3 config list   
global.index-url='https://pypi.tuna.tsinghua.edu.cn/simple'
install.trusted-host='https://pypi.tuna.tsinghua.edu.cn'
~~~



### 安装docker-compose

~~~
#python3 -m pip install --upgrade pip
#pip3 install docker-compose

~~~





