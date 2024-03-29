package fr.thumbnailsdb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;


@XmlRootElement
public class MediaFileDescriptor implements Serializable {
    @XmlElement
	protected String path;
	protected long size;
	protected long mtime;
	protected String md5Digest;
	protected int[] data;
protected double lat;
    protected double lon;

    //id in the database
    protected int id;

    @XmlElement
	protected double rmse;

	public static MediaFileDescriptor readFromDisk(String path) {
		MediaFileDescriptor id = null;
		File f = new File(path);
		long size = f.length();
		long modifiedTime = f.lastModified();

		int[] data;

//		try {
		//	FileInputStream fi = new FileInputStream(f);
			data = new int[(int) size];
			// int read = fi.read(data);
			// System.out.println("ImageDescriptor.main() read " + read +
			// " bytes from file");
			id = new MediaFileDescriptor(path, size, modifiedTime, data, null);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		return id;
	}

	//

	public MediaFileDescriptor() {

	}

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setLat(double lat) {

        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {

        return lat;
    }

    public double getLon() {
        return lon;
    }

    /**
	 * int[] data will be converted to argb byte[]
	 * 
	 * @param path
	 * @param size
	 * @param mtime
	 * @param data
	 * @param md5
	 */
	public MediaFileDescriptor(String path, long size, long mtime, int[] data, String md5) {
		super();
		this.path = path;

		this.size = size;
		this.mtime = mtime;

		// byte[] rgba = convertToARGB(data);
		// this.data = rgba;
		this.data = data;
		this.md5Digest = md5;
	}

	protected byte[] convertToARGB(int[] data) {
		byte[] rgba = new byte[data.length * 4];
		// int j = 0;
		for (int i = 0; i < data.length - 4; i++) {
			if (i == 0) {
				System.out.println("ImageDescriptor.convertToARGB()");
				System.out.println("   " + ((data[i] >>> 16) & 0xFF) + " -> " + (byte) ((data[i] >>> 16) & 0xFF));
				System.out.println("   " + ((data[i] >>> 8) & 0xFF) + " -> " + (byte) ((data[i] >>> 8) & 0xFF));

				System.out.println("   " + ((data[i] >>> 0) & 0xFF) + " -> " + (byte) ((data[i] >>> 0) & 0xFF));

			}
			rgba[4 * i] = (byte) ((data[i] >>> 24) & 0xFF);
			rgba[4 * i + 1] = (byte) ((data[i] >>> 16) & 0xFF);
			rgba[4 * 1 + 2] = (byte) ((data[i] >>> 8) & 0xFF);
			rgba[4 * +3] = (byte) ((data[i] >>> 0) & 0xFF);
			// j++;
		}
		return rgba;
	}

	public double getRmse() {
		return rmse;
	}

	public void setRmse(double rmse) {
		this.rmse = rmse;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public void setMtime(long mtime) {
		this.mtime = mtime;
	}

	public void setMd5Digest(String md5Digest) {
		this.md5Digest = md5Digest;
	}

	public void setData(int[] data) {
		this.data = data; // this.convertToARGB(data);
	}

	//
	// public void setData(byte[] data) {
	// this.data = data;
	// }

	public String getPath() {
		return path;
	}

	public long getSize() {
		return size;
	}

	public long getMtime() {
		return mtime;
	}

	public int[] getData() {
		return data;
	}

	public byte[] getDataAsByte() {
		// convert the int[] array to byte[] array
		if (data == null) {
            System.out.println("MediaFileDescriptor.getDataAsByte found null data");
			return null;
		}
		ByteArrayOutputStream ba = new ByteArrayOutputStream();
		ObjectOutputStream oi;
		try {
			oi = new ObjectOutputStream(ba);
			oi.writeObject(data);
			oi.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ba.toByteArray();
	}

	public String getMD5() {
		return md5Digest;
	}

	@Override
	public String toString() {
		return "[path=" + path + "\n size=" + size + ",\n mtime=" + mtime + "]";
	}

	public static void main(String[] args) {
		String path = "/user/fhuet/desktop/home/workspaces/rechercheefficaceimagessimilaires/images/tn/original.jpg";
		// / System.out.println(ImageDescriptor.readFromDisk(path));
	}

}
