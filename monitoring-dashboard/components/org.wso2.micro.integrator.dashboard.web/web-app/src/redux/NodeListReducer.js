import { FILTER } from './Actions'

const NodeListReducer = (state = [], action) => {
    switch(action.type) {
        case FILTER:
            return action.payload;
        default:
            return state
    }
}

export default NodeListReducer;
