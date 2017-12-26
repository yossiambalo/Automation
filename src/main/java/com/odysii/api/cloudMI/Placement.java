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

    public String getDeletePlacementRoute() {
        return deletePlacementRoute;
    }

    private String createRoute,addRoute,body, deletePlacementRoute;

    public Placement(){
        PropertyLoader propertyLoader = new PropertyLoader();
        Properties properties = propertyLoader.loadPropFile("placement.properties");
        this.createRoute = properties.getProperty("create_placement_route");
        this.addRoute = properties.getProperty("link_placement_route");
        this.body = properties.getProperty("placement_body");
        this.deletePlacementRoute = properties.getProperty("delete_placement_route");
    }
}
