package org.gms.tools;

import org.gms.config.GameConfig;
import org.gms.provider.wz.XMLDomMapleData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.*;
import java.util.Comparator;

/**
 * IMG文件系统 → XML 批量转换工具
 * 用法: java -cp BeiDou.jar org.gms.tools.ImgToXmlConverter F:\Data F:\083xml
 */
public class ImgToXmlConverter {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("用法: ImgToXmlConverter <IMG目录> <XML输出目录>");
            return;
        }
        Path inputDir = Path.of(args[0]);
        Path outputDir = Path.of(args[1]);

        if (!Files.exists(inputDir)) {
            System.err.println("输入目录不存在: " + inputDir);
            return;
        }
        Files.createDirectories(outputDir);

        convertDirectory(inputDir, outputDir, inputDir);
        System.out.println("转换完成！");
    }

    private static void convertDirectory(Path currentDir, Path outputBase, Path inputRoot) throws Exception {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentDir)) {
            for (Path entry : stream) {
                String name = entry.getFileName().toString();
                if (Files.isDirectory(entry)) {
                    Path relative = inputRoot.relativize(entry);
                    Path outSubDir = outputBase.resolve(relative);
                    Files.createDirectories(outSubDir);
                    convertDirectory(entry, outputBase, inputRoot);
                } else if (name.endsWith(".img")) {
                    Path relative = inputRoot.relativize(entry);
                    Path outFile = outputBase.resolve(relative + ".xml");
                    if (!Files.exists(outFile.getParent())) {
                        Files.createDirectories(outFile.getParent());
                    }
                    try {
                        convertImgFile(entry, outFile, name);
                    } catch (Exception e) {
                        System.err.println("转换失败: " + entry + " -> " + e.getMessage());
                    }
                }
            }
        }
    }

    @SuppressWarnings("java:S112")
    private static void convertImgFile(Path imgFile, Path xmlOutput, String imgName) throws Exception {
        // step1: 按路径跳转到对应分类目录，使用 wzimg 命令行提取属性树
        // step2: 由于服务端不直接读取 IMG 二进制，这里解析 XML 格式的属性数据

        // 最终 XML 根元素
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = doc.createElement("imgdir");
        root.setAttribute("name", imgName);
        doc.appendChild(root);

        // 使用 WzLib 解析 IMG 文件
        // 注：WzComparerR2.WzLib 提供 IMG 解析，此处递归写出 XML
        convertWithWzLib(imgFile, doc, root);

        // 写入文件
        try (FileOutputStream fos = new FileOutputStream(xmlOutput.toFile());
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            var transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(new DOMSource(doc), new StreamResult(bos));
        }
    }

    /**
     * 使用 WzComparerR2 WzLib 解析 IMG 并递归转为 XML 节点
     */
    private static void convertWithWzLib(Path imgFile, Document doc, Element parentElement) throws Exception {
        // 创建 Wz_File 并读取 IMG
        // 注：这步需要引用 WzComparerR2.WzLib 的运行时类
        // 此处通过反射加载，避免编译时依赖

        Object wzStructure = createWzStructure();
        Object wzFile = createWzFile(imgFile.toString(), wzStructure);
        Object imgImage = createWzImage(imgFile, wzFile);
        Object rootNode = extractImage(imgImage);

        if (rootNode != null) {
            convertNodeToXml(rootNode, doc, parentElement);
        }

        // 清理
        disposeWzFile(wzFile);
    }

    // ============== 反射调用 WzLib ==============

    private static Object createWzStructure() throws Exception {
        Class<?> clz = Class.forName("WzComparerR2.WzLib.Wz_Structure");
        Object instance = clz.getDeclaredConstructor().newInstance();
        clz.getMethod("set_ImgCheckDisabled", boolean.class).invoke(instance, true);
        return instance;
    }

    private static Object createWzFile(String path, Object wzStructure) throws Exception {
        Class<?> clz = Class.forName("WzComparerR2.WzLib.Wz_File");
        return clz.getDeclaredConstructor(String.class, Class.forName("WzComparerR2.WzLib.Wz_Structure")).newInstance(path, wzStructure);
    }

    private static Object createWzImage(Path imgFile, Object wzFile) throws Exception {
        Class<?> imgClz = Class.forName("WzComparerR2.WzLib.Wz_Image");
        long fileLen = Files.size(imgFile);
        return imgClz.getDeclaredConstructor(String.class, int.class, int.class, long.class, long.class,
                Class.forName("WzComparerR2.WzLib.IMapleStoryFile"))
                .newInstance(imgFile.getFileName().toString(), (int) fileLen, 0, 0L, 0L, wzFile);
    }

    private static Object extractImage(Object imgImage) throws Exception {
        Class<?> imgClz = imgImage.getClass();
        imgClz.getField("IsChecksumChecked").set(imgImage, true);
        imgClz.getField("Offset").setLong(imgImage, 0L);
        java.lang.reflect.Method tryExtract = imgClz.getMethod("TryExtract");
        boolean ok = (boolean) tryExtract.invoke(imgImage);
        if (!ok) {
            return null;
        }
        return imgClz.getField("Node").get(imgImage);
    }

    private static void disposeWzFile(Object wzFile) throws Exception {
        wzFile.getClass().getMethod("Close").invoke(wzFile);
    }

    // ============== WZ Node → XML 递归转换 ==============

    private static void convertNodeToXml(Object wzNode, Document doc, Element parentEl) throws Exception {
        Class<?> nodeClz = wzNode.getClass();
        String name = (String) nodeClz.getField("Text").get(wzNode);
        Object value = nodeClz.getField("Value").get(wzNode);
        Object nodes = nodeClz.getField("Nodes").get(wzNode);

        if (value instanceof Integer || value instanceof Long) {
            Element el = doc.createElement("int");
            el.setAttribute("name", name);
            el.setAttribute("value", String.valueOf(value));
            parentEl.appendChild(el);
        } else if (value instanceof Short) {
            Element el = doc.createElement("short");
            el.setAttribute("name", name);
            el.setAttribute("value", String.valueOf(value));
            parentEl.appendChild(el);
        } else if (value instanceof Float || value instanceof Double) {
            Element el = doc.createElement("float");
            el.setAttribute("name", name);
            el.setAttribute("value", String.valueOf(value));
            parentEl.appendChild(el);
        } else if (value instanceof String) {
            Element el = doc.createElement("string");
            el.setAttribute("name", name);
            el.setAttribute("value", (String) value);
            parentEl.appendChild(el);
        } else {
            // 子属性容器（SubProperty/Canvas/UOL等）
            Element el = doc.createElement("imgdir");
            el.setAttribute("name", name);
            parentEl.appendChild(el);

            if (nodes != null) {
                int count = (int) nodes.getClass().getMethod("getCount").invoke(nodes);
                for (int i = 0; i < count; i++) {
                    Object child = nodes.getClass().getMethod("get", int.class).invoke(nodes, i);
                    convertNodeToXml(child, doc, el);
                }
            }
        }
    }
}
