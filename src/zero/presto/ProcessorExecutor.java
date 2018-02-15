package zero.presto;

import java.util.concurrent.*;

/**
 * Created by zero on 2/15/18.
 */
public class ProcessorExecutor {
  public int count = 0;
  public int total = Configs.apkList.size();
  public synchronized void finish(String apkName, boolean success) {
    System.out.printf("(%d/%d) %s : %s\n", ++count, total, success ? "Success" : "Failed", apkName);
  }

  public void runAll() {
    ExecutorService tpe = Executors.newFixedThreadPool(4);
    for (String str : Configs.apkList) {
      str = str.trim();
      if (str.isEmpty())
        continue;
      ApkProcessor task = new BroadcastProcessor(str, Configs.logDir, Configs.outDir);
      tpe.submit(task);
    }
    try {
      tpe.shutdown();
      tpe.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }
}
