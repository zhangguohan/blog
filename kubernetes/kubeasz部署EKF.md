
# 使用kubeasz安装EFK日志系统
## 安装efk
```
[root@k8s-master01 ~]# kubectl apply -f /etc/kubeasz/manifests/efk/
service/elasticsearch-logging created
configmap/fluentd-es-config-v0.2.0 created
serviceaccount/fluentd-es created
clusterrole.rbac.authorization.k8s.io/fluentd-es created
clusterrolebinding.rbac.authorization.k8s.io/fluentd-es created
Warning: spec.template.metadata.annotations[seccomp.security.alpha.kubernetes.io/pod]: deprecated since v1.19, non-functional in a future release; use the "seccompProfile" field instead
daemonset.apps/fluentd-es-v2.4.0 created
deployment.apps/kibana-logging created
service/kibana-logging created

```
### 由于使用新版本k8s需要修参数

```
[root@k8s-master01 efk]# grep -r seccomp.security.alpha.kubernetes.io/pod* *
fluentd-es-ds.yaml:        seccomp.security.alpha.kubernetes.io/pod: 'docker/default'
kibana-deployment.yaml:        seccomp.security.alpha.kubernetes.io/pod: 'docker/default'
[root@k8s-master01 efk]# vi fluentd-es-ds.yaml  
[root@k8s-master01 efk]# vi kibana-deployment.yaml 
```



### 重新执行部署
```
[root@k8s-master01 efk]# kubectl apply -f /etc/kubeasz/manifests/efk/
service/elasticsearch-logging created
configmap/fluentd-es-config-v0.2.0 created
serviceaccount/fluentd-es created
clusterrole.rbac.authorization.k8s.io/fluentd-es created
clusterrolebinding.rbac.authorization.k8s.io/fluentd-es created
daemonset.apps/fluentd-es-v2.4.0 created
deployment.apps/kibana-logging created
service/kibana-logging created
[root@k8s-master01 efk]# kubectl apply -f /etc/kubeasz/manifests/efk/es-without-pv/
serviceaccount/elasticsearch-logging created
clusterrole.rbac.authorization.k8s.io/elasticsearch-logging created
clusterrolebinding.rbac.authorization.k8s.io/elasticsearch-logging created
statefulset.apps/elasticsearch-logging created
```
### 查看安装结果
```
[root@k8s-master01 efk]# kubectl get pods -n kube-system|grep -E 'elasticsearch|fluentd|kibana'
elasticsearch-logging-0                      0/1     Init:0/1   0                12s
fluentd-es-v2.4.0-57phh                      1/1     Running    0                64s
fluentd-es-v2.4.0-dnlpj                      1/1     Running    0                64s
fluentd-es-v2.4.0-ghttm                      1/1     Running    0                64s
fluentd-es-v2.4.0-jkfn5                      1/1     Running    0                64s
kibana-logging-76b7c849f-9b4sw               1/1     Running    0                64s
[root@k8s-master01 efk]# kubectl get pods -n kube-system|grep -E 'elasticsearch|fluentd|kibana'
elasticsearch-logging-0                      1/1     Running   0                6m
elasticsearch-logging-1                      1/1     Running   0                3m41s
fluentd-es-v2.4.0-57phh                      1/1     Running   0                6m52s
fluentd-es-v2.4.0-dnlpj                      1/1     Running   0                6m52s
fluentd-es-v2.4.0-ghttm                      1/1     Running   0                6m52s
fluentd-es-v2.4.0-jkfn5                      1/1     Running   0                6m52s
kibana-logging-76b7c849f-9b4sw               1/1     Running   0                6m52s
```

### 查看kibana系日志
```
[root@k8s-master01 efk]# kubectl logs -n kube-system kibana-logging-76b7c849f-9b4sw  -f
{"type":"log","@timestamp":"2023-03-16T02:54:15Z","tags":["warning","config","deprecation"],"pid":1,"message":"You should set server.basePath along with server.rewriteBasePath. Starting in 7.0, Kibana will expct that all requests start with server.basePath rather than expecting you to rewrite the requests in your reverse proxy. Set server.rewriteBasePath to false to preserve the current behavior and silence this waning."}

{"type":"log","@timestamp":"2023-03-16T02:58:12Z","tags":["warning","elasticsearch","admin"],"pid":1,"message":"No living 
[root@k8s-master01 efk]#  kubectl cluster-info | grep Kibana
Kibana is running at https://108.88.3.102:6443/api/v1/namespaces/kube-system/services/kibana-logging/proxy
[root@k8s-master01 efk]# 

```

### 部署basic-auth 认证