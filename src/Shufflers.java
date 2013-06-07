import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

public class Shufflers {
	
//	private static final String INPUT_FILENAME = "sample.txt";
	private static final String INPUT_FILENAME = "shufflers.txt";
	private static final String ANSWER_FILENAME = "answer.txt";
	
	private TreeMap<String, TreeSet<String>> idSets = new TreeMap<String, TreeSet<String>>();
	private HashSet<String> solvedIds = new HashSet<String>();
	
	public static void main(String[] args) throws IOException {
		long time = System.currentTimeMillis();
		Shufflers shufflers = new Shufflers();
		shufflers.readFile(INPUT_FILENAME);
		shufflers.start();
		shufflers.writeAnswerFile(ANSWER_FILENAME);
		System.out.printf("time = %dms\n", (System.currentTimeMillis() - time));
	}

	public void start() {
		// 片側だけで解けるかも？
		boolean find = true;
		while (find) {
			find = false;
			for (Map.Entry<String, TreeSet<String>> entry : idSets.entrySet()) {
				if (!solvedIds.contains(entry.getKey())) {
					int size = entry.getValue().size();
					if (size == 1) {
						find = true;
						solvedIds.add(entry.getKey());
						removeId(entry.getKey(), entry.getValue().first());
					} else if (size == 0) {
						System.err.println("なくなっちゃった：" + entry.getKey());
					}
				}
			}
		}
	}
	
	private void removeId(String key, String name) {
		for (Map.Entry<String, TreeSet<String>> entry : idSets.entrySet()) {
			if (!key.equals(entry.getKey())) {
				entry.getValue().remove(name);
			}
		}
	}

	private void readFile(String filename) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line;
			
			while ((line = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line);
				int count = tokenizer.countTokens();
				if (count > 0) {
					if ((count & 1) == 1) {
						int size = count >> 1;
						String[] ids = new String[size];
						String[] names = new String[size];
						for (int i = 0; tokenizer.hasMoreTokens(); ++i) {
							String token = tokenizer.nextToken();
							if (i < size) {
								ids[i] = token;
							} else if (i == size) {
								if (!token.equals("=")) {
									System.err.println("= の区切りがおかしいです。：" + line);
								}
							} else {
								names[i - size - 1] = token;
							}
						}
						for (String id : ids) {
							TreeSet<String> newSet = new TreeSet<String>();
							TreeSet<String> set = idSets.get(id);
							for (String name : names) {
								if (set == null || set.contains(name)) {
									newSet.add(name);
								}
							}
							idSets.put(id, newSet);
						}
					} else {
						System.err.println("トークン数が偶数です：" + line);
					}
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	private void writeAnswerFile(String answerFilename) throws IOException {
		PrintStream out = null;
		
		try {
			out = new PrintStream(new FileOutputStream(ANSWER_FILENAME));
			
			for (Map.Entry<String, TreeSet<String>> entry : idSets.entrySet()) {
				//System.out.println(entry);
				if (entry.getValue().size() != 1) {
					// 仲間を探します
					TreeSet<String> ids = searchIds(entry.getValue());
					if (entry.getKey().equals(ids.first())) {
						StringBuilder builder = new StringBuilder();
						for (String id : ids) {
							builder.append(id);
							builder.append(' ');
						}
						builder.append('=');
						for (String name : entry.getValue()) {
							builder.append(' ');
							builder.append(name);
						}						
						out.println(builder);
					}
				} else {
					out.println(entry.getKey() + " = " + entry.getValue().first());
				}
			}
			
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	private TreeSet<String> searchIds(TreeSet<String> value) {
		TreeSet<String> ids = new TreeSet<String>();
		for (Map.Entry<String, TreeSet<String>> entry : idSets.entrySet()) {
			//System.out.println(entry);
			if (value.equals(entry.getValue())) {
				ids.add(entry.getKey());
			}
		}

		return ids;
	}
}