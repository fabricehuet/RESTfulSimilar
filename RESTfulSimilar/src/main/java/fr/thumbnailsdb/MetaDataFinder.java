package fr.thumbnailsdb;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.GpsDirectory;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: fhuet
 * Date: 13/11/12
 * Time: 11:25
 * To change this template use File | Settings | File Templates.
 */
public class MetaDataFinder {

    public void printTags(File file) {
        //File jpegFile = new File("myImage.jpg");
        Metadata metadata = null;
        try {
            metadata = ImageMetadataReader.readMetadata(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        for (Directory directory : metadata.getDirectories()) {
//            for (Tag tag : directory.getTags()) {
//                System.out.println(tag);
//            }
//        }


    }

    public boolean hasGPSData(File file) {
         return getLatLong(file) !=null;
    }


    public double[] getLatLong(File file)  {
        Metadata metadata = null;

        try {
            metadata = ImageMetadataReader.readMetadata(file);
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
        Directory directory = metadata.getDirectory(GpsDirectory.class);
        if (directory != null && directory.getTags().size()>2) {
//         //   System.out.println("---- " + file + " ----");
//            for (Tag tag : directory.getTags()) {
//                System.out.println(tag);
//            }
            double lat = getAsDecimalDegree(directory.getDescription(2));
            double lon = getAsDecimalDegree(directory.getDescription(4));
//            System.out.println(lat+", " +lon );
            return new double[] {lat, lon};
        } else {

            return null;
        }

    }


    public double getAsDecimalDegree(String co) {

        String [] temp = null;

        double decimaldms=0;
        temp = co.split("[°]|[\"]|[\']" );
        for (int i = 0 ; i < temp.length ; i++) {
//                System.out.println("degree : "+temp[0]);
//                System.out.println("minutes : "+temp[1]);
//                System.out.println("second : "+temp[2]);

            String deg = temp[0] ;
            double ndeg = Double.parseDouble(deg);
            String min = temp[1] ;
            double nmin = Double.parseDouble(min);
            String sec = temp[2] ;
            double nsec = Double.parseDouble(sec);
            if (ndeg <0 ) {
            decimaldms = -(-ndeg+(nmin/60)+(nsec/3600));
            } else {
                decimaldms = (ndeg+(nmin/60)+(nsec/3600));
            }
        }
            return decimaldms;
    }

    public void processMT(File fd) {

        if (fd.isDirectory()) {
            String entries[] = fd.list();
            if (entries != null) {
                for (int i = 0; i < entries.length; i++) {
                    File f = null;
                    try {
                        f = new File(fd.getCanonicalPath() + "/" + entries[i]);
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                    processMT(f);

                }
            }
        } else {
            hasGPSData(fd);
               System.out.println(fd + " " + hasGPSData(fd));
//            }

        }

    }

    class SplitString1 {

        public void doit(String lat) {

            String str = lat;
            String [] temp = null;
            String dtemp = null;
            //temp = str.split("[\"]|\"[\']");
//            temp = str.split("[°][\"]|[\']" );
            temp = str.split("[°]|[\"]|[\']" );
            dtemp = str.replace("\"", "°");
        //    System.out.println("Formated DCM : "+str);
            dump(temp);


        }

        public void dump(String []s) {
            for (int i = 0 ; i < s.length ; i++) {
//                System.out.println("\ndegree : "+s[0]);
//                System.out.println("\nminutes : "+s[1]);
//                System.out.println("\nsecond : "+s[2]);

                String deg = s[0] ;
                double ndeg = Double.parseDouble(deg);
                String min = s[1] ;
                double nmin = Double.parseDouble(min);
                String sec = s[2] ;
                double nsec = Double.parseDouble(sec);
                double decimaldms = (ndeg+(nmin/60)+(nsec/3600));
                System.out.println("finaldecimal : "+decimaldms);
            }
        }

        // Decimal degrees = whole number of degrees, plus minutes divided by 60,
        //plus seconds divided by 3600
    }



    public static void main(String[] args) {
        MetaDataFinder mdf = new MetaDataFinder();
        if (args.length > 0) {
            mdf.processMT(new File(args[0]));
        } else {
            File f = new File("/user/fhuet/desktop/home/workspaces/rechercheefficaceimagessimilaires/surfjavacl/RESTfulSimilar/test.jpg");
            System.out.println(mdf.hasGPSData(f));
            mdf.printTags(f);
        }
    }
}
