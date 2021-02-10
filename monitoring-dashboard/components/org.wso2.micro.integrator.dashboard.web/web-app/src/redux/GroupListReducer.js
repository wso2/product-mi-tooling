import { GROUP_CHANGE } from './Actions'

const GroupListReducer = (state = '', action) => {
    switch(action.type) {
        case GROUP_CHANGE:
            return action.payload;
        default:
            return state
    }
}

export default GroupListReducer;
