
.PHONY: all jar run reload test install update template distclean

all:
	./sbt compile

jar:
	./sbt package

run:
	read | echo -e "container:start\n~; copy-resources; aux-compile" | ./sbt

reload:
	./sbt reload update-classifiers update-sbt-classifiers eclipse

update:
	./sbt reload update-classifiers update-sbt-classifiers

test:
	./sbt test

install:
	./sbt publishLocal publishM2

distclean:
	rm .lib -rf
	rm target -rf
