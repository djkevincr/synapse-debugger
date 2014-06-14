/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.apache.synapse.rest;

import org.apache.synapse.MessageContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class AbstractHandler implements Handler{

    /** A list of simple properties that would be set on the class before being used */
    protected final Map<String, Object> properties = new HashMap<String, Object>();

    public void addProperty(String name, Object value) {
        properties.put(name, value);
    }

    public Map getProperties() {
        return properties;
    }
}
