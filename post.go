package main

import "encoding/json"

// Post represents a message from server to client
type Post struct {
	Lng    float64 `json:"lng"`
	Lat    float64 `json:"lat"`
	Msg    string  `json:"msg"`
	Usr    string  `json:"usr"`
	Image  string  `json:"image"`
	Client string  `json:"client"`
}

func (p Post) toJSON() ([]byte, error) {
	bytes, err := json.Marshal(&p)
	if err != nil {
		return nil, err
	}
	return bytes, nil
}
