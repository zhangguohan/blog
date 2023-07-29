#                            Kubeasz部署多master高可用环境集群



## K8S环境

```
108.88.3.102	k8s-master01         ## 4C4G 50G 
108.88.3.188	k8s-master02         ## 4C4G 50G 
108.88.3.165	k8s-master03         ## 4C4G 50G 
108.88.3.192	k8s-node01         ## 4C4G 50G 


```

### 系统环境：

```
[root@k8s-master01 ~]# cat /etc/redhat-release 
CentOS Linux release 7.9.2009 (Core)
```

## 设置主机名

``` 
hostnamectl set-hostname k8s-master01
hostnamectl set-hostname k8s-master02
hostnamectl set-hostname k8s-master03
hostnamectl set-hostname k8s-node01



```

### 更换为阿里云YUM源

``` 
     yum install wget
     wget -O /etc/yum.repos.d/CentOS-Base.repo https://mirrors.aliyun.com/repo/Centos-7.repo
     sed -i -e '/mirrors.cloud.aliyuncs.com/d' -e '/mirrors.aliyuncs.com/d' /etc/yum.repos.d/CentOS-Base.repo
     yum makecache 
```

### 安装必备软件

~~~
 yum install -y yum-utils device-mapper-persistent-data lvm2 git net-tools telnet wget jq psmisc 

~~~

### 关闭不必要的服务及软件

``` 
   20  systemctl  disable --now firewalld 
   22  systemctl  disable --now NetworkManager
   23  vi /etc/sysconfig/selinux  
      31  setenforce 0
      
```

## 关闭SWAP

```
# swapoff -a && sysctl -w vm.swappiness=0

#sed -i 's/.*swap.*/#&/' /etc/fstab    
```



##  部署时间同步软件

``` 
2 client 端配置
1）编辑配置文件：vim /etc/chrony.conf
将其他 server x.x.x.x iburst 去除，添加：server 108.88.3.249 iburst

2）重启服务：systemctl restart chronyd

```



## 优化内核参数


```


ulimit -SHn 65535

 vi /etc/security/limits.conf 

* soft nofile 655360
* hard nofile 655360
* soft nproc 655360
* hard nproc 655360 
* soft memlock unlimited
* hard memlock unlimited
```
 以上在所节点运行



## 配置 master01为免密登录

```
[root@k8s-master01 ~]# ssh-keygen -t rsa
Generating public/private rsa key pair.
Enter file in which to save the key (/root/.ssh/id_rsa): 
Created directory '/root/.ssh'.
Enter passphrase (empty for no passphrase): 
Enter same passphrase again: 
Your identification has been saved in /root/.ssh/id_rsa.
Your public key has been saved in /root/.ssh/id_rsa.pub.
The key fingerprint is:
SHA256:HvmtuLJnAvJ6B0STTsSSxEiWJF5bKgblZKK/CLgj3/Q root@k8s-master01
The key's randomart image is:
+---[RSA 2048]----+
|*O*=.o           |
|B*= X            |
|.+.B .           |
|o.. o    .       |
|o ..    S        |
|.o..o  . o .     |
|= .o.o  . . .    |
|.o oo.+ o. .     |
|  oo..E*o..      |
+----[SHA256]-----+
You have new mail in /var/spool/mail/root
[root@k8s-master01 ~]# 
```



## 将公钥发到所有节点

```
[root@k8s-master01 ~]# for i in  k8s-master01  k8s-master02  k8s-master03 k8s-node01; do ssh-copy-id -i .ssh/id_rsa.pub $i;done;

```







## 升级系统排除内核

```
yum update -y --exclude=kernel*


安装最新kernel


现在我们就来看看在 CentOS 7 系统上安装 Kernel 4.0。

因为可以通过 ElRepo 库进行安装，是的我们要在 CentOS 7 系统上安装 Kernel 4.0 变得很容易，输入以下命令。

确认系统的内核版本号

uname -r
先切换到 root 账户，添加 Key：

su root
rpm --import https://www.elrepo.org/RPM-GPG-KEY-elrepo.org
为你的系统添加库：

yum install https://www.elrepo.org/elrepo-release-7.el7.elrepo.noarch.rpm
安装 kernel-ml 包：

yum --enablerepo=elrepo-kernel install kernel-ml
查看默认启动顺序

awk -F\' '$1=="menuentry " {print $2}' /etc/grub2.cfg
默认启动的顺序是从0开始，新内核是从头插入（目前位置在0，而4.4.4的是在1），所以需要选择0

grub2-set-default 0
重启：

reboot
卸载 kernel 4.0命令：

yum remove kernel-ml
```



## K8s 内核参数优化

```
[root@k8s-master01 ~]# more /etc/sysctl.d/k8s.conf 
net.ipv4.tcp_keepalive_time=600
net.ipv4.tcp_keepalive_intvl=30
net.ipv4.tcp_keepalive_probes=10
net.ipv6.conf.all.disable_ipv6=1
net.ipv6.conf.default.disable_ipv6=1
net.ipv6.conf.lo.disable_ipv6=1
net.ipv4.neigh.default.gc_stale_time=120
net.ipv4.conf.all.rp_filter=0 
net.ipv4.conf.default.rp_filter=0
net.ipv4.conf.default.arp_announce=2
net.ipv4.conf.lo.arp_announce=2
net.ipv4.conf.all.arp_announce=2
net.ipv4.ip_local_port_range= 45001 65000
net.ipv4.ip_forward=1
net.ipv4.tcp_max_tw_buckets=6000
net.ipv4.tcp_syncookies=1
net.ipv4.tcp_synack_retries=2
net.bridge.bridge-nf-call-ip6tables=1
net.bridge.bridge-nf-call-iptables=1
net.netfilter.nf_conntrack_max=2310720
net.ipv6.neigh.default.gc_thresh1=8192
net.ipv6.neigh.default.gc_thresh2=32768
net.ipv6.neigh.default.gc_thresh3=65536
net.core.netdev_max_backlog=16384
net.core.rmem_max = 16777216 
net.core.wmem_max = 16777216
net.ipv4.tcp_max_syn_backlog = 8096 
net.core.somaxconn = 32768 
fs.inotify.max_user_instances=8192 
fs.inotify.max_user_watches=524288 
fs.file-max=52706963
fs.nr_open=52706963
kernel.pid_max = 4194303
net.bridge.bridge-nf-call-arptables=1
vm.swappiness=0 
vm.overcommit_memory=1 
vm.panic_on_oom=0 
vm.max_map_count = 262144

[root@k8s-master01 ~]# 
## 重启检查ipvs模块

reboot

[root@k8s-master01 ~]#  lsmod |grep --color=auto -e ip_vs -e nf_conntrack
```

# 基本组件安装

## 安装 添加EPEL源

```
 [root@k8s-master01 yum.repos.d]# more epel.repo 
[epel]
name=Extra Packages for Enterprise Linux 7 - $basearch
baseurl=http://mirrors.aliyun.com/epel/7/$basearch
failovermethod=priority
enabled=1
gpgcheck=0
gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-EPEL-7
 
[epel-debuginfo]
name=Extra Packages for Enterprise Linux 7 - $basearch - Debug
baseurl=http://mirrors.aliyun.com/epel/7/$basearch/debug
failovermethod=priority
enabled=0
gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-EPEL-7
gpgcheck=0
 
[epel-source]
name=Extra Packages for Enterprise Linux 7 - $basearch - Source
baseurl=http://mirrors.aliyun.com/epel/7/SRPMS
failovermethod=priority
enabled=0
gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-EPEL-7
gpgcheck=0
[root@k8s-master01 yum.repos.d]# 

```

### 安装Ansiable



```
yum install ansible -y
```



### 下载安装kubeasz

```

4.在部署节点编排k8s安装
4.1 下载项目源码、二进制及离线镜像
下载工具脚本ezdown，举例使用kubeasz版本3.5.0

export release=3.5.2
wget https://github.com/easzlab/kubeasz/releases/download/${release}/ezdown
chmod +x ./ezdown
下载kubeasz代码、二进制、默认容器镜像（更多关于ezdown的参数，运行./ezdown 查看）

# 国内环境
./ezdown -D
# 海外环境
#./ezdown -D -m standard
【可选】下载额外容器镜像（cilium,flannel,prometheus等）

./ezdown -X
【可选】下载离线系统包 (适用于无法使用yum/apt仓库情形)

./ezdown -P
上述脚本运行成功后，所有文件（kubeasz代码、二进制、离线镜像）均已整理好放入目录/etc/kubeasz

4.2 创建集群配置实例
# 容器化运行kubeasz
./ezdown -S

# 创建新集群 k8s-01
docker exec -it kubeasz ezctl new k8s-01
2021-01-19 10:48:23 DEBUG generate custom cluster files in /etc/kubeasz/clusters/k8s-01
2021-01-19 10:48:23 DEBUG set version of common plugins
2021-01-19 10:48:23 DEBUG cluster k8s-01: files successfully created.
2021-01-19 10:48:23 INFO next steps 1: to config '/etc/kubeasz/clusters/k8s-01/hosts'
2021-01-19 10:48:23 INFO next steps 2: to config '/etc/kubeasz/clusters/k8s-01/config.yml'
然后根据提示配置'/etc/kubeasz/clusters/k8s-01/hosts' 和 '/etc/kubeasz/clusters/k8s-01/config.yml'：根据前面节点规划修改hosts 文件和其他集群层面的主要配置选项；其他集群组件等配置项可以在config.yml 文件中修改。



4.3 开始安装 如果你对集群安装流程不熟悉，请阅读项目首页 安装步骤 讲解后分步安装，并对 每步都进行验证
#建议使用alias命令，查看~/.bashrc 文件应该包含：alias dk='docker exec -it kubeasz'
source ~/.bashrc

# 一键安装，等价于执行docker exec -it kubeasz ezctl setup k8s-01 all
dk ezctl setup k8s-01 all

# 或者分步安装，具体使用 dk ezctl help setup 查看分步安装帮助信息
# dk ezctl setup k8s-01 01
# dk ezctl setup k8s-01 02
# dk ezctl setup k8s-01 03
# dk ezctl setup k8s-01 04
...

[root@k8s-master01 k8s]# kubectl get node -o wide
NAME           STATUS                     ROLES    AGE   VERSION   INTERNAL-IP    EXTERNAL-IP   OS-IMAGE                KERNEL-VERSION              CONTAINER-RUNTIME
k8s-master01   Ready,SchedulingDisabled   master   15h   v1.26.1   108.88.3.102   <none>        CentOS Linux 7 (Core)   6.2.3-1.el7.elrepo.x86_64   containerd://1.6.14
k8s-master02   Ready,SchedulingDisabled   master   15h   v1.26.1   108.88.3.188   <none>        CentOS Linux 7 (Core)   6.2.3-1.el7.elrepo.x86_64   containerd://1.6.14
k8s-master03   Ready,SchedulingDisabled   master   15h   v1.26.1   108.88.3.165   <none>        CentOS Linux 7 (Core)   6.2.3-1.el7.elrepo.x86_64   containerd://1.6.14
k8s-node01     Ready                      node     15h   v1.26.1   108.88.3.192   <none>        CentOS Linux 7 (Core)   6.2.3-1.el7.elrepo.x86_64   containerd://1.6.14
[root@k8s-master01 k8s]# 



```





## 验证集群



1. Pod 必须能解释 Service

   ```
   [root@k8s-master01 k8s]# kubectl get svc
   NAME         TYPE        CLUSTER-IP   EXTERNAL-IP   PORT(S)   AGE
   kubernetes   ClusterIP   10.68.0.1    <none>        443/TCP   15h
   [root@k8s-master01 k8s]# 
   
   [root@k8s-master01 k8s]# kubectl exec -i busybox -n default nslookup kubernetes
   kubectl exec [POD] [COMMAND] is DEPRECATED and will be removed in a future version. Use kubectl exec [POD] -- [COMMAND] instead.
   Server:    169.254.20.10
   Address 1: 169.254.20.10
   
   Name:      kubernetes
   Address 1: 10.68.0.1 kubernetes.default.svc.cluster.local
   [root@k8s-master01 k8s]# 
    
   
   ```

    以上测试正常

   

2. Pod 必须能解释跨namespace的 Service

   ```
   [root@k8s-master01 k8s]#  kubectl get svc -n kube-system
   NAME                        TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)                  AGE
   dashboard-metrics-scraper   ClusterIP   10.68.156.224   <none>        8000/TCP                 14h
   kube-dns                    ClusterIP   10.68.0.2       <none>        53/UDP,53/TCP,9153/TCP   14h
   kube-dns-upstream           ClusterIP   10.68.109.3     <none>        53/UDP,53/TCP            14h
   kubernetes-dashboard        NodePort    10.68.6.141     <none>        443:31576/TCP            14h
   metrics-server              ClusterIP   10.68.184.95    <none>        443/TCP                  14h
   node-local-dns              ClusterIP   None            <none>        9253/TCP                 14h
   [root@k8s-master01 k8s]# 
   
   [root@k8s-master01 k8s]# kubectl exec -i busybox -n default nslookup kube-dns.kube-system
   kubectl exec [POD] [COMMAND] is DEPRECATED and will be removed in a future version. Use kubectl exec [POD] -- [COMMAND] instead.
   Server:    169.254.20.10
   Address 1: 169.254.20.10
   
   Name:      kube-dns.kube-system
   Address 1: 10.68.0.2 kube-dns.kube-system.svc.cluster.local
   [root@k8s-master01 k8s]# 
   
   
   ```

   以上测试正常

3. 每个节点都必须要能访问kubernets的 kubernets svc 443和kub-dns的service 53端口

   ```
   [root@k8s-master01 k8s]# telnet 10.68.0.1 443
   Trying 10.68.0.1...
   Connected to 10.68.0.1.
   Escape character is '^]'.
   ^
   ^}
   Connection closed by foreign host.
   [root@k8s-master01 k8s]# telnet 10.68.0.2 53
   Trying 10.68.0.2...
   Connected to 10.68.0.2.
   Escape character is '^]'.
   
   Connection closed by foreign host.
   [root@k8s-master01 k8s]# 
   
   
   ```
   
   以上测试正常
   
   

4. Pod和Pod之间要能通信

   - 同namespace能通信

     ```
     [root@k8s-master01 k8s]# kubectl get pod  -o wide
     NAME      READY   STATUS    RESTARTS       AGE    IP              NODE         NOMINATED NODE   READINESS GATES
     busybox   1/1     Running   14 (49m ago)   14h    172.20.85.219   k8s-node01   <none>           <none>
     nginx     1/1     Running   0              2m4s   172.20.85.221   k8s-node01   <none>           <none>
     [root@k8s-master01 k8s]# kubectl exec -it busybox -- sh
     / # ping 172.20.85.221
     PING 172.20.85.221 (172.20.85.221): 56 data bytes
     64 bytes from 172.20.85.221: seq=0 ttl=63 time=0.260 ms
     64 bytes from 172.20.85.221: seq=1 ttl=63 time=0.208 ms
     64 bytes from 172.20.85.221: seq=2 ttl=63 time=0.363 ms
     ^X64 bytes from 172.20.85.221: seq=3 ttl=63 time=0.179 ms
     64 bytes from 172.20.85.221: seq=4 ttl=63 time=0.224 ms
     ^C
     --- 172.20.85.221 ping statistics ---
     5 packets transmitted, 5 packets received, 0% packet loss
     round-trip min/avg/max = 0.179/0.246/0.363 ms
     / # ^C
     / # 
     
     
     ```
   
     
   
   - 跨namespace能通信
   
     ```
     [root@k8s-master01 k8s]# kubectl get pod -n kuboard -o wide
     NAME                              READY   STATUS    RESTARTS       AGE   IP              NODE           NOMINATED NODE   READINESS GATES
     kuboard-agent-2-d6d8796cb-x8vbd   1/1     Running   2 (160m ago)   14h   172.20.85.208   k8s-node01     <none>           <none>
     kuboard-agent-bbf94cc8b-rgv96     1/1     Running   2 (163m ago)   14h   172.20.85.209   k8s-node01     <none>           <none>
     kuboard-etcd-czhqd                1/1     Running   0              14h   108.88.3.188    k8s-master02   <none>           <none>
     kuboard-v3-5676ffcf57-mv6x7       1/1     Running   2 (164m ago)   14h   172.20.85.210   k8s-node01     <none>           <none>
     [root@k8s-master01 k8s]# kubectl exec -it busybox -- sh
     / # ping 172.20.85.210 
     PING 172.20.85.210 (172.20.85.210): 56 data bytes
     64 bytes from 172.20.85.210: seq=0 ttl=63 time=0.262 ms
     64 bytes from 172.20.85.210: seq=1 ttl=63 time=0.202 ms
     64 bytes from 172.20.85.210: seq=2 ttl=63 time=0.167 ms
     ^C
     --- 172.20.85.210 ping statistics ---
     3 packets transmitted, 3 packets received, 0% packet loss
     round-trip min/avg/max = 0.167/0.210/0.262 ms
     / # 
     
     
     ```
## 安装Kuboard


     [root@k8s-master01 k8s]# kubectl label nodes k8s-master01 k8s.kuboard.cn/role=etcd
     
     kubectl apply -f https://addons.kuboard.cn/kuboard/kuboard-v3-swr.yaml
     
     [root@k8s-master01 k8s]#

## 安装Helm 



     [root@k8s-master01 k8s]# wget -c https://get.helm.sh/helm-v3.11.2-linux-amd64.tar.gz
     
     [root@k8s-master01 k8s]# tar zxvf helm-v3.11.2-linux-amd64.tar.gz
     [root@k8s-master01 k8s]# mv linux-amd64/helm /usr/local/bin/
     [root@k8s-master01 k8s]# helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx


## 安装Ingress-nginx     


          添加ingress-nginx仓库 
          helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
          更新helm仓库
          helm repo update
          查看版本：
         helm search repo ingress-nginx/ingress-nginx -l
         默认下载最新版本（如无法下载请自行翻墙下载）
           helm repo pull ingress-nginx/ingress-nginx 
        解压安装包
        cd /opt/charts/
        tar -zxvf ingress-nginx-4.5.2.tgz
        修改配置文件 values.yaml
        $ vi  ingress-nginx/values.yaml


​        
​        

### 修改配置文件 values.yaml

#### 修改controller镜象 

```
$ vi  ingress-nginx/values.yam      使用可用镜象仓库 v5cn， 禁用digest


controller:
    name: controller
    image:
        ## Keep false as default for now!
        chroot: false
        registry: v5cn
        image: ingress-nginx-controller
        ## for backwards compatibility consider setting the full image url via the repository value below
        ## use *either* current default registry/image or repository format or installing chart by providing the values.yaml will fail
        ## repository:
        tag: "v1.6.4"
        #digest: sha256:15be4666c53052484dd2992efacf2f50ea77a78ae8aa21ccd91af6baaa7ea22f
        #digestChroot: sha256:0de01e2c316c3ca7847ca13b32d077af7910d07f21a4a82f81061839764f8f81
        pullPolicy: IfNotPresent

```
#### 修改 hostNetwork 的值为 true

```
  Required for use with CNI based kubernetes installations (such as ones set up by kubeadm),
    # since CNI and hostport don't mix yet. Can be deprecated once https://github.com/kubernetes/kubernetes/issues/23920
    # is merged
    hostNetwork: true
    ## Use host ports 80 and 443
    ## Disabled by default

```
#### 修改 dnsPolicy 的值为 ClusterFirstWithHostNet
```
    # By default, while using host network, name resolution uses the host's DNS. If you wish nginx-controller
    # to keep resolving names inside the k8s network, use ClusterFirstWithHostNet.
    dnsPolicy: ClusterFirstWithHostNet
    # -- Bare-metal considerations via the host network https://kubernetes.github.io/ingress-nginx/deploy/baremetal/#via-the-host-network
    # Ingress status was blank because there is no Service exposing the NGINX Ingress controller in a configu
```
#### nodeSelector 添加标签: ingress: "true"，用于部署 ingress-controller 到指定节点
```
    # -- Node labels for controller pod assignment
    ## Ref: https://kubernetes.io/docs/user-guide/node-selection/
    ##
    nodeSelector:
        kubernetes.io/os: linux
        ingress: "true"

```
#### 修改 kind 类型为 DaemonSet
```
    # -- Use a `DaemonSet` or `Deployment`
    kind: DaemonSet
    # -- Annotations to be added to the controller Deployment or DaemonSet

```

#### 修改 kube-webhook-certgen 的镜像地址为国内仓库 registry.aliyuncs.com/google_containers
```
            enabled: true
            image:
                registry: registry.aliyuncs.com/google_containers
                image: kube-webhook-certgen
                ## for backwards compatibility consider setting the full image url via the repository value below
                ## use *either* current default registry/image or repository format or installing chart by providing the values.yaml will fail
                ## repository:
                tag: v20220916-gd32f8c343
                #digest: sha256:39c5b2e3310dc4264d638ad28d9d1d96c4cbb2b2dcfb52368fe4e3c63f61e10f
                pullPolicy: IfNotPresent
                
## Default 404 backend
##
defaultBackend:
    ##
    enabled: false
    name: defaultbackend
    image:
        registry: registry.aliyuncs.com/google_containers
        image: defaultbackend-amd64
        ## for backwards compatibility consider setting the full image url via the repository value below
        ## use *either* current default registry/image or repository format or installing chart by providing the values.yaml will fail
        ## repository:
        tag: "1.5"
        pullPolicy: IfNotPresent

```
#### 修改 service 类型为 NodePort
```
        ipFamilies:
            - IPv4
        ports:
            http: 80
            https: 443
        targetPorts:
            http: http
            https: https
        type: NodePort
        ## type: NodePort
        ## nodePorts:
        ##   http: 32080
        ##   https: 32443
        ##   tcp:
        ##     8080: 32808
        nodePorts:
            http: "32080"
            https: "32443"
            tcp: {}
            udp: {}
        external:
            enabled: true

```

#### 安装ingress-nginx
```
打签标
#kubectl label node k8s-master01  ingress=true
#kubectl label node k8s-master02  ingress=true
#kubectl label node k8s-master03  ingress=true
#kubectl label node k8s-node01  ingress=true

[root@k8s-master01 charts]#  helm install ingress-nginx --create-namespace --namespace ingress-nginx -f ./ingress-nginx/values.yaml ./ingress-nginx
NAME: ingress-nginx
LAST DEPLOYED: Tue Mar 14 06:03:46 2023
NAMESPACE: ingress-nginx
STATUS: deployed
REVISION: 1
TEST SUITE: None
NOTES:
The ingress-nginx controller has been installed.
Get the application URL by running these commands:
  export HTTP_NODE_PORT=32080
  export HTTPS_NODE_PORT=32443
  export NODE_IP=$(kubectl --namespace ingress-nginx get nodes -o jsonpath="{.items[0].status.addresses[1].address}")

  echo "Visit http://$NODE_IP:$HTTP_NODE_PORT to access your application via HTTP."
  echo "Visit https://$NODE_IP:$HTTPS_NODE_PORT to access your application via HTTPS."

An example Ingress that makes use of the controller:
  apiVersion: networking.k8s.io/v1
  kind: Ingress
  metadata:
    name: example
    namespace: foo
  spec:
    ingressClassName: nginx
    rules:
      - host: www.example.com
        http:
          paths:
            - pathType: Prefix
              backend:
                service:
                  name: exampleService
                  port:
                    number: 80
              path: /
    # This section is only required if TLS is to be enabled for the Ingress
    tls:
      - hosts:
        - www.example.com
        secretName: example-tls

If TLS is enabled for the Ingress, a Secret containing the certificate and key must also be provided:

  apiVersion: v1
  kind: Secret
  metadata:
    name: example-tls
    namespace: foo
  data:
    tls.crt: <base64 encoded cert>
    tls.key: <base64 encoded key>
  type: kubernetes.io/tls

```
#### 查看安装结果
```
[root@k8s-master01 charts]#  kubectl get all -n ingress-nginx
NAME                                 READY   STATUS    RESTARTS   AGE
pod/ingress-nginx-controller-88qfl   1/1     Running   0          3m4s
pod/ingress-nginx-controller-hh7dq   1/1     Running   0          3m4s
pod/ingress-nginx-controller-mn89w   1/1     Running   0          3m4s
pod/ingress-nginx-controller-vfgld   1/1     Running   0          3m4s

NAME                                         TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)                      AGE
service/ingress-nginx-controller             NodePort    10.68.211.212   <none>        80:32080/TCP,443:32443/TCP   3m4s
service/ingress-nginx-controller-admission   ClusterIP   10.68.155.190   <none>        443/TCP                      3m4s

NAME                                      DESIRED   CURRENT   READY   UP-TO-DATE   AVAILABLE   NODE SELECTOR                         AGE
daemonset.apps/ingress-nginx-controller   4         4         4       4            4           ingress=true,kubernetes.io/os=linux   3m4s
[root@k8s-master01 charts]# 

```

### 测试使用ingress-nginx反向代理
```
[root@k8s-master01 ingress-test]# kubectl apply -f test-ngress-web.yaml 
deployment.apps/nginx created
service/nginx-svc created
[root@k8s-master01 ingress-test]# kubectl apply -f ingress-services.yaml 
ingress.networking.k8s.io/nginx created
[root@k8s-master01 ingress-test]# more test-ngress-web.yaml 
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx
spec:
  replicas: 2
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: myweb
        image: nginx
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 80
 
---
apiVersion: v1
kind: Service
metadata:
  name: nginx-svc
spec:
  selector:
    app: nginx
  ports:
  - protocol: TCP
    port: 80
    targetPort: 80

[root@k8s-master01 ingress-test]# more ingress-services.yaml 
  apiVersion: networking.k8s.io/v1
  kind: Ingress
  metadata:
    name: nginx
  spec:
    ingressClassName: nginx
    rules:
      - host: vue.gzgi.com
        http:
          paths:
            - backend:
                service:
                  name: nginx-svc
                  port:
                    number: 80
              path: /
              pathType: Prefix
    # This section is only required if TLS is to be enabled for the Ingress
    #tls:
    #    - hosts:
    #        - www.example.com
    #      secretName: example-tls

---
[root@k8s-master01 ingress-test]# kubectl get svc,pod 
NAME                 TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)   AGE
service/kubernetes   ClusterIP   10.68.0.1      <none>        443/TCP   4d
service/nginx-svc    ClusterIP   10.68.54.122   <none>        80/TCP    47s

NAME                         READY   STATUS    RESTARTS   AGE
pod/busybox                  1/1     Running   92         3d23h
pod/nginx-7d6ffbc584-h6j2x   1/1     Running   0          47s
pod/nginx-7d6ffbc584-txrjh   1/1     Running   0          47s
[root@k8s-master01 ingress-test]# kubectl get all -n ingress-nginx 
NAME                                 READY   STATUS    RESTARTS   AGE
pod/ingress-nginx-controller-88qfl   1/1     Running   0          9m16s
pod/ingress-nginx-controller-hh7dq   1/1     Running   0          9m16s
pod/ingress-nginx-controller-mn89w   1/1     Running   0          9m16s
pod/ingress-nginx-controller-vfgld   1/1     Running   0          9m16s

NAME                                         TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)                      AGE
service/ingress-nginx-controller             NodePort    10.68.211.212   <none>        80:32080/TCP,443:32443/TCP   9m16s
service/ingress-nginx-controller-admission   ClusterIP   10.68.155.190   <none>        443/TCP                      9m16s

NAME                                      DESIRED   CURRENT   READY   UP-TO-DATE   AVAILABLE   NODE SELECTOR                         AGE
daemonset.apps/ingress-nginx-controller   4         4         4       4            4           ingress=true,kubernetes.io/os=linux   9m16s
[root@k8s-master01 ingress-test]# 

```

#### 修改测试客户端口hosts文件
```
修改测试客户端口hosts文件域名指向ingress-nginx节点服务器，测试通过
```



### 配置不同命名空间服务及 多域名https访问
### 部署dhec应用
```

[root@k8s-master01 ingress-multiple-ns]# more dhec-dep.yaml 
---
apiVersion: v1
kind: Namespace
metadata:
  name: dhec
---

apiVersion: apps/v1
kind: Deployment
metadata:
  name: dhec
  namespace: dhec
spec:
  replicas: 1
  selector:
    matchLabels:
      app: dhec
  template:
    metadata:
      labels:
        app: dhec
    spec:
      containers:
        - name: dhec
          image: nginx
          ports:
            - containerPort: 80
          resources:
            limits:
              memory: 1024Mi


---
apiVersion: v1
kind: Service
metadata:
  name: dhec
  namespace: dhec
spec:
  ports:
    - port: 80
      protocol: TCP
      targetPort: 80
  selector:
    app: dhec
  sessionAffinity: None
  type: ClusterIP


[root@k8s-master01 ingress-multiple-ns]#


```

#### 导入dhec证书 注意需要选择命名空间为
```
[root@k8s-master01 dhec-TLS]# ll
total 20
-rw-r--r-- 1 root root 5766 Nov  4 11:13 dhec.crt
-rw-r--r-- 1 root root 1675 Nov  3 10:31 dhec.key
-rw-r--r-- 1 root root 5330 Mar 15 04:52 for Nginx.zip
[root@k8s-master01 dhec-TLS]# kubectl create secret tls dhec-tls --key dhec.key --cert dhec.crt -n dhec
secret/dhec-tls created
[root@k8s-master01 dhec-TLS]# 
[root@k8s-master01 ingress-multiple-ns]# kubectl apply -f dhec-dep.yaml 


```
#### 部署dhec.com.cn ingress服务
```

[root@k8s-master01 ingress-multiple-ns]# ll
total 8
-rw-r--r-- 1 root root 672 Mar 15 04:40 dhec-dep.yaml
-rw-r--r-- 1 root root 429 Mar 15 05:11 dhec-ingress.yaml
drwxr-xr-x 2 root root  59 Mar 15 04:57 dhec-TLS
[root@k8s-master01 ingress-multiple-ns]# more dhec-ingress.yaml 
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: dhec
  namespace: dhec
spec:
  ingressClassName: nginx
  rules:
    - host: www.dhec.com.cn
      http:
        paths:
          - backend:
              service:
                name: dhec
                port:
                  number: 80
            path: /
            pathType: Prefix
  tls:
    - hosts:
        - www.dhec.com.cn
      secretName: dhec-tls


[root@k8s-master01 ingress-multiple-ns]# kubectl apply -f dhec-ingress.yaml 
ingress.networking.k8s.io/dhec created
```

#### 修改hosts指向ingress控制器所在IP地址测试
```
108.88.3.192 www.dhec.com.cn
```

### 部署chamsrm应用
```
[root@k8s-master01 ingress-multiple-ns]# more chamsrm-dep.yaml 
---
apiVersion: v1
kind: Namespace
metadata:
  name: chamsrm
---

apiVersion: apps/v1
kind: Deployment
metadata:
  name: chamsrm
  namespace: chamsrm
spec:
  replicas: 1
  selector:
    matchLabels:
      app: chamsrm
  template:
    metadata:
      labels:
        app: chamsrm
    spec:
      containers:
        - name: chamsrm
          image: nginx
          ports:
            - containerPort: 80
          resources:
            limits:
              memory: 1024Mi


---
apiVersion: v1
kind: Service
metadata:
  name: chamsrm
  namespace: chamsrm
spec:
  ports:
    - port: 80
      protocol: TCP
      targetPort: 80
  selector:
    app: chamsrm
  sessionAffinity: None
  type: ClusterIP


[root@k8s-master01 ingress-multiple-ns]# kubectl apply -f chamsrm-dep.yaml 

```
#### 导入chamsrm证书 注意需要选择命名空间为chamsrm

```
[root@k8s-master01 ingress-multiple-ns]#  kubectl create secret tls chamsrm-tls --key chamsrm.com.key --cert chamsrm.com.crt -n chamsrm 

```
#### 部署chamsrm.com ingress服务

```
[root@k8s-master01 ingress-multiple-ns]# more chamsrm-ingress.yaml 
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: chamsrm
  namespace: chamsrm
  annotations:
    nginx.ingress.kubernetes.io/proxy-connect-timeout: "600"
    nginx.ingress.kubernetes.io/proxy-send-timeout: "600"
    nginx.ingress.kubernetes.io/proxy-read-timeout: "600"
    nginx.ingress.kubernetes.io/proxy-body-size: "500m"
spec:
  ingressClassName: nginx
  rules:
    - host: www.chamsrm.com
      http:
        paths:
          - backend:
              service:
                name: chamsrm
                port:
                  number: 80
            path: /
            pathType: Prefix
  tls:
    - hosts:
        - www.chamsrm.com
      secretName: chamsrm-tls
[root@k8s-master01 ingress-multiple-ns]# kubectl apply -f chamsrm-ingress.yaml 

```
#### 修改hosts指向ingress控制器所在IP地址测试
```
108.88.3.192 www.chamsrm.com
```

