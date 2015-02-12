package com.readfiles;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.uploadTagsToserver.Utils;

public class ReadCategoryFiles {

	public static void main(String[] args) {

		// to prepare category reference file by reading the category files we
		// defined
		readAllFiles("/home/vazeer/Desktop/TagCategories/categoryReferenceFile.json");

		/** send sub cat value */
		// tempClass(true);

	}

	private static void tempClass(boolean subcatvalue) {
		ReadFiles rf = new ReadFiles();
		ArrayList<String> files = rf
				.getListOfFiles("/home/vazeer/Desktop/TagCategories/DATAA/TagsWithSubCatset11/");
		for (String s : files) {

			ArrayList<String> lines = rf.getDataLines(s);
			System.out.println("processing for: " + s + " lines count: "
					+ lines.size());
			JSONArray arr = new JSONArray();

			for (String dt : lines) {
				if (dt == null)
					continue;

				dt = dt.trim();
				JSONObject obj = new JSONObject();
				obj.put("Text", dt);

				// temporary work
				if (subcatvalue) {
					File fll = new File(s);
					String subName = fll.getName();
					subName = subName.replace("Type", "");
					subName = subName.replace("Style", "");
					System.out.println("@@@@@@@@:  " + subName);
					obj.put("SubText", subName);
				}
				// temporary work

				arr.add(obj);

			}

			String str = arr.toJSONString();
			str = str.replace("\\t", "");
			str = str.replace("\\", "");

			Utils.writeInFile(str,
					"/home/vazeer/Desktop/TagCategories/DATAA/TagsWithSubCatset22/"
							+ (new File(s)).getName() + ".json");
		}
	}

	private static void readAllFiles(String writeTagsFinalPath) {
		ReadFiles rf = new ReadFiles();

		JSONArray finalarray = new JSONArray();

		Map<String, String> map = getOrderedMap();

		for (String tagCategory : map.keySet()) {

			String tagCategoryPath = map.get(tagCategory);
			if (tagCategoryPath != null) {

				ArrayList<String> typePaths = rf
						.getListOfFiles(tagCategoryPath);

				JSONObject typeObj = rf.parseFiles(typePaths, tagCategory);

				finalarray.add(typeObj);

			}
		}

		ArrayList<String> genderPaths = rf
				.getListOfFiles("/home/vazeer/Desktop/TagCategories/Gender/");
		JSONObject genderObj = rf.parseGendeFiles(genderPaths, "Gender");

		finalarray.add(genderObj);

		Utils.writeInFile(finalarray.toJSONString(), writeTagsFinalPath);

	}

	public static Map<String, String> getOrderedMap() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		String[] orderedList = { "Apparel Provider", "Apparel Back Style",
				"Apparel Neckline", "Apparel HemLine", "Apparel Waistline",
				"Apparel Sleeve Type", "Apparel Type", "Occasion",
				"Apparel Brand", "Pattern", "Apparel Accents",
				"Material", "Color", "Apparel Description" };
		File dir = new File(
				"/home/vazeer/Desktop/TagCategories/TagCategoriesOnly/");
		String[] dirs = dir.list();
		ArrayList<String> lst = new ArrayList<String>();

		for (String orderdS : orderedList) {
			map.put(orderdS, dir + "/" + orderdS + "/");
		}

		for (String s : dirs) {
			lst.add(s);
			System.out.println(s);
		}

		if (!(orderedList.length == lst.size())) {
			System.err.print("new Files are added" + orderedList.length + "   "
					+ lst.size());
			System.exit(0);
		}
		return map;

	}
}
