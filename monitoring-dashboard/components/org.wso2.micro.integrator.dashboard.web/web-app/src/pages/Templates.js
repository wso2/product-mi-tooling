import React from 'react';
import EnhancedTable from '../commons/EnhancedTable';
import { useSelector } from 'react-redux';
import HTTPClient from '../utils/HTTPClient';

export default function Templates() {
    const [pageInfo] = React.useState({
        pageId: "templates",
        title: "Templates",
        headCells: [
            {id: 'name', label: 'Template Name'},
            {id: 'nodes', label: 'Nodes'},
            {id: 'type', label: 'Type'}],
        tableOrderBy: 'name'
    });

    const [templateList, setTemplateList] = React.useState([]);

    const globalGroupId = useSelector(state => state.groupId);
    const selectedNodeList = useSelector(state => state.nodeList);

    React.useEffect(() => {
        HTTPClient.getArtifacts("templates", globalGroupId, selectedNodeList).then(response => {
            setTemplateList(response.data)
        })
    },[globalGroupId, selectedNodeList])

    return <EnhancedTable pageInfo={pageInfo} dataSet={templateList}/>
}