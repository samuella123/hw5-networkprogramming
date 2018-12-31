JFLAGS = -d build/
JC = javac

.SUFFIXES: .java .class

.java.class:
				$(JC) $(JFLAGS) $*.java

CLASSES = \
				server/net/GameServer.java \
				server/net/Player.java \
				server/model/Hangman.java \
				server/model/HangmanStatus.java \
				server/controller/Controller.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
				$(RM) *.class
