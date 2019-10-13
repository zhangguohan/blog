### 安装Kubernetes

1、关闭防火墙及SElinux


     略


2、在所有节点安装docker和  kubelet kubeadm kubectl

#### 使用阿里云进行kubenetes 安装

##### 主节点安装kubernets 

~~~

CentOS / RHEL / Fedora

cat <<EOF > /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64/
enabled=1
gpgcheck=1
repo_gpgcheck=1
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
EOF
setenforce 0
yum install -y kubelet kubeadm kubectl
systemctl enable kubelet && systemctl start kubelet


~~~



##### 检查看系统网桥参数是否为1

~~~~
[root@docker01 ~]# cat /proc/sys/net/bridge/bridge-nf-call-iptables 
1
[root@docker01 ~]# cat /proc/sys/net/bridge/bridge-nf-call-ip6tables 
1
[root@docker01 ~]# 

~~~~



#### 初始化kubernets


~~~


root@docker01 ~]# kubeadm init --pod-network-cidr=10.244.0.0/16  --service-cidr=10.96.0.0/12 

I0617 02:52:54.124742   20042 version.go:96] could not fetch a Kubernetes version from the internet: unable to get URL "https://dl.k8s.io/release/stable-1.txt": Get https://dl.k8s.io/release/stable-1.txt: net/http: request canceled while waiting for connection (Client.Timeout exceeded while awaiting headers)
I0617 02:52:54.124979   20042 version.go:97] falling back to the local client version: v1.14.3
[init] Using Kubernetes version: v1.14.3
[preflight] Running pre-flight checks
	[WARNING IsDockerSystemdCheck]: detected "cgroupfs" as the Docker cgroup driver. The recommended driver is "systemd". Please follow the guide at https://kubernetes.io/docs/setup/cri/
error execution phase preflight: [preflight] Some fatal errors occurred:
	[ERROR Swap]: running with swap on is not supported. Please disable swap
[preflight] If you know what you are doing, you can make a check non-fatal with `--ignore-preflight-errors=...`
[root@docker01 ~]# 



编辑配置文件忽略swap-on
 
[root@docker01 ~]# more /etc/sysconfig/kubelet 
KUBELET_EXTRA_ARGS="--fail-swap-on=false"
[root@docker01 ~]# 


重新执行初始化：

[root@docker01 ~]# kubeadm init --pod-network-cidr=10.244.0.0/16  --service-cidr=10.96.0.0/12 --ignore-preflight-errors=Swap



[preflight] You can also perform this action in beforehand using 'kubeadm config images pull'
error execution phase preflight: [preflight] Some fatal errors occurred:
	[ERROR ImagePull]: failed to pull image k8s.gcr.io/kube-apiserver:v1.14.3: output: Error response from daemon: Get https://k8s.gcr.io/v2/: net/http: request canceled while waiting for connection (Client.Timeout exceeded while awaiting headers)
, error: exit status 1
	[ERROR ImagePull]: failed to pull image k8s.gcr.io/kube-controller-manager:v1.14.3: output: Error response from daemon: Get https://k8s.gcr.io/v2/: net/http: request canceled while waiting for connection (Client.Timeout exceeded while awaiting headers)
, error: exit status 1
	[ERROR ImagePull]: failed to pull image k8s.gcr.io/kube-scheduler:v1.14.3: output: Error response from daemon: Get https://k8s.gcr.io/v2/: net/http: request canceled while waiting for connection (Client.Timeout exceeded while awaiting headers)
, error: exit status 1
	[ERROR ImagePull]: failed to pull image k8s.gcr.io/kube-proxy:v1.14.3: output: Error response from daemon: Get https://k8s.gcr.io/v2/: net/http: request canceled while waiting for connection (Client.Timeout exceeded while awaiting headers)
, error: exit status 1
	[ERROR ImagePull]: failed to pull image k8s.gcr.io/pause:3.1: output: Error response from daemon: Get https://k8s.gcr.io/v2/: net/http: request canceled while waiting for connection (Client.Timeout exceeded while awaiting headers)
, error: exit status 1
	[ERROR ImagePull]: failed to pull image k8s.gcr.io/etcd:3.3.10: output: Error response from daemon: Get https://k8s.gcr.io/v2/: net/http: request canceled while waiting for connection (Client.Timeout exceeded while awaiting headers)
, error: exit status 1
	[ERROR ImagePull]: failed to pull image k8s.gcr.io/coredns:1.3.1: output: Error response from daemon: Get https://k8s.gcr.io/v2/: net/http: request canceled while waiting for connection (Client.Timeout exceeded while awaiting headers)
, error: exit status 1
[preflight] If you know what you are doing, you can make a check non-fatal with `--ignore-preflight-errors=...`
[root@docker01 ~]# 


docker.io仓库对google的容器做了镜像，可以通过下列命令下拉取相关镜像：


[root@docker01 ~]#  docker pull mirrorgooglecontainers/kube-apiserver:v1.14.3

v1.14.3: Pulling from mirrorgooglecontainers/kube-apiserver
6cf6a0b0da0d: Pull complete 
5c6605a1cd2a: Pull complete 
Digest: sha256:9e29be1e531b5cd395d4f73aabc39d9734b238493466f8341463d47342b8dbd4
Status: Downloaded newer image for mirrorgooglecontainers/kube-apiserver:v1.14.3


[root@docker01 ~]#  docker pull mirrorgooglecontainers/kube-controller-manager:v1.14.3
v1.14.3: Pulling from mirrorgooglecontainers/kube-controller-manager
6cf6a0b0da0d: Already exists 
f12679407558: Pull complete 
Digest: sha256:7852e602ba829f6657a6642d844bbea353c7cca3304f80f95d863e9f38582182
Status: Downloaded newer image for mirrorgooglecontainers/kube-controller-manager:v1.14.3


[root@docker01 ~]#  docker pull mirrorgooglecontainers/kube-scheduler:v1.14.3

v1.14.3: Pulling from mirrorgooglecontainers/kube-scheduler
6cf6a0b0da0d: Already exists 
c79235704dab: Pull complete 
Digest: sha256:05327634a5d7bb9b7cb53ea60b52827c384a93c7d7128e50b1e8f989aca2e9a2
Status: Downloaded newer image for mirrorgooglecontainers/kube-scheduler:v1.14.3


[root@docker01 ~]#  docker pull mirrorgooglecontainers/kube-proxy:v1.14.3
v1.14.3: Pulling from mirrorgooglecontainers/kube-proxy
6cf6a0b0da0d: Already exists 
8e1ce322a1d9: Pull complete 
8798700eba2a: Pull complete 
Digest: sha256:8901ced7a7ae88dc33fd588fcc9a63e466078067f653e522af0413cf189bb567
Status: Downloaded newer image for mirrorgooglecontainers/kube-proxy:v1.14.3

[root@docker01 ~]#  docker pull mirrorgooglecontainers/pause:3.1

3.1: Pulling from mirrorgooglecontainers/pause
67ddbfb20a22: Pull complete 
Digest: sha256:59eec8837a4d942cc19a52b8c09ea75121acc38114a2c68b98983ce9356b8610
Status: Downloaded newer image for mirrorgooglecontainers/pause:3.1


[root@docker01 ~]#  docker pull mirrorgooglecontainers/etcd:3.3.10
3.3.10: Pulling from mirrorgooglecontainers/etcd
860b4e629066: Pull complete 
3de3fe131c22: Pull complete 
12ec62a49b1f: Pull complete 
Digest: sha256:8a82adeb3d0770bfd37dd56765c64d082b6e7c6ad6a6c1fd961dc6e719ea4183
Status: Downloaded newer image for mirrorgooglecontainers/etcd:3.3.10


[root@docker01 ~]#  docker pull  coredns/coredns:1.3.1
1.3.1: Pulling from coredns/coredns
e0daa8927b68: Pull complete 
3928e47de029: Pull complete 
Digest: sha256:02382353821b12c21b062c59184e227e001079bb13ebd01f9d3270ba0fcbf1e4
Status: Downloaded newer image for coredns/coredns:1.3.1
[root@docker01 ~]# 



重新打标签

[root@docker01 ~]# docker tag docker.io/mirrorgooglecontainers/kube-apiserver:v1.14.3  k8s.gcr.io/kube-apiserver:v1.14.3

[root@docker01 ~]# docker tag docker.io/mirrorgooglecontainers/kube-controller-manager:v1.14.3  k8s.gcr.io/kube-controller-manager:v1.14.3

[root@docker01 ~]# docker tag docker.io/mirrorgooglecontainers/kube-scheduler:v1.14.3  k8s.gcr.io/kube-scheduler:v1.14.3

[root@docker01 ~]# docker tag docker.io/mirrorgooglecontainers/kube-proxy:v1.14.3  k8s.gcr.io/kube-proxy:v1.14.3

[root@docker01 ~]# docker tag docker.io/mirrorgooglecontainers/pause:3.1  k8s.gcr.io/pause:3.1
[root@docker01 ~]# docker tag docker.io/mirrorgooglecontainers/etcd:3.3.10  k8s.gcr.io/etcd:3.3.10
[root@docker01 ~]# docker tag docker.io/coredns/coredns:1.3.1  k8s.gcr.io/coredns:1.3.1
[root@docker01 ~]# docker image ls
REPOSITORY                                       TAG                 IMAGE ID            CREATED             SIZE
mirrorgooglecontainers/kube-proxy                v1.14.3             004666307c5b        11 days ago         82.1MB
k8s.gcr.io/kube-proxy                            v1.14.3             004666307c5b        11 days ago         82.1MB
mirrorgooglecontainers/kube-controller-manager   v1.14.3             ac2ce44462bc        11 days ago         158MB
k8s.gcr.io/kube-controller-manager               v1.14.3             ac2ce44462bc        11 days ago         158MB
mirrorgooglecontainers/kube-apiserver            v1.14.3             9946f563237c        11 days ago         210MB
k8s.gcr.io/kube-apiserver                        v1.14.3             9946f563237c        11 days ago         210MB
k8s.gcr.io/kube-scheduler                        v1.14.3             953364a3ae7a        11 days ago         81.6MB
mirrorgooglecontainers/kube-scheduler            v1.14.3             953364a3ae7a        11 days ago         81.6MB
coredns/coredns                                  1.3.1               eb516548c180        5 months ago        40.3MB
k8s.gcr.io/coredns                               1.3.1               eb516548c180        5 months ago        40.3MB
k8s.gcr.io/etcd                                  3.3.10              2c4adeb21b4f        6 months ago        258MB
mirrorgooglecontainers/etcd                      3.3.10              2c4adeb21b4f        6 months ago        258MB
k8s.gcr.io/pause                                 3.1                 da86e6ba6ca1        18 months ago       742kB
mirrorgooglecontainers/pause                     3.1                 da86e6ba6ca1        18 months ago       742kB
[root@docker01 ~]# 





重新初始化：


[root@docker01 ~]# kubeadm init --pod-network-cidr=10.244.0.0/16  --service-cidr=10.96.0.0/12 --ignore-preflight-errors=Swap
I0617 04:15:04.214839   21848 version.go:96] could not fetch a Kubernetes version from the internet: unable to get URL "https://dl.k8s.io/release/stable-1.txt": Get https://dl.k8s.io/release/stable-1.txt: net/http: request canceled while waiting for connection (Client.Timeout exceeded while awaiting headers)
I0617 04:15:04.215118   21848 version.go:97] falling back to the local client version: v1.14.3
[init] Using Kubernetes version: v1.14.3
[preflight] Running pre-flight checks
	[WARNING IsDockerSystemdCheck]: detected "cgroupfs" as the Docker cgroup driver. The recommended driver is "systemd". Please follow the guide at https://kubernetes.io/docs/setup/cri/
	[WARNING Swap]: running with swap on is not supported. Please disable swap
[preflight] Pulling images required for setting up a Kubernetes cluster
[preflight] This might take a minute or two, depending on the speed of your internet connection
[preflight] You can also perform this action in beforehand using 'kubeadm config images pull'
[kubelet-start] Writing kubelet environment file with flags to file "/var/lib/kubelet/kubeadm-flags.env"
[kubelet-start] Writing kubelet configuration to file "/var/lib/kubelet/config.yaml"
[kubelet-start] Activating the kubelet service
[certs] Using certificateDir folder "/etc/kubernetes/pki"
[certs] Generating "front-proxy-ca" certificate and key
[certs] Generating "front-proxy-client" certificate and key
[certs] Generating "etcd/ca" certificate and key
[certs] Generating "etcd/server" certificate and key
[certs] etcd/server serving cert is signed for DNS names [docker01 localhost] and IPs [108.88.3.112 127.0.0.1 ::1]
[certs] Generating "etcd/healthcheck-client" certificate and key
[certs] Generating "etcd/peer" certificate and key
[certs] etcd/peer serving cert is signed for DNS names [docker01 localhost] and IPs [108.88.3.112 127.0.0.1 ::1]
[certs] Generating "apiserver-etcd-client" certificate and key
[certs] Generating "ca" certificate and key
[certs] Generating "apiserver" certificate and key
[certs] apiserver serving cert is signed for DNS names [docker01 kubernetes kubernetes.default kubernetes.default.svc kubernetes.default.svc.cluster.local] and IPs [10.96.0.1 108.88.3.112]
[certs] Generating "apiserver-kubelet-client" certificate and key
[certs] Generating "sa" key and public key
[kubeconfig] Using kubeconfig folder "/etc/kubernetes"
[kubeconfig] Writing "admin.conf" kubeconfig file
[kubeconfig] Writing "kubelet.conf" kubeconfig file
[kubeconfig] Writing "controller-manager.conf" kubeconfig file
[kubeconfig] Writing "scheduler.conf" kubeconfig file
[control-plane] Using manifest folder "/etc/kubernetes/manifests"
[control-plane] Creating static Pod manifest for "kube-apiserver"
[control-plane] Creating static Pod manifest for "kube-controller-manager"
[control-plane] Creating static Pod manifest for "kube-scheduler"
[etcd] Creating static Pod manifest for local etcd in "/etc/kubernetes/manifests"
[wait-control-plane] Waiting for the kubelet to boot up the control plane as static Pods from directory "/etc/kubernetes/manifests". This can take up to 4m0s
[kubelet-check] Initial timeout of 40s passed.
[apiclient] All control plane components are healthy after 61.520994 seconds
[upload-config] storing the configuration used in ConfigMap "kubeadm-config" in the "kube-system" Namespace
[kubelet] Creating a ConfigMap "kubelet-config-1.14" in namespace kube-system with the configuration for the kubelets in the cluster
[upload-certs] Skipping phase. Please see --experimental-upload-certs
[mark-control-plane] Marking the node docker01 as control-plane by adding the label "node-role.kubernetes.io/master=''"
[mark-control-plane] Marking the node docker01 as control-plane by adding the taints [node-role.kubernetes.io/master:NoSchedule]
[bootstrap-token] Using token: fzmcdn.vehg8o2ugp15hvjc
[bootstrap-token] Configuring bootstrap tokens, cluster-info ConfigMap, RBAC Roles
[bootstrap-token] configured RBAC rules to allow Node Bootstrap tokens to post CSRs in order for nodes to get long term certificate credentials
[bootstrap-token] configured RBAC rules to allow the csrapprover controller automatically approve CSRs from a Node Bootstrap Token
[bootstrap-token] configured RBAC rules to allow certificate rotation for all node client certificates in the cluster
[bootstrap-token] creating the "cluster-info" ConfigMap in the "kube-public" namespace
[addons] Applied essential addon: CoreDNS
[addons] Applied essential addon: kube-proxy

Your Kubernetes control-plane has initialized successfully!

To start using your cluster, you need to run the following as a regular user:

  mkdir -p $HOME/.kube
  sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
  sudo chown $(id -u):$(id -g) $HOME/.kube/config

You should now deploy a pod network to the cluster.
Run "kubectl apply -f [podnetwork].yaml" with one of the options listed at:
  https://kubernetes.io/docs/concepts/cluster-administration/addons/

Then you can join any number of worker nodes by running the following on each as root:

kubeadm join 108.88.3.112:6443 --token fzmcdn.vehg8o2ugp15hvjc \
    --discovery-token-ca-cert-hash sha256:db4bf2b57ed8b2c062d0ce4dd46963fb1c6a58b84b113d6dfdcd2783675a151c 
[root@docker01 ~]# 




[root@docker01 tmp]# mkdir -p $HOME/.kube
[root@docker01 tmp]# cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
[root@docker01 tmp]# kubectl get cs
NAME                 STATUS    MESSAGE             ERROR
controller-manager   Healthy   ok                  
scheduler            Healthy   ok                  
etcd-0               Healthy   {"health":"true"}   
[root@docker01 tmp]# 



### 安装flannel


[root@docker01 tmp]# kubectl get nodes
NAME       STATUS     ROLES    AGE    VERSION
docker01   NotReady   master   106m   v1.14.3

[root@docker01 tmp]# kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
podsecuritypolicy.extensions/psp.flannel.unprivileged created
clusterrole.rbac.authorization.k8s.io/flannel created
clusterrolebinding.rbac.authorization.k8s.io/flannel created
serviceaccount/flannel created
configmap/kube-flannel-cfg created



查看是否存在 flannel镜象



[root@docker01 tmp]# docker image ls
REPOSITORY                                       TAG                 IMAGE ID            CREATED             SIZE
mirrorgooglecontainers/kube-proxy                v1.14.3             004666307c5b        11 days ago         82.1MB
k8s.gcr.io/kube-proxy                            v1.14.3             004666307c5b        11 days ago         82.1MB
k8s.gcr.io/kube-controller-manager               v1.14.3             ac2ce44462bc        11 days ago         158MB
mirrorgooglecontainers/kube-controller-manager   v1.14.3             ac2ce44462bc        11 days ago         158MB
mirrorgooglecontainers/kube-apiserver            v1.14.3             9946f563237c        11 days ago         210MB
k8s.gcr.io/kube-apiserver                        v1.14.3             9946f563237c        11 days ago         210MB
mirrorgooglecontainers/kube-scheduler            v1.14.3             953364a3ae7a        11 days ago         81.6MB
k8s.gcr.io/kube-scheduler                        v1.14.3             953364a3ae7a        11 days ago         81.6MB
coredns/coredns                                  1.3.1               eb516548c180        5 months ago        40.3MB
k8s.gcr.io/coredns                               1.3.1               eb516548c180        5 months ago        40.3MB
mirrorgooglecontainers/etcd                      3.3.10              2c4adeb21b4f        6 months ago        258MB
k8s.gcr.io/etcd                                  3.3.10              2c4adeb21b4f        6 months ago        258MB
mirrorgooglecontainers/pause                     3.1                 da86e6ba6ca1        18 months ago       742kB
k8s.gcr.io/pause                                 3.1                 da86e6ba6ca1        18 months ago       742kB
[root@docker01 tmp]# docker image ls


没有生成，手动使用docker 导入



[root@docker01 tmp]# docker pull quay.io/coreos/flannel:v0.11.0
v0.11.0: Pulling from coreos/flannel
cd784148e348: Pull complete 
04ac94e9255c: Pull complete 
e10b013543eb: Pull complete 
005e31e443b1: Pull complete 
74f794f05817: Pull complete 
Digest: sha256:3fa662e491a5e797c789afbd6d5694bdd186111beb7b5c9d66655448a7d3ae37
Status: Downloaded newer image for quay.io/coreos/flannel:v0.11.0
[root@docker01 tmp]# docker image ls
REPOSITORY                                       TAG                 IMAGE ID            CREATED             SIZE
k8s.gcr.io/kube-proxy                            v1.14.3             004666307c5b        11 days ago         82.1MB
mirrorgooglecontainers/kube-proxy                v1.14.3             004666307c5b        11 days ago         82.1MB
k8s.gcr.io/kube-controller-manager               v1.14.3             ac2ce44462bc        11 days ago         158MB
mirrorgooglecontainers/kube-controller-manager   v1.14.3             ac2ce44462bc        11 days ago         158MB
mirrorgooglecontainers/kube-apiserver            v1.14.3             9946f563237c        11 days ago         210MB
k8s.gcr.io/kube-apiserver                        v1.14.3             9946f563237c        11 days ago         210MB
mirrorgooglecontainers/kube-scheduler            v1.14.3             953364a3ae7a        11 days ago         81.6MB
k8s.gcr.io/kube-scheduler                        v1.14.3             953364a3ae7a        11 days ago         81.6MB
quay.io/coreos/flannel                           v0.11.0             ff281650a721        4 months ago        52.6MB
quay.io/coreos/flannel                           v0.11.0-amd64       ff281650a721        4 months ago        52.6MB
coredns/coredns                                  1.3.1               eb516548c180        5 months ago        40.3MB
k8s.gcr.io/coredns                               1.3.1               eb516548c180        5 months ago        40.3MB
mirrorgooglecontainers/etcd                      3.3.10              2c4adeb21b4f        6 months ago        258MB
k8s.gcr.io/etcd                                  3.3.10              2c4adeb21b4f        6 months ago        258MB
mirrorgooglecontainers/pause                     3.1                 da86e6ba6ca1        18 months ago       742kB
k8s.gcr.io/pause                                 3.1                 da86e6ba6ca1        18 months ago       742kB
[root@docker01 tmp]# kubectl get nodes
NAME       STATUS   ROLES    AGE    VERSION
docker01   Ready    master   130m   v1.14.3
[root@docker01 tmp]# 


[root@docker01 tmp]#  kubectl get pods -n kube-system
NAME                               READY   STATUS    RESTARTS   AGE
coredns-fb8b8dccf-cdz5t            1/1     Running   0          133m
coredns-fb8b8dccf-zjscn            1/1     Running   0          133m
etcd-docker01                      1/1     Running   0          133m
kube-apiserver-docker01            1/1     Running   0          132m
kube-controller-manager-docker01   1/1     Running   0          133m
kube-flannel-ds-amd64-htbnz        1/1     Running   0          24m
kube-proxy-9gf8k                   1/1     Running   0          133m
kube-scheduler-docker01            1/1     Running   0          132m
[root@docker01 tmp]# 



[root@docker01 tmp]# kubectl get ns
NAME              STATUS   AGE
default           Active   134m
kube-node-lease   Active   135m
kube-public       Active   135m
kube-system       Active   135m
[root@docker01 tmp]# 



[root@docker01 ~]#  scp /usr/lib/systemd/system/docker.service docker02:/usr/lib/systemd/system/docker.service
root@docker02's password: 
docker.service                                                                                                                                             100% 1686   277.7KB/s   00:00    
[root@docker01 ~]#  scp /usr/lib/systemd/system/docker.service docker03:/usr/lib/systemd/system/docker.service
root@docker03's password: 
Permission denied, please try again.
root@docker03's password: 
docker.service                                                                                                                                             100% 1686   299.3KB/s   00:00    
[root@docker01 ~]#  scp /etc/sysconfig/kubelet docker01:/etc/sysconfig/kubelet 
The authenticity of host 'docker01 (127.0.0.1)' can't be established.
ECDSA key fingerprint is SHA256:KK3slSAdbd+jsQKQyndKke9nzufXK7to3iqgmuP9xHQ.
ECDSA key fingerprint is MD5:1f:fe:e2:98:02:98:8b:39:aa:55:36:c1:ce:9f:0b:5f.
Are you sure you want to continue connecting (yes/no)? yes^C[root@docker01 ~]# 
[root@docker01 ~]# 
[root@docker01 ~]#  scp /etc/sysconfig/kubelet docker02:/etc/sysconfig/kubelet 
root@docker02's password: 
kubelet                                                                                                                                                    100%   42     0.7KB/s   00:0

~~~


#### 新建Node节点加入集群

~~~

打包本地镜象 导入到其它节点


[root@docker01 tmp]# docker save k8s.gcr.io/kube-proxy:v1.14.3 k8s.gcr.io/kube-apiserver:v1.14.3 k8s.gcr.io/kube-controller-manager:v1.14.3 k8s.gcr.io/kube-scheduler:v1.14.3 quay.io/coreos/flannel:v0.11.0 quay.io/coreos/flannel:v0.11.0-amd64 k8s.gcr.io/coredns:1.3.1 k8s.gcr.io/etcd:3.3.10 k8s.gcr.io/pause:3.1     > /tmp/save5.tar
[root@docker01 tmp]# cd 

#

yum install -y kubelet kubeadm 



导入镜象

[root@docker01 ~]#  docker load -i /tmp/
save5.tar





[root@docker02 ~]# echo "1" > /proc/sys/net/bridge/bridge-nf-call-ip6tables 
[root@docker02 ~]# echo "1" > /proc/sys/net/bridge/bridge-nf-call-iptables 
[root@docker02 ~]# cat /proc/sys/net/bridge/bridge-nf-call-ip6tables 
1
[root@docker02 ~]# kubeadm join 108.88.3.112:6443 --token fzmcdn.ve hg8o2ugp15hvjc     --discovery-token-ca-cert-hash sha256:db4bf2b57ed8b2c062d0ce4dd46963fb1c6a58b84b113d6dfdcd2783675a151c  --ignore-preflight-errors=Swap
[preflight] Running pre-flight checks
	[WARNING IsDockerSystemdCheck]: detected "cgroupfs" as the Docker cgroup driver. The recommended driver is "systemd". Please follow the guide at https://kubernetes.io/docs/setup/cri/
	[WARNING Swap]: running with swap on is not supported. Please disable swap
[preflight] Reading configuration from the cluster...
[preflight] FYI: You can look at this config file with 'kubectl -n kube-system get cm kubeadm-config -oyaml'
[kubelet-start] Downloading configuration for the kubelet from the "kubelet-config-1.14" ConfigMap in the kube-system namespace
[kubelet-start] Writing kubelet configuration to file "/var/lib/kubelet/config.yaml"
[kubelet-start] Writing kubelet environment file with flags to file "/var/lib/kubelet/kubeadm-flags.env"
[kubelet-start] Activating the kubelet service
[kubelet-start] Waiting for the kubelet to perform the TLS Bootstrap...

This node has joined the cluster:
* Certificate signing request was sent to apiserver and a response was received.
* The Kubelet was informed of the new secure connection details.

Run 'kubectl get nodes' on the control-plane to see this node join the cluster.


~~~


#### 查看pods 相关服务状态

~~~

[root@docker01 tmp]#  kubectl get pods -n kube-system
NAME                               READY   STATUS    RESTARTS   AGE
coredns-fb8b8dccf-cdz5t            1/1     Running   0          5h18m
coredns-fb8b8dccf-zjscn            1/1     Running   0          5h18m
etcd-docker01                      1/1     Running   0          5h18m
kube-apiserver-docker01            1/1     Running   0          5h18m
kube-controller-manager-docker01   1/1     Running   0          5h18m
kube-flannel-ds-amd64-htbnz        1/1     Running   0          3h29m
kube-flannel-ds-amd64-z54l5        1/1     Running   0          52m
kube-proxy-2c8xt                   1/1     Running   1          52m
kube-proxy-9gf8k                   1/1     Running   0          5h18m
kube-scheduler-docker01            1/1     Running   0          5h18m
[root@docker01 tmp]#  kubectl get pods -n kube-system -o wide
NAME                               READY   STATUS    RESTARTS   AGE     IP             NODE       NOMINATED NODE   READINESS GATES
coredns-fb8b8dccf-cdz5t            1/1     Running   0          5h51m   10.244.0.3     docker01   <none>           <none>
coredns-fb8b8dccf-zjscn            1/1     Running   0          5h51m   10.244.0.2     docker01   <none>           <none>
etcd-docker01                      1/1     Running   0          5h51m   108.88.3.112   docker01   <none>           <none>
kube-apiserver-docker01            1/1     Running   0          5h51m   108.88.3.112   docker01   <none>           <none>
kube-controller-manager-docker01   1/1     Running   0          5h51m   108.88.3.112   docker01   <none>           <none>
kube-flannel-ds-amd64-htbnz        1/1     Running   0          4h2m    108.88.3.112   docker01   <none>           <none>
kube-flannel-ds-amd64-z54l5        1/1     Running   0          86m     108.88.3.143   docker02   <none>           <none>
kube-proxy-2c8xt                   1/1     Running   1          86m     108.88.3.143   docker02   <none>           <none>
kube-proxy-9gf8k                   1/1     Running   0          5h51m   108.88.3.112   docker01   <none>           <none>
kube-scheduler-docker01            1/1     Running   0          5h51m   108.88.3.112   docker01   <none>           <none>
[root@docker01 tmp]#

~~~


#### 添加docker03 到集群
~~~
[root@docker03 yum.repos.d]# yum install -y kubelet kubeadm 

[root@docker03 yum.repos.d]# systemctl enable kubelet
Created symlink from /etc/systemd/system/multi-user.target.wants/kubelet.service to /usr/lib/systemd/system/kubelet.service.
[root@docker03 yum.repos.d]#

 
 [root@docker03 yum.repos.d]# yum install -y kubelet kubeadm 


Loaded plugins: fastestmirror
Loading mirror speeds from cached hostfile
 * base: mirrors.aliyun.com
 * extras: mirrors.aliyun.com
 * updates: mirrors.aliyun.com
Package kubelet-1.14.3-0.x86_64 already installed and latest version
Package kubeadm-1.14.3-0.x86_64 already installed and latest version
Nothing to do
[root@docker03 yum.repos.d]# systemctl enable kubelet
Created symlink from /etc/systemd/system/multi-user.target.wants/kubelet.service to /usr/lib/systemd/system/kubelet.service.
[root@docker03 yum.repos.d]# docker image ls
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
[root@docker03 yum.repos.d]# docker load -i /tmp/save5.tar 
fe9a8b4f1dcc: Loading layer  43.87MB/43.87MB
2d021e0f751c: Loading layer  167.6MB/167.6MB
Loaded image: k8s.gcr.io/kube-apiserver:v1.14.3
6625d0345c89: Loading layer  115.6MB/115.6MB
Loaded image: k8s.gcr.io/kube-controller-manager:v1.14.3
9a16f8d60ec3: Loading layer  39.26MB/39.26MB
Loaded image: k8s.gcr.io/kube-scheduler:v1.14.3
7bff100f35cb: Loading layer [==================================================>]  4.672MB/4.672MB
5d3f68f6da8f: Loading layer [==================================================>]  9.526MB/9.526MB
9b48060f404d: Loading layer [==================================================>]  5.912MB/5.912MB
3f3a4ce2b719: Loading layer [==================================================>]  35.25MB/35.25MB
9ce0bb155166: Loading layer [==================================================>]   5.12kB/5.12kB
Loaded image: quay.io/coreos/flannel:v0.11.0
Loaded image: quay.io/coreos/flannel:v0.11.0-amd64
fb61a074724d: Loading layer [==================================================>]  479.7kB/479.7kB
c6a5fc8a3f01: Loading layer [==================================================>]  40.05MB/40.05MB
Loaded image: k8s.gcr.io/coredns:1.3.1
8a788232037e: Loading layer [==================================================>]   1.37MB/1.37MB
30796113fb51: Loading layer [==================================================>]    232MB/232MB
6fbfb277289f: Loading layer [==================================================>]  24.98MB/24.98MB
Loaded image: k8s.gcr.io/etcd:3.3.10
e17133b79956: Loading layer [==================================================>]  744.4kB/744.4kB
Loaded image: k8s.gcr.io/pause:3.1
15c9248be8a9: Loading layer [==================================================>]  3.403MB/3.403MB
244244bfaed2: Loading layer [==================================================>]  36.69MB/36.69MB
Loaded image: k8s.gcr.io/kube-proxy:v1.14.3
[root@docker03 yum.repos.d]# docker image ls
REPOSITORY                           TAG                 IMAGE ID            CREATED             SIZE
k8s.gcr.io/kube-proxy                v1.14.3             004666307c5b        11 days ago         82.1MB
k8s.gcr.io/kube-apiserver            v1.14.3             9946f563237c        11 days ago         210MB
k8s.gcr.io/kube-controller-manager   v1.14.3             ac2ce44462bc        11 days ago         158MB
k8s.gcr.io/kube-scheduler            v1.14.3             953364a3ae7a        11 days ago         81.6MB
quay.io/coreos/flannel               v0.11.0             ff281650a721        4 months ago        52.6MB
quay.io/coreos/flannel               v0.11.0-amd64       ff281650a721        4 months ago        52.6MB
k8s.gcr.io/coredns                   1.3.1               eb516548c180        5 months ago        40.3MB
k8s.gcr.io/etcd                      3.3.10              2c4adeb21b4f        6 months ago        258MB
k8s.gcr.io/pause                     3.1                 da86e6ba6ca1        18 months ago       742kB

[root@docker03 yum.repos.d]# echo "1" > /proc/sys/net/bridge/bridge-nf-call-ip6tables 
[root@docker03 yum.repos.d]# echo "1" > /proc/sys/net/bridge/bridge-nf-call-iptables

e
[root@docker03 yum.repos.d]# more /etc/sysconfig/kubelet 
KUBELET_EXTRA_ARGS="--fail-swap-on=false"



[root@docker03 yum.repos.d]# docker info
Containers: 0
 Running: 0
 Paused: 0
 Stopped: 0
Images: 8
Server Version: 18.09.6
Storage Driver: overlay2
 Backing Filesystem: xfs
 Supports d_type: true
 Native Overlay Diff: true
Logging Driver: json-file
Cgroup Driver: cgroupfs
Plugins:
 Volume: local
 Network: bridge host macvlan null overlay
 Log: awslogs fluentd gcplogs gelf journald json-file local logentries splunk syslog
Swarm: inactive
Runtimes: runc
Default Runtime: runc
Init Binary: docker-init
containerd version: bb71b10fd8f58240ca47fbb579b9d1028eea7c84
runc version: 2b18fe1d885ee5083ef9f0838fee39b62d653e30
init version: fec3683
Security Options:
 seccomp
  Profile: default
Kernel Version: 3.10.0-957.21.2.el7.x86_64
Operating System: CentOS Linux 7 (Core)
OSType: linux
Architecture: x86_64
CPUs: 4
Total Memory: 4.193GiB
Name: docker03
ID: O2UB:YA54:YW6R:56VO:QW3R:R6M4:PKVT:G5Z3:TVCK:7IQC:IXGH:KDBM
Docker Root Dir: /var/lib/docker
Debug Mode (client): false
Debug Mode (server): false
Registry: https://index.docker.io/v1/
Labels:
Experimental: false
Insecure Registries:
 docker02:5000
 127.0.0.0/8
Registry Mirrors:
 https://a4gh5fxo.mirror.aliyuncs.com/
Live Restore Enabled: false
Product License: Community Engine

[root@docker03 yum.repos.d]# kubeadm join 108.88.3.112:6443 --token fzmcdn.vehg8o2ugp15hvjc     --discovery-token-ca-cert-hash sha256:db4bf2b57ed8b2c062d0ce4dd46963fb1c6a58b84b113d6dfdcd2783675a151c  --ignore-preflight-errors=Swap



[preflight] Running pre-flight checks
	[WARNING IsDockerSystemdCheck]: detected "cgroupfs" as the Docker cgroup driver. The recommended driver is "systemd". Please follow the guide at https://kubernetes.io/docs/setup/cri/
	[WARNING Swap]: running with swap on is not supported. Please disable swap
[preflight] Reading configuration from the cluster...
[preflight] FYI: You can look at this config file with 'kubectl -n kube-system get cm kubeadm-config -oyaml'
[kubelet-start] Downloading configuration for the kubelet from the "kubelet-config-1.14" ConfigMap in the kube-system namespace
[kubelet-start] Writing kubelet configuration to file "/var/lib/kubelet/config.yaml"
[kubelet-start] Writing kubelet environment file with flags to file "/var/lib/kubelet/kubeadm-flags.env"
[kubelet-start] Activating the kubelet service
[kubelet-start] Waiting for the kubelet to perform the TLS Bootstrap...

This node has joined the cluster:
* Certificate signing request was sent to apiserver and a response was received.
* The Kubelet was informed of the new secure connection details.

Run 'kubectl get nodes' on the control-plane to see this node join the cluster.

[root@docker03 yum.repos.d]#


~~~


#### 查看添加结果 

~~~
[root@docker01 tmp]# kubectl get nodes
NAME       STATUS   ROLES    AGE     VERSION
docker01   Ready    master   6h29m   v1.14.3
docker02   Ready    <none>   122m    v1.14.3
docker03   Ready    <none>   15m     v1.14.3
[root@docker01 tmp]#  kubectl get pods -n kube-system -o wide
NAME                               READY   STATUS    RESTARTS   AGE     IP             NODE       NOMINATED NODE   READINESS GATES
coredns-fb8b8dccf-cdz5t            1/1     Running   0          6h28m   10.244.0.3     docker01   <none>           <none>
coredns-fb8b8dccf-zjscn            1/1     Running   0          6h28m   10.244.0.2     docker01   <none>           <none>
etcd-docker01                      1/1     Running   0          6h28m   108.88.3.112   docker01   <none>           <none>
kube-apiserver-docker01            1/1     Running   0          6h28m   108.88.3.112   docker01   <none>           <none>
kube-controller-manager-docker01   1/1     Running   0          6h28m   108.88.3.112   docker01   <none>           <none>
kube-flannel-ds-amd64-htbnz        1/1     Running   0          4h39m   108.88.3.112   docker01   <none>           <none>
kube-flannel-ds-amd64-hxd7p        1/1     Running   0          15m     108.88.3.122   docker03   <none>           <none>
kube-flannel-ds-amd64-z54l5        1/1     Running   0          122m    108.88.3.143   docker02   <none>           <none>
kube-proxy-2c8xt                   1/1     Running   1          122m    108.88.3.143   docker02   <none>           <none>
kube-proxy-9gf8k                   1/1     Running   0          6h28m   108.88.3.112   docker01   <none>           <none>
kube-proxy-gftv4                   1/1     Running   0          15m     108.88.3.122   docker03   <none>           <none>
kube-scheduler-docker01            1/1     Running   0          6h28m   108.88.3.112   docker01   <none>           <none>
[root@docker01 tmp]# 
~~~