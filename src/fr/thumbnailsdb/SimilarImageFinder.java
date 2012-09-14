package fr.thumbnailsdb;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import javax.imageio.ImageIO;

public class SimilarImageFinder {

	protected ThumbStore thumbstore;

	public SimilarImageFinder(ThumbStore c) {
		this.thumbstore = c;
	}

	public TreeSet<ImageDescriptor> findSimilarImages(String source) {
		ThumbnailGenerator tg = new ThumbnailGenerator(null);
		ImageDescriptor id = tg.generateImageDescriptor(new File(source)); // ImageDescriptor.readFromDisk(s);
		return this.findSimilarImages(id);

	}

	public TreeSet<ImageDescriptor> findSimilarImages(ImageDescriptor id) {
		ProgressBar pb = new ProgressBar();
		int max = thumbstore.size();
		int increment = max/20;
		int i = 0;
		int step =0;
		if (increment==0) {increment =1;}
	//	System.out.println("SimilarImageFinder.findSimilarImages() increment is " + increment);
		TreeSet<ImageDescriptor> list = new TreeSet<ImageDescriptor>(new Comparator<ImageDescriptor>() {
			@Override
			public int compare(ImageDescriptor o1, ImageDescriptor o2) {
				return Double.compare(o1.getRmse(), o2.getRmse());
			}
		});
		try {
			//BufferedImage source = ImageIO.read(new ByteArrayInputStream(id.getData()));
			System.out.println("SimilarImageFinder.findSimilarImages() Number of records " + max);
			ResultSet res = thumbstore.getAllInDataBase();
			while (res.next()) {
				if (i>increment) {
					i=0;
					step++;
				    pb.update(step,20);
				}
				String path = res.getString("path");
				byte[] d = res.getBytes("data");
				if (d != null) {
					
//					ByteArrayInputStream bi = new ByteArrayInputStream(res.getBytes("data"));
//					System.out.println("First 4 elements of data array ");
//					for (int k =0;k<4;k++) {
//						 System.out.print(bi.read()+",");
//					}
//					System.out.println();
//					BufferedImage bf = ImageIO.read(new ByteArrayInputStream(res.getBytes("data")));
//					System.out.println("First RGBA values of image data ");
//					int rgba = bf.getRGB(0, 0);
//					int a = (rgba >>>26) & 0xFF;
//					int red1 = (rgba >>> 16) & 0xFF;
//					int green1 = (rgba >>> 8) & 0xFF;
//					int blue1 = (rgba >>> 0) & 0xFF;
//					System.out.printf("   %d,%d,%d,%d\n",a, red1, green1, blue1);
					
					ObjectInputStream oi = new ObjectInputStream(new ByteArrayInputStream(d));
					int[] idata = (int[]) oi.readObject();
					if (idata!= null) {
					
					double rmse = ImageComparator.compareARGBUsingRMSE(id.getData(), idata);
					ImageDescriptor imd = new ImageDescriptor();
					imd.setPath(path);
					imd.setRmse(rmse);
					list.add(imd);
					}
					else {
						System.err.println("Warning, found null data entry in DB for " +path);
					}
				}
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public void prettyPrintResults(TreeSet<ImageDescriptor> ts, int maxResults) {
		int i = 0;
		for (Iterator iterator = ts.iterator(); iterator.hasNext();) {
			i++;
			ImageDescriptor imageDescriptor = (ImageDescriptor) iterator.next();
			System.out.printf("%1.5f  %s\n", imageDescriptor.getRmse(), imageDescriptor.getPath());
			if (i >= maxResults) {
				break;
			}
		}

	}

	public void testFindSimilarImages(ImageDescriptor id) {
		System.out.println("ThumbStore.test() reading descriptor from disk ");
		String s = "/user/fhuet/desktop/home/workspaces/rechercheefficaceimagessimilaires/images/original.jpg";
		System.out.println("ThumbStore.testFindSimilarImages() Reference Image " + s);

		ThumbnailGenerator tg = new ThumbnailGenerator(null);
		id = tg.generateImageDescriptor(new File(s)); // ImageDescriptor.readFromDisk(s);
		this.prettyPrintResults(this.findSimilarImages(id), 2);
	}

	public static void main(String[] args) {
		ThumbStore tb = new ThumbStore();
		SimilarImageFinder si = new SimilarImageFinder(tb);
		si.testFindSimilarImages(null);
	}

}
