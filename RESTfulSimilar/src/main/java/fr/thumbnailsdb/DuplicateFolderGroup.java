package fr.thumbnailsdb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: fhuet
 * Date: 25/10/12
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
public class DuplicateFolderGroup {
    @XmlElement
    String folder1;
    @XmlElement
    String folder2;
    @XmlElement
     int occurences;
    @XmlElement
    int totalSize;


     public DuplicateFolderGroup(String f1, String f2) {
        folder1=f1;
        folder2=f2;
         occurences=1;
     }

    public void increase(){
       occurences++;
       //System.out.println("occurences was "+ (occurences-1) + " now " +occurences);
    }

    public void addSize(int s){
        totalSize++;
    }

}
