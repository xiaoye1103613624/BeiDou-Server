import json, urllib.request, re
from pathlib import Path

class WzImg:
    def __init__(self, url):
        self.url = url.rstrip('/') + '/'
        self.session = None
        self.rid = 0
    def call(self, method, params=None):
        self.rid += 1
        payload = {"jsonrpc":"2.0","method":method,"params":params or {},"id":self.rid}
        headers = {"Content-Type":"application/json","Accept":"application/json, text/event-stream"}
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
    def text(self, r):
        return r.get("result", {}).get("content", [{}])[0].get("text", "") if r else ""

c = WzImg("http://127.0.0.1:13339")
c.call("initialize", {"protocolVersion":"2024-11-05","capabilities":{},"clientInfo":{"name":"merge","version":"1"}})
c.call("notifications/initialized", {})
c.tool("init_data_source", {"basePath": r"E:/beidou_client_data"})
client_img = Path(r"E:/beidou_client_data/Effect/BasicEff.img")
print("start", client_img.stat().st_size)

print("delete", c.text(c.tool("delete_property", {"category":"Effect","image":"BasicEff.img","path":"damageSkin"})))
print("add", c.text(c.tool("add_property", {"category":"Effect","image":"BasicEff.img","parentPath":"","name":"damageSkin","type":"SubProperty"})))

ids=[]
offset=0
while True:
    txt=c.text(c.tool("get_children", {"category":"Effect","image":"_src_DamageSkin.img","offset":offset,"limit":200}))
    for line in txt.splitlines():
        line=line.strip()
        if line.startswith("- name:"):
            ids.append(line.split(":",1)[1].strip())
    if "has_more: true" not in txt:
        break
    offset += 200
print("ids", len(ids))

ok=0
for i,sid in enumerate(ids,1):
    out=c.text(c.tool("copy_property", {
        "srcCategory":"Effect","srcImage":"_src_DamageSkin.img","srcPath":sid,
        "destCategory":"Effect","destImage":"BasicEff.img","destParentPath":"damageSkin"}))
    if "success: true" in out:
        ok += 1
    elif "error:" in out:
        print("FAIL", sid, out[:120])
        break
    if i % 100 == 0:
        print("progress", i, ok)

print("copied", ok, "/", len(ids))
print("save", c.text(c.tool("save_image", {"category":"Effect","image":"BasicEff.img"})))
print("after", client_img.stat().st_size)
print("count", c.text(c.tool("get_property_count", {"category":"Effect","image":"BasicEff.img","path":"damageSkin"})))
print("canvas", c.text(c.tool("get_canvas_info", {"category":"Effect","image":"BasicEff.img","path":"damageSkin/1/NoRed0/0"})))
