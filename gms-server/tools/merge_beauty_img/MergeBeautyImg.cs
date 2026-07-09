using System.IO;
using MapleLib.WzLib;
using MapleLib.WzLib.Serializer;
using MapleLib.WzLib.Util;
using MapleLib.WzLib.WzProperties;

static WzImage LoadImg(string path) {
  byte[] data = File.ReadAllBytes(path);
  var img = new WzImage(Path.GetFileName(path), new MemoryStream(data), WzMapleVersion.GMS);
  if (!img.ParseImage()) throw new InvalidOperationException("parse fail: " + path);
  return img;
}

static void MergeChild(WzImage src, string child, WzImage dst) {
  var node = src[child] as WzImageProperty;
  if (node == null) throw new InvalidOperationException("missing child " + child + " in " + src.Name);
  if (dst[child] != null) dst.RemoveProperty(child);
  dst.AddProperty(node.DeepClone());
}

static void SaveImg(WzImage img, string path) {
  Directory.CreateDirectory(Path.GetDirectoryName(path)!);
  var wzFile = new WzFile((short)83, WzMapleVersion.GMS);
  var toSave = img.DeepClone();
  wzFile.WzDirectory.AddImage(toSave);
  string tmp = Path.Combine(Path.GetDirectoryName(path)!, Path.GetFileNameWithoutExtension(path) + ".beauty_tmp.img");
  var ser = new WzImgSerializer();
  ser.SerializeImage(toSave, tmp);
  File.Move(tmp, path, true);
}

if (args.Length < 1) {
  Console.WriteLine("Commands: copy-file <src> <dst> | merge-child <srcImg> <dstImg> <child> | merge-sounds <srcUI.img> <dstUI.img> <names...>");
  return 1;
}

string cmd = args[0];
if (cmd == "copy-file") {
  File.Copy(args[1], args[2], true);
  Console.WriteLine($"COPIED {args[2]} {new FileInfo(args[2]).Length}");
  return 0;
}
if (cmd == "merge-child") {
  var src = LoadImg(args[1]);
  WzImage dst = File.Exists(args[2]) ? LoadImg(args[2]) : new WzImage(Path.GetFileName(args[2]));
  MergeChild(src, args[3], dst);
  SaveImg(dst, args[2]);
  Console.WriteLine($"MERGED {args[3]} -> {args[2]} size={new FileInfo(args[2]).Length}");
  return 0;
}
if (cmd == "merge-sounds") {
  var src = LoadImg(args[1]);
  var dst = LoadImg(args[2]);
  int n = 0;
  for (int i = 3; i < args.Length; i++) {
    string name = args[i];
    if (dst[name] != null) continue;
    var node = src[name];
    if (node == null) { Console.WriteLine("SKIP missing src " + name); continue; }
    dst.AddProperty(node.DeepClone());
    n++;
    Console.WriteLine("ADD sound " + name);
  }
  SaveImg(dst, args[2]);
  Console.WriteLine($"SOUNDS merged {n} -> {args[2]} size={new FileInfo(args[2]).Length}");
  return 0;
}
Console.Error.WriteLine("unknown cmd");
return 2;

