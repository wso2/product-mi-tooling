package artifactUtils

import (
	"encoding/json"
	"reflect"
	"testing"
)

func TestTemplateListUnmarshalJSON(t *testing.T) {
	want := TemplateList{
		SequenceTemplates: []Template{{Name: "AuditLogTemplate"}},
		EndpointTemplates: []Template{{Name: "HttpBackendEndpointTemplate"}},
	}
	for name, body := range map[string]string{
		"flat list": `{"count":2,"list":[{"name":"HttpBackendEndpointTemplate","type":"endpoint","Parameters":["name","uri"]},{"name":"AuditLogTemplate","type":"sequence","Parameters":[{"name":"operation","mandatory":true}]}]}`,
		"legacy":    `{"sequenceTemplateList":[{"name":"AuditLogTemplate"}],"endpointTemplateList":[{"name":"HttpBackendEndpointTemplate"}]}`,
	} {
		t.Run(name, func(t *testing.T) {
			var got TemplateList
			if err := json.Unmarshal([]byte(body), &got); err != nil {
				t.Fatal(err)
			}
			if !reflect.DeepEqual(got, want) {
				t.Fatalf("got %+v, want %+v", got, want)
			}
		})
	}
}
