package fr.thumbnailsdb;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ThumbStore {

	protected static String DEFAULT_DB = "localDB";
	protected Connection connexion;

	public ThumbStore() {
		this(DEFAULT_DB);

	}

	public ThumbStore(String path) {
		if (path == null) {
			path = DEFAULT_DB;
		}
		System.out.println("ThumbStore.ThumbStore() using " + path + " as DB");
		try {
			connectToDB(path);
			checkAndCreateTables();
			// test();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void connectToDB(String path) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		Class.forName("org.h2.Driver").newInstance();
		connexion = DriverManager.getConnection("jdbc:h2:" + path + "", "sa",
				"");
		System.out.println("ThumbStore.connectToDB() " + connexion);
	}

	public void checkAndCreateTables() throws SQLException {
		DatabaseMetaData dbm = connexion.getMetaData();
		// check if "employee" table is there
		ResultSet tables = dbm.getTables(null, null, "IMAGES", null);
		if (tables.next()) {
			System.out
					.println("ThumbStore.checkAndCreateTables() tables exists!");
			// Table exists
		} else {
			System.out
					.println("ThumbStore.checkAndCreateTables() table does not exist, should create it");
			// Table does not exist
			String table = "CREATE TABLE IMAGES(path varchar(256), size long, mtime long, data blob,  md5 blob, PRIMARY KEY ( path ))";
			Statement st = connexion.createStatement();
			st.execute(table);
			System.out
					.println("ThumbStore.checkAndCreateTables() table created!");
		}
	}

	/**
	 * Save the descriptor to the db DO NOT check that the key is not used
	 * 
	 * @param id
	 */
	public void saveToDB(ImageDescriptor id) {
		PreparedStatement psmnt;
		try {

			Statement st;
			psmnt = connexion
					.prepareStatement("insert into IMAGES(path, size, mtime, data, md5) "
							+ "values(?,?,?,?,?)");
			psmnt.setString(1, id.getPath());
			psmnt.setLong(2, id.getSize());
			psmnt.setLong(3, id.getMtime());
			// convert the int[] array to byte[] array
			ByteArrayOutputStream ba = new ByteArrayOutputStream();
			ObjectOutputStream oi = new ObjectOutputStream(ba);
			oi.writeObject(id.getData());
			oi.close();
			psmnt.setBytes(4, ba.toByteArray());
			psmnt.setBytes(5, id.getMD5());
			psmnt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int size() {
		String select = "SELECT COUNT(*) FROM IMAGES";
		int count = 0;
		Statement st;
		try {
			st = connexion.createStatement();
			ResultSet res = st.executeQuery(select);
			if (res.next()) {
				count = res.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	public boolean isInDataBaseBasedOnName(String path) {
		boolean result = false;
		String select = "SELECT path FROM IMAGES WHERE path=\'" + path + "\'"; // AND
																				// mtime="
																				// +
																				// id.getMtime();
		Statement st;
		try {
			st = connexion.createStatement();
			ResultSet res = st.executeQuery(select);
			if (res.next()) {
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public ResultSet getAllInDataBase() {
		Statement sta;
		ResultSet res = null;
		try {
			sta = connexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE , ResultSet.CONCUR_UPDATABLE );
			res = sta.executeQuery("SELECT * FROM IMAGES");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * remove incorrect records from the DB
	 * 
	 */
	public void fix() {
		ResultSet all = this.getAllInDataBase();
		ImageDescriptor id = null;
		System.out.println("ThumbStore.fix() BD has " + this.size()
				+ " entries");
		try {
			while (all.next()) {
				id = getCurrentImageDescriptor(all);
				if (id.getData() == null && id.getMD5() == null) {
					System.out.println("ThumbStore.fix() " + id.getPath()
							+ " has null data and md5");
					all.deleteRow();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * remove outdated records from the DB
	 * An outdated record is one which has no corresponding file on the FS
	 * 
	 */
	public void shrink() {
		ResultSet all = this.getAllInDataBase();
		ImageDescriptor id = null;
		System.out.println("ThumbStore.shrink() BD has " + this.size()
				+ " entries");
		try {
			int i=0;
			while (all.next()) {
				id = getCurrentImageDescriptor(all);
				File tmp = new File(id.getPath());
				if (!tmp.exists()) {
					i++;
				}
			}
			System.out.println("ThumbStore.shrink() found " + i + " records to delete");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	

	public ImageDescriptor getCurrentImageDescriptor(ResultSet res) {
		ImageDescriptor id = null;

		try {
			// if (res.next()) {
			// id = new ImageDescriptor();
			String path = res.getString("path");
			byte[] d = res.getBytes("data");
			int[] idata = null;
			if (d != null) {
				ObjectInputStream oi = new ObjectInputStream(
						new ByteArrayInputStream(d));
				idata = (int[]) oi.readObject();
			} else {
				System.err.println("xxxx");
			}
			byte[] md5 = res.getBytes("md5");
			long mtime = res.getLong("mtime");
			long size = res.getLong("size");
			id = new ImageDescriptor(path, size, mtime, idata, md5);
			// }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}

	public void displayImage(BufferedImage bf) {
		Graphics2D gg = bf.createGraphics();
		gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		gg.drawImage(bf, 0, 0, null);
		// drawFeatures(gg, surf, id);
		JFrame frame = null;
		// display results
		if (frame == null) {
			frame = new JFrame();
			final JLabel label = new JLabel(new ImageIcon(bf));
			frame.add(label);

		}

		frame.pack();
		frame.setVisible(true);
	}

	public void test() {
		System.out.println("ThumbStore.test() reading descriptor from disk ");
		String s = "/user/fhuet/desktop/home/workspaces/rechercheefficaceimagessimilaires/images/tn/original.jpg";
		ImageDescriptor id = ImageDescriptor.readFromDisk(s);
		System.out.println("ThumbStore.test() writting to database");
		saveToDB(id);
		System.out.println("ThumbStore.test() dumping entries");
		String select = "SELECT * FROM IMAGES";
		Statement st;
		try {
			st = connexion.createStatement();

			ResultSet res = st.executeQuery(select);
			while (res.next()) {
				String i = res.getString("path");
				System.out.println(i);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

	}

}
