package fr.thumbnailsdb;

/**
 * Created with IntelliJ IDEA.
 * User: fhuet
 * Date: 25/10/12
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */
public class DuplicateFolderGroup {
    String folder1;
    String folder2;

     int occurences;


     public DuplicateFolderGroup(String f1, String f2) {
        folder1=f1;
        folder2=f2;
         occurences=1;
     }

    public void increase(){
       occurences++;
       System.out.println("occurences was "+ (occurences-1) + " now " +occurences);
    }

}
