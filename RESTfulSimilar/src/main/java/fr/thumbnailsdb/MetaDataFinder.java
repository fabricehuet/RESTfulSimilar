package fr.thumbnailsdb;

import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDescriptor;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: fhuet
 * Date: 13/11/12
 * Time: 11:25
 * To change this template use File | Settings | File Templates.
 */
public class MetaDataFinder {

    Metadata metadata;

    public MetaDataFinder(File f) {
        metadata = null;
        try {
            metadata = ImageMetadataReader.readMetadata(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printTags() {
        //File jpegFile = new File("myImage.jpg");

        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                System.out.println(tag);
            }
        }


    }

    public boolean hasGPSData() {
        return getLatLong() != null;
    }


    public String getDate() {
        ExifSubIFDDirectory directory = metadata.getDirectory(ExifSubIFDDirectory.class);
        ExifSubIFDDescriptor descriptor = new ExifSubIFDDescriptor((ExifSubIFDDirectory) directory);
        Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
        return date.toString();
    }

    public String getGPS() {
        String result = "";
        GpsDirectory directory = metadata.getDirectory(GpsDirectory.class);
        if (directory != null && directory.getTags().size() > 2) {
//         //   System.out.println("---- " + file + " ----");
            for (Tag tag : directory.getTags()) {

                result+=tag + "\n";
            }
        }


       return result;

    }

    public double[] getLatLong() {

        GpsDirectory directory = metadata.getDirectory(GpsDirectory.class);
        if (directory != null && directory.getTags().size() > 2) {
//         //   System.out.println("---- " + file + " ----");
//            for (Tag tag : directory.getTags()) {
//                System.out.println(tag);
//            }
//            double lat = getAsDecimalDegree(directory.getDescription(2));
//            double lon = getAsDecimalDegree(directory.getDescription(4));
////            System.out.println(lat+", " +lon );
           GeoLocation gl =  directory.getGeoLocation();
             double lat =     gl.getLatitude();
            double lon = gl.getLongitude();


            return new double[]{lat, lon};
        } else {

            return null;
        }

    }


    public double getAsDecimalDegree(String co) {

        String[] temp = null;

        double decimaldms = 0;
        temp = co.split("[°]|[\"]|[\']");
        for (int i = 0; i < temp.length; i++) {
//                System.out.println("degree : "+temp[0]);
//                System.out.println("minutes : "+temp[1]);
//                System.out.println("second : "+temp[2]);

            String deg = temp[0];
            double ndeg = Double.parseDouble(deg);
            String min = temp[1];
            double nmin = Double.parseDouble(min);
            String sec = temp[2];
            double nsec = Double.parseDouble(sec);
            if (ndeg < 0) {
                decimaldms = -(-ndeg + (nmin / 60) + (nsec / 3600));
            } else {
                decimaldms = (ndeg + (nmin / 60) + (nsec / 3600));
            }
        }
        return decimaldms;
    }

//    public void processMT(File fd) {
//
//        if (fd.isDirectory()) {
//            String entries[] = fd.list();
//            if (entries != null) {
//                for (int i = 0; i < entries.length; i++) {
//                    File f = null;
//                    try {
//                        f = new File(fd.getCanonicalPath() + "/" + entries[i]);
//                    } catch (IOException e) {
//                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                    }
//
//                    processMT(f);
//
//                }
//            }
//        } else {
//            hasGPSData(fd);
//               System.out.println(fd + " " + hasGPSData(fd));
////            }
//
//        }
//
//    }


    public static void main(String[] args) {
        File f = new File("/user/fhuet/desktop/home/workspaces/rechercheefficaceimagessimilaires/surfjavacl/RESTfulSimilar/gps_nsfw/test2.jpg");
        MetaDataFinder mdf = new MetaDataFinder(f);
//        if (args.length > 0) {
//            mdf.processMT(new File(args[0]));
//        } else {

        System.out.println(mdf.hasGPSData());
        mdf.printTags();
        System.out.println(mdf.getDate());
        System.out.println(mdf.getGPS());
        double ll[] = mdf.getLatLong();
        System.out.println("Latitude " + ll[0]);
        System.out.println("Longitude " + ll[1]);
//        }
    }
}
