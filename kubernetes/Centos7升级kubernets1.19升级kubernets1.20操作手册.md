 ### Centos 7 升级kubernets1.19升级kubernets1.20





####  一、首先升级Master 节点：

##### 1.1 将节点置于维护模式，通过 Drain 命令防止在升级过程中在节点上调度新的 Pod

 注意--delete-local-data默认情况下，`kubectl drain` 不会删除带有本地存储的 Pod，因为这可能导致数据丢失。`--delete-local-data` 标志允许您覆盖此行为，并强制进行节点排空，包括具有本地存储的 Pod。

~~~
[root@master1 ~]# kubectl drain master1 --ignore-daemonsets --delete-local-data
node/master1 already cordoned
WARNING: ignoring DaemonSet-managed Pods: kube-system/calico-node-mhmvz, kube-system/kube-proxy-zm5vm
evicting pod kube-system/metrics-server-c7d74c45-rtb84
evicting pod kube-system/calico-kube-controllers-6c89d944d5-xr2ps
evicting pod kube-system/coredns-59c898cd69-2q5dt
evicting pod kube-system/coredns-59c898cd69-zvrgc
pod/metrics-server-c7d74c45-rtb84 evicted
pod/calico-kube-controllers-6c89d944d5-xr2ps evicted
pod/coredns-59c898cd69-2q5dt evicted
pod/coredns-59c898cd69-zvrgc evicted
node/master1 evicted
~~~



##### 1.2 升级 kubeadm，它用于管理 Kubernetes 的升级。

~~~
[root@master1 ~]# sudo yum update -y kubeadm-1.20.0
已加载插件：fastestmirror
Repository base is listed more than once in the configuration
Repository updates is listed more than once in the configuration
Repository extras is listed more than once in the configuration
Repository centosplus is listed more than once in the configuration
Loading mirror speeds from cached hostfile

 * base: mirrors.aliyun.com
 * extras: mirrors.aliyun.com
 * updates: mirrors.aliyun.com
   正在解决依赖关系
   --> 正在检查事务
   ---> 软件包 kubeadm.x86_64.0.1.19.5-0 将被 升级
   ---> 软件包 kubeadm.x86_64.0.1.20.0-0 将被 更新
   --> 解决依赖关系完成

依赖关系解决

=================================================================================================================================================================================================

 Package                                      架构                                        版本                                             源                                               大小
=================================================================================================================================================================================================

正在更新:
 kubeadm                                      x86_64                                      1.20.0-0                                         kubernetes                                      8.3 M

事务概要
=================================================================================================================================================================================================

升级  1 软件包

总下载量：8.3 M
Downloading packages:
Delta RPMs disabled because /usr/bin/applydeltarpm not installed.
91e0f0a3a10ab757acf9611e8b81e1b272d76a5c400544a254d2c34a6ede1c11-kubeadm-1.20.0-0.x86_64.rpm                                                                              | 8.3 MB  00:00:25     
Running transaction check
Running transaction test
Transaction test succeeded
Running transaction
  正在更新    : kubeadm-1.20.0-0.x86_64                                                                                                                                                      1/2 
  清理        : kubeadm-1.19.5-0.x86_64                                                                                                                                                      2/2 
  验证中      : kubeadm-1.20.0-0.x86_64                                                                                                                                                      1/2 
  验证中      : kubeadm-1.19.5-0.x86_64                                                                                                                                                      2/2 

更新完毕:
  kubeadm.x86_64 0:1.20.0-0                                                
~~~



##### 1.3 运行升级计划，查看将要应用的更改。

~~~
[root@master1 ~]# sudo kubeadm upgrade plan
[upgrade/config] Making sure the configuration is correct:
[upgrade/config] Reading configuration from the cluster...
[upgrade/config] FYI: You can look at this config file with 'kubectl -n kube-system get cm kubeadm-config -o yaml'
[preflight] Running pre-flight checks.
[upgrade] Running cluster health checks
[upgrade] Fetching available versions to upgrade to
[upgrade/versions] Cluster version: v1.19.5
[upgrade/versions] kubeadm version: v1.20.0
I0726 15:01:04.085284   25054 version.go:251] remote version is much newer: v1.27.4; falling back to: stable-1.20
[upgrade/versions] Latest stable version: v1.20.15
[upgrade/versions] Latest stable version: v1.20.15
[upgrade/versions] Latest version in the v1.19 series: v1.19.16
[upgrade/versions] Latest version in the v1.19 series: v1.19.16

Components that must be upgraded manually after you have upgraded the control plane with 'kubeadm upgrade apply':
COMPONENT   CURRENT       AVAILABLE
kubelet     3 x v1.19.5   v1.19.16

Upgrade to the latest version in the v1.19 series:

COMPONENT                 CURRENT    AVAILABLE
kube-apiserver            v1.19.5    v1.19.16
kube-controller-manager   v1.19.5    v1.19.16
kube-scheduler            v1.19.5    v1.19.16
kube-proxy                v1.19.5    v1.19.16
CoreDNS                   1.7.0      1.7.0
etcd                      3.4.13-0   3.4.9-1

You can now apply the upgrade by executing the following command:

        kubeadm upgrade apply v1.19.16

_____________________________________________________________________

Components that must be upgraded manually after you have upgraded the control plane with 'kubeadm upgrade apply':
COMPONENT   CURRENT       AVAILABLE
kubelet     3 x v1.19.5   v1.20.15

Upgrade to the latest stable version:

COMPONENT                 CURRENT    AVAILABLE
kube-apiserver            v1.19.5    v1.20.15
kube-controller-manager   v1.19.5    v1.20.15
kube-scheduler            v1.19.5    v1.20.15
kube-proxy                v1.19.5    v1.20.15
CoreDNS                   1.7.0      1.7.0
etcd                      3.4.13-0   3.4.13-0

You can now apply the upgrade by executing the following command:

        kubeadm upgrade apply v1.20.15

Note: Before you can perform this upgrade, you have to update kubeadm to v1.20.15.

_____________________________________________________________________


The table below shows the current state of component configs as understood by this version of kubeadm.
Configs that have a "yes" mark in the "MANUAL UPGRADE REQUIRED" column require manual config upgrade or
resetting to kubeadm defaults before a successful upgrade can be performed. The version to manually
upgrade to is denoted in the "PREFERRED VERSION" column.

API GROUP                 CURRENT VERSION   PREFERRED VERSION   MANUAL UPGRADE REQUIRED
kubeproxy.config.k8s.io   v1alpha1          v1alpha1            no
kubelet.config.k8s.io     v1beta1           v1beta1             no
~~~



##### 1.4 执行升级

~~~
[root@master1 ~]# sudo kubeadm upgrade apply v1.20.0
[upgrade/config] Making sure the configuration is correct:
[upgrade/config] Reading configuration from the cluster...
[upgrade/config] FYI: You can look at this config file with 'kubectl -n kube-system get cm kubeadm-config -o yaml'
[preflight] Running pre-flight checks.
[upgrade] Running cluster health checks
[upgrade/version] You have chosen to change the cluster version to "v1.20.0"
[upgrade/versions] Cluster version: v1.19.5
[upgrade/versions] kubeadm version: v1.20.0
[upgrade/confirm] Are you sure you want to proceed with the upgrade? [y/N]: y
[upgrade/prepull] Pulling images required for setting up a Kubernetes cluster
[upgrade/prepull] This might take a minute or two, depending on the speed of your internet connection
[upgrade/prepull] You can also perform this action in beforehand using 'kubeadm config images pull'
[upgrade/apply] Upgrading your Static Pod-hosted control plane to version "v1.20.0"...
Static pod: kube-apiserver-master1 hash: c7d7f96eaa6d9c5056296420c8d6a37f
Static pod: kube-controller-manager-master1 hash: 74a419023c8d779fde373565ae496233
Static pod: kube-scheduler-master1 hash: 99401f8622a1296ec0708059b972366d
[upgrade/etcd] Upgrading to TLS for etcd
Static pod: etcd-master1 hash: 2ba4f279cd9c0d94e5068243d361dce2
[upgrade/staticpods] Preparing for "etcd" upgrade
[upgrade/staticpods] Renewing etcd-server certificate
[upgrade/staticpods] Renewing etcd-peer certificate
[upgrade/staticpods] Renewing etcd-healthcheck-client certificate
[upgrade/staticpods] Moved new manifest to "/etc/kubernetes/manifests/etcd.yaml" and backed up old manifest to "/etc/kubernetes/tmp/kubeadm-backup-manifests-2023-07-26-15-02-52/etcd.yaml"
[upgrade/staticpods] Waiting for the kubelet to restart the component
[upgrade/staticpods] This might take a minute or longer depending on the component/version gap (timeout 5m0s)
Static pod: etcd-master1 hash: 2ba4f279cd9c0d94e5068243d361dce2
Static pod: etcd-master1 hash: 2ba4f279cd9c0d94e5068243d361dce2
Static pod: etcd-master1 hash: 40bfc565a619cc282544e3482fdbdd1a
[apiclient] Found 1 Pods for label selector component=etcd
[upgrade/staticpods] Component "etcd" upgraded successfully!
[upgrade/etcd] Waiting for etcd to become available
[upgrade/staticpods] Writing new Static Pod manifests to "/etc/kubernetes/tmp/kubeadm-upgraded-manifests839368704"
[upgrade/staticpods] Preparing for "kube-apiserver" upgrade
[upgrade/staticpods] Renewing apiserver certificate
[upgrade/staticpods] Renewing apiserver-kubelet-client certificate
[upgrade/staticpods] Renewing front-proxy-client certificate
[upgrade/staticpods] Renewing apiserver-etcd-client certificate
[upgrade/staticpods] Moved new manifest to "/etc/kubernetes/manifests/kube-apiserver.yaml" and backed up old manifest to "/etc/kubernetes/tmp/kubeadm-backup-manifests-2023-07-26-15-02-52/kube-apiserver.yaml"
[upgrade/staticpods] Waiting for the kubelet to restart the component
[upgrade/staticpods] This might take a minute or longer depending on the component/version gap (timeout 5m0s)
Static pod: kube-apiserver-master1 hash: c7d7f96eaa6d9c5056296420c8d6a37f
Static pod: kube-apiserver-master1 hash: c7d7f96eaa6d9c5056296420c8d6a37f
Static pod: kube-apiserver-master1 hash: 2249b706233aa2f5354d3b91e59119b1
[apiclient] Found 1 Pods for label selector component=kube-apiserver
[upgrade/staticpods] Component "kube-apiserver" upgraded successfully!
[upgrade/staticpods] Preparing for "kube-controller-manager" upgrade
[upgrade/staticpods] Renewing controller-manager.conf certificate
[upgrade/staticpods] Moved new manifest to "/etc/kubernetes/manifests/kube-controller-manager.yaml" and backed up old manifest to "/etc/kubernetes/tmp/kubeadm-backup-manifests-2023-07-26-15-02-52/kube-controller-manager.yaml"
[upgrade/staticpods] Waiting for the kubelet to restart the component
[upgrade/staticpods] This might take a minute or longer depending on the component/version gap (timeout 5m0s)
Static pod: kube-controller-manager-master1 hash: 74a419023c8d779fde373565ae496233
Static pod: kube-controller-manager-master1 hash: 5c167cde7076af2cc1f9dc2e1789ec16
[apiclient] Found 1 Pods for label selector component=kube-controller-manager
[upgrade/staticpods] Component "kube-controller-manager" upgraded successfully!
[upgrade/staticpods] Preparing for "kube-scheduler" upgrade
[upgrade/staticpods] Renewing scheduler.conf certificate
[upgrade/staticpods] Moved new manifest to "/etc/kubernetes/manifests/kube-scheduler.yaml" and backed up old manifest to "/etc/kubernetes/tmp/kubeadm-backup-manifests-2023-07-26-15-02-52/kube-scheduler.yaml"
[upgrade/staticpods] Waiting for the kubelet to restart the component
[upgrade/staticpods] This might take a minute or longer depending on the component/version gap (timeout 5m0s)
Static pod: kube-scheduler-master1 hash: 99401f8622a1296ec0708059b972366d
Static pod: kube-scheduler-master1 hash: 48ac8d1f700dff2f72784e822f2804e2
[apiclient] Found 1 Pods for label selector component=kube-scheduler
[upgrade/staticpods] Component "kube-scheduler" upgraded successfully!
[upgrade/postupgrade] Applying label node-role.kubernetes.io/control-plane='' to Nodes with label node-role.kubernetes.io/master='' (deprecated)
[upload-config] Storing the configuration used in ConfigMap "kubeadm-config" in the "kube-system" Namespace
[kubelet] Creating a ConfigMap "kubelet-config-1.20" in namespace kube-system with the configuration for the kubelets in the cluster
[kubelet-start] Writing kubelet configuration to file "/var/lib/kubelet/config.yaml"
[bootstrap-token] configured RBAC rules to allow Node Bootstrap tokens to get nodes
[bootstrap-token] configured RBAC rules to allow Node Bootstrap tokens to post CSRs in order for nodes to get long term certificate credentials
[bootstrap-token] configured RBAC rules to allow the csrapprover controller automatically approve CSRs from a Node Bootstrap Token
[bootstrap-token] configured RBAC rules to allow certificate rotation for all node client certificates in the cluster
[addons] Applied essential addon: CoreDNS
[addons] Applied essential addon: kube-proxy

[upgrade/successful] SUCCESS! Your cluster was upgraded to "v1.20.0". Enjoy!

[upgrade/kubelet] Now that your control plane is upgraded, please proceed with upgrading your kubelets if you haven't already done so.
~~~



_____________________________________________________________________

#### 二、将 kubelet 和 kubectl 升级到与控制平面版本相匹配

~~~
[root@master1 ~]# sudo yum update -y kubelet-1.20.0 kubectl-1.20.0
已加载插件：fastestmirror
Repository base is listed more than once in the configuration
Repository updates is listed more than once in the configuration
Repository extras is listed more than once in the configuration
Repository centosplus is listed more than once in the configuration
Loading mirror speeds from cached hostfile

 * base: mirrors.aliyun.com
 * extras: mirrors.aliyun.com
 * updates: mirrors.aliyun.com
   正在解决依赖关系
   --> 正在检查事务
   ---> 软件包 kubectl.x86_64.0.1.19.5-0 将被 升级
   ---> 软件包 kubectl.x86_64.0.1.20.0-0 将被 更新
   ---> 软件包 kubelet.x86_64.0.1.19.5-0 将被 升级
   ---> 软件包 kubelet.x86_64.0.1.20.0-0 将被 更新
   --> 解决依赖关系完成

依赖关系解决

=================================================================================================================================================================================================

 Package                                      架构                                        版本                                             源                                               大小
=================================================================================================================================================================================================

正在更新:
 kubectl                                      x86_64                                      1.20.0-0                                         kubernetes                                      8.5 M
 kubelet                                      x86_64                                      1.20.0-0                                         kubernetes                                       20 M

事务概要
=================================================================================================================================================================================================

升级  2 软件包

总下载量：29 M
Downloading packages:
Delta RPMs disabled because /usr/bin/applydeltarpm not installed.
(1/2): 1281489661f7627c8b175b789d300fe3c703c8dd1f618dcdfeac1354131b376d-kubectl-1.20.0-0.x86_64.rpm                                                                       | 8.5 MB  00:00:25     

(2/2): 268447fe89ce41034f21c7a6c73290bd2e1920f856ec413285a5054260625822-kubelet-1.20.0-0.x86_64.rpm                                                                       |  20 MB  00:01:00     
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

总计                                                                                                                                                             487 kB/s |  29 MB  00:01:00     
Running transaction check
Running transaction test
Transaction test succeeded
Running transaction
  正在更新    : kubectl-1.20.0-0.x86_64                                                                                                                                                      1/4 
  正在更新    : kubelet-1.20.0-0.x86_64                                                                                                                                                      2/4 
  清理        : kubectl-1.19.5-0.x86_64                                                                                                                                                      3/4 
  清理        : kubelet-1.19.5-0.x86_64                                                                                                                                                      4/4 
  验证中      : kubelet-1.20.0-0.x86_64                                                                                                                                                      1/4 
  验证中      : kubectl-1.20.0-0.x86_64                                                                                                                                                      2/4 
  验证中      : kubectl-1.19.5-0.x86_64                                                                                                                                                      3/4 
  验证中      : kubelet-1.19.5-0.x86_64                                                                                                                                                      4/4 

更新完毕:
kubectl.x86_64 0:1.20.0-0                                                                       kubelet.x86_64 0:1.20.0-0                                                                      

完毕！
~~~



##### 2.2 重启 kubelet

~~~
[root@master1 ~]# sudo systemctl restart kubelet
Warning: kubelet.service changed on disk. Run 'systemctl daemon-reload' to reload units.
[root@master1 ~]# systemctl daemon-reload
[root@master1 ~]# sudo systemctl restart kubelet
[root@master1 ~]# kubectl uncordon master1
node/master1 uncordoned
[root@master1 ~]# kubectl  get node
NAME      STATUS   ROLES                  AGE   VERSION
master1   Ready    control-plane,master   18d   v1.20.0
n1        Ready    <none>                 18d   v1.19.5
n2        Ready    <none>                 15d   v1.19.5


~~~

##### 2.3 查看节点状态

~~~

[root@master1 ~]# kubectl get pods --all-namespaces -o wide
NAMESPACE     NAME                                       READY   STATUS    RESTARTS   AGE     IP             NODE      NOMINATED NODE   READINESS GATES
ghac          ghac-vue-pts-76dd6cd569-nzq27              1/1     Running   1          15d     10.99.40.136   n1        <none>           <none>
kube-system   calico-kube-controllers-6c89d944d5-dq944   1/1     Running   0          15m     10.99.217.9    n2        <none>           <none>
kube-system   calico-node-cqkdh                          1/1     Running   5          18d     108.88.3.152   n1        <none>           <none>
kube-system   calico-node-m9bdn                          1/1     Running   4          15d     108.88.3.179   n2        <none>           <none>
kube-system   calico-node-mhmvz                          1/1     Running   4          18d     108.88.3.118   master1   <none>           <none>
kube-system   coredns-68b9d7b887-wpv87                   1/1     Running   0          11m     10.99.217.14   n2        <none>           <none>
kube-system   coredns-68b9d7b887-xf6wd                   1/1     Running   0          11m     10.99.217.15   n2        <none>           <none>
kube-system   etcd-master1                               1/1     Running   0          12m     108.88.3.118   master1   <none>           <none>
kube-system   kube-apiserver-master1                     1/1     Running   0          11m     108.88.3.118   master1   <none>           <none>
kube-system   kube-controller-manager-master1            1/1     Running   0          11m     108.88.3.118   master1   <none>           <none>
kube-system   kube-proxy-4gc96                           1/1     Running   0          11m     108.88.3.179   n2        <none>           <none>
kube-system   kube-proxy-dmvcx                           1/1     Running   0          10m     108.88.3.152   n1        <none>           <none>
kube-system   kube-proxy-gg824                           1/1     Running   0          9m55s   108.88.3.118   master1   <none>           <none>
kube-system   kube-scheduler-master1                     1/1     Running   0          11m     108.88.3.118   master1   <none>           <none>
kube-system   metrics-server-c7d74c45-h8b7z              1/1     Running   0          15m     10.99.217.11   n2        <none>           <none>
kuboard       metrics-scraper-64bbf45f8f-vj86r           1/1     Running   3          15d     10.99.217.8    n2        <none>           <none>
~~~



##### 2 .4 将主节点恢复为可调度状态

~~~
[root@master1 ~]# kubectl uncordon master1

~~~





#### 三、工作节点升级

##### 3.1 将工作节点置于维护模式

  注意需要在master节点上运行

~~~
[root@ｍ1 ~]# kubectl drain n1 --ignore-daemonsets
~~~



##### 3.2 在工作节点上升级 kubeadm、kubelet 和 kubectl

~~~~
[root@n1 ~]# sudo yum update -y kubeadm-1.20.0 kubelet-1.20.0 kubectl-1.20.0
已加载插件：fastestmirror
Repository base is listed more than once in the configuration
Repository updates is listed more than once in the configuration
Repository extras is listed more than once in the configuration
Repository centosplus is listed more than once in the configuration
Determining fastest mirrors

 * base: mirrors.aliyun.com
 * extras: mirrors.aliyun.com
 * updates: mirrors.aliyun.com
   base                                                                                                                                                                      | 3.6 kB  00:00:00     
   docker-ce-stable                                                                                                                                                          | 3.5 kB  00:00:00     
   extras                                                                                                                                                                    | 2.9 kB  00:00:00     
   kubernetes                                                                                                                                                                | 1.4 kB  00:00:00     
   updates                                                                                                                                                                   | 2.9 kB  00:00:00     
   (1/2): kubernetes/primary                                                                                                                                                 | 134 kB  00:00:00     
   (2/2): docker-ce-stable/7/x86_64/primary_db                                                                                                                               | 116 kB  00:00:01     
   kubernetes                                                                                                                                                                               992/992
   正在解决依赖关系
   --> 正在检查事务
   ---> 软件包 kubeadm.x86_64.0.1.19.5-0 将被 升级
   ---> 软件包 kubeadm.x86_64.0.1.20.0-0 将被 更新
   ---> 软件包 kubectl.x86_64.0.1.19.5-0 将被 升级
   ---> 软件包 kubectl.x86_64.0.1.20.0-0 将被 更新
   ---> 软件包 kubelet.x86_64.0.1.19.5-0 将被 升级
   ---> 软件包 kubelet.x86_64.0.1.20.0-0 将被 更新
   --> 解决依赖关系完成

依赖关系解决

=================================================================================================================================================================================================

 Package                                      架构                                        版本                                             源                                               大小
=================================================================================================================================================================================================

正在更新:
 kubeadm                                      x86_64                                      1.20.0-0                                         kubernetes                                      8.3 M
 kubectl                                      x86_64                                      1.20.0-0                                         kubernetes                                      8.5 M
 kubelet                                      x86_64                                      1.20.0-0                                         kubernetes                                       20 M

事务概要
=================================================================================================================================================================================================

升级  3 软件包

总下载量：37 M
Downloading packages:
Delta RPMs disabled because /usr/bin/applydeltarpm not installed.
(1/3): 91e0f0a3a10ab757acf9611e8b81e1b272d76a5c400544a254d2c34a6ede1c11-kubeadm-1.20.0-0.x86_64.rpm                                                                       | 8.3 MB  00:00:47     
(2/3): 1281489661f7627c8b175b789d300fe3c703c8dd1f618dcdfeac1354131b376d-kubectl-1.20.0-0.x86_64.rpm                                                                       | 8.5 MB  00:00:48     

(3/3): 268447fe89ce41034f21c7a6c73290bd2e1920f856ec413285a5054260625822-kubelet-1.20.0-0.x86_64.rpm                                                                       |  20 MB  00:01:54     
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

总计                                                                                                                                                             233 kB/s |  37 MB  00:02:42     
Running transaction check
Running transaction test
Transaction test succeeded
Running transaction
  正在更新    : kubelet-1.20.0-0.x86_64                                                                                                                                                      1/6 
  正在更新    : kubectl-1.20.0-0.x86_64                                                                                                                                                      2/6 
  正在更新    : kubeadm-1.20.0-0.x86_64                                                                                                                                                      3/6 
  清理        : kubeadm-1.19.5-0.x86_64                                                                                                                                                      4/6 
  清理        : kubectl-1.19.5-0.x86_64                                                                                                                                                      5/6 
  清理        : kubelet-1.19.5-0.x86_64                                                                                                                                                      6/6 
  验证中      : kubectl-1.20.0-0.x86_64                                                                                                                                                      1/6 
  验证中      : kubelet-1.20.0-0.x86_64                                                                                                                                                      2/6 
  验证中      : kubeadm-1.20.0-0.x86_64                                                                                                                                                      3/6 
  验证中      : kubeadm-1.19.5-0.x86_64                                                                                                                                                      4/6 
  验证中      : kubelet-1.19.5-0.x86_64                                                                                                                                                      5/6 
  验证中      : kubectl-1.19.5-0.x86_64                                                                                                                                                      6/6 

更新完毕:
  kubeadm.x86_64 0:1.20.0-0                                       kubectl.x86_64 0:1.20.0-0                                       kubelet.x86_64 0:1.20.0-0                                      

完毕！
[root@n1 ~]# 
~~~~



##### 3.3 重启 kubelet

~~~
[root@n1 ~]# systemctl daemon-reload
[root@n1 ~]# sudo systemctl restart kubelet
[root@n1 ~]# 

~~~

##### 3.4 取消维护模式，使工作节点恢复可调度状态



~~~
[root@master1 ~]# kubectl uncordon n1
node/n1 uncordoned
[root@master1 ~]# 

~~~

##### 3.5 验证升级

~~~~
在更新所有控制平面和工作节点后，运行 kubectl get nodes 和 kubectl get pods --all-namespaces 检查集群的健康状态，确保所有节点都处于“Ready”状态，并且所有 Pod 正常运行且没有问题。

~~~~

##### 3.6 **检查弃用的 API**



~~~
检查您的应用程序清单和自定义资源是否使用了版本 1.19.5 中弃用的 API，并将其更新为适用于 Kubernetes 1.20 的适当 API。
~~~





如果有pods节点内容

~~~
[root@master1 ~]# kubectl drain n2 --ignore-daemonsets
node/n2 cordoned
error: unable to drain node "n2" due to error:cannot delete Pods with local storage (use --delete-emptydir-data to override): kube-system/metrics-server-c7d74c45-h8b7z, kuboard/metrics-scraper-64bbf45f8f-vj86r, continuing command...
There are pending nodes to be drained:
 n2
cannot delete Pods with local storage (use --delete-emptydir-data to override): kube-system/metrics-server-c7d74c45-h8b7z, kuboard/metrics-scraper-64bbf45f8f-vj86r
[root@master1 ~]# kubectl drain n2 --ignore-daemonsets --delete-emptydir-data 
node/n2 already cordoned
Warning: ignoring DaemonSet-managed Pods: kube-system/calico-node-m9bdn, kube-system/kube-proxy-4gc96
evicting pod kuboard/metrics-scraper-64bbf45f8f-vj86r
evicting pod kube-system/coredns-68b9d7b887-wpv87
evicting pod ghac/ghac-vue-pts-76dd6cd569-m8kvk
evicting pod kube-system/calico-kube-controllers-6c89d944d5-dq944
evicting pod kube-system/coredns-68b9d7b887-xf6wd
evicting pod kube-system/metrics-server-c7d74c45-h8b7z
pod/coredns-68b9d7b887-wpv87 evicted
pod/coredns-68b9d7b887-xf6wd evicted
pod/metrics-server-c7d74c45-h8b7z evicted
pod/metrics-scraper-64bbf45f8f-vj86r evicted
pod/calico-kube-controllers-6c89d944d5-dq944 evicted
pod/ghac-vue-pts-76dd6cd569-m8kvk evicted
node/n2 drained
[root@master1 ~]# 

~~~

### 四、从1.20.0升级1.21.0



##### 4.1 查看kubadmin支持版本

~~~~
yum list --showduplicates kubeadm 
# find the latest 1.20 version in the list
# it should look like 1.20.x-0, where x is the latest patch


~~~~

##### 4.2 **排空master节点**

~~~
  kubectl drain master1 --ignore-daemonsets  --delete-emptydir-data 

~~~





#### 4.2升级 kubeadm 

~~~

[root@master1 ~]# kubeadm version
kubeadm version: &version.Info{Major:"1", Minor:"20", GitVersion:"v1.20.0", GitCommit:"af46c47ce925f4c4ad5cc8d1fca46c7b77d13b38", GitTreeState:"clean", BuildDate:"2020-12-08T17:57:36Z", GoVersion:"go1.15.5", Compiler:"gc", Platform:"linux/amd64"}
[root@master1 ~]#   yum install -y kubeadm-1.21.0-0 
已加载插件：fastestmirror
Repository base is listed more than once in the configuration
Repository updates is listed more than once in the configuration
Repository extras is listed more than once in the configuration
Repository centosplus is listed more than once in the configuration
Loading mirror speeds from cached hostfile
 * base: mirrors.aliyun.com
 * extras: mirrors.aliyun.com
 * updates: mirrors.aliyun.com
正在解决依赖关系
--> 正在检查事务
---> 软件包 kubeadm.x86_64.0.1.20.0-0 将被 升级
---> 软件包 kubeadm.x86_64.0.1.21.0-0 将被 更新
--> 解决依赖关系完成

依赖关系解决

=================================================================================================================================================================================================
 Package                                      架构                                        版本                                             源                                               大小
=================================================================================================================================================================================================
正在更新:
 kubeadm                                      x86_64                                      1.21.0-0                                         kubernetes                                      9.1 M

事务概要
=================================================================================================================================================================================================
升级  1 软件包

总下载量：9.1 M
Downloading packages:
Delta RPMs disabled because /usr/bin/applydeltarpm not installed.
dc4816b13248589b85ee9f950593256d08a3e6d4e419239faf7a83fe686f641c-kubeadm-1.21.0-0.x86_64.rpm                                                                              | 9.1 MB  00:00:27     
Running transaction check
Running transaction test
Transaction test succeeded
Running transaction
  正在更新    : kubeadm-1.21.0-0.x86_64                                                                                                                                                      1/2 
  清理        : kubeadm-1.20.0-0.x86_64                                                                                                                                                      2/2 
  验证中      : kubeadm-1.21.0-0.x86_64                                                                                                                                                      1/2 
  验证中      : kubeadm-1.20.0-0.x86_64                                                                                                                                                      2/2 

更新完毕:
  kubeadm.x86_64 0:1.21.0-0                                                                                                                                                                      

完毕！

~~~

 ##### 4.3 验证升级计划

~~~


[root@master1 ~]#  kubeadm upgrade plan
[upgrade/config] Making sure the configuration is correct:
[upgrade/config] Reading configuration from the cluster...
[upgrade/config] FYI: You can look at this config file with 'kubectl -n kube-system get cm kubeadm-config -o yaml'
[preflight] Running pre-flight checks.
[upgrade] Running cluster health checks
[upgrade] Fetching available versions to upgrade to
[upgrade/versions] Cluster version: v1.20.0
[upgrade/versions] kubeadm version: v1.21.0
I0726 16:55:56.765100   26002 version.go:254] remote version is much newer: v1.27.4; falling back to: stable-1.21
[upgrade/versions] Target version: v1.21.14
[upgrade/versions] Latest version in the v1.20 series: v1.20.15

Components that must be upgraded manually after you have upgraded the control plane with 'kubeadm upgrade apply':
COMPONENT   CURRENT       TARGET
kubelet     3 x v1.20.0   v1.20.15

Upgrade to the latest version in the v1.20 series:

COMPONENT                 CURRENT    TARGET
kube-apiserver            v1.20.0    v1.20.15
kube-controller-manager   v1.20.0    v1.20.15
kube-scheduler            v1.20.0    v1.20.15
kube-proxy                v1.20.0    v1.20.15
CoreDNS                   1.7.0      v1.8.0
etcd                      3.4.13-0   3.4.13-0

You can now apply the upgrade by executing the following command:

        kubeadm upgrade apply v1.20.15

_____________________________________________________________________

Components that must be upgraded manually after you have upgraded the control plane with 'kubeadm upgrade apply':
COMPONENT   CURRENT       TARGET
kubelet     3 x v1.20.0   v1.21.14

Upgrade to the latest stable version:

COMPONENT                 CURRENT    TARGET
kube-apiserver            v1.20.0    v1.21.14
kube-controller-manager   v1.20.0    v1.21.14
kube-scheduler            v1.20.0    v1.21.14
kube-proxy                v1.20.0    v1.21.14
CoreDNS                   1.7.0      v1.8.0
etcd                      3.4.13-0   3.4.13-0

You can now apply the upgrade by executing the following command:

        kubeadm upgrade apply v1.21.14

Note: Before you can perform this upgrade, you have to update kubeadm to v1.21.14.

_____________________________________________________________________


The table below shows the current state of component configs as understood by this version of kubeadm.
Configs that have a "yes" mark in the "MANUAL UPGRADE REQUIRED" column require manual config upgrade or
resetting to kubeadm defaults before a successful upgrade can be performed. The version to manually
upgrade to is denoted in the "PREFERRED VERSION" column.

API GROUP                 CURRENT VERSION   PREFERRED VERSION   MANUAL UPGRADE REQUIRED
kubeproxy.config.k8s.io   v1alpha1          v1alpha1            no
kubelet.config.k8s.io     v1beta1           v1beta1             no
_____________________________________________________________________

[root@master1 ~]# 
~~~



##### 4.5执行升级

~~~
[root@master1 ~]#  kubeadm upgrade apply v1.21.0
[upgrade/config] Making sure the configuration is correct:
[upgrade/config] Reading configuration from the cluster...
[upgrade/config] FYI: You can look at this config file with 'kubectl -n kube-system get cm kubeadm-config -o yaml'
[preflight] Running pre-flight checks.
[upgrade] Running cluster health checks
[upgrade/version] You have chosen to change the cluster version to "v1.21.0"
[upgrade/versions] Cluster version: v1.20.0
[upgrade/versions] kubeadm version: v1.21.0
[upgrade/confirm] Are you sure you want to proceed with the upgrade? [y/N]: y
[upgrade/prepull] Pulling images required for setting up a Kubernetes cluster
[upgrade/prepull] This might take a minute or two, depending on the speed of your internet connection
[upgrade/prepull] You can also perform this action in beforehand using 'kubeadm config images pull'
[preflight] Some fatal errors occurred:
        [ERROR ImagePull]: failed to pull image registry.aliyuncs.com/k8sxio/coredns/coredns:v1.8.0: output: Error response from daemon: pull access denied for registry.aliyuncs.com/k8sxio/coredns/coredns, repository does not exist or may require 'docker login': denied: requested access to the resource is denied
, error: exit status 1
[preflight] If you know what you are doing, you can make a check non-fatal with `--ignore-preflight-errors=...`
To see the stack trace of this error execute with --v=5 or higher
[root@master1 ~]# docker pull  [ERROR ImagePull]: failed to pull image registry.aliyuncs.com/k8sxio/coredns/coredns:v1.8.0: output: Error response from daemon: pull access denied for registry.aliyuncs.com/k8sxio/coredns/coredns, repository does not exist or may require 'docker login': denied: requested access to the resource is denied
"docker pull" requires exactly 1 argument.
See 'docker pull --help'.

Usage:  docker pull [OPTIONS] NAME[:TAG|@DIGEST]

Pull an image or a repository from a registry

### 直接从官方下载coredns:1.8.0

[root@master1 ~]# docker pull coredns/coredns:1.8.0
1.8.0: Pulling from coredns/coredns
c6568d217a00: Already exists 
5984b6d55edf: Pull complete 
Digest: sha256:cc8fb77bc2a0541949d1d9320a641b82fd392b0d3d8145469ca4709ae769980e
Status: Downloaded newer image for coredns/coredns:1.8.0
docker.io/coredns/coredns:1.8.0
[root@master1 ~]# 


### 将下载镜象打成 registry.aliyuncs.com/k8sxio/coredns/coredns:v1.8.0

[root@master1 ~]# docker pull coredns/coredns:1.8.0
1.8.0: Pulling from coredns/coredns
c6568d217a00: Already exists 
5984b6d55edf: Pull complete 
Digest: sha256:cc8fb77bc2a0541949d1d9320a641b82fd392b0d3d8145469ca4709ae769980e
Status: Downloaded newer image for coredns/coredns:1.8.0
docker.io/coredns/coredns:1.8.0

[root@master1 ~]# docker image list
REPOSITORY                                                          TAG                 IMAGE ID            CREATED             SIZE
       2 years ago         46.4MB
coredns/coredns                                                     1.8.0               296a6d5035e2        
[root@master1 ~]# docker tag 296a6d5035e2 registry.aliyuncs.com/k8sxio/coredns/coredns:v1.8.0
[root@master1 ~]# docker image list
REPOSITORY                                                          TAG                 IMAGE ID            CREATED             SIZE
s
coredns/coredns                                                     1.8.0               296a6d5035e2        2 years ago         42.5MB
registry.aliyuncs.com/k8sxio/coredns/coredns                        v1.8.0              296a6d5035e2       
[root@master1 ~]# 
 
 
 再次执行升级
 
 [root@master1 ~]#  kubeadm upgrade apply v1.21.0
[upgrade/config] Making sure the configuration is correct:
[upgrade/config] Reading configuration from the cluster...
[upgrade/config] FYI: You can look at this config file with 'kubectl -n kube-system get cm kubeadm-config -o yaml'
[preflight] Running pre-flight checks.
[upgrade] Running cluster health checks
[upgrade/version] You have chosen to change the cluster version to "v1.21.0"
[upgrade/versions] Cluster version: v1.20.0
[upgrade/versions] kubeadm version: v1.21.0
[upgrade/confirm] Are you sure you want to proceed with the upgrade? [y/N]: y
[upgrade/prepull] Pulling images required for setting up a Kubernetes cluster
[upgrade/prepull] This might take a minute or two, depending on the speed of your internet connection
[upgrade/prepull] You can also perform this action in beforehand using 'kubeadm config images pull'
[upgrade/apply] Upgrading your Static Pod-hosted control plane to version "v1.21.0"...
Static pod: kube-apiserver-master1 hash: 2249b706233aa2f5354d3b91e59119b1
Static pod: kube-controller-manager-master1 hash: 5c167cde7076af2cc1f9dc2e1789ec16
Static pod: kube-scheduler-master1 hash: 48ac8d1f700dff2f72784e822f2804e2
[upgrade/etcd] Upgrading to TLS for etcd
Static pod: etcd-master1 hash: 40bfc565a619cc282544e3482fdbdd1a
[upgrade/staticpods] Preparing for "etcd" upgrade
[upgrade/staticpods] Current and new manifests of etcd are equal, skipping upgrade
[upgrade/etcd] Waiting for etcd to become available
[upgrade/staticpods] Writing new Static Pod manifests to "/etc/kubernetes/tmp/kubeadm-upgraded-manifests613060824"
[upgrade/staticpods] Preparing for "kube-apiserver" upgrade
[upgrade/staticpods] Renewing apiserver certificate
[upgrade/staticpods] Renewing apiserver-kubelet-client certificate
[upgrade/staticpods] Renewing front-proxy-client certificate
[upgrade/staticpods] Renewing apiserver-etcd-client certificate
[upgrade/staticpods] Moved new manifest to "/etc/kubernetes/manifests/kube-apiserver.yaml" and backed up old manifest to "/etc/kubernetes/tmp/kubeadm-backup-manifests-2023-07-27-10-31-07/kube-apiserver.yaml"
[upgrade/staticpods] Waiting for the kubelet to restart the component
[upgrade/staticpods] This might take a minute or longer depending on the component/version gap (timeout 5m0s)
Static pod: kube-apiserver-master1 hash: 2249b706233aa2f5354d3b91e59119b1
Static pod: kube-apiserver-master1 hash: 33cecd23463f1e08e48b041cb48d6770
[apiclient] Found 1 Pods for label selector component=kube-apiserver
[upgrade/staticpods] Component "kube-apiserver" upgraded successfully!
[upgrade/staticpods] Preparing for "kube-controller-manager" upgrade
[upgrade/staticpods] Renewing controller-manager.conf certificate
[upgrade/staticpods] Moved new manifest to "/etc/kubernetes/manifests/kube-controller-manager.yaml" and backed up old manifest to "/etc/kubernetes/tmp/kubeadm-backup-manifests-2023-07-27-10-31-07/kube-controller-manager.yaml"
[upgrade/staticpods] Waiting for the kubelet to restart the component
[upgrade/staticpods] This might take a minute or longer depending on the component/version gap (timeout 5m0s)
Static pod: kube-controller-manager-master1 hash: 5c167cde7076af2cc1f9dc2e1789ec16
Static pod: kube-controller-manager-master1 hash: 71ab50d02954643b0f7cb0027eb471e7
[apiclient] Found 1 Pods for label selector component=kube-controller-manager
[upgrade/staticpods] Component "kube-controller-manager" upgraded successfully!
[upgrade/staticpods] Preparing for "kube-scheduler" upgrade
[upgrade/staticpods] Renewing scheduler.conf certificate
[upgrade/staticpods] Moved new manifest to "/etc/kubernetes/manifests/kube-scheduler.yaml" and backed up old manifest to "/etc/kubernetes/tmp/kubeadm-backup-manifests-2023-07-27-10-31-07/kube-scheduler.yaml"
[upgrade/staticpods] Waiting for the kubelet to restart the component
[upgrade/staticpods] This might take a minute or longer depending on the component/version gap (timeout 5m0s)
Static pod: kube-scheduler-master1 hash: 48ac8d1f700dff2f72784e822f2804e2
Static pod: kube-scheduler-master1 hash: a3d8aae2876ee1c1a49952806f78f616
[apiclient] Found 1 Pods for label selector component=kube-scheduler
[upgrade/staticpods] Component "kube-scheduler" upgraded successfully!
[upgrade/postupgrade] Applying label node-role.kubernetes.io/control-plane='' to Nodes with label node-role.kubernetes.io/master='' (deprecated)
[upgrade/postupgrade] Applying label node.kubernetes.io/exclude-from-external-load-balancers='' to control plane Nodes
[upload-config] Storing the configuration used in ConfigMap "kubeadm-config" in the "kube-system" Namespace
[kubelet] Creating a ConfigMap "kubelet-config-1.21" in namespace kube-system with the configuration for the kubelets in the cluster
[kubelet-start] Writing kubelet configuration to file "/var/lib/kubelet/config.yaml"
[bootstrap-token] configured RBAC rules to allow Node Bootstrap tokens to get nodes
[bootstrap-token] configured RBAC rules to allow Node Bootstrap tokens to post CSRs in order for nodes to get long term certificate credentials
[bootstrap-token] configured RBAC rules to allow the csrapprover controller automatically approve CSRs from a Node Bootstrap Token
[bootstrap-token] configured RBAC rules to allow certificate rotation for all node client certificates in the cluster
[addons] Applied essential addon: CoreDNS
[addons] Applied essential addon: kube-proxy

[upgrade/successful] SUCCESS! Your cluster was upgraded to "v1.21.0". Enjoy!

[upgrade/kubelet] Now that your control plane is upgraded, please proceed with upgrading your kubelets if you haven't already done so.
[root@master1 ~]# 

~~~



##### 4.6 将 kubelet 和 kubectl 升级到与控制平面版本相匹配

~~~
sudo yum update -y kubelet-1.21.0 kubectl-1.21.0

[root@master1 ~]# systemctl daemon-reload
[root@master1 ~]# sudo systemctl restart kubelet
[root@master1 ~]# sudo systemctl status kubelet
● kubelet.service - kubelet: The Kubernetes Node Agent
   Loaded: loaded (/usr/lib/systemd/system/kubelet.service; enabled; vendor preset: disabled)
  Drop-In: /usr/lib/systemd/system/kubelet.service.d
           └─10-kubeadm.conf
   Active: active (running) since 四 2023-07-27 10:54:14 CST; 13s ago
     Docs: https://kubernetes.io/docs/
 Main PID: 31174 (kubelet)
    Tasks: 15
   Memory: 36.3M
   CGroup: /system.slice/kubelet.service
           └─31174 /usr/bin/kubelet --bootstrap-kubeconfig=/etc/kubernetes/bootstrap-kubelet.conf --kubeconfig=/etc/kubernetes/kubelet.conf --config=/var/lib/kubelet/config.yaml --network-pl...

7月 27 10:54:21 master1 kubelet[31174]: E0727 10:54:21.834933   31174 kubelet.go:1690] "Failed creating a mirror pod for" err="pods \"kube-scheduler-master1\" already exists" p...uler-master1"
7月 27 10:54:21 master1 kubelet[31174]: E0727 10:54:21.835285   31174 kubelet.go:1690] "Failed creating a mirror pod for" err="pods \"kube-apiserver-master1\" already exists" p...rver-master1"
7月 27 10:54:21 master1 kubelet[31174]: E0727 10:54:21.835620   31174 kubelet.go:1690] "Failed creating a mirror pod for" err="pods \"etcd-master1\" already exists" pod="kube-s...etcd-master1"
7月 27 10:54:21 master1 kubelet[31174]: I0727 10:54:21.921642   31174 reconciler.go:224] "operationExecutor.VerifyControllerAttachedVolume started for volume \"etcd-certs\" (Un...3ce40709\") "
7月 27 10:54:21 master1 kubelet[31174]: I0727 10:54:21.922292   31174 reconciler.go:224] "operationExecutor.VerifyControllerAttachedVolume started for volume \"etcd-data\" (Uni...3ce40709\") "
7月 27 10:54:21 master1 kubelet[31174]: I0727 10:54:21.922693   31174 reconciler.go:224] "operationExecutor.VerifyControllerAttachedVolume started for volume \"ca-certs\" (Uniq...b4ba8300\") "
7月 27 10:54:21 master1 kubelet[31174]: I0727 10:54:21.924235   31174 reconciler.go:224] "operationExecutor.VerifyControllerAttachedVolume started for volume \"etc-pki\" (Uniqu...b4ba8300\") "
7月 27 10:54:21 master1 kubelet[31174]: I0727 10:54:21.924444   31174 reconciler.go:224] "operationExecutor.VerifyControllerAttachedVolume started for volume \"k8s-certs\" (Uni...b4ba8300\") "
7月 27 10:54:21 master1 kubelet[31174]: I0727 10:54:21.924612   31174 reconciler.go:157] "Reconciler: start to sync state"
7月 27 10:54:22 master1 kubelet[31174]: E0727 10:54:22.198958   31174 kubelet.go:1690] "Failed creating a mirror pod for" err="pods \"kube-controller-manager-master1\" already ...ager-master1"
Hint: Some lines were ellipsized, use -l to show in full.


~~~

##### 4.7重启 kubelet

~~~~
[root@master1 ~]# sudo systemctl restart kubelet
Warning: kubelet.service changed on disk. Run 'systemctl daemon-reload' to reload units.
[root@master1 ~]# systemctl daemon-reload
[root@master1 ~]# sudo systemctl restart kubelet
~~~~



##### 4.8 验证升级

~~~
[root@master1 ~]# kubectl get node  -o wide
NAME      STATUS                     ROLES                  AGE   VERSION   INTERNAL-IP    EXTERNAL-IP   OS-IMAGE                KERNEL-VERSION                CONTAINER-RUNTIME
master1   Ready,SchedulingDisabled   control-plane,master   19d   v1.21.0   108.88.3.118   <none>        CentOS Linux 7 (Core)   3.10.0-1160.92.1.el7.x86_64   docker://19.3.11
n1        Ready                      <none>                 19d   v1.20.0   108.88.3.152   <none>        CentOS Linux 7 (Core)   3.10.0-1160.92.1.el7.x86_64   docker://19.3.11
n2        Ready                      <none>                 16d   v1.20.0   108.88.3.179   <none>        CentOS Linux 7 (Core)   3.10.0-1160.92.1.el7.x86_64   docker://19.3.11

~~~



##### 4.9  将主节点恢复为可调度状态

~~~~

[root@master1 ~]# kubectl uncordon master1

~~~~



##### 4.8 升级工节点 (参考上面工作节点升级)

```
### 在主节点停应用： 
 #### [root@ｍ1 ~]# kubectl drain n1 --ignore-daemonsets
 
 
 ### 将下载镜象打成 registry.aliyuncs.com/k8sxio/coredns/coredns:v1.8.0

[root@ｍ1 ~]# docker pull coredns/coredns:1.8.0
1.8.0: Pulling from coredns/coredns
c6568d217a00: Already exists 
5984b6d55edf: Pull complete 
Digest: sha256:cc8fb77bc2a0541949d1d9320a641b82fd392b0d3d8145469ca4709ae769980e
Status: Downloaded newer image for coredns/coredns:1.8.0
docker.io/coredns/coredns:1.8.0

[root@ｍ1 ~]# docker image list
REPOSITORY                                                          TAG                 IMAGE ID            CREATED             SIZE
       2 years ago         46.4MB
coredns/coredns                                                     1.8.0               296a6d5035e2        
[root@master1 ~]# docker tag 296a6d5035e2 registry.aliyuncs.com/k8sxio/coredns/coredns:v1.8.0
[root@master1 ~]# docker image list
 
 
 
 ### 在升级节点用执行下面
      sudo yum update -y kubeadm-1.21.0 kubelet-1.21.0 kubectl-1.21.0
      sudo systemctl restart kubelet
      systemctl daemon-reload
      sudo systemctl restart kubelet
      sudo systemctl status kubelet
      
## 解锁：



```



