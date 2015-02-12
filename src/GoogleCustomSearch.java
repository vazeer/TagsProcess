import java.util.ArrayList;

public class GoogleCustomSearch {

	public static void main(String[] args) {
		int limit = 50;
		String key = "Wedding dress";

		ArrayList<String> total = new ArrayList<String>();
		
				
		StartDownload download = new StartDownload(key);
		String index ="";

		while (total.size() < limit) {
			
			if(total.size()==0){
				DataS objres = download.readStream("1");
			 total.addAll(objres.list);
			 index = objres.startindex;
			}
			else{
				DataS objres2 = download.readStream(index);
				total.addAll(objres2.list);
				 index = objres2.startindex;
		     }	
			
			
		}
		
		for(String value:total){
			System.out.println("url:  "+value);
		}
		
		download.writeInFile(key,total);
		

	}

}
