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

		Map<String, Object> output = null;
		BufferedOutputStream os = null;
		PrintStream out = null;
		try{
			os = new BufferedOutputStream(response.getOutputStream(), 8*1024);		
			out = new PrintStream(os);
		}catch(Exception e){
			if(ds!=null){
				ds.println("Failed request");
				e.printStackTrace(ds);
			}
			return;
		}

		try{

			SessionManager sm = SessionManager.getInstance();
	
			BufferedInputStream is = new BufferedInputStream(request.getInputStream(), 8*1024);
			InputStreamReader isr = new InputStreamReader(is);
			Map<String, Object> input = null;
	
			input = (Map<String, Object>)json.read(isr);
		
			ClientSession session = null;	
			try{	
				int sessionId = (int)((Long)input.get("sessionid")).longValue();
				String sessionKey = (String)input.get("sessionkey");
				session = SessionManager.getInstance().validate(sessionId, sessionKey);
			}catch(Exception e){
				e.printStackTrace(ds);
				throw new AuthFailException();
			}
			if(session == null){
				ds.println("Session was not found.");
				throw new AuthFailException();
			}
				
			Object result = JsonRequestHandler.getInstance().handle(session, input);
			
			output = new HashMap<>();
			if(result != null)output.put("payload", result);
			output.put("type", "success");
		
		}catch(AuthFailException e){
			output = new HashMap<>();
			output.put("type", "authfail");
		}
		catch(SQLException e){
			e.printStackTrace(ds);

			output = new HashMap<>();			
			output.put("type", "fail");
			
			String fullMessage = e.getMessage();
			int messageEndIndex = fullMessage.indexOf("\n");
			if(messageEndIndex == -1)messageEndIndex = fullMessage.length();
			String trimmedMessage = fullMessage.substring(7, messageEndIndex);
				
			output.put("payload", trimmedMessage);
		}		
		catch(Exception e){
			e.printStackTrace(ds);
			output = new HashMap<>();
			output.put("type", "fail");
			output.put("payload", e.getMessage());
		}
		try{
			json.write(output, out);		
			os.flush();
		}catch(Exception e){return;}

	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		doPost(request, response);
	}
	public void destroy(){
	
	}
}
