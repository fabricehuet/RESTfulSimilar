package rest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.thumbnailsdb.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import com.sun.jersey.multipart.BodyPartEntity;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.spi.resource.Singleton;

@Path("/hello")
@Singleton
public class RestTest {

    protected ThumbStore tb;
    protected SimilarImageFinder si ;
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
        try {
            File f = new File(imageId);
            img = "{\"data\" : \"" + Base64.encodeBase64String(FileUtils.readFileToByteArray(f)) + "\", \"title\" : \"" + f.getParent() + "\" }";

//				img = "<img src=\"data:image/jpg;base64,"
//						+ Base64.encodeBase64String(FileUtils.readFileToByteArray(f)) + "\" title=\""+  f.getParent()  + "\"/>";
//				;
//				System.out.println("RestTest.getPostImage() " + duplicateFileList[i]);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if (result == null) {
//            result = img;
//        } else {
//            result += img;
//        }
//		}

        return Response.status(200).entity(img).build();
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
    @Path("upload/")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    //@Produces({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response uploadImage(MultiPart multipart) {
        System.out.println("RestTest.uploadImage() " + multipart);
        BodyPartEntity bpe = (BodyPartEntity) multipart.getBodyParts().get(0).getEntity();
        Collection<MediaFileDescriptor> c = null;
        String message = null;
        try {
            InputStream source = bpe.getInputStream();
            System.out.println("RestTest.uploadImage() received " + source);
            //BufferedImage bi = ImageIO.read(source);

            File temp = File.createTempFile("tempImage", ".jpg");
            FileOutputStream fo = new FileOutputStream(temp);

            byte[] buffer = new byte[8 * 1024];

          //  InputStream input = urlConnect.getInputStream();

              //  OutputStream output = new FileOutputStream(filename);
                try {
                    int bytesRead;
                    while ((bytesRead = source.read(buffer)) != -1) {
                        fo.write(buffer, 0, bytesRead);
                    }
                } finally {
                    fo.close();
                }

           // ImageIO.write(bi,"jpg", temp);
            System.out.println("RestTest.uploadImage()  written to " + temp);
             c = si.findIdenticalMedia(temp.getAbsolutePath());
            System.out.println("Found identical files " + c.size());



        } catch (Exception e) {
            message = e.getMessage();
            e.printStackTrace();
        }

        // return
        // Response.status(Response.Status.ACCEPTED).entity("Attachements processed successfully.")
        // .type(MediaType.APPLICATION_JSON).build();

//		return Response.created(uri).type(MediaType.TEXT_HTML_TYPE).build();
        //return Response.ok("All good").type(MediaType.TEXT_HTML_TYPE).build();
//        return Response.status(201).entity("{\"test\" : \"toto\"}").type(MediaType.TEXT_HTML).build();
        return Response.status(201).entity(c).build();


    }

}