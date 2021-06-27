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
		if(ds!=null)ds.println("Received getsession request");
		
		SessionManager sm = SessionManager.getInstance();		

		String sessionId = null;
		String sessionKey = null;

		Cookie[] cookies = request.getCookies();
		for(Cookie c : cookies){
			if(c.getName().equals("sessionid"))sessionId = c.getValue();
			else if(c.getName().equals("sessionkey"))sessionKey = c.getValue();
		}	
		ClientSession session = sm.validate(sessionId, sessionKey);	
	
		Map<String, Object> output = new HashMap<String, Object>();
		
		output.put("type", "success")
		output.put("payload", session.toJson());
	
		BufferedOutputStream os = new BufferedOutputStream(response.getOutputStream(), 8*1024);		
		PrintStream out = new PrintStream(os);
		json.write(output, out);		
		os.flush();
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		doPost(request, response);
	}
	public void destroy(){
	
	}

}

