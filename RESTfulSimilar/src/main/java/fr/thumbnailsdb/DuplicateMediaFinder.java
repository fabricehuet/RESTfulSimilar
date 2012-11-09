package fr.thumbnailsdb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class DuplicateMediaFinder {

    protected ThumbStore thumbstore;

    protected DuplicateFileList duplicateFileList;

    public DuplicateMediaFinder(ThumbStore c) {
        this.thumbstore = c;
    }

    public ResultSet findDuplicateMedia() {
        return thumbstore.getDuplicatesMD5();
    }

    public void prettyPrintDuplicate(ResultSet r) {
        DuplicateFileList list = computeDuplicateSets(r);
        for (DuplicateFileGroup dg : list) {
            System.out.println(dg.fileSize + " (" + dg.fileSize * (dg.size() - 1) + " to save) ");
            System.out.println(dg);
        }
    }

    public void prettyPrintDuplicateFolder(ResultSet r) {
       Collection<DuplicateFolderGroup> map = computeDuplicateFolderSets(r).asSortedCollection();
        Iterator<DuplicateFolderGroup> it = map.iterator();
            //System.out.println(k + "  have " + map.get(k).occurences + " common files");
        while (it.hasNext()) {
        DuplicateFolderGroup df = it.next();
           System.out.println(df.occurences + " " + df.folder1 +   "  " + df.folder2);
        }
//        for (DuplicateFileGroup dg : tree) {
//            System.out.println(dg.fileSize + " (" + dg.fileSize * (dg.size() - 1) + " to save) ");
//            System.out.println(dg);
//        }

    }

    public DuplicateFileList computeDuplicateSets(ResultSet r) {
        if (duplicateFileList !=null ) {
            return duplicateFileList;
        }
        duplicateFileList = new DuplicateFileList();
        DuplicateFileGroup dg = new DuplicateFileGroup();
        String currentMd5 = "";
        try {
            while (r.next()) {
                String md5 = r.getString("md5");
                if (md5 != null) {
                    if (md5.equals(currentMd5)) {
                        // add to current group
                        dg.add(r.getLong("size"), r.getString("path"));
                    } else {
                        if (dg.size() > 1) {
                            duplicateFileList.add(dg);
                        }
                        dg = new DuplicateFileGroup();
                        dg.add(r.getLong("size"), r.getString("path"));
                        currentMd5 = md5;

                    }
                }
            }
            if (dg.size() > 1) {
                duplicateFileList.add(dg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return duplicateFileList;
    }

    public DuplicateFolderList computeDuplicateFolderSets(ResultSet r) {
        DuplicateFileList list = new DuplicateFileList();
        DuplicateFileGroup dg = new DuplicateFileGroup();
        String currentMd5 = "";
        //The table to maintain the tree of folder-couples and the
        //the number of common files they have
         DuplicateFolderList dfl = new DuplicateFolderList();
        try {
            while (r.next()) {
                String md5 = r.getString("md5");
                if (md5 != null) {
                    if (md5.equals(currentMd5)) {
                        // add to current group
                        dg.add(r.getLong("size"), r.getString("path"));
                    } else {
                        if (dg.size() > 1) {
                            list.add(dg);
                            dfl.addOrIncrement(dg);
                        }
                        dg = new DuplicateFileGroup();
                        dg.add(r.getLong("size"), r.getString("path"));
                        currentMd5 = md5;

                    }
                }
            }
            if (dg.size() > 1) {
                //ok we have found a tree of duplicate files
                //let's add their parent folder to the tree
                //first compute the tree of folders
               dfl.addOrIncrement(dg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dfl;
    }

    // TODO : save result for subsequent requests
    public String prettyHTMLDuplicate(ResultSet r, int max) {
        DuplicateFileList list = computeDuplicateSets(r);
        System.out.println("DuplicateMediaFinder.prettyHTMLDuplicate() " + max);
        String result = "";
        int i = 0;
        for (DuplicateFileGroup dg : list) {
            result += dg.fileSize + " (" + dg.fileSize * (dg.size() - 1) + " to save)\n";
            result += dg + "\n";
            i++;
            if (i > max) {
                break;
            }
        }
        System.out.println("DuplicateMediaFinder.prettyHTMLDuplicate() " + result);
        return result;
    }


}
