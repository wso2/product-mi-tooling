import React from 'react';
import axios from 'axios';
import Box from '@material-ui/core/Box';
import Select from '@material-ui/core/Select';

export default class GroupSelector extends React.Component {
    componentDidMount() {
        const url = "http://0.0.0.0:9743/api/rest/groups/";
        var groups = [];
        axios.get(url).then(response => {
            response.data.filter(groupName => {
                var group = {
                    label : groupName,
                    value : groupName
                }
                groups.push(group);
            })
            const groupList = groups
            this.setState({groupList})
        })
    }

    constructor(props){
        super(props)
        this.state = { groupList : [] };
    }

    render() {
        const options = this.state.groupList;
        let select;
        if(options.length > 0) {
            select = <Select native
                        value={options[0].value}
                        label="Group Id"
                        style={{ textDecoration: 'none' , color: '#fff'}} >
                        {options.map((option) => (
                            <option value={option.value}>{option.label}</option>
                        ))}
                    </Select>
        } else {
            select = <Select native
                        style={{ textDecoration: 'none' , color: '#fff'}} />
        }

        return (
            <Box p={1}>
                <Box style={{padding: '13px'}}>Group ID</Box>
                <Box>
                    {select}
                </Box>
            </Box>
        );
      }
}
