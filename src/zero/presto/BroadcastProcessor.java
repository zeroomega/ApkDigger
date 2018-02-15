package zero.presto;

import com.google.common.collect.Lists;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.List;

/**
 * Created by zero on 2/14/18.
 */
public class BroadcastProcessor extends ApkProcessor {
  protected static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";
  protected String packageName;
  protected PrintStream bw;
  public BroadcastProcessor(String apkFile, String logDir, String outDir) {
    super(apkFile, logDir, outDir);
    bw = null;
  }

  @Override
  public void traverse() {
    traverseManifest();
  }

  private void traverseManifest() {
    String manifestPath = this.tempDir.getAbsolutePath() + "/AndroidManifest.xml";
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    dbFactory.setNamespaceAware(true);
    try {
      DocumentBuilder db = dbFactory.newDocumentBuilder();
      Document d = db.parse(new File(this.tempDir, "AndroidManifest.xml"));
      Node root = d.getElementsByTagName("manifest").item(0);
      this.packageName = root.getAttributes().getNamedItem("package").getTextContent();
      NodeList receiverList = d.getElementsByTagName("receiver");
      for (int i = 0; i < receiverList.getLength(); i++ ) {
        Node receiver = receiverList.item(i);
        String receiverClass = receiver.getAttributes().getNamedItemNS(ANDROID_NS, "name")
                .getTextContent();
        List<String> actionList = Lists.newArrayList();
        List<String> categoryList = Lists.newArrayList();
        for (int j = 0; j < receiver.getChildNodes().getLength(); j++) {
          Node intent = receiver.getChildNodes().item(j);
          if (!intent.getNodeName().equals("intent-filter"))
            continue;
          for (int k = 0; k < intent.getChildNodes().getLength(); k++) {
            Node actionCat = intent.getChildNodes().item(k);
            String nodeName = actionCat.getNodeName();
            if (!(nodeName.equals("action") || nodeName.equals("category")))
              continue;
            Node nameNode = actionCat.getAttributes().getNamedItemNS(ANDROID_NS, "name");
            String actionName = nameNode == null ? "" : nameNode.getTextContent();
            if (actionName.isEmpty())
              continue;
            if (nodeName.equals("action")) {
              actionList.add(actionName);
            }
            if (nodeName.equals("category")) {
              categoryList.add(actionName);
            }
          }
        }
        logReceiver(receiverClass, actionList, categoryList);
      }

    } catch (Exception e) {
      e.printStackTrace();
      this.success = false;
    }
  }

  private void logReceiver(String className, List<String> actions, List<String> categories) {
    if (bw == null) {
      try {
        bw = new PrintStream(new BufferedOutputStream(new FileOutputStream(
                new File(this.outDir, this.apkName + ".log"))));
      } catch (IOException e) {
        System.err.println("IOException when create output file");
        e.printStackTrace();
        this.success = false;
        System.exit(-1);
      }
    }
    try {
      bw.printf("Receiver: %s\n", className);
      for (String str : actions) {
        bw.printf("\tAction: %s\n", str);
      }
      for (String str : categories) {
        bw.printf("\tCategory: %s\n", str);
      }
      bw.println();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void clean() {
    if (bw != null) {
      bw.close();
      bw = null;
    }
    super.clean();
  }
}
