import { combineReducers } from 'redux';
import NodeListReducer from './NodeListReducer';
import GroupListReducer from './GroupListReducer';
import DataReducer from './DataReducer';

export default combineReducers({
    nodeList : NodeListReducer,
    groupId : GroupListReducer,
    data : DataReducer
})
