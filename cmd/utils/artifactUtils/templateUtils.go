package artifactUtils

type TemplateList struct {
	SequenceTemplates []Template `json:"sequenceTemplateList"`
	EndpointTemplates []Template `json:"endpointTemplateList"`
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
