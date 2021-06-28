package com.mariuspaavel.typemaster;

import java.util.*;
import java.io.*;
import java.nio.*;

public class TextProvider{
	private static TextProvider instance;
	public static TextProvider getInstance() throws IOException{
		if(instance != null)return instance;
		else return instance = new TextProvider();
	}

	private String source;
	private TextProvider() throws IOException{
		try(BufferedInputStream stream = new BufferedInputStream(new FileInputStream(System.getProperty("user.home") + "/data/TRM.txt"))){
			StringBuilder sb = new StringBuilder();
			int index = 0;
			int c;
			while((c = stream.read()) != -1){
				sb.append((char)c);
				index++;
			}
			source = sb.toString();
		}
	}
	
	public String getText(){
		
		try{
			
			int from = (int)(1000 + Math.random() * source.length()-1000);
			while(!Character.isWhitespace(source.charAt(from)))from++;
			while(Character.isWhitespace(source.charAt(from)))from++;
			int to = from + 200;
			String cut = source.substring(from, to);	
			StringBuilder resultBuilder = new StringBuilder();
			boolean lastWs = false;
			for(int i = 0; i < cut.length(); i++){
				char c = cut.charAt(i);
				if(c < 32 || c > 126)c = ' ';
				boolean ws = Character.isWhitespace(c);
				if(!ws){
					resultBuilder.append(c);
				}else if(!lastWs){
					resultBuilder.append(' ');
				}
				lastWs = ws;
			}
			String result = resultBuilder.toString();
			if(result.length() > 100)return result;
			else return getText();

		}catch(IndexOutOfBoundsException e){
			return getText();
		}
	}
}
