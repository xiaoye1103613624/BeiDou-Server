import json, re, shutil, subprocess, urllib.request
from datetime import datetime
from pathlib import Path

SERVER_XML = Path(r"E:/pro/BeiDou-Server_xy/gms-server/wz/Effect.wz/BasicEff.img.xml")
CLIENT_IMG = Path(r"E:/mxd_soft/2.客户端/083/beidou_client_xiaoye/BeiDou-Client_1/Data/Effect/BasicEff.img")
TMP = Path(r"E:/pro/BeiDou-Server_xy/gms-server/tools/_dmg_skin_tmp")
XMLTOIMG = Path(r"E:/pro/WzImg-MCP-Server/Tools/XmlToImg/XmlToImg.csproj")
IMGTOXML = Path(r"E:/pro/WzImg-MCP-Server/Tools/ImgToXml/ImgToXml.csproj")

def is_open_imgdir(s, i):
    if not s.startswith("<imgdir", i):
        return False
    j = s.find(">", i)
    return j != -1 and s[j-1] != "/"

def extract_block(xml_text, anchor_name):
    pat = rf"<imgdir name=\"{anchor_name}\">"
    m = re.search(pat, xml_text)
    if not m:
        raise RuntimeError(f"{anchor_name} not found")
    start, depth, i = m.start(), 0, m.start()
    while i < len(xml_text):
        if is_open_imgdir(xml_text, i):
            depth += 1
        elif xml_text.startswith("</imgdir>", i):
            depth -= 1
            if depth == 0:
                return xml_text[start:i+len("</imgdir>")]
        i += 1
    raise RuntimeError(f"unterminated {anchor_name}")

def replace_block(base_xml, anchor_name, new_block):
    pat = rf"<imgdir name=\"{anchor_name}\">"
    m = re.search(pat, base_xml)
    if not m:
        raise RuntimeError(f"{anchor_name} anchor missing")
    start, depth, i = m.start(), 0, m.start()
    while i < len(base_xml):
        if is_open_imgdir(base_xml, i):
            depth += 1
        elif base_xml.startswith("</imgdir>", i):
            depth -= 1
            if depth == 0:
                end = i + len("</imgdir>")
                return base_xml[:start] + new_block + base_xml[end:]
        i += 1
    raise RuntimeError(f"unterminated {anchor_name} in base")

def count_skins(xml_text):
    m = re.search(r"<imgdir name=\"damageSkin\">", xml_text)
    return len(re.findall(r"<imgdir name=\"(\d+)\">", xml_text[m.start():])) if m else 0

def count_canvas(xml_text):
    m = re.search(r"<imgdir name=\"damageSkin\">", xml_text)
    return len(re.findall(r"<canvas name=", xml_text[m.start():])) if m else 0

def run_dotnet(project, *args):
    cmd = ["dotnet", "run", "--project", str(project), "--", *args]
    print(">", " ".join(cmd))
    subprocess.run(cmd, check=True)


def run_java(classname, *args):
    cmd = ["java", "-cp", ORZ_CP, classname, *args]
    print(">", " ".join(cmd))
    return subprocess.run(cmd, capture_output=True, text=True, check=False)


class WzImgMcp:
    def __init__(self, url):
        self.url = url.rstrip("/") + "/"
        self.session = None
        self.rid = 0

    def call(self, method, params=None):
        self.rid += 1
        payload = {"jsonrpc": "2.0", "method": method, "params": params or {}, "id": self.rid}
        headers = {"Content-Type": "application/json", "Accept": "application/json, text/event-stream"}
        if self.session:
            headers["Mcp-Session-Id"] = self.session
        req = urllib.request.Request(self.url, data=json.dumps(payload).encode(), headers=headers, method="POST")
        with urllib.request.urlopen(req, timeout=600) as resp:
            if "Mcp-Session-Id" in resp.headers:
                self.session = resp.headers["Mcp-Session-Id"]
            body = resp.read().decode()
            m = re.search(r"data: (\{.*\})", body, re.S)
            return json.loads(m.group(1)) if m else None

    def tool(self, name, args):
        return self.call("tools/call", {"name": name, "arguments": args})

    def text(self, resp):
        return resp.get("result", {}).get("content", [{}])[0].get("text", "") if resp else ""


def mcp_merge_damage_skin(skin_block):
    src_xml = TMP / "_src_damageSkin.xml"
    src_img = CLIENT_DATA / "Effect/_src_DamageSkin.img"
    src_xml.write_text(
        '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>\n'
        f'<imgdir name="_src_DamageSkin.img">\n{skin_block}\n</imgdir>\n',
        encoding="utf-8",
    )
    run_dotnet(XMLTOIMG, str(src_xml), str(src_img), "GMS")
    print("src_img", src_img.stat().st_size)

    mcp = WzImgMcp(MCP_URL)
    mcp.call("initialize", {"protocolVersion": "2024-11-05", "capabilities": {}, "clientInfo": {"name": "merge", "version": "1"}})
    mcp.call("notifications/initialized", {})
    print(mcp.text(mcp.tool("init_data_source", {"basePath": str(CLIENT_DATA).replace("\\", "/")})))

    print("delete", mcp.text(mcp.tool("delete_property", {"category": "Effect", "image": "BasicEff.img", "path": "damageSkin"})))
    print("add", mcp.text(mcp.tool("add_property", {"category": "Effect", "image": "BasicEff.img", "parentPath": "", "name": "damageSkin", "type": "SubProperty"})))

    ids = []
    offset = 0
    while True:
        chunk = mcp.text(mcp.tool("list_properties", {"category": "Effect", "image": "_src_DamageSkin.img", "path": "damageSkin", "offset": offset, "limit": 200}))
        for line in chunk.splitlines():
            line = line.strip()
            if line.startswith("- name:"):
                ids.append(line.split(":", 1)[1].strip())
        if "has_more: true" not in chunk:
            break
        offset += 200

    copied = 0
    for sid in ids:
        out = mcp.text(mcp.tool("copy_property", {
            "srcCategory": "Effect", "srcImage": "_src_DamageSkin.img", "srcPath": f"damageSkin/{sid}",
            "destCategory": "Effect", "destImage": "BasicEff.img", "destParentPath": "damageSkin",
        }))
        if "success: true" in out:
            copied += 1
    print("copied", copied, "/", len(ids))
    print("save", mcp.text(mcp.tool("save_image", {"category": "Effect", "image": "BasicEff.img"})))
    print("canvas", mcp.text(mcp.tool("get_canvas_info", {"category": "Effect", "image": "BasicEff.img", "path": "damageSkin/1/NoRed0/0"})))
    if src_img.exists():
        src_img.unlink()


ORZ_CP = r"E:/pro/orange-wz/target/classes;E:/pro/orange-wz/target/lib/*"
CLIENT_DATA = Path(r"E:/mxd_soft/2.客户端/083/beidou_client_xiaoye/BeiDou-Client_1/Data")
MCP_URL = "http://127.0.0.1:13339"
REF_UI_WZ = Path(r"E:/pro/BeiDou-Server_xy/gms-server/tools/_ref_wz_needed/WZ needed/UI.wz")
REF_STRING_WZ = Path(r"E:/pro/BeiDou-Server_xy/gms-server/tools/_ref_wz_needed/WZ needed/String.wz")
REF_ITEM_WZ = Path(r"E:/pro/BeiDou-Server_xy/gms-server/tools/_ref_wz_needed/WZ needed/Item.wz")

TMP.mkdir(parents=True, exist_ok=True)
ts = datetime.now().strftime("%Y%m%d_%H%M%S")
backup = CLIENT_IMG.with_suffix(f".img.bak_{ts}")
shutil.copy2(CLIENT_IMG, backup)
print("backup", backup, backup.stat().st_size)

client_xml = TMP / "client_BasicEff_export.xml"
merged_xml = TMP / "client_BasicEff_merged.xml"
merged_img = TMP / "BasicEff_merged.img"

run_dotnet(IMGTOXML, str(CLIENT_IMG), str(client_xml))
server_text = SERVER_XML.read_text(encoding="utf-8")
client_text = client_xml.read_text(encoding="utf-8")
skin_block = extract_block(server_text, "damageSkin")
merged = replace_block(client_text, "damageSkin", skin_block)
merged_xml.write_text(merged, encoding="utf-8")
print("server", count_skins(server_text), count_canvas(server_text))
print("client_before", count_skins(client_text), count_canvas(client_text))
print("merged", count_skins(merged), count_canvas(merged), merged_xml.stat().st_size)

mcp_merge_damage_skin(skin_block)
print("client_img", CLIENT_IMG.stat().st_size)

verify_xml = TMP / "client_BasicEff_verify.xml"
run_dotnet(IMGTOXML, str(CLIENT_IMG), str(verify_xml))
vt = verify_xml.read_text(encoding="utf-8")
print("verify", count_skins(vt), count_canvas(vt))
