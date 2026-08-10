using MapleLib.WzLib;
using MapleLib.WzLib.Serializer;
using MapleLib.WzLib.Util;
using MapleLib.WzLib.WzProperties;

byte[] gmsIv = WzTool.GetIvByMapleVersion(WzMapleVersion.GMS);
var deser = new WzImgDeserializer(false);
string ringDir = @"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data\Character\Ring";
string ringOut = @"E:\pro\BeiDou-Server_xy\tools\soul-eff-extract\out\rings_witheff";
Directory.CreateDirectory(ringOut);
for (int id = 1115201; id <= 1115234; id++)
{
    string name = id.ToString("D8") + ".img";
    var img = deser.WzImageFromIMGFile(Path.Combine(ringDir, name), gmsIv, name, out _);
    img.ParseImage();
    var info = (WzSubProperty)img["info"]!;
    if (info["effect"] != null) info.RemoveProperty(info["effect"]);
    var effect = new WzSubProperty("effect");
    effect.AddProperty(new WzStringProperty("path", $"Effect/CharacterEff.img/{id}"));
    effect.AddProperty(new WzIntProperty("z", -1));
    effect.AddProperty(new WzIntProperty("pos", 1));
    effect.AddProperty(new WzIntProperty("fixed", 1));
    effect.AddProperty(new WzIntProperty("animate", 1));
    info.AddProperty(effect);
    if (info["onlyEquip"] == null) info.AddProperty(new WzIntProperty("onlyEquip", 1));
    string tmp = Path.Combine(ringOut, name);
    using (var fs = File.Create(tmp))
    using (var w = new WzBinaryWriter(fs, gmsIv))
        img.SaveImage(w, true, forceReadFromData: true);
}
GC.Collect(); GC.WaitForPendingFinalizers();
int ok=0;
foreach (var f in Directory.GetFiles(ringOut, "*.img"))
{
    string dest = Path.Combine(ringDir, Path.GetFileName(f));
    for (int i=0;i<40;i++){
      try { File.Copy(f, dest, true); ok++; break; }
      catch { Thread.Sleep(200); if(i==39) Console.WriteLine("FAIL "+Path.GetFileName(f)); }
    }
}
Console.WriteLine("rings copied="+ok);

// verify
var ce = deser.WzImageFromIMGFile(@"E:\mxd_soft\2.客户端\083\beidou_client_xiaoye\BeiDou-Client_1\Data\Effect\CharacterEff.img", gmsIv, "CharacterEff.img", out _);
ce.ParseImage();
var c = (WzCanvasProperty)ce["1115201"]!["0"]!["0"]!;
var head = c.PngProperty.GetCompressedBytes(true)!;
Console.WriteLine($"live ce fmt={c.PngProperty.Format} head={BitConverter.ToString(head,0,2)}");
var r = deser.WzImageFromIMGFile(Path.Combine(ringDir,"01115201.img"), gmsIv, "01115201.img", out _);
r.ParseImage();
Console.WriteLine("ring path="+((WzStringProperty)r["info"]!["effect"]!["path"]!).Value);
Console.WriteLine("ring icon="+((WzCanvasProperty)r["info"]!["icon"]!).PngProperty.Format);
