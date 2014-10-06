package com.mp.runand.app.logic.network;

import com.mp.runand.app.Enums.ServerAnswerStatus;
import com.mp.runand.app.Enums.ServerQueryType;

import org.json.JSONObject;

/**
 * Created by Mateusz on 2014-10-05.
 */
public class ServerResponse {

    private JSONObject json;
    private ServerAnswerStatus serverAnswer;
    private ServerQueryType serverQueryType;

    public ServerResponse(ServerAnswerStatus ss, ServerQueryType sqt){
        this(null, ss, sqt);
    }

    public ServerResponse(JSONObject json, ServerAnswerStatus ss, ServerQueryType sqt){
        this.serverAnswer=ss;
        this.json=json;
        this.serverQueryType=sqt;
    }
}
