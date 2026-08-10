using System;
using System.IO;
using MapleLib.WzLib;
using MapleLib.WzLib.Util;

class P {
  static int Main(string[] args) {
    foreach (var path in args) {
      try {
        byte[] data = File.ReadAllBytes(path);
        var img = new WzImage(Path.GetFileName(path), new MemoryStream(data), WzMapleVersion.GMS);
        if (!img.ParseImage()) { Console.WriteLine("FAIL parse: " + path); return 1; }
        Console.WriteLine("OK parse: " + path + " size=" + data.Length + " props=" + img.WzProperties.Count);
        foreach (var p in new[]{"Synthesizing","Bag","DailyCheckin","DamageSkin"}) {
          var n = img[p];
          Console.WriteLine("  " + p + ": " + (n != null ? "YES" : "no"));
        }
      } catch (Exception ex) {
        Console.WriteLine("EX " + path + ": " + ex.Message);
        return 1;
      }
    }
    return 0;
  }
}
