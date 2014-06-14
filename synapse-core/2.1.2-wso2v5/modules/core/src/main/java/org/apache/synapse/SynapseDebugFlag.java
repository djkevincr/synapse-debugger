/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.synapse;


public class SynapseDebugFlag {
    public static boolean DEBUG_FLAG; //Command line argument to start a debug session.
    public static boolean RUN_FLAG; //flag to indiate continue or exit from the mediation flow
    public static boolean SUSPEND_FLAG; //flag to indicate mediation flow suspension and listen to debug commands
    public static boolean BREAK_POINT_FLAG; //flag indicate mediator break point
    public static boolean STEP_OVER_DEBUG_FLAG;
    public static boolean SKIP_DEBUG_FLAG;
    public static boolean STEP_OUT_DEBUG_FLAG;
    public static boolean START_FLAG;
    public static boolean STEP_FLAG;

    public SynapseDebugFlag(){
        DEBUG_FLAG=true; //Command line argument to start a debug session.
        RUN_FLAG=true; //flag to indiate continue or exit from the mediation flow
        SUSPEND_FLAG=true; //flag to indicate mediation flow suspension and listen to debug commands
        BREAK_POINT_FLAG=false; //flag indicate mediator break point
        STEP_OVER_DEBUG_FLAG=false;
        SKIP_DEBUG_FLAG=false;
        STEP_OUT_DEBUG_FLAG=false;
        START_FLAG=true;
        STEP_FLAG=false;
    }


}
