# Kubernets使用NFS

```
[root@k8s-master01 tank]# more nginx-nfs.yaml 
apiVersion: v1
kind: Pod
metadata:
  name: test-pd
spec:
  containers:
  - image: nginx
    name: nginx
    volumeMounts:
    - mountPath: /www
      name: cache-volume
  - image: nginx
    name: nginx2
    command:
    - sh 
    - -c 
    - sleep 3600    
    volumeMounts:
    - mountPath: /opt
      name: cache-volume
    - mountPath: /mnt
      name: nfs-volume
  volumes:
  - name: test-volume
    hostPath:
      # directory location on host
      path: /usr/share/zoneinfo/Asia/Shanghai
      # this field is optional
      type: FileOrCreate
  - name: cache-volume
    emptyDir: {} 
  - name: nfs-volume
    nfs:
      server: 108.88.3.185
      path: /files/nfs/tank        
[root@k8s-master01 tank]# 
```

新建测试pod

```
[root@k8s-master01 tank]# kubectl  create -f nginx-nfs.yaml 
```

查看结果：

```
[root@k8s-master01 tank]# kubectl exec -it  test-pd -c nginx2 -- sh
# df
Filesystem                    1K-blocks      Used  Available Use% Mounted on
overlay                        47285700   5471068   41814632  12% /
tmpfs                             65536         0      65536   0% /dev
tmpfs                           2013592         0    2013592   0% /sys/fs/cgroup
/dev/mapper/centos-root        47285700   5471068   41814632  12% /opt
108.88.3.185:/files/nfs/tank 2883604480 247475200 2635820032   9% /mnt
shm                               65536         0      65536   0% /dev/shm
tmpfs                           2013592        12    2013580   1% /run/secrets/kubernetes.io/serviceaccount
tmpfs                           2013592         0    2013592   0% /proc/acpi
tmpfs                           2013592         0    2013592   0% /proc/scsi
tmpfs                           2013592         0    2013592   0% /sys/firmware
# 
```