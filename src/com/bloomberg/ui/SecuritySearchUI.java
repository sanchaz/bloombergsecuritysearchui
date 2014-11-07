package com.bloomberg.ui;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.bloomberg.com.SecuritySearchClient;

import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public class SecuritySearchUI {

	protected Shell shlBloombergApiSecurity;
	private Text d_search;
	private Combo d_ykFilter;
	private Label d_maxResultsLabel;
	private Text d_maxResults;
	private Label d_hostLabel;
	private Text d_host;
	private Label d_portLabel;
	private Text d_port;
	
	private SecuritySearchClient ssc;
	private Table table;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SecuritySearchUI window = new SecuritySearchUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		ssc = new SecuritySearchClient();
		Display display = Display.getDefault();
		createContents();
		shlBloombergApiSecurity.open();
		shlBloombergApiSecurity.layout();
		while (!shlBloombergApiSecurity.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlBloombergApiSecurity = new Shell();
		shlBloombergApiSecurity.setSize(800, 640);
		shlBloombergApiSecurity.setText("Bloomberg API Security Search");
		
		d_search = new Text(shlBloombergApiSecurity, SWT.BORDER);
		d_search.setBounds(157, 41, 190, 25);
		
		Label d_seachLabel = new Label(shlBloombergApiSecurity, SWT.NONE);
		d_seachLabel.setFont(SWTResourceManager.getFont("Sans", 14, SWT.NORMAL));
		d_seachLabel.setBounds(10, 41, 141, 25);
		d_seachLabel.setText("Search Text");
		
		Button d_searchBtn = new Button(shlBloombergApiSecurity, SWT.NONE);
		d_searchBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				search();
			}
		});
		d_searchBtn.setBounds(705, 41, 83, 29);
		d_searchBtn.setText("Search");
		
		d_ykFilter = new Combo(shlBloombergApiSecurity, SWT.DROP_DOWN | SWT.BORDER);
		d_ykFilter.setBounds(579, 41, 120, 27);
		d_ykFilter.add(SecuritySearchClient.ALL);
		d_ykFilter.setText(SecuritySearchClient.ALL);
		d_ykFilter.add(SecuritySearchClient.CURRENCY);
		d_ykFilter.add(SecuritySearchClient.EQUITY);
		d_ykFilter.add(SecuritySearchClient.GOVERNMENT);
		d_ykFilter.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				System.out.println(arg0.toString());
				String filter = d_ykFilter.getText();
				if(filter == "") {
					return;
				}
				boolean doit = (filter.compareToIgnoreCase(SecuritySearchClient.ALL) == 0 ||
						        filter.compareToIgnoreCase(SecuritySearchClient.CURRENCY) == 0 ||
								filter.compareToIgnoreCase(SecuritySearchClient.EQUITY) == 0 ||
								filter.compareToIgnoreCase(SecuritySearchClient.GOVERNMENT) == 0);
				if(doit == false) {
					d_ykFilter.setText(SecuritySearchClient.ALL);
				}
			}
		});
		
		d_maxResultsLabel = new Label(shlBloombergApiSecurity, SWT.NONE);
		d_maxResultsLabel.setFont(SWTResourceManager.getFont("Sans", 14, SWT.NORMAL));
		d_maxResultsLabel.setBounds(353, 41, 120, 25);
		d_maxResultsLabel.setText("Max Results");
		
		d_maxResults = new Text(shlBloombergApiSecurity, SWT.BORDER);
		d_maxResults.setBounds(479, 41, 94, 25);
		d_maxResults.addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e) {
				String string = e.text;
				e.doit = testInteger(string);
			}
		});
		
		d_hostLabel = new Label(shlBloombergApiSecurity, SWT.NONE);
		d_hostLabel.setFont(SWTResourceManager.getFont("Sans", 14, SWT.NORMAL));
		d_hostLabel.setText("Host");
		d_hostLabel.setBounds(10, 10, 50, 25);
		
		d_host = new Text(shlBloombergApiSecurity, SWT.BORDER);
		d_host.setText("localhost");
		d_host.setBounds(66, 10, 172, 25);
		
		d_portLabel = new Label(shlBloombergApiSecurity, SWT.NONE);
		d_portLabel.setFont(SWTResourceManager.getFont("Sans", 14, SWT.NORMAL));
		d_portLabel.setBounds(244, 10, 50, 25);
		d_portLabel.setText("Port");
		
		d_port = new Text(shlBloombergApiSecurity, SWT.BORDER);
		d_port.setText("13377");
		d_port.setBounds(300, 10, 75, 25);
		d_port.addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e) {
				String string = e.text;
				e.doit = (d_port.getText().length() <= 4 || e.text.isEmpty()) &&
						 testInteger(string);
			}
		});
		
		TableViewer tableViewer = new TableViewer(shlBloombergApiSecurity, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table = tableViewer.getTable();
		table.setBounds(10, 94, 778, 511);
		
		TableColumn tc1 = new TableColumn(table, SWT.LEFT);
	    TableColumn tc2 = new TableColumn(table, SWT.LEFT);
	    tc1.setText("Security");
	    tc2.setText("Description");
	    tc1.setWidth(300);
	    tc2.setWidth(478);
	    table.setHeaderVisible(true);
		
		TableItem item1 = new TableItem(table, SWT.NONE);
	    item1.setText(new String[] { "Tim", "Hatton", "Kentucky" });

		TableItem item2 = new TableItem(table, SWT.NONE);
	    item2.setText(new String[] { "Yo", "Hatton", "Kentucky" });

		TableItem item3 = new TableItem(table, SWT.NONE);
	    item3.setText(new String[] { "Hey", "Hatton", "Kentucky" });
	}
	
	private boolean testInteger(String number) {
		
		char[] chars = new char[number.length()];
		number.getChars(0, chars.length, chars, 0);
		for (int i = 0; i < chars.length; i++) {
			if (!(chars[i] >= '0' && chars[i] <= '9')) {
				return false;
			}
		}
		return true;
	}
	
	private void search() {
		if(d_host.getText().isEmpty() || d_port.getText().isEmpty()) {
			return;
		}
		String host = d_host.getText();
		int port = Integer.parseInt(d_port.getText());
		JSONObject ret = ssc.sendRequest(host, port, createJSONObject());
		if(ret == null) {
			//spit error
			return;
		}
		System.out.println("GOT: " + ret.toJSONString());
		fillTable(ret);
	}
	
	private void fillTable(JSONObject result) {
		
		if(!result.containsKey("result")) {
			return;
		}
		
		JSONArray array = (JSONArray) result.get("result");
		
		table.removeAll();
		
		for(Object ob : array) {
			JSONObject item = (JSONObject) ob;
			String security;
			String desc = "";
			if(!item.containsKey("security")) {
				continue;
			}
			security = (String) item.get("security");
			if(item.containsKey("description")) {
				desc = (String) item.get("description");
			}
			
			TableItem tbItem = new TableItem(table, SWT.NONE);
			tbItem.setText(new String[] {security, desc});
		}
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject createJSONObject() {
		
		JSONObject obj = new JSONObject();
		JSONObject request = new JSONObject();
		obj.put("request", request);
		
		String yk;
		
		switch(d_ykFilter.getText()) {
			case SecuritySearchClient.GOVERNMENT:
				yk = SecuritySearchClient.YK_GOVERNMENT;
			case SecuritySearchClient.EQUITY:
				yk = SecuritySearchClient.YK_EQUITY;
			case SecuritySearchClient.CURRENCY:
				yk = SecuritySearchClient.YK_CURRENCY;
			case SecuritySearchClient.ALL:
			default:
				yk = SecuritySearchClient.YK_ALL; 
		}
		request.put("yk_filter", yk);
		request.put("query_string", d_search.getText());
		if(d_maxResults.getText().isEmpty()) {
			request.put("max_results", 10);
		} else {
			request.put("max_results", Integer.parseInt(d_maxResults.getText()));
		}
		
		return obj;
	}
}
