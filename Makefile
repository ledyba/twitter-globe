.PHONY: all get run clean bind dbind deploy

all:
	gofmt -w .
	go install github.com/ledyba/twitter-globe/...

get:
	go get -u "github.com/jteeuwen/go-bindata/..."
	go get -u "github.com/elazarl/go-bindata-assetfs/..."
	go get -u "github.com/ChimeraCoder/anaconda"
	go get -u "golang.org/x/net/websocket"
	go get -u "github.com/tjgq/broadcast"

bind:
	PATH=$(GOPATH)/bin:$(PATH) $(GOPATH)/bin/go-bindata-assetfs -pkg=main ./assets/...

dbind:
	PATH=$(GOPATH)/bin:$(PATH) $(GOPATH)/bin/go-bindata-assetfs -debug=true -pkg=main ./assets/...

run: all
	$(GOPATH)/bin/twitter-globe

clean:
	go clean github.com/ledyba/twitter-globe/...

deploy: bind
	GOOS=linux GOARCH=amd64 go build -o twitter-globe github.com/ledyba/twitter-globe/...
	ssh ledyba.org mkdir -p /opt/run/twitter-globe
	scp twitter-globe twitter-globe.conf ledyba:/opt/run/twitter-globe
