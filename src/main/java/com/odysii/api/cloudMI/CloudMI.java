package com.odysii.api.cloudMI;

import com.odysii.general.PropertyLoader;

import java.util.Properties;

public abstract class CloudMI {

     protected String token, cloudMIUri, projectID;
     protected CloudMIUser cloudMIUser;

     protected void init(){
        PropertyLoader propertyLoader = new PropertyLoader();
        Properties properties = propertyLoader.loadPropFile("cloud_mi.properties");
        token = properties.getProperty("token");
        cloudMIUri = properties.getProperty("coloudMI_uri");
        projectID = properties.getProperty("project_id");
        cloudMIUser = new CloudMIUser(properties.getProperty("user_name"));
    }
}
