.PHONY: Message.java GossipServer_stub.class

all : Message.java GossipServer_Stub.class

Message.java : message.proto
	protoc --java_out=. message.proto
	
GossipServer_Stub.class : GossipServer.class
	rmic GossipServer
	
%.class : %.java
	javac $^
	
clean: 
	rm -f Message.java GossipServer_Stub.class
	
