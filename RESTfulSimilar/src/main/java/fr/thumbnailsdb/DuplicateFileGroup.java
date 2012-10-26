package fr.thumbnailsdb;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DuplicateFileGroup {
	long fileSize;
	@XmlElement
	ArrayList<String> al = new ArrayList<String>();

	public DuplicateFileGroup() {
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

    public Collection<String> getParentFolderList() {
        ArrayList<String> l = new ArrayList<String>();
        for (String s : al) {
            File file = new File(s);
            //File parentDir = file.getParentFile(); // to get the parent dir
            String parentDirName = file.getParent(); // to get the parent dir name
            System.out.println("Folder name is " + parentDirName);
            l.add(parentDirName);
        }
        return l;
    }


}