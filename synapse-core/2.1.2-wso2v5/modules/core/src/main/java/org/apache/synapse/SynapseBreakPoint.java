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

import org.apache.synapse.mediators.base.SequenceMediator;


public class SynapseBreakPoint {
    private SynapseBreakPointType breakPointType;
    private Mediator medRef;
    private String key;
    private int[] mediatorPosition;

    public String getKey(){return key;}
    public void setKey(String key){this.key=key;}
    public void setMediatorPosition(int[] mediatorPosition){this.mediatorPosition=mediatorPosition;}
    public int[] getMediatorPosition(){return mediatorPosition;}
    public void setSynapseBreakPointType(SynapseBreakPointType breakPointType){this.breakPointType=breakPointType;}
    public SynapseBreakPointType getSynapseBreakPointType(){return breakPointType;}
    public void setMediatorReference(Mediator reference){this.medRef=medRef;}
    public Mediator getMediatorReference(Mediator reference){return medRef;}
    public String toString(){
        return "";
    }

}
