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
    private String dpcRoute,createDPCBody,gameRedemptionRoute,url;
    private List<String> dpcRedemptionBodyList;
    private RequestUtil requestUtil;

    public DigitalPunchCard(String propFile){
        PropertyLoader propertyLoader = new PropertyLoader();
        this.properties = propertyLoader.loadPropFile(propFile);
        this.dpcRoute = properties.getProperty("dpcRoute");
        this.createDPCBody = properties.getProperty("create_dpc_body");
        this.gameRedemptionRoute = properties.getProperty("redemption_route");
        this.dpcRedemptionBodyList = new ArrayList<>();
        this.dpcRedemptionBodyList.add(properties.getProperty("redemption_body1"));
        this.dpcRedemptionBodyList.add(properties.getProperty("redemption_body2"));
        url = cloudMIUri+ dpcRoute +"?ProjectId="+projectID+"&UserEmail="+cloudMIUser.getUserEmail();
        requestUtil = new RequestUtil(token,url, MediaType.APPLICATION_JSON);
        init(dpcRoute);
    }
    public JSONObject createDPC(){
        String result = requestUtil.postRequest(createDPCBody);
        return JsonHandler.stringToJson(result);
    }
}
