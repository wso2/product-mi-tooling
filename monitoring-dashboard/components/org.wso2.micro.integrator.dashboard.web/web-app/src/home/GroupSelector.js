import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import FormHelperText from '@material-ui/core/FormHelperText';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import { changeGroup, selectGroup } from '../redux/Actions';
import { useDispatch, useSelector } from 'react-redux';
import HTTPClient from '../utils/HTTPClient';
import { useLocation } from "react-router-dom";

const ICP_NAME = window.icp.name;

export default function GroupSelector() {

    const [groupList, setGroupList] = React.useState([]);
    const dispatch = useDispatch();
    const location = useLocation();

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
                loadNodesForGroup(groups[0].value, dispatch)
                dispatch(changeGroup(groups[0].value))
            }
        })
    }, [])

    React.useEffect(() => {
      setGroupList((prevGroups) => {
        const filteredGroups = prevGroups.filter(
          (group) => group.label !== ICP_NAME
        );
        return location.pathname.startsWith("/users") ||
          location.pathname.startsWith("/roles") ||
          location.pathname.startsWith("/update-password")
          ? [...filteredGroups, { label: ICP_NAME, value: ICP_NAME }]
          : filteredGroups;
      });
    }, [location]);

    return (
        <SelectComponent groupList={groupList} />
    );
}

function loadNodesForGroup(group, dispatch) {
    HTTPClient.getAllNodes(group).then(response => {
        response.data = response.data.map(node => ({...node, details: JSON.parse(node.details)}));
        dispatch(selectGroup(group, response.data));
    })
}

function SelectComponent(props) {
    const location = useLocation();
    const classes = useStyles();
    var options = props.groupList;

    const [selectedGroupId, setselectedGroupId] = React.useState('');

    const globalGroupId = useSelector(state => state.groupId);

    React.useEffect(() => {
      if (
        globalGroupId === ICP_NAME &&
        options.length > 0 &&
        !(
          location.pathname.startsWith("/users") ||
          location.pathname.startsWith("/roles") ||
          location.pathname.startsWith("/update-password")
        )
      ) {
        changeSelectedGroupId(options[0].value);
      } else if (globalGroupId === "" && options.length !== 0) {
        changeSelectedGroupId(options[0].value);
      }
    }, [options]);

    const changeSelectedGroupId = (groupId) => {
      if (groupId === ICP_NAME) {
        dispatch(selectGroup(groupId, []));
      } else {
        loadNodesForGroup(groupId, dispatch);
      }
      dispatch(changeGroup(groupId));
      setselectedGroupId(groupId);
    };

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
