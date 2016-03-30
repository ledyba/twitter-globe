.PHONY: all get run clean bind dbind

all:
	gofmt -w .
	go install github.com/ledyba/twitter-globe/...

get:
	go get -u "github.com/jteeuwen/go-bindata/..."
	go get -u "github.com/ChimeraCoder/anaconda"

bind:
	$(GOPATH)/bin/go-bindata -pkg=main -o=assets.go ./assets/...

dbind:
	$(GOPATH)/bin/go-bindata -debug=true -pkg=main -o=server/assets.go ./assets/...

run:
	$(GOPATH)/bin/twitter-globe

clean:m
	go clean github.com/ledyba/github-crawler/...
