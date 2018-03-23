#!/usr/bin/env bash
#@Copyright (C) Locket, Inc.

SERVER_NAME=sha256

FINDNAME=$0
while [ -h $FINDNAME ] ; do FINDNAME=`ls -ld $FINDNAME | awk '{print $NF}'` ; done
APP_HOME=`echo $FINDNAME | sed -e 's@/[^/]*$@@'`
unset FINDNAME

if [ "$APP_HOME" = '.' ]; then
   APP_HOME=$(echo `pwd` | sed 's/\/bin//')
else
   APP_HOME=$(echo $APP_HOME | sed 's/\/bin//')
fi

echo "APP_HOME: $APP_HOME"

# Create pid file
if [ ! -d "$APP_HOME"/pids ]; then
    mkdir "$APP_HOME"/pids
fi

HEAP_MEMORY=512m
PERM_MEMORY=32m
PIDFILE="$APP_HOME"/pids/$SERVER_NAME.pid
#echo "PIDFILE = $PIDFILE"

start() {
    if test -e "$PIDFILE"
    then
        if test -d "/proc"/$(cat "$PIDFILE")
        then
            echo "$SERVER_NAME service still running, please stop it first"
            exit 1
        fi
   	else
   		touch "$PIDFILE"
    fi

    if test -e "$APP_HOME"/$SERVER_NAME.jar
    then
        echo  "starting $SERVER_NAME ... "
        JAVA_OPTS="-server -XX:+HeapDumpOnOutOfMemoryError "

        shift
        ARGS=($*)
        for ((i=0; i<${#ARGS[@]}; i++)); do
            case "${ARGS[$i]}" in
            -D*)    JAVA_OPTS="${JAVA_OPTS} ${ARGS[$i]}" ;;
            -Heap*) HEAP_MEMORY="${ARGS[$i+1]}" ;;
            -Perm*) PERM_MEMORY="${ARGS[$i+1]}" ;;
            esac
        done
        JAVA_OPTS="${JAVA_OPTS} -Xms${HEAP_MEMORY} -Xmx${HEAP_MEMORY} -XX:PermSize=${PERM_MEMORY} -XX:MaxPermSize=${PERM_MEMORY} -Dapp.name=$SERVER_NAME"
        echo "start jvm args ${JAVA_OPTS}"
        nohup java $JAVA_OPTS -Dfile.encoding=UTF-8 -Duser.dir="$APP_HOME" -jar "."/$SERVER_NAME.jar >/dev/null 2>&1 &
        echo $! > "$PIDFILE"
        echo "started $SERVER_NAME OK"
    else
        echo "could not find $SERVER_NAME"
        exit 1
    fi
}

stop() {

    if test -e "$PIDFILE"
    then
        echo "stopping server"
        if kill -TERM `cat "$PIDFILE"`
        then
            sleep 2
            loop_check_process_status 3
        elif
            kill -KILL `cat "$PIDFILE"`
        then
            sleep 2
            loop_check_process_status 3
        elif
            kill -9 `cat "$PIDFILE"`
        then
            echo "server stop OK"
            /bin/rm "$PIDFILE"
        fi
    else
        echo "no server running"
        exit 1
    fi
}

restart() {
    echo "restarting server"
    stop
    start "$1"
}

info() {
	echo "System Information:"
	echo "****************************"
    echo `head -n 1 /etc/issue`
    echo `uname -a`
    echo
    echo "JAVA_HOME=$JAVA_HOME"
    echo `$JAVA_HOME/bin/java -version`
    echo
    echo "APP_HOME=$APP_HOME"
}


loop_check_process_status() {
    COUNT=$1
    KILLED=false
    for (( i = 0; i < $COUNT; i++));do
		if test -d "$APP_HOME"/$(cat "$PIDFILE")
        then
            sleep 10
        else
            KILLED=true
            break
        fi
    done
    if $KILLED; then
        echo "server stop OK"
        /bin/rm "$PIDFILE"
    else
        echo "server stop failed"
    fi
}
case $1 in
start)
    start "$2"
    ;;

stop)
    stop
    ;;

restart)
    restart "$2"
    ;;
info)
	info
	;;
*)
    echo "Usage: $0 {start|stop|restart}" >&2
    ;;

esac

exit 0