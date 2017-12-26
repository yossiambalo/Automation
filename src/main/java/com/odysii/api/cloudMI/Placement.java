package com.odysii.api.cloudMI;

import com.odysii.general.PropertyLoader;

import java.util.Properties;

public class Placement {

    public String getCreateRoute() {
        return createRoute;
    }

    public String getAddRoute() {
        return addRoute;
    }

    public String getBody() {
        return body;
    }

    private String createRoute,addRoute,body;

    public Placement(){
        PropertyLoader propertyLoader = new PropertyLoader();
        Properties properties = propertyLoader.loadPropFile("placement.properties");
        this.createRoute = properties.getProperty("create_placement_route");
        this.addRoute = properties.getProperty("link_placement_route");
        this.body = properties.getProperty("placement_body");
    }
}
