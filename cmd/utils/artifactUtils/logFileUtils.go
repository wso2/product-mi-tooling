/*
* Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* WSO2 Inc. licenses this file to you under the Apache License,
* Version 2.0 (the "License"); you may not use this file except
* in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
 */
package artifactUtils

type LogFileList struct {
    Count    int32     `json:"count"`
    LogFiles []LogFile `json:"list"`
}

type LogFile struct {
    FileName string `json:"FileName"`
    Size string `json:"size"`
}

func (fileList *LogFileList) GetDataIterator() <-chan []string {
    ch := make(chan []string)

    go func() {
        for _, logFile := range fileList.LogFiles {
            ch <- []string{logFile.FileName, logFile.Size}
        }
        close(ch)
    }()

    return ch
}

func (fileList *LogFileList) GetCount() int32 {
    return fileList.Count
}
