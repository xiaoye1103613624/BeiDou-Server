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
  Console.WriteLine("Commands: copy-file | merge-child | merge-sounds | inspect <img> [path] | merge-deep <srcImg> <dstImg> <path...>");
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
if (cmd == "inspect") {
  var img = LoadImg(args[1]);
  WzObject cur = img;
  if (args.Length > 2) {
    foreach (var part in args[2].Split('/', StringSplitOptions.RemoveEmptyEntries)) {
      if (cur is WzImage wi) cur = wi[part];
      else if (cur is IPropertyContainer pc) cur = pc[part];
      else { cur = null; break; }
      if (cur == null) {
        Console.WriteLine("MISSING " + args[2]);
        return 2;
      }
    }
  }
  void Dump(WzObject o, string indent, int depth) {
    if (depth < 0 || o == null) return;
    string extra = o is WzCanvasProperty c ? $" canvas {c.PngProperty?.Width}x{c.PngProperty?.Height}" : "";
    Console.WriteLine(indent + o.Name + " (" + o.GetType().Name + ")" + extra);
    if (o is IPropertyContainer container) {
      foreach (var p in container.WzProperties) Dump(p, indent + "  ", depth - 1);
    }
  }
  Dump(cur, "", args.Length > 2 ? 4 : 1);
  return 0;
}
if (cmd == "merge-deep") {
  // merge-deep <srcImg> <dstImg> <path/to/node>  — copy nested node into dst at same path
  var src = LoadImg(args[1]);
  var dst = LoadImg(args[2]);
  string[] parts = args[3].Split('/', StringSplitOptions.RemoveEmptyEntries);
  WzObject srcCur = src;
  for (int i = 0; i < parts.Length; i++) {
    if (srcCur is WzImage wi) srcCur = wi[parts[i]];
    else if (srcCur is IPropertyContainer pc) srcCur = pc[parts[i]];
    else { srcCur = null; break; }
  }
  if (srcCur is not WzImageProperty srcProp) {
    Console.Error.WriteLine("SRC missing " + args[3]);
    return 2;
  }
  // Ensure parent path exists on dst; create empty subprops as needed not supported — parent must exist
  IPropertyContainer parent = dst;
  for (int i = 0; i < parts.Length - 1; i++) {
    var next = parent[parts[i]];
    if (next == null) {
      var created = new WzSubProperty(parts[i]);
      parent.AddProperty(created);
      parent = created;
    } else if (next is IPropertyContainer npc) {
      parent = npc;
    } else {
      Console.Error.WriteLine("DST path not container: " + parts[i]);
      return 2;
    }
  }
  string leaf = parts[^1];
  if (parent[leaf] != null) parent.RemoveProperty(leaf);
  parent.AddProperty(srcProp.DeepClone());
  SaveImg(dst, args[2]);
  Console.WriteLine($"MERGED-DEEP {args[3]} -> {args[2]} size={new FileInfo(args[2]).Length}");
  return 0;
}
Console.Error.WriteLine("unknown cmd");
return 2;

