package artifactUtils

type CompositeAppList struct {
	ActiveCount         int32                 `json:"activeCount"`
	ActiveCompositeApps []CompositeAppSummary `json:"activeList"`
	FaultyCount         int32                 `json:"faultyCount"`
	FaultyCompositeApps []CompositeAppSummary `json:"faultyList"`
	TotalCount          int32                 `json:"totalCount"`
}

type CompositeAppSummary struct {
	Name    string `json:"name"`
	Version string `json:"version"`
}

type CompositeApp struct {
	Name      string     `json:"name"`
	Version   string     `json:"version"`
	Artifacts []Artifact `json:"artifacts"`
}

type Artifact struct {
	Name string `json:"name"`
	Type string `json:"type"`
}

func (compositeApps *CompositeAppList) GetDataIterator() <-chan []string {
	ch := make(chan []string)

	go func() {
		ch <- []string{"\n----------------------\nActive Composite Apps:\n----------------------"}
		for _, compositeApp := range compositeApps.ActiveCompositeApps {
			ch <- []string{compositeApp.Name, compositeApp.Version}
		}
		ch <- []string{"\n----------------------\nFaulty Composite Apps:\n----------------------"}
		for _, compositeApp := range compositeApps.FaultyCompositeApps {
			ch <- []string{compositeApp.Name, compositeApp.Version}
		}
		close(ch)
	}()

	return ch
}

func (compositeApps *CompositeAppList) GetCount() int32 {
	return compositeApps.TotalCount
}
