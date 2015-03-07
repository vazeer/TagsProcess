package com.readfiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ReadFiles {

	// remove duplicates
	public Map<String, String> duplicateRemoval;

	public ReadFiles() {
		duplicateRemoval = new HashMap<String, String>();
	}

	public ArrayList<String> getListOfFiles(String directory) {

		ArrayList<String> listofFilepaths = new ArrayList<String>();
		File folder = new File(directory);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				listofFilepaths.add(listOfFiles[i].getAbsolutePath());
				// System.out.println("File " +
				// listOfFiles[i].getAbsolutePath());
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}
		return listofFilepaths;
	}

	public String getData(String filePath) {
		BufferedReader br = null;
		String everything = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
			StringBuilder sb = new StringBuilder();
			String line = null;
			try {
				line = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (line != null) {
				sb.append(line);
				// sb.append(System.lineSeparator());
				line = br.readLine();
			}
			everything = sb.toString();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return everything;
	}

	public ArrayList<String> getDataLines(String filePath) {
		ArrayList<String> allLines = new ArrayList<String>();
		BufferedReader br = null;
		String everything = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
			StringBuilder sb = new StringBuilder();
			String line = null;
			try {
				line = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (line != null) {
				sb.append(line);
				// sb.append(System.lineSeparator());
				allLines.add(line);
				line = br.readLine();
				
			}
			everything = sb.toString();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return allLines;
	}

	public static boolean isNumeric(String str) {
		try {
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public JSONObject parseFiles(ArrayList<String> files, String category) {
		JSONObject obj = new JSONObject();
		JSONArray array = new JSONArray();
		Map<String, String> filterDuplicates = new HashMap<String, String>();

		for (String path : files) {
			if (path.contains("~")) {
				continue;
			}
			System.out.println("Category: " + category + " Path:" + path);

			String data = getData(path);
			data = data.toString();
			JSONParser parser = new JSONParser();
			try {
				JSONArray arrayofWords = (JSONArray) parser.parse(data);

				for (int i = 0; i < arrayofWords.size(); i++) {
					JSONObject tagobj = (JSONObject) arrayofWords.get(i);
					String tag = (String) tagobj.get("Text");

					if (tag == null) {
						tag = (String) tagobj.get("Column 4");
					}
					if (tag == null) {
						tag = (String) tagobj.get("Column");
					}

					if (tag == null)
						tag = (String) tagobj.get("Word");

					if (tag != null) {
						String[] values = tag.split(" ");
						for (String s : values) {
							if (isNumeric(s)) {
								// System.out.println("**********:"+tag);
								tag = tag.replace(s, "");
								// System.out.println("********** RESSSSS: "+
								// tag);
							}
						}
						if (tag.contains("₹") || tag.contains("%") ) {
							System.err.println("Not using this tag: "+tag);
							continue;
						}
						
						if(tag.isEmpty())
							continue;
						
						
					}

					String tagSubValue = (String) tagobj.get("SubText");

					if (tagSubValue != null) {
						tagSubValue = tagSubValue.trim();
						tagSubValue = tagSubValue.replaceAll("\\(.*?\\)", "")
								.trim();
					}

					tag = tag.replaceAll("\\(.*?\\)", "").trim();

					if (tag != null && !tag.isEmpty()) {

						//convert first letter to upper case
						tag = Character.toUpperCase(tag.charAt(0)) + tag.substring(1);
					
						
						if (tagSubValue == null) {
							if (tag.contains("Swimsuits"))
								tagSubValue = "Swimsuits";
							else if (tag.contains("Pants"))
								tagSubValue = "Pants";
							else if (tag.contains("Sarees"))
								tagSubValue = "Saree";
						}

						if (filterDuplicates.get(tag) == null)
							filterDuplicates.put(tag, tagSubValue);

					}

					// System.out.println("@@@@@@@@@@@@@@@"+tag);

				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println(path);
			}
		}

		System.out.println(category + "*********************"
				+ filterDuplicates.size() + " ==: "
				+ duplicateRemoval.keySet().size());
		for (String tagValue : filterDuplicates.keySet()) {
			if (duplicateRemoval.get(tagValue) == null) {
				duplicateRemoval.put(tagValue, tagValue);
				String tagSubValue = filterDuplicates.get(tagValue);

				JSONObject tagObj = new JSONObject();
				tagObj.put("tagString", tagValue);
				tagObj.put("tagSubCategory", tagSubValue);

				array.add(tagObj);

			}
		}
		obj.put(category, array);

		return obj;
	}

	public JSONObject parseGendeFiles(ArrayList<String> files, String category) {
		JSONObject obj = new JSONObject();
		JSONArray array = new JSONArray();
		HashMap<String, JSONArray> map = new HashMap<String, JSONArray>();
		for (String path : files) {
			String data = getData(path);
			data = data.toString();
			JSONParser parser = new JSONParser();
			try {
				JSONArray arrayofWords = (JSONArray) parser.parse(data);
				for (int i = 0; i < arrayofWords.size(); i++) {
					JSONObject tagobj = (JSONObject) arrayofWords.get(i);
					String tag = (String) tagobj.get("Tag");
					JSONArray supportlist = (JSONArray) tagobj
							.get("SuppotTags");
					map.put(tag, supportlist);
					System.out.println("=====" + tag + "   " + path);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println(path);
			}
		}
		for (String key : map.keySet()) {
			JSONArray value = map.get(key);
			if (value != null) {
				JSONObject jobj = new JSONObject();
				jobj.put(key, value);
				array.add(jobj);
			}

		}
		obj.put(category, array);

		return obj;
	}

}
