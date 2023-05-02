# mount CIFS share on Linux: mount error(13): Permission denied

Unable to mount CIFS share on Linux: mount error(13): Permission denied (cifs_mount failed w/return code = -13)

There are a couple of ways how to mount a CIFS/Samba share on a Linux client. However some tutorials are outdated and meanwhile completely wrong. I just ran into a (stupid) case of a wrong mount.cifs syntax:

```
root@focal:~# mount -t cifs //server/Share /mnt -o rw,user=domain\myuser,password=secret
mount error(13): Permission denied
Refer to the mount.cifs(8) manual page (e.g. man mount.cifs) and kernel log messages (dmesg) 
```

Unfortunately, the additional output in dmesg is not helpful to figure out the problem:

```
root@focal:~# dmesg
[...]
[16444886.307684] CIFS: Attempting to mount //server/Share
[16444886.307717] No dialect specified on mount. Default has changed to a more secure dialect, SMB2.1 or later (e.g. SMB3), from CIFS (SMB1). To use the less secure SMB1 dialect to access old servers which do not support SMB3 (or SMB2.1) specify vers=1.0 on mount.
[16444886.539770] Status code returned 0xc000006d STATUS_LOGON_FAILURE
[16444886.539795] CIFS VFS: \\server Send error in SessSetup = -13
[16444886.539901] CIFS VFS: cifs_mount failed w/return code = -13
```

After additional try and errors (and looking up a recent share mount from the history), the problem turned out to be the user=domainmyuser syntax. This way of combining the domain/workgroup and the username is not working (anymore).

Note: Both user= and username= are accepted in the options.

Instead use:

```
root@focal:~# mount -t cifs "//server/Share" /mnt -o "user=myuser,password=secret,workgroup=DOMAIN"
root@focal:~# ll /mnt/
total 0
drwxr-xr-x 2 root root 0 Sep  1  2020 _Archiv
drwxr-xr-x 2 root root 0 Aug  9 12:10 Client
[..]
```

This way it worked.

Of course the password should not be used on the command line, so for the final (and automatic) mount of the share use the following entry in /etc/fstab:

```
root@focal:~# cat /etc/fstab
[...]
# Mount CIFS share from server
//server/Share /mnt cifs rw,relatime,vers=3.1.1,credentials=/etc/samba/servershare.conf,uid=0 0 0
```

Where /etc/samba/servershare.conf contains the credentials:

```
root@focal:~# cat /etc/samba/servershare.conf
user=myuser
password=secret
domain=DOMAIN
```