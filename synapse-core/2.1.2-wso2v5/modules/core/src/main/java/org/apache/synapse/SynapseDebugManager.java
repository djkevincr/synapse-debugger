/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.synapse;

import org.apache.synapse.config.SynapseConfiguration;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.mediators.AbstractListMediator;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.synapse.rest.RESTConstants;

import java.io.IOException;


public class SynapseDebugManager {

    private MessageContext synCtx;
    private SynapseDebugInterface debugInterface;
   // private static SynapseDebugFlag synapseDebugFlag;
    private static SynapseDebugManager debugManagerInstance = null;
    private SynapseConfiguration synCfg;
    private SynapseEnvironment synEnv;
    private MediationFlowState medFlowState;


    protected SynapseDebugManager() {
    }


   public void setMessageContext(MessageContext synCtx){
        this.synCtx = synCtx;
    }


   public static SynapseDebugManager getInstance() {
        if(debugManagerInstance == null) {
            debugManagerInstance = new SynapseDebugManager();
           // synapseDebugFlag= new SynapseDebugFlag();
        }
        return debugManagerInstance;
    }


    public void init(SynapseConfiguration synCfg, SynapseDebugInterface debugInterface,SynapseEnvironment synEnv ){
        if(synEnv.isDebugEnabled()){
            this.synCfg=synCfg;
            this.debugInterface=debugInterface;
            this.synEnv =synEnv;
        }

    }

    public void closeCommunicationChannel(){
        if(synEnv.isDebugEnabled()){
            debugInterface.closeConnection();
        }

    }




    public void hitSynapseBreakPoint(MessageContext synCtx, SynapseBreakPoint breakPoint){
        try{
        setMessageContext(synCtx);
        medFlowState=MediationFlowState.SUSPENDED;
        advertiseDebugEvent(SynapseDebugEventConstants.DEBUG_EVENT_SUSPENDED_BREAKPOINT + " " +breakPoint.getSynapseBreakPointType().toString()+" "+breakPoint.getKey()+" "+toString(breakPoint.getMediatorPosition()));
        String debug_line=null;
        while(medFlowState.equals(MediationFlowState.SUSPENDED)){
            if(debugInterface.getPortListenReader().ready()){
            debug_line=debugInterface.getPortListenReader().readLine();
            }
            if(debug_line!=null){
                processDebugCommand(debug_line);
                debug_line=null;
            }
        }
        advertiseDebugEvent(SynapseDebugEventConstants.DEBUG_EVENT_RESUMED_CLIENT);
        }catch(IOException e){

        }
    }

   public void listenCommunicationChannel(){
        if(synEnv.isDebugEnabled()){
            try{
            String debug_line=null;
            if(debugInterface.getPortListenReader().ready()){
            debug_line=debugInterface.getPortListenReader().readLine();
            }
            if(debug_line!=null){
                processDebugCommand(debug_line);
                debug_line=null;
                if(debugInterface.getPortListenReader().ready()){
                debug_line=debugInterface.getPortListenReader().readLine();
                }
                while(debug_line!=null){
                    processDebugCommand(debug_line);
                    debug_line=null;
                    debug_line=debugInterface.getPortListenReader().readLine();
                }

            }else{ System.out.println("No new line to read"); }
            }catch(IOException ex){}
        }

    }

   public void processDebugCommand(String debug_line){
        String[] command = debug_line.split("\\s+");
        if(command[0].equals(SynapseDebugCommandConstants.DEBUG_COMMAND_CLEAR_BREAKPOINT)){
            int[] position=new int[command.length-3];
            String breakPointType=command[1];
            String seqKey=command[2];
            for(int counter=3;counter<command.length;counter++){
                position[counter-3]= Integer.parseInt(command[counter]);
            }
            this.registerSynapseBreakPoint(breakPointType,seqKey,position);
        }else if(command[0].equals(SynapseDebugCommandConstants.DEBUG_COMMAND_LOG)){
            String[] arguments=new String[command.length-1];
            for(int counter=1;counter<command.length;counter++){
                arguments[counter-1]= command[counter];
            }
            this.debugLog(arguments);
        }else if(command[0].equals(SynapseDebugCommandConstants.DEBUG_COMMAND_RESUME)){
            this.debugResume();
        }else if(command[0].equals(SynapseDebugCommandConstants.DEBUG_COMMAND_SET_BREAKPOINT)){
            int[] position=new int[command.length-3];
            String breakPointType=command[1];
            String seqKey=command[2];
            for(int counter=3;counter<command.length;counter++){
                position[counter-3]= Integer.parseInt(command[counter]);
            }
            this.unregisterSynapseBreakPoint(breakPointType, seqKey, position);
        }else
            System.out.println("command not found");
    }

    public void advertiseDebugEvent(String event){
        if(synEnv.isDebugEnabled()){
            debugInterface.getPortSendWriter().println(event);
            debugInterface.getPortSendWriter().flush();
        }
    }


    public void registerSynapseBreakPoint(String breakPointType,String seqKey,int[] position){
        SynapseBreakPointType synapseBreakPointType=SynapseBreakPointType.valueOf(breakPointType);
        SynapseBreakPoint breakPoint=new SynapseBreakPoint();
        breakPoint.setKey(seqKey);
        breakPoint.setMediatorPosition(position);
        breakPoint.setSynapseBreakPointType(synapseBreakPointType);
        Mediator seqMediator=null;
        if(breakPointType.equals(SynapseBreakPointType.NAMED)){
            seqMediator=synCfg.getSequence(seqKey);
        }else if(breakPointType.equals(SynapseBreakPointType.PROXY_INSEQ)){
            seqMediator=synCfg.getProxyService(seqKey).getTargetInLineInSequence();
        }else if(breakPointType.equals(SynapseBreakPointType.PROXY_OUTSEQ)){
            seqMediator=synCfg.getProxyService(seqKey).getTargetInLineOutSequence();
        }else if(breakPointType.equals(SynapseBreakPointType.PROXY_FAULTSEQ)){
            seqMediator=synCfg.getProxyService(seqKey).getTargetInLineFaultSequence();
        }else if(breakPointType.equals(SynapseBreakPointType.API_INSEQ)){
            seqMediator=synCfg.getAPI(seqKey).getResource((String) synCtx.getProperty(RESTConstants.SYNAPSE_RESOURCE)).getInSequence();
        }else if(breakPointType.equals(SynapseBreakPointType.API_OUTSEQ)){
            seqMediator=synCfg.getAPI(seqKey).getResource((String) synCtx.getProperty(RESTConstants.SYNAPSE_RESOURCE)).getOutSequence();
        }else if(breakPointType.equals(SynapseBreakPointType.API_FAULTSEQ)){
            seqMediator=synCfg.getAPI(seqKey).getResource((String) synCtx.getProperty(RESTConstants.SYNAPSE_RESOURCE)).getFaultSequence();
        }
        Mediator current_mediator=((AbstractListMediator)seqMediator).getChild(position[0]);
        for(int counter=1;counter<position.length;counter++){
            current_mediator=((AbstractListMediator)current_mediator).getChild(position[counter]);
        }
        breakPoint.setMediatorReference(current_mediator);
        ((AbstractMediator)current_mediator).setBreakPoint(false);
        ((AbstractMediator)current_mediator).unregisterBreakPoint();
        debugInterface.getPortListenWriter().println(SynapseDebugCommandConstants.DEBUG_COMMAND_SUCCESS);
        debugInterface.getPortListenWriter().flush();
    }


    public void debugResume(){
        medFlowState=MediationFlowState.RESUMED;
        debugInterface.getPortListenWriter().println(SynapseDebugCommandConstants.DEBUG_COMMAND_SUCCESS);
        debugInterface.getPortListenWriter().flush();
    }


   public void unregisterSynapseBreakPoint(String breakPointType,String seqKey,int[] position){
        SynapseBreakPointType synapseBreakPointType=SynapseBreakPointType.valueOf(breakPointType);
        SynapseBreakPoint breakPoint=new SynapseBreakPoint();
        breakPoint.setKey(seqKey);
        breakPoint.setMediatorPosition(position);
        breakPoint.setSynapseBreakPointType(synapseBreakPointType);
        Mediator seqMediator=null;
        if(breakPointType.equals(SynapseBreakPointType.NAMED)){
            seqMediator=synCfg.getSequence(seqKey);
        }else if(breakPointType.equals(SynapseBreakPointType.PROXY_INSEQ)){
            seqMediator=synCfg.getProxyService(seqKey).getTargetInLineInSequence();
        }else if(breakPointType.equals(SynapseBreakPointType.PROXY_OUTSEQ)){
            seqMediator=synCfg.getProxyService(seqKey).getTargetInLineOutSequence();
        }else if(breakPointType.equals(SynapseBreakPointType.PROXY_FAULTSEQ)){
            seqMediator=synCfg.getProxyService(seqKey).getTargetInLineFaultSequence();
        }else if(breakPointType.equals(SynapseBreakPointType.API_INSEQ)){
            seqMediator=synCfg.getAPI(seqKey).getResource((String) synCtx.getProperty(RESTConstants.SYNAPSE_RESOURCE)).getInSequence();
        }else if(breakPointType.equals(SynapseBreakPointType.API_OUTSEQ)){
            seqMediator=synCfg.getAPI(seqKey).getResource((String) synCtx.getProperty(RESTConstants.SYNAPSE_RESOURCE)).getOutSequence();
        }else if(breakPointType.equals(SynapseBreakPointType.API_FAULTSEQ)){
            seqMediator=synCfg.getAPI(seqKey).getResource((String) synCtx.getProperty(RESTConstants.SYNAPSE_RESOURCE)).getFaultSequence();
        }
        Mediator current_mediator=((AbstractListMediator)seqMediator).getChild(position[0]);
        for(int counter=1;counter<position.length;counter++){
            current_mediator=((AbstractListMediator)current_mediator).getChild(position[counter]);
        }
        breakPoint.setMediatorReference(current_mediator);
        ((AbstractMediator)current_mediator).setBreakPoint(true);
        ((AbstractMediator)current_mediator).registerBreakPoint(breakPoint);
        debugInterface.getPortListenWriter().println(SynapseDebugCommandConstants.DEBUG_COMMAND_SUCCESS);
        debugInterface.getPortListenWriter().flush();
    }

   public String toString(int[] position){
        String positionString="";
        for(int counter=0;counter<position.length;counter++){
            positionString=positionString.concat(String.valueOf(position[counter])).concat(" ");
        }
        return  positionString;
    }


    public void debugLog(String[] arguments){
        /** Only properties specified to the Log mediator */
        final int CUSTOM= 0;
        /** To, From, WSAction, SOAPAction, ReplyTo, MessageID and any properties */
        final int SIMPLE= 1;
        /** All SOAP header blocks and any properties */
        final int HEADERS = 2;
        /** all attributes of level 'simple' and the SOAP envelope and any properties */
        final int FULL    = 3;
        String logLevelArgument=arguments[0];
        int logLevel=1;

        if(logLevelArgument.equals("simple")){
            logLevel=1;
        }else if(logLevelArgument.equals("headers")){
            logLevel=2;
        }else if(logLevelArgument.equals("full")){
            logLevel=3;
        }else if(logLevelArgument.equals("custom")){
            logLevel=0;
        }
        String log="";
        SynapseDebugLogger debugLogger=new SynapseDebugLogger();
        switch (logLevel) {
            case CUSTOM:
                log=debugLogger.getCustomLogMessage(synCtx);
                break;
            case SIMPLE:
                log=debugLogger.getSimpleLogMessage(synCtx);
                break;
            case HEADERS:
                log=debugLogger.getHeadersLogMessage(synCtx);
                break;
            case FULL:
                log=debugLogger.getFullLogMessage(synCtx);
                break;
            default:
                log="Invalid log level specified";
        }
       //all the data needed in the current mediation point. Message envelope used as a sample
        debugInterface.getPortListenWriter().println(log);
        debugInterface.getPortListenWriter().flush();
    }

}
