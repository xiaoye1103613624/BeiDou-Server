import json, urllib.request

class McpClient:
    def __init__(self, url):
        self.url = url
        self.session = None
    def call(self, method, params=None, req_id=1):
        payload = {"jsonrpc":"2.0","method":method,"params":params or {},"id":req_id}
        headers = {"Content-Type":"application/json"}
        if self.session:
            headers["Mcp-Session-Id"] = self.session
        req = urllib.request.Request(self.url, data=json.dumps(payload).encode(), headers=headers, method="POST")
        with urllib.request.urlopen(req, timeout=120) as resp:
            body = resp.read().decode()
            if "Mcp-Session-Id" in resp.headers:
                self.session = resp.headers["Mcp-Session-Id"]
            if not body.strip():
                return None, dict(resp.headers)
            return json.loads(body), dict(resp.headers)

c = McpClient("http://127.0.0.1:10002/mcp")
c.call("initialize", {"protocolVersion":"2024-11-05","capabilities":{},"clientInfo":{"name":"beidou","version":"1.0"}})
c.call("notifications/initialized", {})
tools, _ = c.call("tools/list", {}, 2)
for t in tools.get("result",{}).get("tools",[]):
    print(t["name"])
