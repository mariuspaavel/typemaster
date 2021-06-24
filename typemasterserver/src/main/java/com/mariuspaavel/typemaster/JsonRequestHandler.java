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
		String requestType = (String)jsonRequest.get("type");
		DBC dbc = DBC.getInstance();		

		switch(requestType){
			case "regsubmit":
			{
				String email = (String)jsonRequest.get("email");
				String password = (String)jsonRequest.get("password");
				return dbc.callFunction("regsubmit", session.getSessionId(), email, password);	
			}	
			case "register":
			{
				return dbc.callFunction("register", session.getSessionId());
			}
			case "login":
			{
				String email = (String)jsonRequest.get("email");
				String password = (String)jsonRequest.get("password");
				return dbc.callFunction("login", session.getSessionId(),  email, password);
			}	
			default: throw new UnknownRequestException();
		}
		
	}

}
