VAR="$(ps -ef | grep org.gradle.launcher.daemon.bootstrap.GradleDaemon | grep -v grep | awk '{print $2}')"
echo "PID: $VAR"
kill -9 $VAR