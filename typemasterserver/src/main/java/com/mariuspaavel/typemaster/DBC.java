package com.mariuspaavel.typemaster;

import com.mariuspaavel.jsonlib.*;
import java.util.*;
import java.io.*;
import java.sql.*;
import static java.sql.Types.*;

public class DBC{
	private static DBC instance;
	public static DBC getInstance()throws SQLException{
		if(instance == null)return instance = new DBC();
		else return instance;
	}
	private Connection c;	

	private PrintStream ds = System.out;
	
	public Connection getConnection(){
		return c;
	}

	private DBC(){
		String dbPassword = null;
		try(FileInputStream fs = new FileInputStream("/opt/dapp/dbpassword")){
			StringBuilder sb = new StringBuilder();
			int c;
			while((c = fs.read()) != -1)sb.append((char)c);
			dbPassword = sb.toString().split("\n", 2)[0];
			ds.println("---------PASSWORD---------");
			ds.println(dbPassword);
			ds.println("--------------------------");
		}catch(IOException e){
			e.printStackTrace(ds);
			throw new RuntimeException("Couldn't read DB credentials from the filesystem.");
		}
		try{
			Class.forName("org.postgresql.Driver");
			final String url = "jdbc:postgresql://localhost:5432/dapp";
			c = DriverManager.getConnection(url, "tomcat", dbPassword);
		}catch(Exception e){
			e.printStackTrace(ds);
			throw new RuntimeException("Failed to connect to the database.");
		}
	}
	
	public Object callFunction(String name, Object... args)throws SQLException{
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT * FROM ");
		queryBuilder.append(name);
		queryBuilder.append('(');
		for(int i = 0; i < args.length; i++){
			if(args[i] instanceof String)writeStringLiteral(queryBuilder, args[i]);
			else queryBuilder.append(args[i].toString());
			if(i != args.length-1)queryBuilder.append(", ");
		}
		queryBuilder.append(");");
		String sql = queryBuilder.toString();
		if(ds != null)ds.println(sql);
		try(Statement stmt = c.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
			if(!rs.next())return null;
			return rs.getObject(name);
		}
	}
	
	private void writeStringLiteral(StringBuilder sb, Object input){
		sb.append('\'');
		sb.append(input.toString());
		sb.append('\'');
	}	


	public List<Map<String, Object>> query(String sql) throws SQLException{
		if(ds != null)ds.println(sql);
		try(Statement stmt = c.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
			ResultSetMetaData rsmd = rs.getMetaData();
			int numOfColumns = rsmd.getColumnCount();
			List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
			while(rs.next()){
				Map<String, Object> row = new HashMap<String, Object>();
				for(int i = 0; i < numOfColumns; i++){
					String columnName = rsmd.getColumnName(i);
					Object o = rs.getObject(columnName);
					
					row.put(columnName, o);
				}
				rows.add(row);
			}
			return rows;
		}
	}
	public Map<String, Object> query1Row(String sql)throws SQLException{
		if(ds != null)ds.println(sql);
		try(Statement stmt = c.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
			ResultSetMetaData rsmd = rs.getMetaData();
			int numOfColumns = rsmd.getColumnCount();
			if(!rs.next())return null;
			Map<String, Object> row = new HashMap<String, Object>();
			for(int i = 0; i < numOfColumns; i++){
				String columnName = rsmd.getColumnName(i);
				Object o = rs.getObject(columnName);
				row.put(columnName, o);
			}
			return row;
		}
	}
}
