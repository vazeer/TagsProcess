import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.omg.CORBA.PUBLIC_MEMBER;

import com.readfiles.ReadCategoryFiles;
import com.uploadTagsToserver.Utils;

public class GetInfo {

	public void updateTags(String dataPath, String tagsPath, String shopname) {

		File file = new File(dataPath);
		String dir = file.getParent() + "/";

		Map<String, ArrayList<String>> map = readTagsData(tagsPath);
		JSONParser parser = new JSONParser();
		BufferedReader bufferedReader = null;
		String line = null;
		BufferedWriter bw = null;
		try {
			File writeHerePath = new File(dir + shopname + "_processed_data.jl");
			if (!writeHerePath.exists()) {
				writeHerePath.createNewFile();
			}
			FileWriter fw = new FileWriter(writeHerePath);
			bw = new BufferedWriter(fw);

			bufferedReader = new BufferedReader(new FileReader(dataPath));
			line = bufferedReader.readLine();
			while (line != null) {
				Object obj = parser.parse(line);
				JSONObject item = (JSONObject) obj;
				String pid = (String) item.get("product_item_num");
				JSONArray tags_data = (JSONArray) item.get("product_tags");

				ArrayList<String> existing = map.get(pid.trim());

				Set<String> newList = new HashSet<String>();

				if (existing != null) {
					newList.addAll(existing);
				}

				if (tags_data != null)
					for (int m = 0; m < tags_data.size(); m++) {
						String string = (String) tags_data.get(m);

						if (existing == null)
							newList.add(string);

						if (existing != null) {
							boolean val = true;
							for (String str : existing) {
								if (string.equalsIgnoreCase(str)) {
									val = false;
								}
							}
							if (val)
								newList.add(string);
						}
					}

				JSONArray finalArray = new JSONArray();
				finalArray.addAll(newList);
				item.put("product_tags", finalArray);
				System.out.println("===Fist List  :: (" + pid + ")"
						+ tags_data.toJSONString());
				System.out.println("===Second List:: (" + pid + ")"
						+ finalArray.toJSONString());
				bw.write(item.toJSONString() + "\n");
				line = bufferedReader.readLine();
			}

			System.out.print("written dat here: " + writeHerePath);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// String parsedData = getParsedData(dataPath);

		/*
		 * try { Object obj = parser.parse(parsedData); JSONArray array =
		 * (JSONArray) obj; for (int i = 0; i < array.size(); i++) { JSONObject
		 * item = (JSONObject) array.get(i); String pid = (String)
		 * item.get("product_item_num"); JSONArray urltemp = (JSONArray)
		 * item.get("product_tags");
		 * 
		 * ArrayList<String> existing = map.get(pid.trim());
		 * System.out.println("&&&&&&&&&&&:: " + existing);
		 * 
		 * Set<String> newList = new HashSet<String>();
		 * 
		 * if (existing != null) { newList.addAll(existing); }
		 * 
		 * if (urltemp != null) for (int m = 0; m < urltemp.size(); m++) {
		 * String string = (String) urltemp.get(m);
		 * 
		 * if (existing == null) newList.add(string);
		 * 
		 * if (existing != null) { boolean val = true; for (String str :
		 * existing) { if (string.equalsIgnoreCase(str)) { val = false; } } if
		 * (val) newList.add(string); } }
		 * 
		 * item.put("product_tags", new ArrayList<String>(newList));
		 * System.out.println("$$$$$ pos: " + i + "   " + item.toJSONString());
		 * 
		 * bw.write(item.toJSONString());
		 * 
		 * }
		 */

	}

	@SuppressWarnings("unchecked")
	public void organiseTags(String path, String referenceFilePath,
			String taggedPath, String shopname) {
		
		Map<String, String> tags_filters = readFilterTagsFromPygrams(taggedPath);
		Map<String, String> tags_filtersReverse = readFilterTagsFromPygramsReverse(taggedPath);
		
		File tagsnotMatched = new File(path);
		String dir = tagsnotMatched.getParent() + "/";
		String droppedWordspath = dir + shopname + "_ref_dropped.txt";

		String comparisonPath = dir + shopname + "_comparison.txt";
		String datatoUpload = dir + shopname + "_upload_to_server.jl";

		ArrayList<String> providerList = new ArrayList<String>();

		Map<String, ArrayList<String>> genderMap = new HashMap<String, ArrayList<String>>();

		JSONParser parser = new JSONParser();

		String referenceData = Utils.readFileDataToString(referenceFilePath);
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
					ArrayList<JSONObject> typeList = Utils
							.getJsonObjectList(jsonList);
					ArrayList<String> listOfStrings = new ArrayList<String>();
					for (JSONObject obj : typeList) {
						String string = (String) obj.get("tagString");
						listOfStrings.add(string);
					}
					tags_List.put(name, listOfStrings);
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
		// JSONArray tagsorganised = new JSONArray();

		BufferedReader br = null;
		BufferedWriter droppedWordsBW = null, comparisonBW = null, uploadBW = null;
		FileWriter droppedWordsFW, comparisonFW, uploadFW;
		try {
			File filePath = new File(droppedWordspath);
			if (!filePath.exists()) {
				filePath.createNewFile();
			}
			droppedWordsFW = new FileWriter(filePath.getAbsoluteFile());
			droppedWordsBW = new BufferedWriter(droppedWordsFW);

			File comp = new File(comparisonPath);
			if (!comp.exists()) {
				comp.createNewFile();
			}
			comparisonFW = new FileWriter(comp.getAbsoluteFile());
			comparisonBW = new BufferedWriter(comparisonFW);

			File uploadFile = new File(datatoUpload);
			if (!uploadFile.exists()) {
				uploadFile.createNewFile();
			}
			uploadFW = new FileWriter(uploadFile.getAbsoluteFile());
			uploadBW = new BufferedWriter(uploadFW);

			br = new BufferedReader(new FileReader(path));
			String line = null;
			try {
				line = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			HashMap<String, String> tofilterdups = new HashMap<String, String>();
			while (line != null) {
				// /
				Object obj = null;
				try {
					try {
						obj = parser.parse(line);
					} catch (Exception e) {
						e.printStackTrace();
						System.err.println("**: " + line);
					}

					if (obj == null) {
						line = br.readLine();
						continue;
					}

					JSONObject item = (JSONObject) obj;
					String item_id = (String) item.get("product_item_num");
					JSONArray item_tags_array = (JSONArray) item
							.get("product_tags");

					String source_site = (String) item.get("source_site");

					comparisonBW.write("Product ID:" + item_id
							+ " Before Reference Run Tags count:"
							+ item_tags_array.size() + " values:"
							+ item_tags_array.toJSONString() + "\n");
					Set<String> newOnlyTagsMatched = new HashSet<String>();

					for (String shop : providerList) {
						if (source_site.contains(shop)) {
							JSONObject tag_object = new JSONObject();
							tag_object.put("tagString", shop);
							tag_object.put("tagCategory", "Apparel Provider");
							tag_object.put("tagSubCategory", null);
							// tagsorganised.add(tag_object);
							newOnlyTagsMatched.add(shop);
						}
					}
					System.out.println("processing for: "
							+ item_tags_array.toJSONString());

					boolean logs = false;
					for (int j = 0; j < item_tags_array.size(); j++) {
						String item_tag = (String) item_tags_array.get(j);
						item_tag = item_tag.trim();
						String[] comma_tags = item_tag.split(",");

						for (String tag : comma_tags) {
							tag = tag.trim();
							// System.out
							// .println("========================== start for: "
							// + tag);
							// if (tofilterdups.get(tag) != null) {
							//
							// continue;
							// } else {
							// tofilterdups.put(tag, tag);
							// }

							boolean ifbreaked = false;
							for (String keyy : tags_List.keySet()) {

								ArrayList<String> listToProcess = new ArrayList<String>();
								listToProcess = tags_List.get(keyy);

								TagProces tagObj1 = processForList(
										listToProcess, tag, keyy, logs);
								if (tagObj1 != null && tagObj1.list.size() > 0) {
									// tagsorganised.add(tagObj1);
									newOnlyTagsMatched.addAll(tagObj1.list);
								}
								if (tagObj1.tagRemaining.isEmpty()) {
									ifbreaked = true;
									break;
								} else {
									tag = tagObj1.tagRemaining;
								}
							}

							if (ifbreaked) {
								continue;
							}

							TagProces tagObj8 = processForList(providerList,
									tag, "Apparel Provider", logs);
							if (tagObj8 != null && tagObj8.list.size() > 0) {
								// tagsorganised.add(tagObj1);
								newOnlyTagsMatched.addAll(tagObj8.list);
							}
							if (tagObj8.tagRemaining.isEmpty()) {
								continue;
							} else {
								tag = tagObj8.tagRemaining;
							}

							// for gender
							ArrayList<String> genderList = new ArrayList<String>();
							for (String key : genderMap.keySet()) {
								ArrayList<String> listValues = genderMap
										.get(key);
								genderList.addAll(listValues);

							}

							TagProces tagObj10 = processForList(genderList,
									tag, "Gender", logs);
							if (tagObj10 != null && tagObj10.list.size() > 0) {
								// tagsorganised.add(tagObj1);

								ArrayList<String> lst = new ArrayList<String>();
								for (String matchedGender : tagObj10.list) {
									for (String key : genderMap.keySet()) {
										if (genderMap.get(key).contains(
												matchedGender)) {
											lst.add(key);
										}
									}

								}
								newOnlyTagsMatched.addAll(lst);
							}
							if (tagObj10.tagRemaining.isEmpty()) {
								continue;
							} else {
								tag = tagObj10.tagRemaining;
							}

							// tagsorganised.add(tag_object);
							System.err.println("*****" + tag
									+ "******************");
							droppedWordsBW.write(tag + "\n");

						}

					}
					JSONArray jsonAraay = new JSONArray();
					jsonAraay.addAll(newOnlyTagsMatched);
					System.out.println("process result for: "
							+ jsonAraay.toJSONString());

					item.put("product_tags", jsonAraay);

					Set<String> set = getUplodableTags(newOnlyTagsMatched,tags_filters,tags_filtersReverse);
					JSONArray jsonAraayFinal = new JSONArray();
					jsonAraayFinal.addAll(set);
					item.put("product_tags_final", jsonAraayFinal);

					uploadBW.write(item.toJSONString() + "\n");
					comparisonBW.write("Product ID:" + item_id
							+ " After Reference Run Tags count:"
							+ jsonAraay.size() + " values:"
							+ jsonAraay.toJSONString() + "\n");

					comparisonBW.write("Product ID:" + item_id
							+ " After Reference Run Tags count FINAL:"
							+ jsonAraayFinal.size() + " values:"
							+ jsonAraayFinal.toJSONString() + "\n\n");

				} catch (Exception e) {
					e.printStackTrace();
				}
				// //// /
				line = br.readLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				droppedWordsBW.close();
				comparisonBW.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("data written succesfully check here: \n"
				+ droppedWordspath + "\n" + comparisonPath + "\n"
				+ datatoUpload);

	}

	private Set<String> getUplodableTags(Set<String> newOnlyTagsMatched,
			Map<String, String> tags_filters,Map<String, String> tags_filtersReverse) {
		

		tags_filters.put("unisex", "Unisex");
		tags_filters.put("juniour", "Juniours");
		tags_filters.put("men", "Men");
		tags_filters.put("boi", "Boys");
		tags_filters.put("girl", "Girls");
		tags_filters.put("women", "Women");

	

		Set<String> myList = new HashSet<String>();

		for (String tag : newOnlyTagsMatched) {
			String stemmed = tags_filtersReverse.get(tag);
			if (stemmed == null) {
				System.err.println("prepare for stemmed for this tag: " + tag);
				System.exit(0);
			} else {
				String uplodable = tags_filters.get(stemmed);
				if (uplodable != null) {
					myList.add(uplodable);
				} else {
					System.err.println("No Value for this stemmed: " + stemmed);
					System.exit(0);
				}

			}
		}

		return myList;
	}

	public void addTagsFromPygrams(String productsDataPath,
			String tagsDataPath, String filterPath, String shop) {

		File productsData = new File(productsDataPath);
		String dir = productsData.getParent() + "/";

		String afteraddingPygramstagsWritehere = dir + shop
				+ "_after_addingPygrams.jl";
		String dropListwriteHere = dir + shop + "_pygrams_dropped_.txt";

		// String data = readFileDataToString(productsDataPath);
		Map<String, ArrayList<String>> tagsdata = readTagsFromPygrams(tagsDataPath);
		Map<String, String> tags_filters = readFilterTagsFromPygrams(filterPath);

		FileWriter fw, fwSec;
		BufferedWriter bw, bwSec;
		BufferedReader brReader = null;
		try {
			File file = new File(dropListwriteHere);
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);

			File filePath = new File(afteraddingPygramstagsWritehere);
			if (!filePath.exists()) {
				filePath.createNewFile();
			}

			fwSec = new FileWriter(filePath.getAbsoluteFile());
			bwSec = new BufferedWriter(fwSec);

			JSONParser parser = new JSONParser();

			brReader = new BufferedReader(new FileReader(productsDataPath));
			String line = null;
			line = brReader.readLine();
			while (line != null) {
				Object obj = parser.parse(line);
				JSONObject item = (JSONObject) obj;
				String item_id = (String) item.get("product_item_num");
				JSONArray item_tags_array = (JSONArray) item
						.get("product_tags");
				ArrayList<String> ls = tagsdata.get(item_id);
				// System.out.println("@@@@aIDD: " + item_id+"  "+ls);
				if (ls != null) {
					for (String str : ls) {
						String value = tags_filters.get(str);
						if (value != null) {
							item_tags_array.add(value);
							System.out.println("@@@@adding tags: " + value);
						} else {
							bw.write(str + "\n");
						}
					}
				}

				bwSec.write(item.toJSONString() + "\n");
				line = brReader.readLine();
			}

			// writeInFile(array.toJSONString(),
			// afteraddingPygramstagsWritehere);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				brReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("data written succesfully check here: \n"
				+ afteraddingPygramstagsWritehere + "\n" + dropListwriteHere);

	}

	public TagProces processForList(ArrayList<String> typelist, String tag,
			String category, boolean printout) {
		if (printout)
			System.out.println("=Input: " + tag + "   category: " + category);
		String[] listOfTags = tag.split(" ");
		if (listOfTags.length == 1) {
			TagProces tagP = new TagProces();
			for (String type : typelist) {
				if (tag.equalsIgnoreCase(type)) {
					tagP.list.add(type);
					tagP.tagRemaining = "";
					if (printout)
						System.out.println("Result11: " + tagP.list
								+ "    Remaining: " + tagP.tagRemaining);
					return tagP;
				}
			}
			tagP.tagRemaining = tag;
			if (printout)
				System.out.println("Fail11: " + tagP.list + "    Remaining: "
						+ tagP.tagRemaining);
			return tagP;
		} else {
			TagProces tagP = new TagProces();

			for (String type : typelist) {
				String[] typeArray = type.split(" ");
				ArrayList<TagMatch> dsList = new ArrayList<GetInfo.TagMatch>();
				for (String val : typeArray) {
					TagMatch tgm = new TagMatch();
					tgm.string = val;
					tgm.pos = -1;
					dsList.add(tgm);
				}

				for (TagMatch typeWord : dsList) {
					for (int i = 0; i < listOfTags.length; i++) {
						String tagWord = listOfTags[i].trim();
						if (typeWord.string.equalsIgnoreCase(tagWord)) {
							typeWord.pos = i;
						}
					}
				}

				boolean notadd = false;
				for (TagMatch typeWord : dsList) {
					String str = typeWord.string;
					if (typeWord.pos < 0) {
						notadd = true;
					}
				}
				if (!notadd)
					tagP.list.add(type);

				/*
				 * if (positions.size() > 0 && positions.size() ==
				 * typeArray.length) { int p1 = positions.get(0); int p2 =
				 * positions.get(positions.size() - 1); int count =
				 * typeArray.length;
				 * 
				 * if ((p2 - p1) == (count - 1)) {
				 * 
				 * tagP.list.add(type);
				 * 
				 * } }
				 */

			}

			if (tagP.list.size() > 0) {
				String s = "";
				for (String str : listOfTags) {
					str = str.trim();
					boolean val = false;
					for (String captured : tagP.list) {
						String[] words = captured.split(" ");
						for (String word : words) {
							if (word.equalsIgnoreCase(str)) {
								val = true;
							}
						}
					}
					if (!val) {
						if (s.isEmpty()) {
							s = str;
						} else {
							s = s + " " + str;
						}
					}

				}
				tagP.tagRemaining = s;

				if (printout)
					System.out.println("Result22: " + tagP.list
							+ "    Remaining: " + tagP.tagRemaining);

				return tagP;

			} else {
				tagP.tagRemaining = tag;
				if (printout)
					System.out.println("Fail22: " + tagP.list
							+ "    Remaining: " + tagP.tagRemaining);
				return tagP;
			}

		}

		/*
		 * String tagToReturn = tag; TagProces tagP = new TagProces(); for
		 * (String type : typelist) { if (tagToReturn.contains(type)) {
		 * tagP.list.add(type); tagToReturn =
		 * tagToReturn.replace(type.toLowerCase(), ""); tagToReturn =
		 * tagToReturn.replace(type, ""); tagToReturn = tagToReturn.trim(); } }
		 * tagP.tagRemaining = tagToReturn.trim();
		 */

	}

	private class TagProces {
		ArrayList<String> list = new ArrayList<String>();
		String tagRemaining;
	}

	private class TagMatch {
		String string;
		int pos;
	}

	public ArrayList<String> filedata(String path) {

		ArrayList<String> list_of_brands = new ArrayList<>();

		// Open the file
		FileInputStream fstream = null;
		try {
			fstream = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;

		// Read File Line By Line
		try {
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				strLine = strLine.trim();
				String[] brands = strLine.split("	");

				if (brands.length >= 2) {
					String brnd = brands[1];
					list_of_brands.add(brnd);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Close the input stream
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list_of_brands;
	}

	public Map<String, ArrayList<String>> readTagsFromPygrams(String path) {
		Map<String, ArrayList<String>> list_of_brands = new HashMap<String, ArrayList<String>>();
		FileInputStream fstream = null;
		BufferedReader br = null;
		String strLine;
		try {
			fstream = new FileInputStream(path);
			br = new BufferedReader(new InputStreamReader(fstream));
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				strLine = strLine.trim();
				String[] brands = strLine.split("\\t");
				if (brands.length >= 2) {
					String id = brands[0];
					String tagString = brands[1];

					tagString = tagString.replace("\"", "");
					id = id.replace("\"", "");

					String[] listoftags = tagString.split(" ");

					ArrayList<String> tagsfinal = new ArrayList<String>();
					for (String s : listoftags) {
						s = s.replace("_", " ");
						tagsfinal.add(s);
					}

					String[] idlist = id.split("_");
					String idfinal = idlist[idlist.length - 1];

					// System.out.println("++++++++++adding tags ID: " +
					// idfinal+"  "+tagsfinal);
					if (list_of_brands.get(idfinal) == null) {
						list_of_brands.put(idfinal, tagsfinal);
					} else {
						list_of_brands.get(idfinal).addAll(tagsfinal);
					}

				}
			}
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
		return list_of_brands;
	}

	public Map<String, String> readFilterTagsFromPygrams(String path) {
		Map<String, String> filter_List = new HashMap<String, String>();
		FileInputStream fstream = null;
		BufferedReader br = null;
		String strLine;
		try {
			fstream = new FileInputStream(path);
			br = new BufferedReader(new InputStreamReader(fstream));
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				strLine = strLine.trim();
				String[] brands = strLine.split("\\t");
				if (brands.length >= 2) {
					String key = brands[0];
					String value = brands[1];

					key = key.replace("\"", "");
					value = value.replace("\"", "");

					filter_List.put(key, value);
				}
			}
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
		return filter_List;
	}

	public Map<String, String> readFilterTagsFromPygramsReverse(String path) {
		Map<String, String> filter_List = new HashMap<String, String>();
		FileInputStream fstream = null;
		BufferedReader br = null;
		String strLine;
		try {
			fstream = new FileInputStream(path);
			br = new BufferedReader(new InputStreamReader(fstream));
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				strLine = strLine.trim();
				String[] brands = strLine.split("\\t");
				if (brands.length >= 2) {
					String key = brands[0];
					String value = brands[1];

					key = key.replace("\"", "");
					value = value.replace("\"", "");

					filter_List.put(value, key);
				}
			}
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
		return filter_List;
	}

	public void parseAndWriteinFile(String path, String writeAt) {

		StringBuffer buffer = new StringBuffer();
		buffer.append("id" + "               " + "Title"
				+ "                Description \n");
		JSONParser parser = new JSONParser();

		BufferedReader br = null;
		System.out.println(path);
		try {
			br = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {

			String line = null;
			try {
				line = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			while (line != null) {
				line = line.toString();

				JSONObject item = (JSONObject) parser.parse(line);

				JSONArray urltemp = (JSONArray) item.get("image_urls");

				JSONArray arraydescription = (JSONArray) item
						.get("description");

				String objid = (String) item.get("product_item_num");

				String objTitle = (String) item.get("product_title");

				String source = (String) item.get("source_site");

				String sourcename = source.replace(".", "_");

				String desc = "";
				for (int j = 0; j < arraydescription.size(); j++) {
					if (desc.isEmpty()) {
						desc = (String) arraydescription.get(j);

					} else {
						desc = desc + " " + (String) arraydescription.get(j);

					}
				}

				buffer.append("\"" + sourcename + "_" + objid + "\"");

				buffer.append("\t");

				buffer.append("\"" + objTitle + "\"");

				buffer.append("\t");

				buffer.append("\"" + desc + "\"");

				buffer.append("\n");

				line = br.readLine();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		Utils.writeInFile(buffer.toString(), writeAt);
		System.out.println("completed");

	}

	public String getParsedData(String path) {
		BufferedReader br = null;
		String everything = null;
		try {
			br = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
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

			// everything = everything.replace("}", "},");
			// everything = "["+everything+"]";

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

	/*
	 * public void writeInFile(String text, String path) {
	 * 
	 * FileOutputStream fos = null; File file;
	 * 
	 * try { // Specify the file path here
	 * 
	 * file = new File(path); fos = new FileOutputStream(file);
	 * 
	 * 
	 * This logic will check whether the file exists or not. If the file is not
	 * found at the specified location it would create a new file
	 * 
	 * if (!file.exists()) { file.createNewFile(); }
	 * 
	 * 
	 * String content cannot be directly written into a file. It needs to be
	 * converted into bytes
	 * 
	 * byte[] bytesArray = text.getBytes();
	 * 
	 * fos.write(bytesArray); fos.flush();
	 * System.out.println("File Written Successfully"); } catch (IOException
	 * ioe) { ioe.printStackTrace(); } finally { try {
	 * 
	 * if (fos != null) { fos.close(); } } catch (IOException ioe) {
	 * System.out.println("Error in closing the Stream"); } }
	 * 
	 * }
	 */

	public Map<String, ArrayList<String>> readTagsData(String path) {
		Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		String everything = getParsedData(path);

		JSONParser parser = new JSONParser();

		Set<String> lss = new HashSet<>();

		try {
			Object obj = parser.parse(everything);
			JSONArray array = (JSONArray) obj;

			for (int i = 0; i < array.size(); i++) {
				JSONObject item = (JSONObject) array.get(i);

				JSONArray idlist = (JSONArray) item.get("tag_product_ids");

				JSONArray taglist = (JSONArray) item.get("tag");

				for (int d = 0; d < taglist.size(); d++) {
					String tag = (String) taglist.get(d);

					for (int k = 0; k < idlist.size(); k++) {
						String pId = (String) idlist.get(k);
						lss.add(pId);
						if (pId != null && !pId.isEmpty()) {
							pId = pId.trim();
							if (map.get(pId) == null) {
								ArrayList<String> valueSet = new ArrayList<String>();
								valueSet.add(tag);
								map.put(pId, new ArrayList<String>(valueSet));
							} else {
								map.get(pId).add(tag);
							}
						}
					}
				}
				System.out.println("$$$$$$$$$$$$$$$$$$: " + taglist.size()
						+ " size:" + idlist.size());
			}

			System.out.println("total ids we got:" + map.keySet().size());

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return map;
	}

}
