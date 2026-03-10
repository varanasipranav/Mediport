package com.ezc.aragenPR.webapp.sap;

import com.sap.conn.jco.ext.DestinationDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.util.Properties;

@PropertySource("classpath:sap.properties")
@Configuration
public class SAPRFCConfig {

    @Autowired
    private Environment env;

    @Bean
    public Properties sapConnectionProperties() {
        Properties props = new Properties();
        props.setProperty(DestinationDataProvider.JCO_ASHOST, env.getProperty("sap.ashost"));
        props.setProperty(DestinationDataProvider.JCO_SYSNR,  env.getProperty("sap.sysnr"));
        props.setProperty(DestinationDataProvider.JCO_CLIENT, env.getProperty("sap.client"));
        props.setProperty(DestinationDataProvider.JCO_USER,   env.getProperty("sap.user"));
        props.setProperty(DestinationDataProvider.JCO_PASSWD, env.getProperty("sap.passwd"));
        props.setProperty(DestinationDataProvider.JCO_LANG,   env.getProperty("sap.lang"));

        String router = env.getProperty("sap.router");
        if(router != null && !router.isEmpty()) {
            props.setProperty(DestinationDataProvider.JCO_SAPROUTER, router);
        }

        return props;
    }
}
