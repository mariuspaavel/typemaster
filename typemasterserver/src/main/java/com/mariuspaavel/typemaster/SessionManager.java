package com.mariuspaavel.typemaster;

import java.util.*;
import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;


/**
*Manages logged in devices
*/
public class SessionManager {
	private static SessionManager instance;
	public static SessionManager getInstance(){
		if(instance == null)return instance = new SessionManager();
		else return instance;
	}
	private SessionManager(){
	}	

	private PrintStream ds = System.out;
	

	
	ClientSession validate(int sessionId, String sessionKey)throws SQLException{		

		String query = String.format("SELECT * FROM validate(%d, '%s');", sessionId, sessionKey);
		ds.println(query);
		try(Statement stmt=DBC.getInstance().getConnection().createStatement(); ResultSet rs = stmt.executeQuery(query)){
			if(!rs.next())return null;
			ClientSession session = new ClientSession(sessionId, sessionKey);
			session.setAccountId(rs.getInt("accountid"));
			return session;
		}
	}
	void syncCookies(ClientSession session, HttpServletRequest request, HttpServletResponse response){
		Cookie[] cookies = request.getCookies();
		Integer readSessionId = null;
		String readSessionKey = null;
		if(cookies != null){
			for(Cookie c : cookies){
				String value = c.getValue();
				if(value == null)continue;
				try{
					if(c.getName().equals("sessionid"))readSessionId = new Integer(c.getValue());
					else if(c.getName().equals("sessionkey"))readSessionKey = c.getValue();
				}catch(NumberFormatException e){continue;}
			}
		}
		if(
		readSessionId == null || 
		readSessionKey == null ||
		readSessionId != session.getSessionId() ||
		readSessionKey != session.getSessionKey()
		){
			response.addCookie(new Cookie("sessionid", Integer.toString(session.getSessionId())));
			response.addCookie(new Cookie("sessionKey", session.getSessionKey()));
		}
	}
	ClientSession createSession() throws SQLException{
		try(Statement stmt = DBC.getInstance().getConnection().createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM createsession();")){
			rs.next();
			int sessionId = rs.getInt("sessionid");
			String sessionKey = rs.getString("sessionkey");
			
			return new ClientSession(sessionId, sessionKey);	
		}
	}
}
