package com.atg.consulting.evoter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates and open the template
 * in the editor.
 */
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

/**
 *
 * @author justice
 */
public class Configuration {

    private static Configuration instance;

    public static Configuration getInstance() throws Exception {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }
    private Properties coreProperties = new Properties();

    private Configuration() throws Exception {
        File confDir = new File("conf");
        coreProperties.load(new FileReader(new File(confDir, "evm.properties")));
    }

    public String getTomcatDir() {
        return coreProperties.getProperty(PropertyNames.TOMCAT_DIR);
    }

    public int getTomcatPort() {
        return Integer.parseInt(coreProperties.getProperty(PropertyNames.TOMCAT_PORT));
    }

    public String getJdbcUsername() {
        return coreProperties.getProperty(PropertyNames.JDBC_USER);
    }

    public String getJdbcDriver() {
        return coreProperties.getProperty(PropertyNames.JDBC_DRIVER);
    }

    public String getJdbcPassword() {
        return coreProperties.getProperty(PropertyNames.JDBC_PASSWORD);
    }

    public String getJdbcUrl() {
        return coreProperties.getProperty(PropertyNames.JDBC_URL);
    }

    public boolean verifyDBProperties() {
        if (getJdbcDriver() == null) {
            return false;
        } else if (getJdbcUrl() == null) {
            return false;
        } else if (getJdbcUsername() == null) {
            return false;
        } else if (getJdbcPassword() == null) {
            return false;
        } else {
            return true;
        }
    }

    public String getFromEmail() {
        return coreProperties.getProperty(PropertyNames.FROM_EMAIL);
    }

    public String getMailHost() {
        return coreProperties.getProperty(PropertyNames.MAIL_HOST);
    }

    public String getMailPort() {
        return coreProperties.getProperty(PropertyNames.MAIL_PORT);
    }

    public boolean getMailSmtpAuth() {
        return Boolean.parseBoolean(coreProperties.getProperty(PropertyNames.MAIL_SMTP_AUTH));
    }

    public boolean getMailStartTlsEnabled() {
        return Boolean.parseBoolean(coreProperties.getProperty(PropertyNames.MAIL_START_TLS_ENABLED));
    }

    public String getMailUsername() {
        return coreProperties.getProperty(PropertyNames.MAIL_USERNAME);
    }

    public String getMailPassword() {
        return coreProperties.getProperty(PropertyNames.MAIL_PASSWORD);
    }

    public String getStatementMailSubject() {
        return coreProperties.getProperty(PropertyNames.MAIL_SUBJECT);
    }

    public String getCronExpressionInvoiceGenerator() {
        return coreProperties.getProperty(PropertyNames.INVOICE_GENERATOR_CRON);
    }

    public String getCronExpressionNotificationSender() {
        return coreProperties.getProperty(PropertyNames.NOTIFICATION_SENDER_CRON);
    }

    public String getActiveSmsId() {
        return coreProperties.getProperty(PropertyNames.ACTIVE_SMS_PLUGIN_ID);
    }

    public String[] getSmsConfiguration() {
        String smsConf = coreProperties.getProperty(PropertyNames.ACIVE_SMS_PLUGIN_CONFIGURATION);
        return smsConf.split(";");
    }

    public String getFromSmsName() {
        return coreProperties.getProperty(PropertyNames.FROM_SMS_NAME);
    }

    public String getSMSReference() {
        return coreProperties.getProperty(PropertyNames.SMS_REFERENCE);
    }

    public String getCurrencyCode() {
        String code = coreProperties.getProperty(PropertyNames.CURRENCY_CODE);
        if (code == null || code.length() == 0) {
            code = "BWP";
        }
        return code;
    }
}
