import zmq
import subprocess
import time

context = zmq.Context()
s = context.socket(zmq.REP)
p = "tcp://127.0.0.1:8765"

s.bind(p)

while True:
    msg = s.recv_string()
    if msg == "KILL":
        subprocess.call(["sh", "./kill-tomcat.sh"])
        print(time.asctime()+": Script executed!")
        s.send_string("OK")
    elif msg == "STOP":
        break
