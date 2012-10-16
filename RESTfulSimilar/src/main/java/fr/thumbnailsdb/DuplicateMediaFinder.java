package fr.thumbnailsdb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

public class DuplicateMediaFinder {

	protected ThumbStore thumbstore;

	public DuplicateMediaFinder(ThumbStore c) {
		this.thumbstore = c;
	}

	public ResultSet findDuplicateMedia() {
		return thumbstore.getDuplicatesMD5();
	}

	public void prettyPrintDuplicate(ResultSet r) {
		DuplicateList list = computeDuplicateSets(r);

		for (DuplicateGroup dg : list) {
			System.out.println(dg.fileSize + " (" + dg.fileSize * (dg.size() - 1) + " to save) ");
			System.out.println(dg);
		}
	}

	public DuplicateList computeDuplicateSets(ResultSet r) {
		DuplicateList list = new DuplicateList();
		System.out.println("DuplicateMediaFinder.prettyPrintDuplicate() ");
		// ArrayList<DuplicateGroup> al = new
		// ArrayList<DuplicateMediaFinder.DuplicateGroup>();
		DuplicateGroup dl = new DuplicateGroup();
		String currentMd5 = "";
		try {
			while (r.next()) {
				String md5 = r.getString("md5");
				if (md5 != null) {
					if (md5.equals(currentMd5)) {
						// add to current group
						dl.add(r.getLong("size"), r.getString("path"));
					} else {
						if (dl.size() > 1) {
							list.add(dl);
						}
						dl = new DuplicateGroup();
						dl.add(r.getLong("size"), r.getString("path"));
						currentMd5 = md5;

					}
				}
			}
			if (dl.size() > 1) {
				list.add(dl);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// TODO : save result for subsequent requests
	public String prettyHTMLDuplicate(ResultSet r, int max) {
		DuplicateList list = computeDuplicateSets(r);
		System.out.println("DuplicateMediaFinder.prettyHTMLDuplicate() " + max);
		String result = "";
		int i = 0;
		for (DuplicateGroup dg : list) {

			result += dg.fileSize + " (" + dg.fileSize * (dg.size() - 1) + " to save)\n";
			result += dg + "\n";
			i++;
			if (i > max) {
				break;
			}
		}
		System.out.println("DuplicateMediaFinder.prettyHTMLDuplicate() " + result);
		return result;
	}

}
