package rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.sun.jersey.multipart.FormDataMultiPart;
import fr.thumbnailsdb.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import com.sun.jersey.multipart.BodyPartEntity;

import com.sun.jersey.spi.resource.Singleton;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Path("/hello")
@Singleton
public class RestTest {

    protected ThumbStore tb;
    protected SimilarImageFinder si;
    protected DuplicateMediaFinder df;


    public RestTest() {
        System.out.println("RestTest.RestTest()");
        tb = new ThumbStore();
        si = new SimilarImageFinder(tb);
        df = new DuplicateMediaFinder(tb);

    }

    @GET
    @Path("/{param}")
    public Response getMsg(@PathParam("param") String msg) {
        String output = "Jersey say : " + msg;
        return Response.status(200).entity(output).build();

    }

    @GET
    @Path("/db/{param}")
    public Response getSize(@PathParam("param") String info) {
        System.out.println("RestTest.getSize() " + info);
        if ("size".equals(info)) {
            System.out.println("RestTest.getSize() " + tb.size());
            return Response.status(200).entity(tb.size() + "").build();
        }
        if ("path".equals(info)) {
            return Response.status(200).entity(tb.getPath() + "").build();
        }
        // System.out.println("RestTest.getSize() thumbstore is " + tb);
        return Response.status(404).build();
    }

    @GET
    @Path("/identical")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getDuplicate(@QueryParam("max") String max) {


        // String source = args[1];
        // String r =
        // df.prettyHTMLDuplicate(df.findDuplicateMedia(),Integer.parseInt(max));
        Collection dc = (Collection) df.computeDuplicateSets(df.findDuplicateMedia()).toCollection(Integer.parseInt(max));
        //	DuplicateFileGroup dl = df.computeDuplicateSets(df.findDuplicateMedia()).getFirst();
        // System.out.println("RestTest.getDuplicate() " + dl);
        //	Test t = new Test();
        return Response.status(200).entity(dc).build();
    }


    @GET
    @Path("/duplicateFolder")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getDuplicateFolder() {
        System.out.println("RestTest.getDuplicateFolder ");
        Collection<DuplicateFolderGroup> dc = (Collection) df.computeDuplicateFolderSets(df.findDuplicateMedia()).asSortedCollection();
        for (DuplicateFolderGroup dfg : dc) {
            System.out.println(dfg);
        }
        return Response.status(200).entity(dc).build();
    }

//	@GET
//	@Path("getImage/{imageId}")
//	@Produces("image/jpg")
//	public Response getImage(@PathParam(value = "imageId") String imageId) {
//		Image image = null;
//		System.out.println("RestTest.getImage() " + imageId);
//		try {
//			image = ImageIO.read(new File("/user/fhuet/desktop/home/Perso/photos/DSC00012.JPG"));
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		if (image != null) {
//			final ByteArrayOutputStream out = new ByteArrayOutputStream();
//			try {
//				ImageIO.write((BufferedImage) image, "jpg", out);
//				final byte[] imgData = out.toByteArray();
//				final InputStream bigInputStream = new ByteArrayInputStream(imgData);
//				return Response.ok(bigInputStream).build();
//			} catch (final IOException e) {
//				return Response.noContent().build();
//			}
//		}
//		return Response.noContent().build();
//	}

    @POST
//	@Path("getImage/{imageId}")
    @Path("getImage/")
    //@Produces("image/jpg")
    @Produces({MediaType.APPLICATION_JSON})
//	public Response getPostImage(@PathParam(value = "imageId") String imageId) {
    public Response getPostImage(String imageId) {
        // System.out.println("RestTest.getImage() " + imageId);
//		String[] duplicateFileList = new String[] { "/user/fhuet/desktop/home/NOSAVE/img-073020qbs9f.jpg",
//				"/user/fhuet/desktop/home/Perso/photos/DSC00006.JPG" };
        String result = null;
        String img = null;
        System.out.println("imageID " + imageId);
//		for (int i = 0; i < duplicateFileList.length; i++) {
        //  int i= Integer.parseInt(imageId)%2;
        img = getImageAsHTMLImg(imageId);
//        if (result == null) {
//            result = img;
//        } else {
//            result += img;
//        }
//		}

        return Response.status(200).entity(img).build();
    }

    protected String getImageAsHTMLImg(String imageId) {
        String img = "";
        try {
            File f = new File(imageId);
            img = "{\"data\" : \"" + Base64.encodeBase64String(FileUtils.readFileToByteArray(f)) + "\", \"title\" : \"" + f.getParent() + "\" }";

        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    @GET
    @Path("shrink/")
    public Response shrink() {
        tb.shrink();
        return Response.status(200).entity("Shrink done").build();
    }

    @GET
    @Path("index/")
    public Response index(@QueryParam("path") String path) {
        // tb.shrink();
        System.out.println("RestTest.index() input_path " + path);
        return Response.status(200).entity("Indexing in progress").build();
    }

    @POST
    @Path("findSimilar/")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    //@Produces({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response findSimilar(FormDataMultiPart multipart) {
        //  System.out.println("xxx stream is " + uploadedInputStream);
        System.out.println("RestTest.findSimilar() " + multipart.getBodyParts().size() + " parts");

        for (String s : multipart.getHeaders().keySet()) {
            System.out.println("RestTest.findSimilar() Header : " + s + " : " + multipart.getHeaders().get(s));
        }


//        String json = " {\"images\" : [";
        BodyPartEntity bpe = (BodyPartEntity) multipart.getBodyParts().get(0).getEntity();
        Collection<MediaFileDescriptor> c = null;
//        String message = null;
        ArrayList<SimilarImage> al = null;
        File temp = null;
        try {
            InputStream source = bpe.getInputStream();
            System.out.println("RestTest.findSimilar() received " + source);
            //BufferedImage bi = ImageIO.read(source);

           temp = File.createTempFile("tempImage", ".jpg");
            FileOutputStream fo = new FileOutputStream(temp);

            byte[] buffer = new byte[8 * 1024];

            int total = 0;
            try {
                int bytesRead;
                while ((bytesRead = source.read(buffer)) != -1) {
                    fo.write(buffer, 0, bytesRead);
                    total += bytesRead;
                }
            } finally {
                fo.close();
            }
            System.out.println("RestTest.findSimilar()  written to " + temp + " with size " + total);
        } catch (Exception e) {
            // message = e.getMessage();
            e.printStackTrace();

        }

            // ImageIO.write(bi,"jpg", temp);

//             c = si.findIdenticalMedia(temp.getAbsolutePath());
        long t1 = System.currentTimeMillis();
            c = si.findSimilarMedia(temp.getAbsolutePath());
        long t2 = System.currentTimeMillis();
            System.out.println("Found similar files " + c.size() + " took " + (t2-t1) + "ms");

            al = new ArrayList<SimilarImage>(c.size());
            for (MediaFileDescriptor mdf : c) {

                String path = mdf.getPath();

                String data = null;
                try {
                    data = Base64.encodeBase64String(FileUtils.readFileToByteArray(new File(path)));
                } catch (IOException e) {
                   // e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    System.err.println("Err: File " + path + " not found");
                }

                SimilarImage si = new SimilarImage(path, data, mdf.getRmse());
                al.add(si);
                System.out.println(si);
//                String img = this.getImageAsHTMLImg(mdf.getPath());
//                json+=img; //"{\"path\" : " + mdf.getPath()+ ", \"data\" : "+  img +" },";
            }



//        json+="]}";
//        System.out.println(json);
        System.out.println("RestTest.findSimilar sending " + al.size() + " elements");

        JSONArray mJSONArray = new JSONArray();
        for (int i = 0; i < al.size(); i++) {
             JSONObject json = new JSONObject();
            try {
                json.put("path", al.get(i).path);
                json.put("base64Data", al.get(i).base64Data);
                json.put("rmse", al.get(i).rmse);
                mJSONArray.put(json);
            } catch (JSONException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        JSONObject responseDetailsJson = new JSONObject();
        try {
            responseDetailsJson.put("success", true);

            responseDetailsJson.put("images", mJSONArray);
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return Response.status(200).entity(responseDetailsJson).type(MediaType.APPLICATION_JSON).build();
    }


    @XmlRootElement
    public class SimilarImage {
        @XmlElement
        public String path;
        @XmlElement
        public String base64Data;
        @XmlElement
        public double rmse;

        public SimilarImage(String path, String base64Data, double rmse) {
            this.rmse = rmse;
            this.path = path;
            this.base64Data = base64Data;
        }
    }


}