package fr.thumbnailsdb;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: fhuet
 * Date: 25/10/12
 * Time: 16:47
 * To change this template use File | Settings | File Templates.
 */
public class DuplicateFolderList {
    Map<String, DuplicateFolderGroup> folderWithDuplicates = new HashMap<String, DuplicateFolderGroup>();

    public DuplicateFolderList() {

    }

    public void addOrIncrement(DuplicateFileGroup dg) {
        ArrayList<String> folderList = (ArrayList<String>) dg.getParentFolderList();
        if (folderList.size() > 1) {
            try {
               Collections.sort(folderList);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("NPE for " + folderList);
            }
            for (int i = 0; i <  folderList.size() - 1; i++) {
                for (int j = i + 1; j < folderList.size(); j++) {
                    // System.out.println(folderList.get(i) + " <-> " + folderList.get(j) );
                    String couple = folderList.get(i) + " <-> " + folderList.get(j);
                    //   System.out.println(" Looking for key " + couple);
                    DuplicateFolderGroup dfg = folderWithDuplicates.get(couple);
                    if (dfg != null) {
                        //     System.out.println(" Key found, incrementing");
                        //folderWithDuplicates.put(couple, dfg + 1);
                        dfg.increase();
                    } else {
                        //   System.out.println(" Key not found, adding");
                        folderWithDuplicates.put(couple, new DuplicateFolderGroup(folderList.get(i), folderList.get(j)));
                    }
                }
            }
        }
    }


    public Collection asSortedCollection() {
        TreeSet<DuplicateFolderGroup> list = new TreeSet<DuplicateFolderGroup>(new Comparator<DuplicateFolderGroup>() {
            //	@Override
            public int compare(DuplicateFolderGroup o1, DuplicateFolderGroup o2) {
                return Double.compare(o2.occurences, o1.occurences);
            }
        });
        for (DuplicateFolderGroup d : folderWithDuplicates.values()) {
            list.add(d);
        }
        ArrayList<DuplicateFolderGroup> al = new ArrayList<DuplicateFolderGroup>(list.size());
        Iterator<DuplicateFolderGroup> it = list.iterator();
        int i =0;
        while(it.hasNext()) {
            al.add(it.next());

        }
        return al;
    }


}
