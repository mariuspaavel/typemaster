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
	
	
	public Object handle(ClientSession session, Map<String, Object> jsonRequest) throws Exception{
		String requestType = (String)jsonRequest.get("type");
		Map<String, Object> args = (Map<String, Object>)jsonRequest.get("args");
		DBC dbc = DBC.getInstance();		

		switch(requestType){
			case "register":
			{
				String email = (String)args.get("email");
				String password = (String)args.get("password");
				
				if(!validEmail(email))scream("Invalid email");
				if(!validPassword(password))scream("Invalid password");

				return dbc.callFunction("register", session.getSessionId(), email, password);	
			}	
			case "login":
			{
				String email = (String)args.get("email");
				String password = (String)args.get("password");
				
				if(!validEmail(email))scream("Invalid email");
				if(!validPassword(password))scream("Invalid password");

				return dbc.callFunction("login", session.getSessionId(),  email, password);
			}
			case "logout":{
				dbc.callFunction("logout", session.getSessionId());
				return -1;
			}
			case "gettext":
			{
				return TextProvider.getInstance().getText();
			}
			case "insertrecord":
			{
				long timetaken = (Long)args.get("timetaken");
				int keystrokes = (int)((Long)args.get("keystrokes")).longValue();
				int misses = (int)((Long)args.get("misses")).longValue();
				return dbc.callFunction("insertrecord", session.getAccountId(), timetaken, keystrokes, misses);
			
			}
			case "getdailystatistics":{
				final String query = "SELECT * FROM get_daily_statistics(?);";
				try(PreparedStatement stmt = DBC.getInstance().getConnection().prepareStatement(query)){
					stmt.setInt(1, session.getAccountId());
					ResultSet rs = stmt.executeQuery();
					if(!rs.next())throw new RuntimeException();
					double averageSpeed = rs.getDouble("average_speed");
					double averageErrors = rs.getDouble("average_mistakes");
					Map<String, Object> result = new HashMap<>();
					result.put("average_speed", averageSpeed);
					result.put("average_errors", averageErrors);
					return result;
				}
			}
			default: throw new UnknownRequestException();
		}
		
	}
	private void scream(String message) throws Exception{
		throw new Exception(message);
	}

	private boolean validEmail(String email){
		if(email == null)return false;
		if(email.length() < 5)return false;
		else return true;
	}	
	private boolean validPassword(String password){
		if(password.length() < 5)return false;
		return true;
	}

}
