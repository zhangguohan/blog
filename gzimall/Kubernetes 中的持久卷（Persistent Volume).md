# Kubernetes 中的持久卷（Persistent Volume)

### 一、新建一个PV卷

```
[root@k8s-master01 tank]# more pv.yaml 
apiVersion: v1
kind: PersistentVolume
metadata:
  name: pv0001
spec:
  capacity:
    storage: 5Gi
  volumeMode: Filesystem
  accessModes:
    - ReadWriteMany
  persistentVolumeReclaimPolicy: Recycle
  storageClassName: nfs-slow
  mountOptions:
    - hard
    - nfsvers=4.1
  nfs:
    path: /files/nfs/tank
    server: 108.88.3.185

[root@k8s-master01 tank]#kubectl create -f pv.yaml 

[root@k8s-master01 tank]# kubectl get pv
NAME     CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS   CLAIM             STORAGECLASS   REASON   AGE
pv0001   5Gi        RWX            Recycle          Bound    default/myclaim   nfs-slow                38m
[root@k8s-master01 tank]# 
```

### 二、新建一个PVC申领

```
[root@k8s-master01 tank]# more pvc.yaml 
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: myclaim
spec:
  accessModes:
    - ReadWriteMany
  volumeMode: Filesystem
  resources:
    requests:
      storage: 5Gi
  storageClassName: nfs-slow
```

### 三、将新建的PVC绑定到pod中

```
[root@k8s-master01 tank]#  more nginx-pod-pv-pvc.yaml 
apiVersion: v1
kind: Pod
metadata:
  name: mypod
spec:
  containers:
    - name: myfrontend
      image: nginx
      volumeMounts:
      - mountPath: "/var/www/html"
        name: mypd
  volumes:
    - name: mypd
      persistentVolumeClaim:
        claimName: myclaim
[root@k8s-master01 tank]#
```

### 四、查看PVPVC状态

```
[root@k8s-master01 tank]# kubectl get pv,pvc
NAME                      CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS   CLAIM             STORAGECLASS   REASON   AGE
persistentvolume/pv0001   5Gi        RWX            Recycle          Bound    default/myclaim   nfs-slow                48m

NAME                            STATUS   VOLUME   CAPACITY   ACCESS MODES   STORAGECLASS   AGE
persistentvolumeclaim/myclaim   Bound    pv0001   5Gi        RWX            nfs-slow       39m
[root@k8s-master01 tank]# 
```