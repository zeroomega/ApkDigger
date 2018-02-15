package zero.presto;

import java.util.List;

/**
 * Created by zero on 2/14/18.
 */
public class Configs {
  static String apkListFile;
  static int procCount = 4;
  static Object lock = new Object();
  static List<String> apkList;
  static String apkToolPath;
  static String outputDir;
  static ProcessorExecutor pe;
  static String logDir;
  static String outDir;
}
