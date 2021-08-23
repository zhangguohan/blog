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
## 使用scapy定制数据包进行高级扫描

1 scapy定制ARP协议

```

>>> ARP().display()
###[ ARP ]###
  hwtype= 0x1
  ptype= IPv4
  hwlen= None
  plen= None
  op= who-has
  hwsrc= 08:00:27:73:ad:3f
  psrc= 108.88.3.166
  hwdst= 00:00:00:00:00:00
  pdst= 0.0.0.0

>>>

**定义向108.88.3.254 发送ARP请求数据包**

>>> sr1(ARP(pdst="108.88.3.254"))
Begin emission:
Finished sending 1 packets.
*
Received 1 packets, got 1 answers, remaining 0 packets
<ARP  hwtype=0x1 ptype=IPv4 hwlen=6 plen=4 op=is-at hwsrc=78:45:c4:f0:2b:2d psrc=108.88.3.254 hwdst=08:00:27:73:ad:3f pdst=108.88.3.166 |<Padding  load='\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00' |>>
>>> sr1(ARP(pdst="108.88.3.7"))
Begin emission:
Finished sending 1 packets.



psrc=108.88.3.254，说明已经收到网关的应答包
```
2、 scapy定制ping包


```

>>> sr1(IP(dst="108.88.3.254")/ICMP(),timeout=1)
Begin emission:
Finished sending 1 packets.
..*
Received 3 packets, got 1 answers, remaining 0 packets
<IP  version=4 ihl=5 tos=0x0 len=28 id=39610 flags= frag=0 ttl=64 proto=icmp chksum=0xffd2 src=108.88.3.254 dst=108.88.3.166 |<ICMP  type=echo-reply code=0 chksum=0xffff id=0x0 seq=0x0 |<Padding  load='\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00' |>>>



>>> sr1(IP(dst="108.88.3.24")/ICMP(),timeout=1)
Begin emission:
WARNING: Mac address to reach destination not found. Using broadcast.
Finished sending 1 packets.
..................................................................................................
Received 98 packets, got 0 answers, remaining 1 packets
>>>


```

3、scapy定制TCP协议请求

```
>>> sr1(IP(dst="108.88.3.254")/TCP(flags="S",dport=880),timeout=1)
Begin emission:
Finished sending 1 packets.
...*
Received 4 packets, got 1 answers, remaining 0 packets
<IP  version=4 ihl=5 tos=0x0 len=44 id=0 flags=DF frag=0 ttl=64 proto=tcp chksum                                                                                                             =0x5a78 src=108.88.3.254 dst=108.88.3.166 |<TCP  sport=880 dport=ftp_data seq=33                                                                                                             87894663 ack=1 dataofs=6 reserved=0 flags=SA window=5840 chksum=0xa7f6 urgptr=0                                                                                                              options=[('MSS', 1460)] |<Padding  load='\x00\x00' |>>>
>>>

我们能收到一个flags=SA 的数据包，SA标地即SYN+ACK，我们收到服务器tcp三次握手中的第二个包，能收到回应，表示端口开放。

注：这种基于tcp的半链接扫描，更隐密，更不容易被发现。


```




4.5 僵尸扫描

