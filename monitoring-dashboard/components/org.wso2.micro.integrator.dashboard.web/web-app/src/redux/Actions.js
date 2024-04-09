// Action types
export const FILTER = "FILTER"
export const GROUP_CHANGE = "GROUP_CHANGE"
export const DATA_CHANGE = "DATA_CHANGE"
export const SET_SUPER_USER = "SET_SUPER_USER"
export const IS_REFRESHED ="IS_REFRESHED"
export const SELECT_NODE ="SELECT_NODE"
export const SELECT_GROUP = "SELECT_GROUP"
export const DESELECT_NODE = "DESELECT_NODE"

// Action creators
export const filterNodes = (list) => {
    return {
        type : FILTER,
        payload: list
    }
}

export const changeGroup = (groupName) => {
    return {
        type : GROUP_CHANGE,
        payload: groupName
    }
}

export const changeData = (data) => {
    return {
        type : DATA_CHANGE,
        payload: data
    }
}

export const setSuperAdmin = (userName) => {
    return {
        type : SET_SUPER_USER,
        payload: userName
    }
}

export const setIsRefreshed = (isRefreshed) => {
    return {
        type : IS_REFRESHED,
        payload: isRefreshed
    }
}


export const selectNode = (nodeId) => {
    return {
        type : SELECT_NODE,
        payload: { nodeId }
    }
}

export const deselectNode = (nodeId) => {
    return {
        type : DESELECT_NODE,
        payload: { nodeId }
    }
}

export const selectGroup = (groupId, nodes) => {
    return {
        type : SELECT_GROUP,
        payload: { groupId, nodes }
    }
};

export const currentGroupSelector = (state) => {
    const { nodeState } = state;
    return nodeState.groupStates[nodeState.groupId];
};
