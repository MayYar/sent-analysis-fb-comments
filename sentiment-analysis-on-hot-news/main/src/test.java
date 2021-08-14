

import java.io.BufferedReader;    
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


import facebook4j.internal.org.json.JSONException;
import facebook4j.internal.org.json.JSONObject;
 

public class test {

	public static void main(String[] args) throws IOException, JSONException {
		// TODO Auto-generated method stub
			
		URL urlns = null;
		//Reader reader = null;
		BufferedReader brns = null;

		HttpURLConnection httpConnns = null;
		
		urlns = new URL("http://graph.facebook.com/comments?id=" 
				+ URLEncoder.encode("http://www.setn.com/News.aspx?NewsID=125137&PageGroupID=6/", "UTF8"));
		httpConnns = (HttpURLConnection) urlns.openConnection();
		httpConnns.setConnectTimeout(3 * 60000);
		httpConnns.setReadTimeout(3 * 60000);
		brns = new BufferedReader(new InputStreamReader(httpConnns.getInputStream()));
		
		String strns;
		String[] alinearrayns = null;
		String[] alinearray2ns = null;
		
		
		
			
			JSONObject jsonns = new JSONObject(brns.readLine());
			strns=jsonns.toString().replace("\\n",",");
			alinearrayns=strns.split("\"message\":\"");
			alinearray2ns=strns.split("\"from\"");
			for(int i=1;i<alinearrayns.length;i++)
			 {
				String mixname;
				String mixid;
				mixname=alinearray2ns[i].substring(alinearray2ns[i].indexOf('n')).substring(7);
				mixid=alinearray2ns[i].substring(alinearray2ns[i].indexOf('i')).substring(5);
				System.out.println(mixname.substring(0,mixname.indexOf('"')));
				System.out.println(mixid.substring(0, mixid.indexOf('"')));
				//alinearrayns[i].substring(0,alinearrayns[i].indexOf('"'));
			    //System.out.println(alinearrayns[i].substring(0,alinearrayns[i].indexOf('"')));
				
			   
			 }
			
			//System.out.println(strns);
		
	
		
	
	}

}
