package fr.thumbnailsdb;

import java.util.*;


public class DuplicateFileList implements Iterable<DuplicateFileGroup>{

	TreeSet<DuplicateFileGroup> tree = new TreeSet<DuplicateFileGroup>(new Comparator<DuplicateFileGroup>() {
		// @Override
		public int compare(DuplicateFileGroup o1, DuplicateFileGroup o2) {
			return Double.compare(o2.getFileSize(), o1.getFileSize());
		}
	});
	
	
	public void add(DuplicateFileGroup dg) {
		tree.add(dg);
	}


	public Iterator<DuplicateFileGroup> iterator() {
		// TODO Auto-generated method stub
		return tree.iterator();
	}
	
	public DuplicateFileGroup[] toArray() {
		return tree.toArray(new DuplicateFileGroup[] {});
	}
	

	public DuplicateFileGroup getFirst() {
		return tree.first();
	}
	
	public Collection<DuplicateFileGroup> toCollection(int max) {
        ArrayList<DuplicateFileGroup> al = new ArrayList<DuplicateFileGroup>(max);
        Iterator<DuplicateFileGroup> it = tree.iterator();
        int i =0;
        while(it.hasNext() && i<max) {
            al.add(it.next());
            i++;
        }
		return al;
	}
	
}
