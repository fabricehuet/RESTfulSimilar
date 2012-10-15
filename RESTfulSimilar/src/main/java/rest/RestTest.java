package rest;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/hello")
public class RestTest {

	@GET
	@Path("/{param}")
	public Response getMsg(@PathParam("param") String msg) {

		String output = "Jersey say : " + msg;

		return Response.status(200).entity(output).build();

	}

	@GET
	@Path("getImage/{imageId}")
	@Produces("image/jpg")
	public Response getImage(@PathParam(value = "imageId") String imageId) {
		Image image = null;
		System.out.println("RestTest.getImage() " +imageId);
		try {
			image = ImageIO.read(new File("/Users/fhuet/Documents/Perso/Photos/Wow Photos/IMG_3803.JPG"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (image != null) {
			// resize the image to fit the GUI's image frame
			//image = resize((BufferedImage) image, 300, 300);
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				ImageIO.write((BufferedImage) image, "jpg", out);
				final byte[] imgData = out.toByteArray();
				final InputStream bigInputStream = new ByteArrayInputStream(
						imgData);
				return Response.ok(bigInputStream).build();
			} catch (final IOException e) {
				return Response.noContent().build();
			}
		}
		return Response.noContent().build();
	}

}