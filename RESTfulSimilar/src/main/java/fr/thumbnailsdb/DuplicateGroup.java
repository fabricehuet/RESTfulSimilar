package fr.thumbnailsdb;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DuplicateGroup {
	long fileSize;
	@XmlElement
	ArrayList<String> al = new ArrayList<String>();

	public DuplicateGroup() {
		super();
		// this.individualSize = individualSize;
	}

	public void add(long size, String path) {
		this.fileSize = size;
		al.add(path);
	}

	public long getFileSize() {
		return fileSize;
	}

	public int size() {
		return al.size();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Iterator iterator = al.iterator(); iterator.hasNext();) {
			sb.append(iterator.next() + "\n");
		}

		return sb.toString();
	}
}