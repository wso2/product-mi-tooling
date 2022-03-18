// Action types
export const FILTER = "FILTER"
export const GROUP_CHANGE = "GROUP_CHANGE"
export const DATA_CHANGE = "DATA_CHANGE"
export const SET_SUPER_USER = "SET_SUPER_USER"

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
