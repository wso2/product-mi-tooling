import React from 'react';
import axios from 'axios';
import Box from '@material-ui/core/Box';
import Select from '@material-ui/core/Select';
import { changeGroup } from '../redux/Actions';
import { useDispatch, useSelector } from 'react-redux';

export default function GroupSelector() {

    const [groupList, setGroupList] = React.useState([]);
    const dispatch = useDispatch();
    const basePath = useSelector(state => state.basePath);

    React.useEffect(()=>{
        const url = basePath.concat('/groups/');
        let groups = [];
        
        axios.get(url).then(response => {
            response.data.filter(groupName => {
                var group = {
                    label : groupName,
                    value : groupName
                }
                groups.push(group);
            })
            setGroupList(groups)
            if (groups.length > 0) {
                dispatch(changeGroup(groups[0].value))
            }
        })
    },[])

    return (
        <Box component="span" m={1}>
            <Box style={{padding: '13px'}}>Group ID  <SelectComponent groupList={groupList}/></Box>
        </Box>
    );
}

function SelectComponent(props) {

    var options = props.groupList;
    const dispatch = useDispatch();
    let select;

    if(options.length > 0) {
        select = <Select native
                    label="Group Id"
                    onChange={(e) => dispatch(changeGroup(e.target.value))}
                    style={{ textDecoration: 'none' , color: '#fff'}} >
                    {options.map((option) => (
                        <option value={option.value}>{option.label}</option>
                    ))}
                </Select>
    } else {
        select = <Select native
                    style={{ textDecoration: 'none' , color: '#fff'}} />
    }
    return select;
}
