public class OrganizeTags {

	public static void main(String[] args) {
		GetInfo info = new GetInfo();

		// to add pygrams words we grabbed
	//	 processPygramsData(info);

		processRefData(info);

	}

	private static void processPygramsData(GetInfo info) {

		/**
		 * @arg1 shop data path
		 * @arg2 pygrams extracted from title and description
		 * @arg3 stemmed pygrams file for reference
		 */
		info.addTagsFromPygrams(
				"/home/vazeer/Desktop/TagProcess/jabong/jabong_processed_data.jl",
				"/home/vazeer/Desktop/TagProcess/jabong/jabong_content_tagged.txt",
				"/home/vazeer/Desktop/TagCategories/Stemmed_Tagsv2+Tagsv2.txt",
				"jabong");
	}

	private static void processRefData(GetInfo info) {
		/**
		 * @arg1 after adding pygrams file path
		 * @arg2 reference tags files to process
		 */

		info.organiseTags(
				"/home/vazeer/Desktop/TagProcess/jabong/jabong_after_addingPygrams.jl",
				"/home/vazeer/Desktop/TagCategories/categoryReferenceFile.json",
				"jabong");

	}

}
