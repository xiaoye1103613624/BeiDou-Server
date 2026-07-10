using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using MapleLib.WzLib;
using MapleLib.WzLib.Serializer;
using MapleLib.WzLib.Util;
using MapleLib.WzLib.WzProperties;

static WzImage LoadImg(string path)
{
    byte[] data = File.ReadAllBytes(path);
    var img = new WzImage(Path.GetFileName(path), new MemoryStream(data), WzMapleVersion.GMS);
    if (!img.ParseImage()) throw new InvalidOperationException("parse fail: " + path);
    return img;
}

static void SaveImg(WzImage img, string path)
{
    Directory.CreateDirectory(Path.GetDirectoryName(path)!);
    var wzFile = new WzFile((short)83, WzMapleVersion.GMS);
    var toSave = img.DeepClone();
    wzFile.WzDirectory.AddImage(toSave);
    string tmp = Path.Combine(Path.GetDirectoryName(path)!,
        Path.GetFileNameWithoutExtension(path) + ".storage_bg_tmp.img");
    var ser = new WzImgSerializer();
    ser.SerializeImage(toSave, tmp);
    File.Move(tmp, path, true);
}

const int TrunkSlotFirstY = 107; // CUIStorage merchant draw loop (IDA: Y=107, step +40)
const int TrunkSlotArtFirstY = 122; // WZ slot-box tops align 122+40n (both columns; +15px from code Y)

static int FindTrunkFooterStart(Bitmap src)
{
    int h = src.Height;
    // Meso footer divider: scan bottom-up so extended images resolve the real footer, not stale rows.
    for (int y = h - 15; y >= 70; y--)
    {
        Color mid = src.GetPixel(200, y);
        if (mid.A > 100 && mid.R < 100 && mid.G < 130 && mid.B > 140)
        {
            int top = y;
            while (top > 70)
            {
                Color above = src.GetPixel(200, top - 1);
                if (above.A > 100 && above.R < 100 && above.G < 130 && above.B > 140)
                    top--;
                else
                    break;
            }
            return top;
        }
    }
    return Math.Max(70, h - 27);
}

static int FindTrunkTransitionStart(int footerStart, int rowH, int slotFirstY)
{
    // Partial strip between last full slot row and footer (e.g. 267..289 before footer at 290).
    int remainder = (footerStart - slotFirstY) % rowH;
    return remainder == 0 ? footerStart - rowH : footerStart - remainder;
}

static int FindTrunkTileRowStart(Bitmap src, int transitionStart, int rowH, int slotArtFirstY)
{
    // transitionStart is art-grid aligned (122+40n); tile the last full row before it.
    int preferred = transitionStart - rowH;
    if (preferred >= slotArtFirstY)
        return preferred;

    for (int start = preferred; start >= slotArtFirstY; start -= rowH)
    {
        if (IsTrunkSlotRow(src, start, rowH))
            return start;
    }
    return slotArtFirstY + rowH;
}

static bool IsTrunkSlotRow(Bitmap src, int start, int rowH)
{
    // Name-area blue fill (x=350) marks full slot rows; left slot boxes are beige and vary.
    int probeY = Math.Min(start + rowH / 2, src.Height - 1);
    Color slot = src.GetPixel(350, probeY);
    return slot.B >= 150 && slot.G >= 100 && slot.R <= 220;
}

static int DefaultTargetHeightForHigherStorageList(int transitionStart, int bottomH)
{
    // higherstoragelist patches move meso controls from Y=294 to Y=454 (+160px).
    const int mesoDelta = 160;
    return transitionStart + mesoDelta + bottomH;
}

static void DumpTrunkNodes(string path)
{
    var img = LoadImg(path);
    var trunk = img["Trunk"] as WzSubProperty
        ?? throw new InvalidOperationException("missing Trunk");
    Console.WriteLine($"Trunk children in {Path.GetFileName(path)}:");
    foreach (var p in trunk.WzProperties)
    {
        string extra = "";
        if (p is WzCanvasProperty canvas)
            extra = $" {canvas.PngProperty?.Width}x{canvas.PngProperty?.Height}";
        else if (p is WzVectorProperty vec)
            extra = $" ({vec.X?.Value},{vec.Y?.Value})";
        else if (p is WzIntProperty ip)
            extra = $" ={ip.Value}";
        Console.WriteLine($"  {p.Name,-20} {p.PropertyType}{extra}");
    }
}

static void Inspect(string path)
{
    var img = LoadImg(path);
    var trunk = img["Trunk"]?["backgrnd"] as WzCanvasProperty;
    var shop = img["Shop"]?["backgrnd"] as WzCanvasProperty;
    var bag = img["Bag"]?["backgrnd"] as WzCanvasProperty;
    var synth = img["Synthesizing"]?["backgrnd"] as WzCanvasProperty;
    var checkin = img["DailyCheckin"]?["backgrnd"] as WzCanvasProperty;
    var dmg = img["DamageSkin"]?["backgrnd"] as WzCanvasProperty;
    string Dim(WzCanvasProperty? c) => c == null ? "MISSING" : $"{c.PngProperty?.Width}x{c.PngProperty?.Height}";
    Console.WriteLine($"{Path.GetFileName(path),45} bytes={new FileInfo(path).Length,12} Trunk={Dim(trunk),-12} Shop={Dim(shop)}");
    Console.WriteLine($"{"",45} Bag={Dim(bag),-12} Synthesizing={Dim(synth)} DailyCheckin={Dim(checkin)} DamageSkin={Dim(dmg)}");
}

static void RestoreTrunkBg(string targetPath, string sourcePath)
{
    var target = LoadImg(targetPath);
    var source = LoadImg(sourcePath);
    var trunkTarget = target["Trunk"] as WzSubProperty
        ?? throw new InvalidOperationException("missing Trunk in target");
    var trunkSourceBg = source["Trunk"]?["backgrnd"] as WzCanvasProperty
        ?? throw new InvalidOperationException("missing Trunk/backgrnd in source");

    if (trunkTarget["backgrnd"] != null) trunkTarget.RemoveProperty("backgrnd");
    trunkTarget.AddProperty(trunkSourceBg.DeepClone());
    SaveImg(target, targetPath);
    Console.WriteLine($"RESTORED Trunk/backgrnd from {Path.GetFileName(sourcePath)} -> {targetPath}");
}

static void ExtendTrunkBg(string imgPath, int targetHeight, string outDir)
{
    Directory.CreateDirectory(outDir);
    var img = LoadImg(imgPath);
    var trunkNode = img["Trunk"] as WzSubProperty
        ?? throw new InvalidOperationException("missing Trunk");
    var trunkBg = trunkNode["backgrnd"] as WzCanvasProperty
        ?? throw new InvalidOperationException("missing Trunk/backgrnd");

    int srcW = trunkBg.PngProperty?.Width ?? 0;
    int srcH = trunkBg.PngProperty?.Height ?? 0;
    if (srcW <= 0 || srcH <= 0) throw new InvalidOperationException("invalid Trunk/backgrnd size");

    string srcPng = Path.Combine(outDir, "Trunk_backgrnd_src.png");
    using (var srcBmp = trunkBg.GetLinkedWzCanvasBitmap())
    {
        srcBmp.Save(srcPng, ImageFormat.Png);
    }

    int newH = Math.Max(targetHeight, srcH);
    string extPng = Path.Combine(outDir, $"Trunk_backgrnd_{srcW}x{newH}.png");
    using (var srcBmp = new Bitmap(srcPng))
    using (var dstBmp = new Bitmap(srcW, newH, PixelFormat.Format32bppArgb))
    using (var g = Graphics.FromImage(dstBmp))
    {
        g.Clear(Color.FromArgb(0, 0, 0, 0));
        int footerStart = FindTrunkFooterStart(srcBmp);
        const int rowH = 40; // CUIStorage item row step (IDA: v61/v60 += 40)
        int transitionStart = FindTrunkTransitionStart(footerStart, rowH, TrunkSlotArtFirstY);
        int bottomH = srcH - transitionStart; // transition strip + meso footer
        int tileStart = FindTrunkTileRowStart(srcBmp, transitionStart, rowH, TrunkSlotArtFirstY);

        if (newH <= srcH)
        {
            g.DrawImage(srcBmp, 0, 0, srcW, newH);
        }
        else
        {
            int insertH = newH - transitionStart - bottomH;
            g.DrawImage(srcBmp, new Rectangle(0, 0, srcW, transitionStart), 0, 0, srcW, transitionStart, GraphicsUnit.Pixel);
            for (int y = transitionStart; y < transitionStart + insertH; y += rowH)
            {
                int drawH = Math.Min(rowH, transitionStart + insertH - y);
                g.DrawImage(srcBmp,
                    new Rectangle(0, y, srcW, drawH),
                    0, tileStart, srcW, drawH, GraphicsUnit.Pixel);
            }
            g.DrawImage(srcBmp,
                new Rectangle(0, newH - bottomH, srcW, bottomH),
                0, transitionStart, srcW, bottomH, GraphicsUnit.Pixel);
        }
        dstBmp.Save(extPng, ImageFormat.Png);
    }

    using (var probeBmp = new Bitmap(srcPng))
    {
        int footerStart = FindTrunkFooterStart(probeBmp);
        int transitionStart = FindTrunkTransitionStart(footerStart, 40, TrunkSlotArtFirstY);
        int tileStart = FindTrunkTileRowStart(probeBmp, transitionStart, 40, TrunkSlotArtFirstY);
        Console.WriteLine($"EXPORT src={srcW}x{srcH} footerStart={footerStart} transitionStart={transitionStart} tileRow={tileStart} -> {extPng} ({srcW}x{newH})");
    }

    var newCanvas = new WzCanvasProperty("backgrnd");
    newCanvas.PngProperty = new WzPngProperty();
    using (var extBmp = new Bitmap(extPng))
    {
        newCanvas.PngProperty.PNG = new Bitmap(extBmp);
    }
    int originX = 0;
    int originY = 0;
    if (trunkBg["origin"] is WzVectorProperty originProp)
    {
        originX = originProp.X?.Value ?? 0;
        originY = originProp.Y?.Value ?? 0;
    }
    newCanvas.AddProperty(new WzVectorProperty("origin",
        new WzIntProperty("X", originX),
        new WzIntProperty("Y", originY)));

    if (trunkNode["backgrnd"] != null) trunkNode.RemoveProperty("backgrnd");
    trunkNode.AddProperty(newCanvas);
    SaveImg(img, imgPath);
    Console.WriteLine($"PATCHED {imgPath} Trunk/backgrnd={srcW}x{newH} bytes={new FileInfo(imgPath).Length}");
}

if (args.Length < 1)
{
    Console.WriteLine("usage:");
    Console.WriteLine("  PatchStorageBg inspect <UIWindow.img> [...]");
    Console.WriteLine("  PatchStorageBg restore-trunk <target.img> <source.img>");
    Console.WriteLine("  PatchStorageBg extend <UIWindow.img> <targetHeight> <pngOutDir>");
    Console.WriteLine("  PatchStorageBg extend-auto <UIWindow.img> <pngOutDir>  (meso+160 => ~478px; art grid 122+40n, splice@282, tile@242)");
    Console.WriteLine("  PatchStorageBg dump-trunk <UIWindow.img>");
    Console.WriteLine("  PatchStorageBg shopcopy <UIWindow.img> <pngOutDir>  (legacy, not recommended)");
    return 1;
}

string cmd = args[0].ToLowerInvariant();
if (cmd == "inspect")
{
    foreach (string path in args.Skip(1)) Inspect(path);
    return 0;
}

if (cmd == "restore-trunk")
{
    if (args.Length < 3)
    {
        Console.WriteLine("usage: PatchStorageBg restore-trunk <target.img> <source.img>");
        return 1;
    }
    RestoreTrunkBg(args[1], args[2]);
    Inspect(args[1]);
    return 0;
}

if (cmd == "extend")
{
    if (args.Length < 4)
    {
        Console.WriteLine("usage: PatchStorageBg extend <UIWindow.img> <targetHeight> <pngOutDir>");
        return 1;
    }
    ExtendTrunkBg(args[1], int.Parse(args[2]), args[3]);
    return 0;
}

if (cmd == "dump-trunk")
{
    if (args.Length < 2)
    {
        Console.WriteLine("usage: PatchStorageBg dump-trunk <UIWindow.img>");
        return 1;
    }
    DumpTrunkNodes(args[1]);
    return 0;
}

if (cmd == "extend-auto")
{
    if (args.Length < 3)
    {
        Console.WriteLine("usage: PatchStorageBg extend-auto <UIWindow.img> <pngOutDir>");
        return 1;
    }
    var probe = LoadImg(args[1]);
    var probeBg = probe["Trunk"]?["backgrnd"] as WzCanvasProperty
        ?? throw new InvalidOperationException("missing Trunk/backgrnd");
    using var probeBmp = probeBg.GetLinkedWzCanvasBitmap();
    int footerStart = FindTrunkFooterStart(probeBmp);
    int transitionStart = FindTrunkTransitionStart(footerStart, 40, TrunkSlotArtFirstY);
    int bottomH = probeBmp.Height - transitionStart;
    int targetH = DefaultTargetHeightForHigherStorageList(transitionStart, bottomH);
    Console.WriteLine($"AUTO targetHeight={targetH} (footerStart={footerStart} transitionStart={transitionStart} bottomH={bottomH})");
    ExtendTrunkBg(args[1], targetH, args[2]);
    Inspect(args[1]);
    return 0;
}

if (cmd == "shopcopy")
{
    if (args.Length < 3)
    {
        Console.WriteLine("usage: PatchStorageBg shopcopy <UIWindow.img> <pngOutDir>");
        return 1;
    }
    string imgPath = args[1];
    string outDir = args[2];
    Directory.CreateDirectory(outDir);
    var img = LoadImg(imgPath);
    var shopBg = img["Shop"]?["backgrnd"] as WzCanvasProperty
        ?? throw new InvalidOperationException("missing Shop/backgrnd");
    var trunk = img["Trunk"] as WzSubProperty
        ?? throw new InvalidOperationException("missing Trunk");
    string pngPath = Path.Combine(outDir, "Shop_backgrnd_default.png");
    using (var bmp = shopBg.GetLinkedWzCanvasBitmap())
    {
        bmp.Save(pngPath, ImageFormat.Png);
    }
    Console.WriteLine($"EXPORT {pngPath} {shopBg.PngProperty?.Width}x{shopBg.PngProperty?.Height}");
    if (trunk["backgrnd"] != null) trunk.RemoveProperty("backgrnd");
    trunk.AddProperty(shopBg.DeepClone());
    SaveImg(img, imgPath);
    Console.WriteLine($"PATCHED Trunk/backgrnd <- Shop/backgrnd size={new FileInfo(imgPath).Length}");
    return 0;
}

Console.WriteLine($"unknown command: {cmd}");
return 1;
