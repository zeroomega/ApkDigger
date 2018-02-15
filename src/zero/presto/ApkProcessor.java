package zero.presto;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * Created by zero on 2/14/18.
 */
public abstract class ApkProcessor implements Runnable {
  protected String apkFile;
  protected String logDir;
  protected String outDir;
  protected File tempDir;
  protected String apkName;
  protected boolean success;
  public ApkProcessor(String apkFile, String logDir, String outDir) {
    this.apkFile = apkFile;
    this.logDir = logDir;
    this.outDir = outDir;
    File apkFO = new File(apkFile);
    this.apkName = apkFO.getName();
    this.success = true;
  }

  @Override
  public void run() {
    unpack();
    traverse();
    clean();
    Configs.pe.finish(this.apkName, this.success);
  }

  protected void unpack() {
    tempDir = Files.createTempDir();
    File logFile = new File(logDir, apkName + ".log");
    ProcessBuilder builder = new ProcessBuilder(
            "/usr/bin/java", "-jar",
            Configs.apkToolPath,
            "d",
            this.apkFile,
            "-o",
            tempDir.getAbsolutePath(),
            "-f");
    builder.redirectError(logFile);
    builder.redirectOutput(logFile);
    try {
      Process p = builder.start();
      int retVal = p.waitFor();
      if (retVal != 0) {
        System.err.println("Unpack failure at :" + apkFile);
        this.success = false;
      }
    } catch (IOException e) {
      System.err.println("IOException at unpack: " + apkFile);
      e.printStackTrace();
      this.success = false;
    } catch (InterruptedException e) {
      System.err.println("Interrupted at unpacking: " + apkFile);
      e.printStackTrace();
      this.success = false;
    }
  }

  public abstract void traverse();

  protected void clean() {
    if (apkFile.isEmpty())
      return;
    apkFile = "";
    deleteFile(tempDir);
  }

  public static void deleteFile(File element) {
    if (element.isDirectory()) {
      for (File sub : element.listFiles()) {
        deleteFile(sub);
      }
    }
    element.delete();
  }

  @Override
  protected void finalize() {
    this.clean();
  }
}
