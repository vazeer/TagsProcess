import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class StartDownload {

	String DIRECTORY = "/home/vazeer/Desktop/imaggafiles/";
	
	String keyString = "";
	int limit = 10;
	String urldef;
	public StartDownload(String key){
		this.keyString = key;
		
		urldef = "https://www.googleapis.com/customsearch/v1?key=AIzaSyAYSqda3PrEYgX6tQZRwfErN6V13bBTNn0&cx=003028876438771006616:8mqp52bl6ai&searchType=image&q=" + key + "&dateRestrict=m[10]&imgType=photo";
	}
	
	
	
	public DataS readStream(String index){
		String url;
		if(index==null || index.length()==0){
			url = urldef;
		}else{
			url = urldef+"&start="+index;
		}
		url = url.replaceAll(" ", "%20");
		 URL oracle = null;
		try {
			oracle = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        BufferedReader in = null;
			try {
				in = new BufferedReader(
				new InputStreamReader(oracle.openStream()));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

	        String inputLine;
	        StringBuilder builder = new StringBuilder();
	        try {
				while ((inputLine = in.readLine()) != null)
					builder.append(inputLine);
				
			return	parseAndWriteinFile(builder.toString());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        
	        
	        try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		
	}



	private DataS parseAndWriteinFile(String s) {
		ArrayList<String> list = new ArrayList<>();
		JSONParser parser=new JSONParser();
	      
	      try{
	         Object obj = parser.parse(s);
	         JSONObject jobj = (JSONObject) obj;
	         JSONArray array = (JSONArray) jobj.get("items");
	         
	         JSONObject queries = (JSONObject) jobj.get("queries");
	         JSONArray nextPage = (JSONArray) queries.get("nextPage");
	         
	         JSONObject index = (JSONObject) nextPage.get(0);
	         
	        // System.out.print(index.toString());
	         long temp = (long) index.get("startIndex");
	         String startIndex = Long.toString(temp);
	         
	         for(int i=0;i<array.size();i++){
	        	 JSONObject item = (JSONObject) array.get(i);
	        	 
	        	 String urltemp = (String) item.get("link");
	        	 list.add(urltemp);
	        	 
	         }  
	         
	    return new DataS(list, startIndex);
	      }catch(ParseException pe){
	         System.out.println("position: " + pe.getPosition());
	         System.out.println(pe);
	      }
		return null;
		
	}



	public void writeInFile(String key, ArrayList<String> total) {
		
	//	"images={'cat':['url1', 'url2', 'url3'], 'dog': ['url1']}"
		
		String finalvalue = "images={'"+key+"':[";
		
		String first = "";
		for(String u:total){
			if(first.isEmpty()){
				first = "'"+u+"'";
			}else{
				first = first+","+"'"+u+"'";
			}
		}
		finalvalue = finalvalue+first+"}";
		
		
		
		 FileOutputStream fos = null;
	      File file;
	     
	      try {
	          //Specify the file path here
	    	  
	    	  key = key.replaceAll(" ", "_");
		  file = new File(DIRECTORY+key+".txt");
		  fos = new FileOutputStream(file);

	          /* This logic will check whether the file
		   * exists or not. If the file is not found
		   * at the specified location it would create
		   * a new file*/
		  if (!file.exists()) {
		     file.createNewFile();
		  }

		  /*String content cannot be directly written into
		   * a file. It needs to be converted into bytes
		   */
		  byte[] bytesArray = finalvalue.getBytes();

		  fos.write(bytesArray);
		  fos.flush();
		  System.out.println("File Written Successfully");
	       } 
	       catch (IOException ioe) {
		  ioe.printStackTrace();
	       } 
	       finally {
		  try {
		     if (fos != null) 
		     {
			 fos.close();
		     }
	          } 
		  catch (IOException ioe) {
		     System.out.println("Error in closing the Stream");
		  }
	       }
		
		
		
	}
	
	
	
	
	
}
