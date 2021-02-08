import { combineReducers } from 'redux';
import NodeListReducer from './NodeListReducer';
import GroupListReducer from './GroupListReducer';
import BasePathReducer from './BasePathReducer'

export default combineReducers({
    nodeList : NodeListReducer,
    groupId : GroupListReducer,
    basePath : BasePathReducer
})
