package com.ezc.aragenPR.webapp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class RFCReadSAPService {

    @Autowired
    private Environment env;

    private static final String DEST_NAME = "ARAGEN_DEST";
    private static boolean providerRegistered = false;

    /**
     * Read SAP table using JCo RFC_READ_TABLE
     */
    public List<String[]> readRFCTable(String tableName, String[] outputFields, String[] queryValues) {
        List<String[]> outList = new ArrayList<>();
        try {
            JCoDestination destination = getDestination();

            JCoFunction function = destination.getRepository().getFunction("RFC_READ_TABLE");
            if (function == null) {
                throw new RuntimeException("RFC_READ_TABLE not found in SAP.");
            }

            // Input parameters
            function.getImportParameterList().setValue("QUERY_TABLE", tableName);
            function.getImportParameterList().setValue("DELIMITER", "#");

            // Fields
            JCoTable fieldsTable = function.getTableParameterList().getTable("FIELDS");
            for (String field : outputFields) {
                fieldsTable.appendRow();
                fieldsTable.setValue("FIELDNAME", field);
            }

            // Options (filters)
            JCoTable optionsTable = function.getTableParameterList().getTable("OPTIONS");
            for (String query : queryValues) {
                if (query != null && !"".equals(query) && !"null".equals(query)) {
                    optionsTable.appendRow();
                    optionsTable.setValue("TEXT", query);
                }
            }

            // Execute RFC
            function.execute(destination);

            // Read data
            JCoTable dataTable = function.getTableParameterList().getTable("DATA");
            for (int i = 0; i < dataTable.getNumRows(); i++) {
                dataTable.setRow(i);
                String wa = dataTable.getString("WA");
                outList.add(wa.split("#"));
            }

        } catch (JCoException e) {
            log.error("Error reading SAP table: {}", e.getMessage(), e);
        }

        return outList;
    }

    /**
     * Get SAP Destination
     */
    public JCoDestination getDestination() throws JCoException {
        System.out.println("IN::getDestination:: TRY"+env.getProperty("sap.user"));
        Properties connectProperties = new Properties();
        connectProperties.setProperty("jco.client.ashost", env.getProperty("sap.ashost"));
        connectProperties.setProperty("jco.client.sysnr", env.getProperty("sap.sysnr"));
        connectProperties.setProperty("jco.client.client", env.getProperty("sap.client"));
        connectProperties.setProperty("jco.client.user", env.getProperty("sap.user"));
        connectProperties.setProperty("jco.client.passwd", env.getProperty("sap.passwd"));
        connectProperties.setProperty("jco.client.lang", env.getProperty("sap.lang"));
        String sapRouter = env.getProperty("sap.saprouter");
        if (sapRouter != null && !sapRouter.isEmpty()) {
            connectProperties.setProperty("jco.client.saprouter", sapRouter);
        }
        System.out.println("IN::getDestination:: saprouter"+env.getProperty("sap.saprouter"));

        // Register provider only once
        if (!providerRegistered) {
            com.sap.conn.jco.ext.Environment.registerDestinationDataProvider(
                    new MyDestinationDataProvider(DEST_NAME, connectProperties)
            );
            providerRegistered = true;
        }
        JCoDestination destination = JCoDestinationManager.getDestination(DEST_NAME);

        // Test connection
        try {
            destination.ping();  // Will throw JCoException if connection fails
            System.out.println("SAP Connection established successfully!");
            System.out.println("SAP USER"+destination.getUser());
            System.out.println("SAP client"+destination.getClient());
            System.out.println("SAP application"+destination.getApplicationServerHost());
            log.info("Connected User: {}, Client: {}, Host: {}",
                    destination.getUser(),
                    destination.getClient(),
                    destination.getApplicationServerHost());
        } catch (JCoException e) {
            log.error("Error connecting to SAP: {}", e.getMessage(), e);
            throw e;  // rethrow exception if needed
        }
        return JCoDestinationManager.getDestination(DEST_NAME);
    }

    /**
     * Inner class: Custom DestinationDataProvider
     */
    static class MyDestinationDataProvider implements DestinationDataProvider {
        private final Map<String, Properties> destinations = new HashMap<>();

        public MyDestinationDataProvider(String destName, Properties properties) {
            destinations.put(destName, properties);
        }

        @Override
        public Properties getDestinationProperties(String destinationName) {
            return destinations.get(destinationName);
        }

        @Override
        public void setDestinationDataEventListener(DestinationDataEventListener eventListener) {
            // Not needed for basic usage
        }

        @Override
        public boolean supportsEvents() {
            return false;
        }
    }
}
