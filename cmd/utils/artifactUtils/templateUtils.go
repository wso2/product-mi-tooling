package artifactUtils

import "encoding/json"

type TemplateList struct {
	SequenceTemplates []Template `json:"sequenceTemplateList"`
	EndpointTemplates []Template `json:"endpointTemplateList"`
}

func (l *TemplateList) UnmarshalJSON(data []byte) error {
	type legacyTemplateList TemplateList
	var response struct {
		legacyTemplateList
		List []struct {
			Template
			Type string `json:"type"`
		} `json:"list"`
	}
	if err := json.Unmarshal(data, &response); err != nil {
		return err
	}
	*l = TemplateList(response.legacyTemplateList)
	if response.List != nil {
		*l = TemplateList{}
		for _, item := range response.List {
			switch item.Type {
			case "sequence":
				l.SequenceTemplates = append(l.SequenceTemplates, item.Template)
			case "endpoint":
				l.EndpointTemplates = append(l.EndpointTemplates, item.Template)
			}
		}
	}
	return nil
}

type TemplateListByType struct {
	Count     int32      `json:"count"`
	Templates []Template `json:"list"`
}

type TemplateSequenceListByName struct {
	Parameters []TemplateSequenceDetail `json:"Parameters"`
	Name       string                   `json:"name"`
}

type TemplateEndpointListByName struct {
	Parameters []string `json:"Parameters"`
	Name       string   `json:"name"`
}

type Template struct {
	Name string `json:"name"`
}

type TemplateSequenceDetail struct {
	Name         string `json:"name"`
	IsMandatory  bool   `json:"mandatory"`
	DefaultValue string `json:"defaultValue"`
}
