package com.uploadTagsToserver;

public class ProductTagBase {

	public String getTagString() {
		return tagString;
	}

	public void setTagString(String tagString) {
		this.tagString = tagString;
	}

	public String getTagCategory() {
		return tagCategory;
	}

	public void setTagCategory(String tagCategory) {
		this.tagCategory = tagCategory;
	}

	public String getTagSubCategory() {
		return tagSubCategory;
	}

	public void setTagSubCategory(String tagSubCategory) {
		this.tagSubCategory = tagSubCategory;
	}

	String tagString;

	String tagCategory;
	String tagSubCategory;
}
