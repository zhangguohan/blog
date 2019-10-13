### Docker资源限制及验证

#### 使用测试镜象

~~~
[root@docker01 ~]# docker pull lorel/docker-stress-ng
Using default tag: latest
latest: Pulling from lorel/docker-stress-ng
c52e3ed763ff: Pull complete 
a3ed95caeb02: Pull complete 
7f831269c70e: Pull complete 
Digest: sha256:c8776b750869e274b340f8e8eb9a7d8fb2472edd5b25ff5b7d55728bca681322
Status: Downloaded newer image for lorel/docker-stress-ng:latest
[root@docker01 ~]#

~~~


#### 测试Docker内存限制
~~~

### 指定容器使用限制256内存 

[root@docker01 ~]# docker run --name tank -it --rm -m 256m  lorel/docker-stress-ng stress-ng --vm 4
stress-ng: info: [1] defaulting to a 86400 second run per stressor
stress-ng: info: [1] dispatching hogs: 4 vm

查看容器使用情况（内存限制在256M）

[root@docker01 ~]# docker stats

CONTAINER ID        NAME                CPU %               MEM USAGE / LIMIT   MEM %               NET I/O             BLOCK I/O           PIDS
e6a455063cf1        tank                0.02%               255.8MiB / 256MiB   99.94%              656B / 0B           508kB / 978MB       9

~~~




#### 测试Docker CPU 限制

~~~

指定容器使用限制2个CPU

[root@docker01 ~]# docker run --name tank -it --rm  --cpus 2  lorel/docker-stress-ng stress-ng --cpu 28
stress-ng: info: [1] defaulting to a 86400 second run per stressor
stress-ng: info: [1] dispatching hogs: 28 cpu


查看容器使用情况 (CPU限制200%）



[root@docker01 ~]# docker stats



CONTAINER ID        NAME                CPU %               MEM USAGE / LIMIT     MEM %               NET I/O             BLOCK I/O           PIDS
d8e3028d1435        tank                245.17%             6.441MiB / 4.193GiB   0.15%               586B / 0B           0B / 0B             29

CONTAINER ID        NAME                CPU %               MEM USAGE / LIMIT     MEM %               NET I/O             BLOCK I/O           PIDS
d8e3028d1435        tank                245.17%             6.441MiB / 4.193GiB   0.15%               586B / 0B           0B / 0B             29



指定容器使用在那几个CPU核上，（第1核，第2核）
[root@docker01 ~]# docker run --name tank -it --rm  --cpuset-cpus 0,2  lorel/docker-stress-ng stress-ng --cpu 28
stress-ng: info: [1] defaulting to a 86400 second run per stressor
stress-ng: info: [1] dispatching hogs: 28 cpu



[root@docker01 ~]#  top
top - 06:34:58 up 3 days,  3:30,  2 users,  load average: 21.89, 8.78, 5.75
Tasks: 151 total,  29 running, 122 sleeping,   0 stopped,   0 zombie
%Cpu0  : 99.7 us,  0.3 sy,  0.0 ni,  0.0 id,  0.0 wa,  0.0 hi,  0.0 si,  0.0 st
%Cpu1  :  0.0 us,  0.3 sy,  0.0 ni, 99.7 id,  0.0 wa,  0.0 hi,  0.0 si,  0.0 st
%Cpu2  : 99.3 us,  0.3 sy,  0.0 ni,  0.0 id,  0.0 wa,  0.0 hi,  0.3 si,  0.0 st
%Cpu3  :  0.3 us,  0.0 sy,  0.0 ni, 99.7 id,  0.0 wa,  0.0 hi,  0.0 si,  0.0 st
KiB Mem :  4396580 total,  3383088 free,   355524 used,   657968 buff/cache
KiB Swap:  4587516 total,  4583416 free,     4100 used.  3773732 avail Mem 




[root@docker01 ~]# docker stats

CONTAINER ID        NAME                CPU %               MEM USAGE / LIMIT     MEM %               NET I/O             BLOCK I/O           PIDS
e8d915e0a11c        tank                200.63%             109.8MiB / 4.193GiB   2.56%               656B / 0B           0B / 0B             29

CONTAINER ID        NAME                CPU %               MEM USAGE / LIMIT     MEM %               NET I/O             BLOCK I/O           PIDS
e8d915e0a11c        tank                200.63%             109.8MiB / 4.193GiB   2.56%               656B / 0B           0B / 0B             29

CONTAINER ID        NAME                CPU %               MEM USAGE / LIMIT     MEM %               NET I/O             BLOCK I/O           PIDS
e8d915e0a11c        tank                200.05%             109.8MiB / 4.193GiB   2.56%               656B / 0B           0B / 0B             29


指定容器在CPU使用比例（默为用完成所有）


[root@docker01 ~]# docker run --name tank -it --rm  --cpu-shares 1024  lorel/docker-stress-ng stress-ng --cpu 28
stress-ng: info: [1] defaulting to a 86400 second run per stressor
stress-ng: info: [1] dispatching hogs: 28 cpu

[root@docker01 ~]# docker stats

ONTAINER ID        NAME                CPU %               MEM USAGE / LIMIT   MEM %               NET I/O             BLOCK I/O           PIDS
d4aa3a1c6a05        tank                559.41%             101MiB / 4.193GiB   2.35%               656B / 0B           0B / 0B             29
^X^C


添加个一容器



[root@docker01 ~]# docker run --name tank2 -it --rm  --cpu-shares 512  lorel/docker-stress-ng stress-ng --cpu 28
stress-ng: info: [1] defaulting to a 86400 second run per stressor
stress-ng: info: [1] dispatching hogs: 28 cpu



可以看到2个容器使用CPU比例 

[root@docker01 ~]# docker stats

CONTAINER ID        NAME                CPU %               MEM USAGE / LIMIT     MEM %               NET I/O             BLOCK I/O           PIDS
2296c23d9a95        tank2               182.96%             4.344MiB / 4.193GiB   0.10%               586B / 0B           0B / 0B             29
5018377a5e74        tank                370.09%             89.53MiB / 4.193GiB   2.09%               656B / 0B           0B / 0B             29
^C
[root@docker01 ~]# 
~~~


