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
			case "status":
			{
				return session.getAccountId();
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
