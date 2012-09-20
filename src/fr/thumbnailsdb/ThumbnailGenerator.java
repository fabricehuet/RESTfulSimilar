package fr.thumbnailsdb;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.codec.digest.DigestUtils;

public class ThumbnailGenerator {

	
	protected boolean debug;
	protected boolean software = true;
	protected ThumbStore ts;

	protected Logger log = Logger.getLogger();
	
	protected ExecutorService executorService = Executors.newFixedThreadPool(3);

	public ThumbnailGenerator(ThumbStore t) {
		this.ts = t;
	}

	/**
	 * Load the image and resize it if necessary
	 * 
	 * @param imagePath
	 * @return
	 * @throws IOException
	 */
	public BufferedImage downScaleImageToGray(BufferedImage bi, int nw, int nh) throws IOException {

		if (debug) {
			System.out.println("ThumbnailGenerator.downScaleImageToGray()  original image is " + bi.getWidth() + "x"
					+ bi.getHeight());
		}
		BufferedImage scaledBI = null;
		// if (nw < width || nh < height) {
		if (debug) {
			System.out.println("ThumbnailGenerator.downScaleImageToGray() to " + nw + "x" + nh);
		}
		if (debug) {
			System.out.println("resizing to " + nw + "x" + nh);
		}
		scaledBI = new BufferedImage(nw, nh, BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g = scaledBI.createGraphics();
		g.setComposite(AlphaComposite.Src);
		g.drawImage(bi, 0, 0, nw, nh, null);
		g.dispose();
		// }
		return scaledBI;
	}

	public String generateMD5(File f) throws IOException {
		InputStream fis = new FileInputStream(f);
		byte[] buffer  = DigestUtils.md5(fis);
		String s = DigestUtils.md5Hex(buffer);
		fis.close();
		return s; 
//		= new byte[1024];
//		MessageDigest complete = null;
//		try {
//			complete = MessageDigest.getInstance("MD5");
//			int numRead;
//			do {
//				numRead = fis.read(buffer);
//				if (numRead > 0) {
//					complete.update(buffer, 0, numRead);
//				}
//			} while (numRead != -1);
//
//			fis.close();
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		}
//		return complete.digest();
	}

	protected int[] generateThumbnail(File f) {
		// byte[] data;
		BufferedImage source;
		int[] data1 = null;
		try {
			source = ImageIO.read(f);

			BufferedImage dest = null;
			if (software) {
				dest = this.downScaleImageToGray(source, 10, 10);
			}
			// ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// ImageIO.write(dest, "jpg", baos);
			// baos.flush();
			// data = baos.toByteArray();
			// baos.close();

			data1 = new int[dest.getWidth() * dest.getHeight()];
			dest.getRGB(0, 0, dest.getWidth(), dest.getHeight(), data1, 0, dest.getWidth());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data1;
	}

	public MediaFileDescriptor buildMediaDescriptor(File f) {
		MediaFileDescriptor id = new MediaFileDescriptor();
		int[] data;
		String md5;
		try {
			id.setPath(f.getCanonicalPath());
			id.setMtime(f.lastModified());
			id.setSize(f.length());
			//generate thumbnails only for images, not video
			if (Utils.isValideImageName(f.getName())) {
		     	data = generateThumbnail(f);
		     	id.setData(data);
			}
			md5 = generateMD5(f);
			id.setMd5Digest(md5);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return id;
	}

	public void generateAndSave(String s) {
		File f = new File(s);
		if (Utils.isValideImageName(f.getName()) || Utils.isValideVideoName(f.getName())) {
//			System.out.println("ThumbnailGenerator.generateAndSave() processing " + f);
//			System.out.println("checking if in DB");
			try {
				if (ts.isInDataBaseBasedOnName(f.getCanonicalPath())) {
					//System.out.println("ThumbnailGenerator.generateImageDescriptor() Already in DB, ignoring");
					log.log(f.getCanonicalPath() + " already in DB");
				} else {
					MediaFileDescriptor id = this.buildMediaDescriptor(f);
					if (id != null) {
						ts.saveToDB(id);
					}
					log.log(f.getCanonicalPath() + " ..... OK");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	

	public void process(String path) {
		try {
			this.process(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void process(File fd) throws IOException {
		if (fd.isFile()) {
			this.generateAndSave(fd.getCanonicalPath());
			// }
		} else {
			String entries[] = fd.list();
			if (entries != null) {
				for (int i = 0; i < entries.length; i++) {
					File f = new File(fd.getCanonicalPath() + "/" + entries[i]);
					if (f.isFile()) {
						this.generateAndSave(fd.getCanonicalPath() + "/" + entries[i]);
					} else {
						this.process(f);
					}
				}
			}
		}
	}

	public void processMT(String path) {
	//	System.out.println("ThumbnailGenerator.processMT()");
		try {
			this.processMT(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		executorService.shutdown();
		try {
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void processMT(File fd) throws IOException {
		if (fd.isFile()) {
			// System.out.println("ThumbnailGenerator.process() " + fd);
			//if (this.isValideImageName(fd.getName())) {
				// this.testDownscalingOpenCL(f);
				executorService.execute(new RunnableProcess(fd.getCanonicalPath()));
				// this.generateAndSave(fd);
			//}
		} else {
			String entries[] = fd.list();
			if (entries != null) {
				for (int i = 0; i < entries.length; i++) {
					File f = new File(fd.getCanonicalPath() + "/" + entries[i]);
					if (f.isFile()) {
					//	if (this.isValideImageName(f.getName())) {
							executorService.execute(new RunnableProcess(fd.getCanonicalPath() + "/" + entries[i]));
					//	}
					} else {
						this.processMT(f);
					}
				}
			}
		}
	}

	protected class RunnableProcess implements Runnable {
		protected String fd;

		public RunnableProcess(String fd) {
			this.fd = fd;
		}

		@Override
		public void run() {
		//	System.out.println("ThumbnailGenerator.RunnableProcess.run()");
			generateAndSave(fd);
		}
	}

	public static void main(String[] args) {
		String pathToDB = "test";
		String source = ".";
		if (args.length == 2 || args.length == 4) {
			for (int i = 0; i < args.length; i++) {
				if ("-db".equals(args[i])) {
					pathToDB = args[i + 1];
					i++;
				}
				if ("-source".equals(args[i])) {
					source = args[i + 1];
					i++;
				}
			}
			// pathToDB=args[0];
		} else {
			System.err.println("Usage: java " + ThumbnailGenerator.class.getName()
					+ "[-db path_to_db] -source folder_or_file_to_process");
			System.exit(0);
		}
		ThumbStore ts = new ThumbStore(pathToDB);
		ThumbnailGenerator tb = new ThumbnailGenerator(ts);
		File fs = new File(source);
		try {
			tb.processMT(fs);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
