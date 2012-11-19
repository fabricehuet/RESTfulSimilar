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
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ThumbStore {

    protected static String DEFAULT_DB = "localDB";
    protected Connection connexion;
    protected String path;


    public ThumbStore() {
        this(DEFAULT_DB);

    }

    public ThumbStore(String path) {
        if (path == null) {
            this.path = DEFAULT_DB;
        } else {
            this.path = path;
        }
        System.out.println("ThumbStore.ThumbStore() using " + path + " as DB");
        try {
            connectToDB(this.path);
            checkAndCreateTables();
            // test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectToDB(String path) throws InstantiationException, IllegalAccessException, ClassNotFoundException,
            SQLException {
        Class.forName("org.h2.Driver").newInstance();
        connexion = DriverManager.getConnection("jdbc:h2:" + path + "", "sa", "");
        System.out.println("ThumbStore.connectToDB() " + connexion);
    }

    public void checkAndCreateTables() throws SQLException {
        DatabaseMetaData dbm = connexion.getMetaData();
        // check if "employee" table is there
        ResultSet tables = dbm.getTables(null, null, "IMAGES", null);

        if (tables.next()) {
            System.out.println("ThumbStore.checkAndCreateTables() table IMAGES exists!");
            checkOrAddColumns(dbm);
            // Table exists
        } else {
            System.out.println("ThumbStore.checkAndCreateTables() table IMAGES does not exist, should create it");
            // Table does not exist
            String table = "CREATE TABLE IMAGES(path varchar(256), size long, mtime long, md5 varchar(256), data blob,  lat double, lon double,  PRIMARY KEY ( path ))";
            Statement st = connexion.createStatement();
            st.execute(table);
            System.out.println("ThumbStore.checkAndCreateTables() table created!");
        }
        //now we look for the paths table
        tables = dbm.getTables(null, null, "PATHS", null);
        if (tables.next()) {
            System.out.println("ThumbStore.checkAndCreateTables() table PATHS exists!");
            // Table exists
        } else {
            System.out.println("ThumbStore.checkAndCreateTables() table PATHS does not exist, should create it");
            // Table does not exist
            String table = "CREATE TABLE PATHS(path varchar(256),  PRIMARY KEY ( path ))";
            Statement st = connexion.createStatement();
            st.execute(table);
            System.out.println("ThumbStore.checkAndCreateTables() table created!");
        }
    }

    private void checkOrAddColumns(DatabaseMetaData dbm) throws SQLException {

        ResultSet rs = dbm.getColumns(null, null, "IMAGES", "LAT");
        if (!rs.next()) {
            //Column in table exist
            System.out.println("Lat not found, updating table");
            Statement st = connexion.createStatement();
            st.executeUpdate("ALTER TABLE IMAGES ADD lat double");

        }
        rs = dbm.getColumns(null, null, "IMAGES", "LON");
        if (!rs.next()) {
            System.out.println("Lon not found, updating table");
            Statement st = connexion.createStatement();
            st.executeUpdate("ALTER TABLE IMAGES ADD lon double");
        }

    }


    public void addIndexPath(String path) {
        PreparedStatement psmnt;


        try {

            psmnt = connexion.prepareStatement("SELECT path FROM PATHS WHERE path=?");
            psmnt.setString(1, path);
            //		st = connexion.createStatement();
            psmnt.execute();
            ResultSet res = psmnt.getResultSet();
            if (res.next()) {
                //it's already in the DB, return
                return;
            }
            psmnt = connexion.prepareStatement("insert into PATHS(path)" + "values(?)");
            psmnt.setString(1, path);
            psmnt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the descriptor to the db DO NOT check that the key is not used
     *
     * @param id
     */
    public void saveToDB(MediaFileDescriptor id) {
        PreparedStatement psmnt;
        try {

            Statement st;
            psmnt = connexion.prepareStatement("insert into IMAGES(path, size, mtime, md5, data, lat, lon) "
                    + "values(?,?,?,?,?,?,?)");
            psmnt.setString(1, id.getPath());
            psmnt.setLong(2, id.getSize());
            psmnt.setLong(3, id.getMtime());

            // convert the int[] array to byte[] array
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            ObjectOutputStream oi = new ObjectOutputStream(ba);
            oi.writeObject(id.getData());
            oi.close();

            psmnt.setString(4, id.getMD5());
            psmnt.setBytes(5, ba.toByteArray());
            psmnt.setDouble(6, id.getLat());
            psmnt.setDouble(7, id.getLon());
            psmnt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateToDB(MediaFileDescriptor id) {
        PreparedStatement psmnt;
        try {
            Statement st;
            psmnt = connexion
                    .prepareStatement("UPDATE IMAGES SET path=?, size=?, mtime=?, data=?, md5=? , lat=?, lon=? WHERE path=? ");
            psmnt.setString(1, id.getPath());
            psmnt.setLong(2, id.getSize());
            psmnt.setLong(3, id.getMtime());

            psmnt.setBytes(4, id.getDataAsByte());
            psmnt.setString(5, id.getMD5());

            System.out.println("ThumbStore.updateToDB lat : " + id.getLat());
            psmnt.setDouble(6, id.getLat());
            psmnt.setDouble(7,id.getLon());
            psmnt.setString(8, id.getPath());

            psmnt.execute();
        } catch (SQLException e) {
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
        ResultSet res =  get(path);
        if (res!=null) {
            try {
                result = res.next();
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        return result;
    }

    private ResultSet get(String path) {
        ResultSet res = null;
        try {
            PreparedStatement psmnt = connexion.prepareStatement("SELECT * FROM IMAGES WHERE path=?");
            psmnt.setString(1, path);
            //		st = connexion.createStatement();
            psmnt.execute();
           res = psmnt.getResultSet();

        } catch (SQLException e) {
            e.printStackTrace();
        }
      return  res;
    }


    public ArrayList<String> getIndexedPaths() {
        Statement sta;
        ResultSet res = null;
        ArrayList<String> paths = new ArrayList<String>();
        try {
            sta = connexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            res = sta.executeQuery("SELECT * FROM PATHS");

            while (res.next()) {
                String s = res.getString("path");
                paths.add(s);
                System.out.println("path found " + s);

            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return paths;
    }


    public ArrayList<String> getAllWithGPS() {
        Statement sta;
        ResultSet res = null;
        ArrayList<String> al = null;
        try {
            sta = connexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            res = sta.executeQuery("SELECT * FROM IMAGES WHERE lat > 0");

            al = new ArrayList<String>();
            while (res.next()) {
                System.out.println("getAllWithGPS adding  " + res);
                al.add(res.getString("path"));
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return al;
    }

    public ResultSet getAllInDataBase() {
        Statement sta;
        ResultSet res = null;
        try {
            sta = connexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            res = sta.executeQuery("SELECT * FROM IMAGES");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public ResultSet getOrderedByMD5() {

        Statement sta;
        ResultSet res = null;
        try {
            sta = connexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
//			res = sta
//					.executeQuery("SELECT DISTINCT A.path, A.size, A.md5 from images A JOIN ( SELECT COUNT(*) as Count, B.md5   FROM Images B   GROUP BY B.md5) AS B ON A.md5 = B.md5 WHERE B.Count > 1 ORDER by A.md5;");
            res = sta
                    .executeQuery("SELECT path, md5, size from IMAGES order by md5;");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;

    }

    public ResultSet getDuplicatesMD5(MediaFileDescriptor mfd) {
        Statement sta;
        ResultSet res = null;
        try {
            sta = connexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            res = sta
                    .executeQuery("SELECT path, md5, size from IMAGES WHERE md5=\'" + mfd.getMD5() + "\'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;

    }

    /**
     * remove incorrect records from the DB
     */
    public void fix() {
        ResultSet all = this.getAllInDataBase();
        MediaFileDescriptor id = null;
        System.out.println("ThumbStore.fix() BD has " + this.size() + " entries");
        try {
            while (all.next()) {
                id = getCurrentMediaFileDescriptor(all);
                if (Utils.isValideImageName(id.path)) {
                    if (id.getData() == null || id.getMD5() == null) {
                        System.out.println("ThumbStore.fix() " + id.getPath() + " has null data ord md5");
                        all.deleteRow();
                    }
                }
                if (Utils.isValideVideoName(id.path)) {
                    if (id.getMD5() == null) {
                        System.out.println("ThumbStore.fix() " + id.getPath() + " has null md5");
                        all.deleteRow();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * remove outdated records from the DB An outdated record is one which has
     * no corresponding file on the FS
     */
    public void shrink() {
        ResultSet all = this.getAllInDataBase();
        MediaFileDescriptor id = null;
        System.out.println("ThumbStore.shrink() BD has " + this.size() + " entries");
        try {
            int i = 0;
            while (all.next()) {
                id = getCurrentMediaFileDescriptor(all);
                File tmp = new File(id.getPath());
                if (!tmp.exists()) {
                    i++;
                    all.deleteRow();
                }
            }
            System.out.println("ThumbStore.shrink() has deleted  " + i + " records");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MediaFileDescriptor getCurrentMediaFileDescriptor(ResultSet res) {
        MediaFileDescriptor id = null;
        try {
            // if (res.next()) {
            // id = new ImageDescriptor();
            String path = res.getString("path");
            byte[] d = res.getBytes("data");
            int[] idata = null;
            if (d != null) {
                ObjectInputStream oi = new ObjectInputStream(new ByteArrayInputStream(d));
                idata = (int[]) oi.readObject();
            } else {
                System.err.println("xxxx");
            }
            String md5 = res.getString("md5");
            long mtime = res.getLong("mtime");
            long size = res.getLong("size");
            id = new MediaFileDescriptor(path, size, mtime, idata, md5);
            // }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block             get
            e.printStackTrace();
        }
        return id;
    }

    public MediaFileDescriptor getMediaFileDescriptor(String path) {
        MediaFileDescriptor id = null;
        try {
           // System.out.println("path is " + path);
            ResultSet res = get(path);
            res.next();
//            ResultSetMetaData md = res.getMetaData();
//            int col = md.getColumnCount();
//            System.out.println("Number of Column : "+ col);
//            System.out.println("Columns Name: ");
//            for (int i = 1; i <= col; i++){
//                String col_name = md.getColumnName(i);
//                System.out.println(col_name);
//            }


           // return getCurrentMediaFileDescriptor(res);

            // if (res.next()) {
            // id = new ImageDescriptor();
           // String path = res.getString("path");
            byte[] d = res.getBytes("data");
            int[] idata = null;
            if (d != null) {
                ObjectInputStream oi = new ObjectInputStream(new ByteArrayInputStream(d));
                idata = (int[]) oi.readObject();
            } else {
                System.err.println("xxxx");
            }
            String md5 = res.getString("md5");
            long mtime = res.getLong("mtime");
            long size = res.getLong("size");
            id = new MediaFileDescriptor(path, size, mtime, idata, md5);
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
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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
        MediaIndexer tg = new MediaIndexer(this);
        String s = "/user/fhuet/desktop/home/workspaces/rechercheefficaceimagessimilaires/images/test.jpg";
        MediaFileDescriptor id = tg.buildMediaDescriptor(new File(s));
        System.out.println("ThumbStore.test() writting to database");
        // id.data=null;
        // id.mtime=0;
        saveToDB(id);
        // updateToDB(id);
        System.out.println("ThumbStore.test() dumping entries");
        String select = "SELECT * FROM IMAGES";
        Statement st;
        try {
            st = connexion.createStatement();

            ResultSet res = st.executeQuery(select);
            while (res.next()) {
                String i = res.getString("path");
                byte[] d = res.getBytes("data");
                System.out.println(i + " has data " + d + " and mtime " + res.getLong("mtime"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Testing update ");
        id.data = null;
        id.mtime = 0;
        updateToDB(id);
        System.out.println("ThumbStore.test() dumping entries");

        try {
            st = connexion.createStatement();

            ResultSet res = st.executeQuery(select);
            while (res.next()) {
                String i = res.getString("path");
                byte[] d = res.getBytes("data");
                System.out.println(i + " has data " + d + " and mtime " + res.getLong("mtime"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void testDuplicate() {
        System.out.println("ThumbStore.testDuplicate()");
        ResultSet rs = getOrderedByMD5();
        try {
            while (rs.next()) {
                System.out.println(rs.getLong("size") + "  " + rs.getString("path"));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getPath() {
        try {
            return new File(this.path).getCanonicalPath();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return this.path;
    }

    public static void main(String[] args) {

        ThumbStore ts = new ThumbStore("localDB");

        ts.test();
        ts.testDuplicate();
        ArrayList<String> al = ts.getAllWithGPS();
        for (Iterator<String> iterator = al.iterator(); iterator.hasNext(); ) {
            String next = iterator.next();
            System.out.println(next);
        }

    }

}
