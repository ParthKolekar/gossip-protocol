.PHONY : clean all

all : com/ds/gossip/GossipRequestProto.class GossipServer_Stub.class

com/ds/gossip/GossipRequestProto.java : gossip.proto
	protoc --java_out=. gossip.proto
	
GossipServer_Stub.class : GossipServer.class
	rmic GossipServer

%.class : %.java
	javac $^ -classpath .:./protobuf.jar
	
clean: 
	rm -rf com/ *.class
	
