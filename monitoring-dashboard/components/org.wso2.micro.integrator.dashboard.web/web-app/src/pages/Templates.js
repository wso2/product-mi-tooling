import React from 'react';
import EnhancedTable from '../commons/EnhancedTable';

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

    return <EnhancedTable pageInfo={pageInfo}/>
}