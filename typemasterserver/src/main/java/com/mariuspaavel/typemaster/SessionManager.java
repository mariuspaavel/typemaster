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

	PrintStream ds = System.out;

	public ClientSession authenticate(HttpServletRequest request, HttpServletResponse response)throws ServletException, SQLException{
		
		Cookie[] cookies = request.getCookies();
		if(cookies == null){
			return authReset(response);
		}
	
		ClientSession clientSession = readFromCookies(cookies);
		
		if(!validate(clientSession)){
			return authReset(response);
		}
		return clientSession;
			
	}
	private ClientSession readFromCookies(Cookie[] cookies){
	
		Cookie sessionIdCookie = null;
		Cookie sessionKeyCookie = null;
		outer:for(Cookie c : cookies){
			String name = c.getName();
			switch(name){
				case "sessionid": sessionIdCookie = c;
				if(sessionKeyCookie != null)break outer;
				break;
				case "sessionkey": sessionKeyCookie = c;
				if(sessionIdCookie != null)break outer;
				break;
			}
		}
		int sessionId = Integer.parseInt(sessionIdCookie.getValue());
		long sessionKey = Long.parseLong(sessionKeyCookie.getValue());
		return new ClientSession(sessionId, sessionKey);
	}
	private boolean validate(ClientSession session)throws SQLException{
	
	
		String query = String.format("SELECT * FROM validate(%d, %d);", session.getSessionId(), session.getSessionKey());
		try(Statement stmt=DBC.getInstance().getConnection().createStatement(); ResultSet rs = stmt.executeQuery(query)){
			if(!rs.next())return false;
			session.setAccountId(rs.getInt("accountid"));
			return true;
		}
	}
	
	
	private ClientSession authReset(HttpServletResponse response) throws SQLException{
		if(ds != null)ds.println("Resetting session");
		
		ClientSession session = null;		

		Cookie sessionIdCookie = null;
		Cookie sessionKeyCookie = null;
		
		try(Statement stmt = DBC.getInstance().getConnection().createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM createsession();")){
			rs.next();
			int sessionId = rs.getInt("sessionid");
			long sessionKey = rs.getLong("sessionkey");
			
			session = new ClientSession(sessionId, sessionKey);	
			
			sessionIdCookie = new Cookie("sessionid", Integer.toString(sessionId));
			sessionKeyCookie = new Cookie("sessionkey", Long.toString(sessionKey));
		}
	
		response.addCookie(sessionIdCookie);
		response.addCookie(sessionKeyCookie);
		return session;
	}
}
