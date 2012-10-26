package fr.thumbnailsdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
        Collections.sort(folderList);
        for (int i = 0; i < folderList.size() - 1; i++) {
            for (int j = i + 1; j < folderList.size(); j++) {
                // System.out.println(folderList.get(i) + " <-> " + folderList.get(j) );
                String couple = folderList.get(i) + " <-> " + folderList.get(j);
                System.out.println(" Looking for key " + couple);
                DuplicateFolderGroup dfg = folderWithDuplicates.get(couple);
                if (dfg != null) {
                    System.out.println(" Key found, incrementing");
                    //folderWithDuplicates.put(couple, dfg + 1);
                    dfg.increase();
                } else {
                    System.out.println(" Key not found, adding");
                    folderWithDuplicates.put(couple, new DuplicateFolderGroup(folderList.get(i) ,folderList.get(j)));
                }
            }
        }
    }


}
