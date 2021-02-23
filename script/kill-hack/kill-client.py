import zmq

context = zmq.Context()
p = "tcp://127.0.0.1:8765"
s = context.socket(zmq.REQ)
s.connect(p)
s.send_string("KILL")
