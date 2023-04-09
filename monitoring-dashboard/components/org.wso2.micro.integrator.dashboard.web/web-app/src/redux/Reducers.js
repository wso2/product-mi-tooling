import { combineReducers } from 'redux';
import NodeListReducer from './NodeListReducer';
import GroupListReducer from './GroupListReducer';
import DataReducer from './DataReducer';
import SuperUserReducer from './SuperAdminReducer';
import IsRefreshedReducer from './IsRefreshedReducer';

export default combineReducers({
    nodeList : NodeListReducer,
    groupId : GroupListReducer,
    data : DataReducer,
    superAdmin : SuperUserReducer,
    isRefreshed : IsRefreshedReducer
})
