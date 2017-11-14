#Postgresql 设置慢查询


### 修改postgresql.conf文件
````
log_destination = 'stderr'		# Valid values are combinations of //错误日志
					# stderr, csvlog, syslog, and eventlog,
					# depending on platform.  csvlog
					# requires logging_collector to be on.
logging_collector = on		# Enable capturing of stderr and csvlog
					# into log files. Required to be on for
					# csvlogs.
					# (change requires restart)
log_directory = '/usr/local/pg9.5.2/logs/'		# directory where log files are written,
					# can be absolute or relative to PGDATA
log_filename = 'postgresql-%Y-%m-%d_%H%M%S.log'	# log file name pattern,
					# can include strftime() escapes
log_file_mode = 0777			# creation mode for log files,
					# begin with 0 to use octal notation
log_truncate_on_rotation = on		# If on, an existing log file with the
					# same name as the new log file will be
					# truncated rather than appended to.
					# But such truncation only occurs on
					# time-driven rotation, not on restart					# or size-driven rotation.  Default is
					# off, meaning append to existing files
					# in all cases.
log_rotation_age = 1d			# Automatic rotation of logfiles will  //每天一个文件
					# happen after that time.  0 disables.
log_rotation_size = 10MB		# Automatic rotation of logfiles will //每10M一个文件
					# happen after that much log output.

					#   panic (effectively off)
log_min_duration_statement = 60	# -1 is disabled, 0 logs all statements //查询时间为毫秒
					# and their durations, > 0 logs only
					# statements running at least this number
					# of milliseconds
log_line_prefix = 'time=%t:db=%d;user=%u;type=%i;'			# special values: //日志格式
					#   %a = application name
					#   %u = user name
					#   %d = database name
					#   %r = remote host and port
					#   %h = remote host
					#   %p = process ID
					#   %t = timestamp without milliseconds
					#   %m = timestamp with milliseconds
					#   %i = command tag
					#   %e = SQL state
					#   %c = session ID
					#   %l = session line number
					#   %s = session start timestamp
					#   %v = virtual transaction ID
					#   %x = transaction ID (0 if none)
					#   %q = stop here in non-session
					#        processes
					#   %% = '%'
					# e.g. '<%u%%%d> '
					# than the specified size in kilobytes;
					# -1 disables, 0 logs all temp files

````