package com.odysii.test.impulse.serial;

import com.odysii.api.cloudMI.CloudMIUser;
import com.odysii.api.cloudMI.Survey;
import com.odysii.general.PropertyLoader;
import org.testng.annotations.BeforeClass;

import java.util.Properties;

public class SurveyTest {
    String token, surveyRoute,cloudMIUri,projectID,createSurveyBody;
    CloudMIUser cloudMIUser;
    @BeforeClass
    public void setUp(){
        PropertyLoader propertyLoader = new PropertyLoader();
        Properties properties = propertyLoader.loadPropFile("survey.properties");
        token = properties.getProperty("token");
        surveyRoute = properties.getProperty("surveyRoute");
        cloudMIUri = properties.getProperty("coloudMI_uri");
        projectID = properties.getProperty("project_id");
        createSurveyBody = properties.getProperty("create_survey_body");
        cloudMIUser = new CloudMIUser(properties.getProperty("user_name"));
        Survey survey = new Survey(token,surveyRoute);

    }
}
