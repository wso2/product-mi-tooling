import { SELECT_NODE, DESELECT_NODE, SELECT_GROUP } from './Actions'

const NodeStateReducer = (state = {
    groupId: "", // get rid of this and use selectedGroupState instead
    groupStates: {
        "": {
            nodes: [],
            selectedType: "",
            selected: [],
            beenSelected: []
        }
    },
    nodes: []
}, action) => {
    const group = state.groupStates[state.groupId];
    switch (action.type) {
        case DESELECT_NODE:
            const { nodeId } = action.payload;
            if (group.selected.length === 1) {
                return state;
            }
            return {
                ...state,
                groupStates: {
                    ...state.groupStates,
                    [state.groupId]: {
                        ...group,
                        selected: group.selected.filter(selectedNodeId => selectedNodeId !== nodeId),
                    }
                }
            };
        case SELECT_NODE:
            const selectedNodeId = action.payload.nodeId;
            const selectedNodeType = group.nodes.find(node => node.nodeId === selectedNodeId).type;
            if (selectedNodeType !== group.selectedType) {
                return {
                    ...state,
                    groupStates: {
                        ...state.groupStates,
                        [state.groupId]: {
                            ...group,
                            selectedType: selectedNodeType,
                            selected: [selectedNodeId],
                            beenSelected: group.selected
                        }
                    }
                };
            }
            return {
                ...state,
                groupStates: {
                    ...state.groupStates,
                    [state.groupId]: {
                        ...group,
                        selected: [...group.selected, selectedNodeId],
                    }
                }
            };
        case SELECT_GROUP:
            const { groupId, nodes } = action.payload;
            const groupToSwitch = state.groupStates[groupId];
            if (groupToSwitch !== undefined && JSON.stringify(groupToSwitch.nodes) === JSON.stringify(action.payload.nodes)) {
                if (state.groupId === groupId) {
                    return state;
                } else {
                    return { ...state, groupId: action.payload.groupId };
                }
            }
            if (nodes.length === 0) {
                return withGroup(state, action.payload.groupId, nodes, "", []);
            }
            const selectedType = nodes[0].type;
            const selected = [];
            for (let node of nodes) {
                if (node.type === selectedType) {
                    selected.push(node.nodeId);
                }
            }
            return withGroup(state, action.payload.groupId, nodes, selectedType, selected);
        default:
            return state
    }
}

function withGroup(state, groupId, nodes, selectedType, selected) {
    return {
        ...state,
        groupId,
        groupStates: {
            ...state.groupStates,
            [groupId]: {
                nodes,
                selectedType,
                selected,
                beenSelected: []
            }
        }
    };
}

export default NodeStateReducer;
