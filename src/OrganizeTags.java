import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.readfiles.ReadCategoryFiles;
import com.uploadTagsToserver.Utils;

public class OrganizeTags {

	public static void main(String[] args) {
		OrganizeTags oganiser = new OrganizeTags();

		GetInfo info = new GetInfo();

		// to add pygrams words we grabbed
	    //  oganiser.processPygramsData(info);
		

		 oganiser.processRefData(info);
		 
		 

//		oganiser.writeFinalTagsToUpload(
//				info,
//				"/home/vazeer/Desktop/TagCategories/categoryReferenceFile.json",
//				"/home/vazeer/Desktop/TagCategories/Stemmed_Tagsv5+Tagsv5.txt",
//				"/home/vazeer/Desktop/TagCategories/uploadable_final_tags.json");

	}

	private void processPygramsData(GetInfo info) {

		/**
		 * @arg1 shop data path
		 * @arg2 pygrams extracted from title and description
		 * @arg3 stemmed pygrams file for reference
		 */
		info.addTagsFromPygrams(
				"/home/vazeer/Desktop/TagProcess/snapdeal/snapdeal_processed_data.jl",
				"/home/vazeer/Desktop/TagProcess/snapdeal/snapdeal_content_tagged.txt",
				"/home/vazeer/Desktop/TagCategories/Stemmed_Tagsv5+Tagsv5.txt",
				"snapdeal");
	}

	private void processRefData(GetInfo info) {
		/**
		 * @arg1 after adding pygrams file path
		 * @arg2 reference tags files to process
		 */

		info.organiseTags(
				"/home/vazeer/Desktop/TagProcess/snapdeal/snapdeal_after_addingPygrams.jl",
				"/home/vazeer/Desktop/TagCategories/categoryReferenceFile.json",
				"/home/vazeer/Desktop/TagCategories/Stemmed_Tagsv5+Tagsv5.txt",
				"snapdeal");

	}

	private void writeFinalTagsToUpload(GetInfo info, String preparedPath,
			String stemmedPath, String finalPath) {
		Map<String, String> tags_filters = info
				.readFilterTagsFromPygrams(stemmedPath);
		System.out.println("Key Set First: " + tags_filters.keySet().size());
		Map<String, String> tags_filtersReverse = info
				.readFilterTagsFromPygramsReverse(stemmedPath);
		System.out.println("Key Set Second: "
				+ tags_filtersReverse.keySet().size());

		Map<String, ArrayList<String>> genderMap = new HashMap<String, ArrayList<String>>();
		JSONParser parser = new JSONParser();

		String referenceData = Utils
				.readFileDataToString("/home/vazeer/Desktop/TagCategories/categoryReferenceFile.json");
		JSONArray category = null;
		try {
			category = (JSONArray) parser.parse(referenceData);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Map<String, String> map = ReadCategoryFiles.getOrderedMap();
		System.out.println("map size:" + map.keySet().size());

		Map<String, JSONObject> tags_Ogj_List = new HashMap<String, JSONObject>();

		for (int i = 0; i < category.size(); i++) {
			JSONObject catObj = (JSONObject) category.get(i);

			for (String name : map.keySet()) {
				if (catObj.containsKey(name)) {
					JSONArray jsonList = (JSONArray) catObj.get(name);
					ArrayList<JSONObject> objectList = Utils
							.getJsonObjectList(jsonList);
					Map<String,JSONObject> newlist = new HashMap<String,JSONObject>();
					for (JSONObject object : objectList) {
						String tagString = (String) object.get("tagString");
						String stemmed = tags_filtersReverse.get(tagString);
						String finalTag = tags_filters.get(stemmed);
						if (finalTag != null) {
							
							object.put("tagString", finalTag);
							//System.out.println("expected------"+finalTag);
							if(tags_Ogj_List.get(finalTag)==null){
							newlist.put(finalTag,object);
							tags_Ogj_List.put(finalTag, object);
							}
						} else {
							System.err.println("No Tag");
							System.exit(0);
						}
					}

					JSONArray newArray = new JSONArray();
					newArray.addAll(newlist.values());
					System.out.println("==: "+name+" first list: "+jsonList.size());
					catObj.remove(name);
					catObj.put(name, newArray);
					System.out.println("==: "+name+" second list: "+newArray.size());
				}

				if (catObj.containsKey("Gender")) {
					JSONArray jsonList = (JSONArray) catObj.get("Gender");
					for (int k = 0; k < jsonList.size(); k++) {
						JSONObject genObj = (JSONObject) jsonList.get(k);
						if (genObj.containsKey("Women")) {
							JSONArray womenArray = (JSONArray) genObj
									.get("Women");
							genderMap.put("Women",
									Utils.getArrayList(womenArray));
						} else if (genObj.containsKey("Men")) {
							JSONArray menArray = (JSONArray) genObj.get("Men");
							genderMap.put("Men", Utils.getArrayList(menArray));
						} else if (genObj.containsKey("Boys")) {
							JSONArray boysArray = (JSONArray) genObj
									.get("Boys");
							genderMap
									.put("Boys", Utils.getArrayList(boysArray));
						} else if (genObj.containsKey("Girls")) {
							JSONArray girlsArray = (JSONArray) genObj
									.get("Girls");
							genderMap.put("Girls",
									Utils.getArrayList(girlsArray));
						} else if (genObj.containsKey("Juniors")) {
							JSONArray uniorsArray = (JSONArray) genObj
									.get("Juniors");
							genderMap.put("Juniors",
									Utils.getArrayList(uniorsArray));
						} else if (genObj.containsKey("Unisex")) {
							JSONArray uniorsArray = (JSONArray) genObj
									.get("Unisex");
							genderMap.put("Unisex",
									Utils.getArrayList(uniorsArray));
						}
					}
				}

			}

		}

		Utils.writeInFile(category.toJSONString(), finalPath);

	}

}
