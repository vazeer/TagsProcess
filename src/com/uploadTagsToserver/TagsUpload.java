package com.uploadTagsToserver;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.omg.CORBA.CTX_RESTRICT_SCOPE;

import com.readfiles.ReadCategoryFiles;

public class TagsUpload {

	public static void main(String[] args) {
		Map<String, ArrayList<String>> genderMap = new HashMap<String, ArrayList<String>>();
		JSONParser parser = new JSONParser();

		String referenceData = Utils
				.readFileDataToString("/home/vazeer/Desktop/TagCategories/uploadable_final_tags.json");
		JSONArray category = null;
		try {
			category = (JSONArray) parser.parse(referenceData);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Map<String, String> map = ReadCategoryFiles.getOrderedMap();

		Map<String, ArrayList<String>> tags_List = new HashMap<String, ArrayList<String>>();

		for (int i = 0; i < category.size(); i++) {
			JSONObject catObj = (JSONObject) category.get(i);

			for (String name : map.keySet()) {
				if (catObj.containsKey(name)) {
					JSONArray jsonList = (JSONArray) catObj.get(name);
					ArrayList<String> typeList = Utils.getArrayList(jsonList);
					tags_List.put(name, typeList);
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
		
		Utils.generatePygramsInputFile(tags_List, genderMap,"/home/vazeer/Desktop/TagCategories/pygramsTagsInput_set1.txt");
		uploadTags(tags_List);

	}

	private static void uploadTags(Map<String, ArrayList<String>> tags_List) {
		for (String key : tags_List.keySet()) {
			System.out.println("name:  " + key + " List:"
					+ tags_List.get(key).size());
		}
	}

	private static void uploadTag(String tagCategory, String tagString,
			String tagSubCategory) {
		ClientProductTag clTag = new ClientProductTag();
		clTag.setTagCategory(tagCategory);
		clTag.setTagString(tagString);
		clTag.setTagSubCategory(tagSubCategory);

	}
	


}
