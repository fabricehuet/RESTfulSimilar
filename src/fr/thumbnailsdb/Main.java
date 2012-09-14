package fr.thumbnailsdb;

public class Main {

	public static void imageAction(String dbPath, String[] args) {
		System.out.println("Main.imageAction() db " + dbPath);
		for (int i = 0; i < args.length; i++) {
			System.out.println("    " + args[i]);
		}
		ThumbStore tb = new ThumbStore(dbPath);
		SimilarImageFinder si = new SimilarImageFinder(tb);
		if ("similar".equals(args[0])) {
			String source = args[1];
			si.prettyPrintResults(si.findSimilarImages(source), 5);
		}
	}

	public static void dbAction(String dbPath, String[] args) {
		System.out.println("Main.dbAction() db " + dbPath);
		for (int i = 0; i < args.length; i++) {
			System.out.println("    " + args[i]);
		}
		ThumbStore tb = new ThumbStore(dbPath);
		ThumbnailGenerator tg = new ThumbnailGenerator(tb);
		if ("index".equals(args[0])) {
			String source = args[1];
			tg.processMT(source);
		}
		if ("fix".equals(args[0])) {
			tb.fix();
		}
		if ("shrink".equals(args[0])) {
			tb.shrink();
		}
	}

	public static void main(String[] args) {
		// try {
		// Thread.sleep(5000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		if (args.length < 2) {
			System.err.println("Usage : java " + Main.class
					+ " [-db path_to_db]  target [options]");
			System.err.println("where target [options] are ");
			System.err
					.println("   db index  folder_or_file_to_process");
			System.err.println("   db clean");
			System.err.println("   db fix");
			System.err.println("   db shrink");
			System.err.println("   image similar folder_or_file_to_process");
			System.exit(-1);
		} else {
			int i = 0;
			String db = null;
			while (i < args.length) {
				if ("-db".equals(args[i])) {
					i++;
					db = args[i];
					i++;
					System.out.println("Database is " + db);
				} else if ("db".equals(args[i])) {
					i++;
					String[] newArgs = new String[args.length - i];
					System.arraycopy(args, i, newArgs, 0, args.length - i);
					dbAction(db, newArgs);
					break;

				} else if ("image".equals(args[i])) {
					i++;
					String[] newArgs = new String[args.length - i];
					System.arraycopy(args, i, newArgs, 0, args.length - i);
					imageAction(db, newArgs);
					break;

				}
			}

		}

	}

}
