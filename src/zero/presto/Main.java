package zero.presto;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class Main {
  public static void main(String[] args) {
	  // write your code here
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-p")) {
        Configs.procCount = Integer.parseInt(args[++i]);
      } else if (args[i].equals("-a")) {
        Configs.apkToolPath = args[++i];
      } else if (args[i].equals("-o")) {
        Configs.outputDir = args[++i];
      } else {
        Configs.apkListFile = args[i];
      }
    }
    if (Configs.apkToolPath == null || Configs.outputDir == null || Configs.apkListFile == null) {
      System.exit(0);
    }
    ApkProcessor.deleteFile(new File(Configs.outputDir));
    File outFileDir = new File(Configs.outputDir, "out");
    outFileDir.mkdirs();
    Configs.outDir = outFileDir.getAbsolutePath();

    File logFileDir = new File(Configs.outputDir, "log");
    logFileDir.mkdirs();
    Configs.logDir = logFileDir.getAbsolutePath();
    //test();
    readApkListFile();
    Configs.pe = new ProcessorExecutor();
    Configs.pe.runAll();
    System.exit(0);
  }

  private static void readApkListFile() {
    if (Configs.apkListFile.isEmpty()) {
      System.err.println("ApkList file is missing");
      System.exit(-1);
    }

    try {
      File apkListFile = new File(Configs.apkListFile);
      if (!apkListFile.exists()) {
        System.err.println("ApkList file is missing");
        System.exit(-1);
      }

      Configs.apkList = Files.readLines(apkListFile, Charsets.UTF_8);

    } catch (IOException e) {
      System.err.println("IOException when reading the apkFile");
      e.printStackTrace();
    }
  }

  private static void test() {
    BroadcastProcessor bp = new BroadcastProcessor("/export/share/all-apk/fdroid/com.csipsimple_2459.apk", "/home/zero/SRC/test/log", "/home/zero/SRC/test/out");
    bp.run();
  }
}
