# 端口扫描

## 使用nmap进行半连接扫描

$ sudo nmap -sS 211.155.25.184 -p 80,8080,22,25,443


```

└─$ sudo nmap -sS 211.155.25.184 -p 80,8080,22,25,443
Starting Nmap 7.91 ( https://nmap.org ) at 2021-02-19 22:15 EST
Nmap scan report for 211.155.25.184
Host is up (0.0051s latency).

PORT     STATE SERVICE
22/tcp   open  ssh
25/tcp   open  smtp
80/tcp   open  http
443/tcp  open  https
8080/tcp open  http-proxy

Nmap done: 1 IP address (1 host up) scanned in 2.26 seconds


```


## 使用NC进行端口扫描

 


 $ sudo nc -nv -w 1 -z 211.155.25.184 1-1000

```
(UNKNOWN) [211.155.25.184] 995 (pop3s) open
(UNKNOWN) [211.155.25.184] 993 (imaps) open
(UNKNOWN) [211.155.25.184] 593 (?) : Connection timed out
(UNKNOWN) [211.155.25.184] 465 (submissions) open
(UNKNOWN) [211.155.25.184] 445 (microsoft-ds) : Connection timed out
(UNKNOWN) [211.155.25.184] 443 (https) open
(UNKNOWN) [211.155.25.184] 143 (imap2) open
(UNKNOWN) [211.155.25.184] 139 (netbios-ssn) : Connection timed out
(UNKNOWN) [211.155.25.184] 137 (?) : Connection timed out
(UNKNOWN) [211.155.25.184] 135 (epmap) : Connection timed out
(UNKNOWN) [211.155.25.184] 110 (pop3) open
(UNKNOWN) [211.155.25.184] 82 (?) open
(UNKNOWN) [211.155.25.184] 80 (http) open
(UNKNOWN) [211.155.25.184] 25 (smtp) open
(UNKNOWN) [211.155.25.184] 22 (ssh) open
(UNKNOWN) [211.155.25.184] 21 (ftp) open

```

