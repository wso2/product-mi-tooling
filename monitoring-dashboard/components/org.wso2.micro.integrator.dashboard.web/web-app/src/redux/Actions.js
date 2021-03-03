// Action types
export const FILTER = "FILTER"
export const GROUP_CHANGE = "GROUP_CHANGE"
export const DATA_CHANGE = "DATA_CHANGE"

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
