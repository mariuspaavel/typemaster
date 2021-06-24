package com.mariuspaavel.typemaster;
import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

@WebServlet("/test")
public class Test extends HttpServlet{	

	
	public void init(){


	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
	new PrintStream(response.getOutputStream()).println("hello");
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
	doPost(request, response);
	}
}
