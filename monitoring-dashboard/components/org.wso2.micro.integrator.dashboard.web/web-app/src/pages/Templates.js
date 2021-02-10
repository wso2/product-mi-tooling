import React from 'react';
import axios from 'axios';
import EnhancedTable from '../commons/EnhancedTable';
import { useSelector } from 'react-redux';

export default function Templates() {
    const [pageInfo] = React.useState({
        pageId: "templates",
        title: "Templates",
        headCells: [
            {id: 'name', label: 'Template Name'},
            {id: 'template_nodes', label: 'Nodes'},
            {id: 'type', label: 'Type'}],
        tableOrderBy: 'name'
    });

    const [templateList, setTemplateList] = React.useState([]);

    const globalGroupId = useSelector(state => state.groupId);
    const selectedNodeList = useSelector(state => state.nodeList);
    const basePath = useSelector(state => state.basePath);

    React.useEffect(() => {
        var nodeListQueryParams="";
        selectedNodeList.filter(node => {
            nodeListQueryParams = nodeListQueryParams.concat(node, '&nodes=')
        })
        const url = basePath.concat('/groups/').concat(globalGroupId).concat("/templates?nodes=").concat(nodeListQueryParams.slice(0,-7));
        axios.get(url).then(response => {
            response.data.map(data => 
                data.nodes.map(node => node.details = JSON.parse(node.details))
            )
            setTemplateList(response.data)
        })
    },[globalGroupId, selectedNodeList])

    return <EnhancedTable pageInfo={pageInfo} dataSet={templateList}/>
}