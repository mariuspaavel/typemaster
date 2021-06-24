package com.mariuspaavel.typemaster;

import java.util.*;
import java.io.*;
import java.sql.*;

public class JsonRequestHandler{
	private static JsonRequestHandler instance;
	public static JsonRequestHandler getInstance(){
		if(instance == null)return instance = new JsonRequestHandler();
		else return instance;
	}
	
	
	public Object handle(ClientSession session, Map<String, Object> jsonRequest) throws SQLException{
		String requestType = (String)jsonRequest.get("requesttype");
		DBC dbc = DBC.getInstance();		

		switch(requestType){
			case "submitage":
			{
				long birth = ((Long)jsonRequest.get("birth")).longValue();
				return dbc.callFunction("submitbirth", session.getSessionId(), birth);	
			}
			case "submitcontact":
			{
				String contact = (String)jsonRequest.get("contact");
				return dbc.callFunction("submitcontact", session.getSessionId(), contact);
			}
			case "submitnameandgender":
			{
				String name = (String)jsonRequest.get("name");
				String gender = (String)jsonRequest.get("gender");
				return dbc.callFunction("submitnameandgender", session.getSessionId(), name, gender);
			}
			case "submitpassword":
			{			
				String password = (String)jsonRequest.get("password");
				return dbc.callFunction("submitpassword", session.getSessionId(), password);
			}
			case "register":
			{
				return dbc.callFunction("register", session.getSessionId());
			}
			case "getcovers":
			{
				int accountid = (Integer)jsonRequest.get("accountid");
				List<Map<String, Object>> covers = new ArrayList<Map<String, Object>>();
				String query = String.format("SELECT * FROM getcovers(%d)",accountid);
				try(Statement stmt = dbc.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(query)){
					Map<String, Object> cover = new HashMap<String, Object>();
					while(rs.next()){
						cover.put("coverid", rs.getInt("coverid"));
						cover.put("mediaid", rs.getInt("mediaid"));
						cover.put("position", rs.getInt("position"));
						covers.add(cover);
					}
				}
				return covers;
			}
			case "addcover": 
			{
				int mediaId = (Integer)jsonRequest.get("mediaid");
				return dbc.callFunction("addcover", session.getAccountId(), mediaId);
			}
			case "deletecover":
			{
				int coverId = (Integer)jsonRequest.get("coverid");
				return dbc.callFunction("deletecover", session.getAccountId(), coverId);
			}
			case "getswipes":
			{
				String query = String.format("SELECT * FROM getswipes(%d)", session.getAccountId());
				List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
				try(Statement stmt = dbc.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(query)){
					Map<String, Object> currentAccount = new HashMap<String, Object>();
					while(rs.next()){
						currentAccount.put("accountid", rs.getInt("accountid"));
						currentAccount.put("name", rs.getString("name"));
						currentAccount.put("birth", rs.getLong("birth"));
						currentAccount.put("gender", rs.getString("gender"));
						currentAccount.put("bio", rs.getInt("bio"));
						results.add(currentAccount);
					}
				}
		
				//return dbc.callFunction("getswipes", session.getAccountId());		
			}
			default: throw new UnknownRequestException();
		}
		
	}

}
