# RBAC使用例子
## 新建服务账号bob
```
[root@k8s-master01 ~]# kubectl create serviceaccount bob
serviceaccount/bob created
```
## 新建测试命名空间
```
[root@k8s-master01 ~]# kubectl create namespace bob
namespace/bob created
[root@k8s-master01 ~]# kubectl get ns
NAME              STATUS   AGE
bob               Active   8s
chamsrm           Active   29h
default           Active   6d4h
dhec              Active   29h
ghac-ut           Active   35h
ingress-nginx     Active   2d4h
kube-node-lease   Active   6d4h
kube-public       Active   6d4h
kube-system       Active   6d4h
kuboard           Active   6d3h
monitor           Active   5d6h
nginx-ingress     Active   3d4h
rabbitmq          Active   5d16h

```
## 新建集群角色及绑订

https://github.com/kubernetes-sigs/krew/releases/tag/v0.4.3
```
[root@k8s-master01 tools]# ll
total 19616
-rw-r--r-- 1 root root  4100150 Mar 17 06:07 krew-darwin_amd64.tar.gz
-rwxr-xr-x 1 1001  121 11836580 Dec 31  1999 krew-linux_amd64
-rw-r--r-- 1 root root  4128657 Mar 17 06:31 krew-linux_amd64.tar.gz
-rw-r--r-- 1 root root     2982 Mar 17 06:33 krew.yaml
-rw-r--r-- 1 1001  121    11358 Dec 31  1999 LICENSE
[root@k8s-master01 tools]# ./krew-linux_amd64 install --manifest=krew.yaml --archive=krew-linux_amd64.tar.gz
Installing plugin: krew
Installed plugin: krew
\
 | Use this plugin:
 | 	kubectl krew
 | Documentation:
 | 	https://krew.sigs.k8s.io/
 | Caveats:
 | \
 |  | krew is now installed! To start using kubectl plugins, you need to add
 |  | krew's installation directory to your PATH:
 |  | 
 |  |   * macOS/Linux:
 |  |     - Add the following to your ~/.bashrc or ~/.zshrc:
 |  |         export PATH="${KREW_ROOT:-$HOME/.krew}/bin:$PATH"
 |  |     - Restart your shell.
 |  | 
 |  |   * Windows: Add %USERPROFILE%\.krew\bin to your PATH environment variable
 |  | 
 |  | To list krew commands and to get help, run:
 |  |   $ kubectl krew
 |  | For a full list of available plugins, run:
 |  |   $ kubectl krew search
 |  | 
 |  | You can find documentation at
 |  |   https://krew.sigs.k8s.io/docs/user-guide/quickstart/.
 | /
/
[root@k8s-master01 tools]# 


```

### 安装使用K9S集群管理插件
```
  988  tar -zxvf krew-linux_amd64.tar.gz 
  989  ll
  990  ./krew-linux_amd64 install --manifest=krew.yaml --archive=krew-linux_amd64.tar.gz


```

### 更新 krew

```
由于github不稳定，需要执行下载每5秒
#!/bin/bash
export PATH="${KREW_ROOT:-$HOME/.krew}/bin:$PATH"
while true;
do
 kubectl krew update
 sleep 5;
done


```




### 安装view-serviceaccount 插件
```
[root@k8s-master01 ~]# kubectl krew install view-serviceaccount-kubeconfig
Updated the local copy of plugin index.
Installed plugin: view-serviceaccount-kubeconfig
\
 | Use this plugin:
 | 	kubectl view-serviceaccount-kubeconfig
 | Documentation:
 | 	https://github.com/superbrothers/kubectl-view-serviceaccount-kubeconfig-plugin
/
WARNING: You installed plugin "view-serviceaccount-kubeconfig" from the krew-index plugin repository.
   These plugins are not audited for security by the Krew maintainers.
   Run them at your own risk.
[root@k8s-master01 ~]# 
[root@k8s-master01 ~]# 
[root@k8s-master01 ~]# 
[root@k8s-master01 ~]# 
[root@k8s-master01 ~]# kubectl view-serviceaccount-kubeconfig
Error: exactly one SERVICEACCOUT is required, got 0
Usage:
  kubectl view-serviceaccount-kubeconfig SERVICEACCOUNT [options] [flags]

Examples:
  # Show a kubeconfig setting of serviceaccount/default
  kubectl view-serviceaccount-kubeconfig default
  
  # Show a kubeconfig setting of serviceaccount/bot in namespace/kube-system
  kubectl view-serviceaccount-kubeconfig bot -n kube-system
  
  # Show a kubeconfig setting of serviceaccount/default in JSON format
  kubectl view-serviceaccount-kubeconfig default -o json


[root@k8s-master01 ~]# 


```

