package fr.thumbnailsdb;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;


public class DuplicateList implements Iterable<DuplicateGroup>{

	TreeSet<DuplicateGroup> list = new TreeSet<DuplicateGroup>(new Comparator<DuplicateGroup>() {
		// @Override
		public int compare(DuplicateGroup o1, DuplicateGroup o2) {
			return Double.compare(o2.getFileSize(), o1.getFileSize());
		}
	});
	
	
	public void add(DuplicateGroup dg) {
		list.add(dg);
	}


	public Iterator<DuplicateGroup> iterator() {
		// TODO Auto-generated method stub
		return list.iterator();
	}
	
	public DuplicateGroup[] toArray() {
		return list.toArray(new DuplicateGroup[] {});
	}
	

	public DuplicateGroup getFirst() {
		return list.first();
	}
	
	public Collection<DuplicateGroup> toCollection() {
		return list;
	}
	
}
