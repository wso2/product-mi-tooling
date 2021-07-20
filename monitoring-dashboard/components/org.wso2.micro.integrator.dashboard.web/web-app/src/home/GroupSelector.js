import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import FormHelperText from '@material-ui/core/FormHelperText';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import { changeGroup } from '../redux/Actions';
import { useDispatch, useSelector } from 'react-redux';
import HTTPClient from '../utils/HTTPClient';

export default function GroupSelector() {

    const [groupList, setGroupList] = React.useState([]);
    const dispatch = useDispatch();

    React.useEffect(() => {
        let groups = [];
        HTTPClient.getGroups().then(response => {
            response.data.filter(groupName => {
                var group = {
                    label: groupName,
                    value: groupName
                }
                groups.push(group);
            })
            setGroupList(groups)
            if (groups.length > 0) {
                dispatch(changeGroup(groups[0].value))
            }
        })
    }, [])

    return (
        <SelectComponent groupList={groupList} />
    );
}

function SelectComponent(props) {
    const classes = useStyles();
    var options = props.groupList;

    const [selectedGroupId, setselectedGroupId] = React.useState('');

    const globalGroupId = useSelector(state => state.groupId);

    React.useEffect(()=>{
        if (globalGroupId === '' && options.length !== 0) {
            setselectedGroupId(options[0].value)
        }
    },[props.groupList]);

    const changeSelectedGroupId = (groupId) => {
        dispatch(changeGroup(groupId))
        setselectedGroupId(groupId)
    }

    const dispatch = useDispatch();
    return <FormControl style={{ width: 150 }}>
        <Select
            classes={{ root: classes.selectRoot }}
            value={selectedGroupId}
            labelId="group-id-select-label"
            id="group-id-select"
            onChange={(e) => changeSelectedGroupId(e.target.value)}
        >
            {options.map((option) => (
                <MenuItem value={option.value}>{option.label}</MenuItem>
            ))}

        </Select>
        <FormHelperText>Group ID</FormHelperText>

    </FormControl>;
}

const useStyles = makeStyles((theme) => ({
    selectRoot: {
        minHeight: '25px',
        lineHeight: '25px',
    },
}));
