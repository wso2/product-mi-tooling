/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.com) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 *
 */

import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Breadcrumbs from '@material-ui/core/Breadcrumbs';
import Typography from '@material-ui/core/Typography';
import { NavigateNext } from "@material-ui/icons";
import Link from '@material-ui/core/Link';

export default function RegistryBreadCrumbs(props) {
    const { registryPath, handleBreadCrumbClick } = props;
    const classes = useStyles();
    const pathArray = (registryPath.split("/")).map((el,index) => [el,index])

  return (
    <div role="presentation">
      <Breadcrumbs className={classes.breadcrumbs} maxItems={5} itemsBeforeCollapse={2} itemsAfterCollapse={2} aria-label="breadcrumb" separator={<NavigateNext fontSize="small" />}>
        {pathArray.map(pathComponent => <BreadCrumbItems pathComponent={pathComponent} pathArray={pathArray} handleBreadCrumbClick={handleBreadCrumbClick} />)}
      </Breadcrumbs>
    </div>
  );
}

function BreadCrumbItems(props) {
    const { pathComponent, pathArray, handleBreadCrumbClick } = props;
    const index = pathArray.indexOf(pathComponent);
    if (index !== (pathArray.length-1)){
        return <Link underline="hover" color="inherit" component={"button"} onClick={() => handleBreadCrumbClick(index, pathArray)}>{pathComponent[0]}</Link>
    } else {
        return <Typography color="text.primary">{pathComponent[0]}</Typography>
    }
}

const useStyles = makeStyles((theme) => ({
  breadcrumbs : {
      width: '100%',
      color: '#000000',
      padding: '2px',
      fontSize: 5,
      borderBottom: 'none',
      marginBottom: theme.spacing(2)
  },
  drawerPaper: {
      backgroundColor: '#fff',
  },
}));
