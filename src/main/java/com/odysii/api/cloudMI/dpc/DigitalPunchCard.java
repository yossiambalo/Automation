package com.odysii.api.cloudMI.dpc;

import com.odysii.api.MediaType;
import com.odysii.api.cloudMI.CloudMI;
import com.odysii.api.util.RequestUtil;
import com.odysii.general.JsonHandler;
import com.odysii.general.PropertyLoader;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DigitalPunchCard extends CloudMI {

    private Properties properties;
    private String dpcRoute,createDPCBody, redemptionRoute,url;
    private List<String> dpcRedemptionBodyList;
    private RequestUtil requestUtil;

    public DigitalPunchCard(String propFile){
        PropertyLoader propertyLoader = new PropertyLoader();
        this.properties = propertyLoader.loadPropFile(propFile);
        this.dpcRoute = properties.getProperty("dpcRoute");
        this.createDPCBody = properties.getProperty("create_dpc_body");
        this.redemptionRoute = properties.getProperty("redemption_route");
        this.dpcRedemptionBodyList = new ArrayList<>();
        this.dpcRedemptionBodyList.add(properties.getProperty("redemption_body1"));
        this.dpcRedemptionBodyList.add(properties.getProperty("redemption_body2"));
        init(dpcRoute);
        url = cloudMIUri+ dpcRoute +"?ProjectId="+projectID+"&UserEmail="+cloudMIUser.getUserEmail();
        requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_JSON);
}
    public JSONObject createDPC(){
        String result = requestUtil.postRequest(createDPCBody);
        return JsonHandler.stringToJson(result);
    }
    public JSONObject createRedemption(String dpcID){
        String url = cloudMIUri+ redemptionRoute +"?ProjectId="+projectID+"&UserEmail="+cloudMIUser.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_JSON);
        String result = "";
        JSONObject jsonObject = null;
        JSONObject jsonObject2 = null;
        for (String optionBody : dpcRedemptionBodyList){
            jsonObject = JsonHandler.stringToJson(optionBody);
            jsonObject2 = JsonHandler.stringToJson(optionBody).getJSONObject("loyalty_campaign_redemption").put("loyalty_campaign_id",dpcID);
            jsonObject.put("loyalty_campaign_redemption",jsonObject2);
            result = requestUtil.postRequest(jsonObject.toString());
        }
        return JsonHandler.stringToJson(result);
    }
    public JSONObject createDPC(String key,String value){
        JSONObject jsonObject = JsonHandler.stringToJson(createDPCBody);
        JSONObject jsonObject2 = JsonHandler.stringToJson(createDPCBody).getJSONObject("loyalty_campaign").put(key,value);
        jsonObject.put("loyalty_campaign",jsonObject2);
        String result = requestUtil.postRequest(jsonObject.toString());
        return JsonHandler.stringToJson(result);
    }
    public JSONObject deleteDPC(String gameID){
        String url = cloudMIUri+ dpcRoute +"/"+gameID+"?ProjectId="+projectID+"&UserEmail="+cloudMIUser.getUserEmail();
        RequestUtil requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_JSON);
        String result = requestUtil.deleteRequest();
        return JsonHandler.stringToJson(result);
    }
}
