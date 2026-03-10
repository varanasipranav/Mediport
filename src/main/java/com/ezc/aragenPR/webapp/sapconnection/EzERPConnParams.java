/**
* To Read ERP Connection Parameters from Database.
* Using for JCO Connector and Java Server(Reports)
*/
package com.ezc.aragenPR.webapp.sapconnection;

import java.sql.*;
import java.util.*;



public class EzERPConnParams
{
   /**
	* 
	*/
	private static final long serialVersionUID = 1L;
		ResourceBundle siteProps = ResourceBundle.getBundle("Site");
        public EzERPConnParams(){}
        public Hashtable ezGetERPConnParams()
        {
             Connection conn = null;
             Statement stmt = null;
             try
             {
            	  //log(">>>>>>>>>>>>> Enter into ezGetERPConnParams ........");
                  String erpNo  =  siteProps.getString("ERPGRP");
                  String SiteNo = siteProps.getString("DBGRP");
                  Class.forName(siteProps.getString("Driver_"+SiteNo));
                  conn = DriverManager.getConnection(siteProps.getString("ConnectString_"+SiteNo),siteProps.getString("UserId_"+SiteNo),siteProps.getString("Password_"+SiteNo));
                  stmt = conn.createStatement();
                  ResultSet retObj = stmt.executeQuery("select * from EZC_USER_GROUPS where EUG_ID = "+erpNo);
                  Hashtable connParams = new Hashtable();
                  while (retObj.next())
                  {
                        connParams.put("R3_HOST",retObj.getString("EUG_R3_HOST"));
                        connParams.put("R3_CLIENT",retObj.getString("EUG_R3_CLIENT"));
                        connParams.put("R3_USER_ID",retObj.getString("EUG_R3_USER_ID"));
                        connParams.put("R3_PASSWD",retObj.getString("EUG_R3_PASSWD"));
                        connParams.put("R3_SYS_NO",retObj.getString("EUG_R3_SYS_NO"));
                        connParams.put("R3_NO_OF_CONN",retObj.getString("EUG_R3_NO_OF_CONN"));
                  }
                  return connParams;
             }catch (Exception cnfe)
             {
                  //log("ERROR While Getting ERP Params from DB:Check Site.Properties Entries:"+cnfe,"E");
                  return null;
             }
             finally{
                  try{
                     stmt.close();
                     conn.close();
                  }catch (Exception cnfe)
                  {
                     //log("ERROR While Getting ERP Params from DB:Check Site.Properties Entries:"+cnfe,"E");
                     return null;
                  }
            }
        }
        
        
        public Hashtable ezGetERPConnParams(String SID)
        {
             Connection conn = null;
             Statement stmt = null;
             try
             {
            	  //log(">>>>>>>>>>>>> Enter into ezGetERPConnParams ........");
                  String erpNo  = ""; //siteProps.getString("ERPGRP");
            	  String SiteNo = ""; //siteProps.getString("DBGRP");
            	  if(SID.indexOf('~')!=-1){
            		  SiteNo = SID.substring(0, SID.indexOf('~'));
            		  erpNo  = SID.substring(SID.indexOf('~')+1, SID.length());
            	  }else{
            		  //log("Problem with SID (SID should be a combination of Connection Grp and Erp Group Ex:'243~999')","E");
            		  return null;
            	  }
                  
                  Class.forName(siteProps.getString("Driver_"+SiteNo));
                  conn = DriverManager.getConnection(siteProps.getString("ConnectString_"+SiteNo),siteProps.getString("UserId_"+SiteNo),siteProps.getString("Password_"+SiteNo));
                  stmt = conn.createStatement();
                  ResultSet retObj = stmt.executeQuery("select * from EZC_USER_GROUPS where EUG_ID = "+erpNo);
                  Hashtable connParams = new Hashtable();
                  while (retObj.next())
                  {
                        connParams.put("R3_HOST",retObj.getString("EUG_R3_HOST"));
                        connParams.put("R3_CLIENT",retObj.getString("EUG_R3_CLIENT"));
                        connParams.put("R3_USER_ID",retObj.getString("EUG_R3_USER_ID"));
                        connParams.put("R3_PASSWD",retObj.getString("EUG_R3_PASSWD"));
                        connParams.put("R3_SYS_NO",retObj.getString("EUG_R3_SYS_NO"));
                        connParams.put("R3_NO_OF_CONN",retObj.getString("EUG_R3_NO_OF_CONN"));
                  }
                  return connParams;
             }catch (Exception cnfe)
             {
                  //log("ERROR While Getting ERP Params from DB:Check Site.Properties Entries:"+cnfe,"E");
                  return null;
             }
             finally{
                  try{
                     stmt.close();
                     conn.close();
                  }catch (Exception cnfe)
                  {
                     //log("ERROR While Getting ERP Params from DB:Check Site.Properties Entries:"+cnfe,"E");
                     return null;
                  }
            }
        }
        
        
        
        public static void main(String args[]){
                EzERPConnParams obj=new EzERPConnParams();
                obj.ezGetERPConnParams();
        }
}
