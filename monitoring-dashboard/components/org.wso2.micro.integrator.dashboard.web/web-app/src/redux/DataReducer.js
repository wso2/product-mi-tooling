import { DATA_CHANGE } from './Actions'

const DataReducer = (state = [], action) => {
    switch(action.type) {
        case DATA_CHANGE:
            return action.payload;
        default:
            return state
    }
}

export default DataReducer;