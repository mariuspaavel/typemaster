package com.mariuspaavel.typemaster;
import java.util.*;
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import com.mariuspaavel.jsonlib.*;
import org.postgresql.util.PSQLException;

@WebServlet("/jsonrequest")
public class JsonRequest extends HttpServlet{	
	final PrintStream ds = System.out;
	private com.mariuspaavel.jsonlib.Json json = new com.mariuspaavel.jsonlib.Json();
	
	public void init(){


	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		if(ds!=null)ds.println("Received http json request");
		ClientSession session;
	
		BufferedInputStream is = new BufferedInputStream(request.getInputStream(), 8*1024);
		InputStreamReader isr = new InputStreamReader(is);
		Object input = null;
	
		input = json.read(isr);
		
		if(!(input instanceof Map))throw new RuntimeException("Wrong input type, must be a json map.");	
	
		try{
			ds.println("Writing input to debug stream.");
			json.write(input, ds);
		}catch(IOException e){
			ds.println("Couldn't print the input for debugging due to an IOException.");
		}	

		try{
			session = SessionManager.getInstance().authenticate(input, request, response);
		}catch(Exception e){
			e.printStackTrace(ds);	
			return;
		}

	
		BufferedOutputStream os = new BufferedOutputStream(response.getOutputStream(), 8*1024);		

		PrintStream out = new PrintStream(os);

		Map<String, Object> output = new HashMap<String, Object>();
		try{
			Object result = JsonRequestHandler.getInstance().handle(session, (Map<String, Object>)input);
			if(result != null)output.put("payload", result);
			output.put("type", "success");	
		}catch(SQLException e){
			e.printStackTrace(ds);

			output.put("type", "fail");
			/*
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			PrintStream errorPrinter = new PrintStream(buffer);
			e.printStackTrace(errorPrinter);
			String errorPrintout = new String(buffer.toByteArray());
			*/
			String fullMessage = e.getMessage();
			int messageEndIndex = fullMessage.indexOf("\n");
			if(messageEndIndex == -1)messageEndIndex = fullMessage.length();
			String trimmedMessage = fullMessage.substring(7, messageEndIndex);
				
			output.put("payload", trimmedMessage);
		

		}catch(Exception e){

			e.printStackTrace(ds);
			output.put("type", "fail");
			output.put("payload", e.getMessage());

		}
		json.write(output, out);		

		os.flush();
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		doPost(request, response);
	}
	public void destroy(){
	
	}

	private ClientSession validate(String sessionId, String sessionKey)throws SQLException{		

		String query = String.format("SELECT * FROM validate(%d, %d);", sessionId, sessionKey);
		try(Statement stmt=DBC.getInstance().getConnection().createStatement(); ResultSet rs = stmt.executeQuery(query)){
			if(!rs.next())return null;
			ClientSession session = new ClientSession(sessionId, sessionKey);
			session.setAccountId(rs.getInt("accountid"));
			return true;
		}
	}
	private void syncCookies(ClientSession session, HttpServletRequest request, HttpServletResponse response){
		Cookie[] cookies = request.getCookies();
		String readSessionId = null;
		String readSessionKey = null;
		if(cookies != null){
			for(Cookie c : cookies){
				if(c.getName().equals("sessionid"))readSessionId = c.getValue();
				else if(e.getName().equals("sessionkey"))readSessionKey = c.getValue();
			}
		}
		if(
		readSessionId == null || 
		readSessionKey == null ||
		!readSessionId.equals(session.getSessionId()) ||
		!readSessionKey.equals(session.getSessionKey())
		){
			response.setCookie(new Cookie("sessionid", session.getSessionId()));
			response.setCookie(new Cookie("sessionKey", session.getSessionKey()));
		}
	}

}
