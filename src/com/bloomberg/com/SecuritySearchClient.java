package com.bloomberg.com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class SecuritySearchClient {

	public static final String YK_ALL = "";
	public static final String ALL = "All";
	public static final String YK_CURRENCY = "YK_FILTER_CRNY";
    public static final String CURRENCY = "Currency";
    public static final String YK_EQUITY = "YK_FILTER_EQTY";
    public static final String EQUITY = "Equity";
    public static final String YK_GOVERNMENT = "YK_FILTER_GOVT";
    public static final String GOVERNMENT = "Government";
	
	@SuppressWarnings("unchecked")
	public JSONObject sendRequest(String host, int port, JSONObject request) {
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(host, port), 5000);
			Writer out = new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			out.write(request.toJSONString());
			out.flush();
			System.out.println("wrote " + request.toJSONString());
			Reader in = new BufferedReader(
					new InputStreamReader(socket.getInputStream(), "UTF-8"));
			JSONObject ret = read(in);
			out.close();
			in.close();
			socket.close();
			return ret;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject read(Reader in) {
		try {
			while(true)
			{
				System.out.println("Waiting to read");
				int c = in.read();
				System.out.println("Read" + c);
				char ch = (char)c;
				if(c == -1)
				{
					return null;
				}
				if(ch != '{')
				{
					//if the first char isn't { we ignore it
					continue;
				}
				int brackets = 1;
				StringBuffer sb = new StringBuffer();
				sb.append(ch);
				while(brackets > 0) {

					c = in.read();
					ch = (char)c;

					if(c == -1) {
						return null;
					}
					if(ch == '}') {
						--brackets;
					}
					if(ch == '{') {
						++brackets;
					}

					sb.append(ch);
				}
				//we got a JSON object
				JSONObject jobj = (JSONObject) JSONValue.parse(sb.toString());
				System.out.println("Returning " + jobj.toJSONString());
				return jobj;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
