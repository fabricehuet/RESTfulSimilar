package fr.thumbnailsdb;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public class SimilarImageFinder {

	protected ThumbStore thumbstore;

	public SimilarImageFinder(ThumbStore c) {
		this.thumbstore = c;
	}

	public TreeSet<MediaFileDescriptor> findSimilarMedia(String source) {
		ThumbnailGenerator tg = new ThumbnailGenerator(null);
		MediaFileDescriptor id = tg.buildMediaDescriptor(new File(source)); // ImageDescriptor.readFromDisk(s);
		return this.findSimilarMedia(id);
	}

	public TreeSet<MediaFileDescriptor> findSimilarMedia(MediaFileDescriptor id) {
		ProgressBar pb = new ProgressBar();
		int max = thumbstore.size();
		int increment = max / 20;
		int i = 0;
		int step = 0;
		if (increment == 0) {
			increment = 1;
		}
		// System.out.println("SimilarImageFinder.findSimilarImages() increment is "
		// + increment);
		TreeSet<MediaFileDescriptor> list = findSimilarImage(id, pb, max, increment, i, step);
		return list;
	}

	protected TreeSet<MediaFileDescriptor> findSimilarImage(MediaFileDescriptor id, ProgressBar pb, int max,
			int increment, int i, int step) {
		TreeSet<MediaFileDescriptor> list = new TreeSet<MediaFileDescriptor>(new Comparator<MediaFileDescriptor>() {
			@Override
			public int compare(MediaFileDescriptor o1, MediaFileDescriptor o2) {
				return Double.compare(o1.getRmse(), o2.getRmse());
			}
		});
		try {
			// BufferedImage source = ImageIO.read(new
			// ByteArrayInputStream(id.getData()));
			System.out.println("SimilarImageFinder.findSimilarImages() Number of records " + max);
			ResultSet res = thumbstore.getAllInDataBase();
			while (res.next()) {
				if (i > increment) {
					i = 0;
					step++;
					pb.update(step, 20);
				}
				String path = res.getString("path");
				byte[] d = res.getBytes("data");
				if (d != null) {
					ObjectInputStream oi = new ObjectInputStream(new ByteArrayInputStream(d));
					int[] idata = (int[]) oi.readObject();
					if (idata != null) {
						double rmse = ImageComparator.compareARGBUsingRMSE(id.getData(), idata);
						MediaFileDescriptor imd = new MediaFileDescriptor();
						imd.setPath(path);
						imd.setRmse(rmse);
						list.add(imd);
					} else {
<<<<<<< HEAD
						// System.err.println("Warning, found null data entry in DB for "
						// + path);
=======
						//System.err.println("Warning, found null data entry in DB for " + path);
>>>>>>> b9b7be63ba7ac23f0e8c0cdfbc985a2bf7c8a18d
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

	public void prettyPrintSimilarResults(TreeSet<MediaFileDescriptor> ts, int maxResults) {
		int i = 0;
		for (Iterator iterator = ts.iterator(); iterator.hasNext();) {
			i++;
			MediaFileDescriptor imageDescriptor = (MediaFileDescriptor) iterator.next();
			System.out.printf("%1.5f  %s\n", imageDescriptor.getRmse(), imageDescriptor.getPath());
			if (i >= maxResults) {
				break;
			}
		}
	}

	public ArrayList<MediaFileDescriptor> findIdenticalMedia(String source) {

		ThumbnailGenerator tg = new ThumbnailGenerator(null);
		MediaFileDescriptor id = tg.buildMediaDescriptor(new File(source)); // ImageDescriptor.readFromDisk(s);
		ResultSet res = thumbstore.getDuplicatesMD5(id);
		ArrayList<MediaFileDescriptor> al = new ArrayList<MediaFileDescriptor>();
		try {
			while (res.next()) {
				MediaFileDescriptor imd = new MediaFileDescriptor();
				String path = res.getString("path");
				String md5 = res.getString("md5");
				long size = res.getLong("size");
				imd.setPath(path);
				imd.setMd5Digest(md5);
				imd.setSize(size);
				al.add(imd);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return al;
	}

	public void prettyPrintIdenticalResults(ArrayList<MediaFileDescriptor> findIdenticalMedia) {
		Iterator<MediaFileDescriptor> it = findIdenticalMedia.iterator();
		while (it.hasNext()) {
			MediaFileDescriptor mediaFileDescriptor = (MediaFileDescriptor) it.next();
			System.out.println(mediaFileDescriptor.getPath() + " " + mediaFileDescriptor.getSize());
		}

	}

	public void testFindSimilarImages(MediaFileDescriptor id) {
		System.out.println("ThumbStore.test() reading descriptor from disk ");
		String s = "/user/fhuet/desktop/home/workspaces/rechercheefficaceimagessimilaires/images/original.jpg";
		System.out.println("ThumbStore.testFindSimilarImages() Reference Image " + s);

		ThumbnailGenerator tg = new ThumbnailGenerator(null);
		id = tg.buildMediaDescriptor(new File(s)); // ImageDescriptor.readFromDisk(s);
		this.prettyPrintSimilarResults(this.findSimilarMedia(id), 2);
	}

	public static void main(String[] args) {
		ThumbStore tb = new ThumbStore();
		SimilarImageFinder si = new SimilarImageFinder(tb);
		si.testFindSimilarImages(null);
	}

}
