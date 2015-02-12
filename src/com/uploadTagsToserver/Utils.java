package com.uploadTagsToserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Utils {
	public static String readFileDataToString(String path) {
		BufferedReader br = null;
		String everything = null;
		try {
			br = new BufferedReader(new FileReader(path));
			StringBuilder sb = new StringBuilder();
			String line = null;
			try {
				line = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			everything = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return everything;
	}

	public static ArrayList<String> getArrayList(JSONArray jsonArray) {
		ArrayList<String> list = new ArrayList<String>();
		if (jsonArray != null) {
			int len = jsonArray.size();
			for (int i = 0; i < len; i++) {
				list.add(jsonArray.get(i).toString());
			}
		}
		return list;
	}

	public static ArrayList<JSONObject> getJsonObjectList(JSONArray jsonArray) {
		ArrayList<JSONObject> list = new ArrayList<JSONObject>();
		if (jsonArray != null) {
			int len = jsonArray.size();
			for (int i = 0; i < len; i++) {
				 JSONObject obj = (JSONObject) jsonArray.get(i);
				 if(obj!=null)
				list.add(obj);
			}
		}
		return list;
	}

	public static void generatePygramsInputFile(
			Map<String, ArrayList<String>> tagList,
			Map<String, ArrayList<String>> genderMap, String writehere) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("id" + "               " + "Text" + "\n");

		for (String key : tagList.keySet()) {
			ArrayList<String> listOfTags = tagList.get(key);

			if (listOfTags != null)
				buffer.append(doBygramsString(listOfTags, key));
		}

		for (String key : genderMap.keySet()) {
			ArrayList<String> listOfValues = genderMap.get(key);
			if (listOfValues != null) {
				buffer.append(doBygramsString(listOfValues, "Gender " + key));
			}
		}
		writeInFile(buffer.toString(), writehere);
	}

	public static String doBygramsString(ArrayList<String> list, String category) {
		StringBuffer buffer = new StringBuffer();
		String id_cat = category.replace(" ", "_");
		for (String s : list) {

			String ids = s.replace(" ", "_");
			buffer.append("\"" + id_cat + "|" + ids + "\"");

			buffer.append("\t");

			buffer.append("\"" + s + "\"");

			buffer.append("\n");
		}
		return buffer.toString();
	}

	public static void writeInFile(String text, String path) {

		FileOutputStream fos = null;
		File file;

		try {
			// Specify the file path here

			file = new File(path);
			fos = new FileOutputStream(file);

			/*
			 * This logic will check whether the file exists or not. If the file
			 * is not found at the specified location it would create a new file
			 */
			if (!file.exists()) {
				file.createNewFile();
			}

			/*
			 * String content cannot be directly written into a file. It needs
			 * to be converted into bytes
			 */
			byte[] bytesArray = text.getBytes();

			fos.write(bytesArray);
			fos.flush();
			System.out.println("File Written Successfully");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {

				if (fos != null) {
					fos.close();
				}
			} catch (IOException ioe) {
				System.out.println("Error in closing the Stream");
			}
		}

	}
}
