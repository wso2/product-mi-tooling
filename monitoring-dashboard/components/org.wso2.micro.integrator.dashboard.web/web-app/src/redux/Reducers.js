import { combineReducers } from 'redux';
import NodeStateReducer from './NodeStateReducer';
import GroupListReducer from './GroupListReducer';
import DataReducer from './DataReducer';
import SuperUserReducer from './SuperAdminReducer';
import IsRefreshedReducer from './IsRefreshedReducer';

export default combineReducers({
    nodeState: NodeStateReducer,
    groupId : GroupListReducer,
    data : DataReducer,
    superAdmin : SuperUserReducer,
    isRefreshed : IsRefreshedReducer
})
