package labelPrinterPackage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class httpPost {

	public httpPost(String username, String password, String url, String shippingNumber) throws Exception{

		DefaultHttpClient httpclient = new DefaultHttpClient();

		String basic_auth = new String(Base64.encodeBase64((username + ":" + password).getBytes()));

		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Authorization", "Basic " + basic_auth);

		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("username", username));
		nvps.add(new BasicNameValuePair("password", password));
		nvps.add(new BasicNameValuePair("id", shippingNumber));
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		HttpResponse response = httpclient.execute(httpPost);

		try {

			System.out.println(response.getStatusLine());

			HttpEntity entity = response.getEntity();
			System.out.println(response);
			if(response.getStatusLine().getStatusCode() == 302){
				redirected = true;
			}else{
				
				//Only try and read shipping code if login was successful
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				String line = "";

				while ((line = rd.readLine()) != null) {
					//Parse our JSON response               
					JSONParser j = new JSONParser();
					JSONObject o = (JSONObject)j.parse(line);

					if(!o.containsKey("label")){
						JSONArray array = (JSONArray)o.get("labels_decoded");
						decoded = (String) array.get(0);
					}else{
						labelFailed = true;
					}
				}
			}    
			// and ensure it is fully consumed
			EntityUtils.consume(entity);
		}
		finally {
			httpPost.releaseConnection();
		}
	}

	public String getDecoded(){
		return decoded;
	}

	public boolean wasRedirected(){
		return redirected;
	}
	
	public boolean labelFailed(){
		return labelFailed;
	}

	/*Private Instance Variables*/
	private String decoded;
	private boolean redirected;
	private boolean labelFailed;
}
