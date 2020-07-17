package artifactUtils

import "strconv"

type EndpointList struct {
	Count     int32             `json:"count"`
	Endpoints []EndpointSummary `json:"list"`
}

type EndpointSummary struct {
	Name   string `json:"name"`
	Type   string `json:"type"`
	Active bool `json:"isActive"`
}

type Endpoint struct {
	Name   string `json:"name"`
	Type   string `json:"type"`
	Active bool `json:"isActive"`
	Method string `json:"method"`
	Url    string `json:"url"`
	Stats  string `json:"stats"`
	Address    string `json:"address"`
	URITemplate string `json:"uriTemplate"`
	ServiceName string `json:"serviceName"`
	PortName string `json:"portName"`
	WsdlURI string `json:"wsdlUri"`
}

func (endpoints *EndpointList) GetDataIterator() <-chan []string {
	ch := make(chan []string)

	go func() {
		for _, endpoint := range endpoints.Endpoints {
			ch <- []string{endpoint.Name, endpoint.Type, strconv.FormatBool(endpoint.Active)}
		}
		close(ch)
	}()

	return ch
}

func (endpoints *EndpointList) GetCount() int32 {
	return endpoints.Count
}
