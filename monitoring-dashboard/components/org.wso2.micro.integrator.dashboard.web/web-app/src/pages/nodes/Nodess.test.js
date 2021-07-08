/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

import React from "react";
import {render, unmountComponentAtNode} from "react-dom";
import {act} from "react-dom/test-utils";
import * as redux from "react-redux";
import axios from "axios";
import Nodes from "./Nodes";

jest.mock('axios');

let container = null;
beforeEach(() => {
    container = document.createElement("div");
    document.body.appendChild(container);
});

afterEach(() => {
    unmountComponentAtNode(container);
    container.remove();
    container = null;
    jest.restoreAllMocks();
});

it("renders user data", async () => {

    const spy = jest.spyOn(redux, 'useSelector')
    spy.mockReturnValue({groupId: 'my_test'})

    const nodesPayload = {
        "data": [
            {
                "details": "{\"productVersion\":\"4.0.0-M8\",\"osVersion\":\"10.0\",\"javaVersion\":\"1.8.0_261\",\"carbonHome\":\"D:\\\\wso2mi-4.0.0-SNAPSHOT\\\\bin\\\\..\",\"javaVendor\":\"Oracle Corporation\",\"osName\":\"Windows 10\",\"productName\":\"WSO2 Micro Integrator\",\"javaHome\":\"C:\\\\Program Files\\\\Java\\\\jdk1.8.0_261\\\\jre\"}",
                "nodeId": "node_test_2"
            }
        ]
    };

    axios.request.mockImplementation(() => Promise.resolve(nodesPayload))

    await act(async () => {
        render(<Nodes/>, container);
    });

    expect(container.textContent).toContain("node_test_2");

});
