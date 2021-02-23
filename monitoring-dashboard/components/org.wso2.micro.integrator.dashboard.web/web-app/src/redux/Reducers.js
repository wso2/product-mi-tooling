import { combineReducers } from 'redux';
import NodeListReducer from './NodeListReducer';
import GroupListReducer from './GroupListReducer';

export default combineReducers({
    nodeList : NodeListReducer,
    groupId : GroupListReducer
})
