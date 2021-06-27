package com.mariuspaavel.typemaster;
import java.util.*;
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import com.mariuspaavel.jsonlib.*;
import org.postgresql.util.PSQLException;

@WebServlet("/getsession")
public class GetSession extends HttpServlet{	
	final PrintStream ds = System.out;
	private com.mariuspaavel.jsonlib.Json json = new com.mariuspaavel.jsonlib.Json();
	
	public void init(){


	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		Map<String, Object> output = null;
		
		try{
			if(ds!=null)ds.println("Received getsession request");
		
			output = new HashMap<>();
			
			SessionManager sm = SessionManager.getInstance();		

			Integer sessionId = null;
			String sessionKey = null;

		
			cookiereader:try{
				Cookie[] cookies = request.getCookies();
				if(cookies == null)break cookiereader;
				for(Cookie c : cookies){
					String value = c.getValue();
					if(value == null)continue;
					if(c.getName().equals("sessionid"))sessionId = new Integer(c.getValue());
					else if(c.getName().equals("sessionkey"))sessionKey = c.getValue();
				}
			}catch(Exception e){}
	
			ClientSession session = null;
			if(sessionId != null && sessionKey != null)session = sm.validate(sessionId, sessionKey);
	
			if(session == null)session = sm.createSession();		
			sm.syncCookies(session, request, response);
		
			output.put("type", "success");
			output.put("payload", session.toJson());
	
		
		}catch(Exception e){
			e.printStackTrace(ds);
			output = new HashMap<>();
			output.put("type", "fail");
			output.put("payload", e.getMessage());	
		}
		try{
			BufferedOutputStream os = new BufferedOutputStream(response.getOutputStream(), 8*1024);		
			PrintStream out = new PrintStream(os);
			json.write(output, out);		
			os.flush();
		}catch(Exception e){
			e.printStackTrace(ds);
			return;
		}
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		doPost(request, response);
	}
	public void destroy(){
	
	}

}

