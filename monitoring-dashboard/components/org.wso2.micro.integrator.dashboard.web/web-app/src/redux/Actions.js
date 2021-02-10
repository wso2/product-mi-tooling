// Action types
export const FILTER = "FILTER"
export const GROUP_CHANGE = "GROUP_CHANGE"
export const SET_BASE_PATH = "SET_BASE_PATH"

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

export const setBasePath = (basePath) => {
    return {
        type : SET_BASE_PATH,
        payload: basePath
    }
}
