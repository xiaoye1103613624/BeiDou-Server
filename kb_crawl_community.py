#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
冒险岛改端社区论坛爬虫 —— 抓取技术帖正文，整理成 markdown 供向量库索引
================================================================
目标站点：
  - 蘑菇物语 moguwuyu.com         (Flarum 论坛，走 JSON API)
  - 枫叶物语 fengyewuyu.com       (Discuz 论坛，服务端渲染 HTML)
输出：resource_doc/版本开发资料/社区资料/{moguwuyu,fengye}/*.md
之后运行 update_vector_db.py 增量索引到 maplestory_kb。

用法：
  python kb_crawl_community.py            # 按默认上限抓取
说明：仅抓取公开可见的帖子正文，礼貌延迟，自动跳过失败项。
"""
import os, re, time, sys
import requests
from urllib.parse import urljoin
from bs4 import BeautifulSoup

sys.stdout.reconfigure(encoding="utf-8", errors="replace")

OUT = "E:/pro/BeiDou-Server_xy/resource_doc/版本开发资料/社区资料"
UA = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"}
AH = {**UA, "Accept": "application/vnd.api+json"}  # Flarum JSON:API 头
DELAY = 0.4  # 每次请求间隔（秒），礼貌爬取


def html_to_text(h: str) -> str:
    """HTML 转纯文本，去除脚本/样式，压缩多余空行"""
    s = BeautifulSoup(h or "", "html.parser")
    for x in s(["script", "style"]):
        x.decompose()
    t = s.get_text("\n", strip=True)
    return re.sub(r"\n{3,}", "\n\n", t)


def safe(name: str) -> str:
    """文件名安全化"""
    return re.sub(r'[\\/:*?"<>|\r\n]', "_", name).strip()[:80] or "untitled"


def save(path: str, content: str):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)


# ============== 蘑菇物语（Flarum） ==============
def crawl_flarum(base="https://moguwuyu.com", limit_disc=400):
    s = requests.Session(); s.headers.update(UA)
    discs, offset = [], 0
    # 1) 分页枚举所有讨论
    while len(discs) < limit_disc:
        u = f"{base}/api/discussions?page%5Boffset%5D={offset}"
        try:
            r = s.get(u, headers=AH, timeout=25)
        except Exception as e:
            print("[flarum] list err", e, flush=True); break
        if r.status_code != 200:
            break
        d = r.json()
        batch = d.get("data", [])
        for it in batch:
            a = it.get("attributes", {})
            discs.append((it["id"], a.get("title", ""), a.get("commentCount", 0)))
        if not d.get("links", {}).get("next") or not batch:
            break
        offset += 20
        time.sleep(DELAY)
    print(f"[flarum] 讨论数: {len(discs)}", flush=True)

    saved = 0
    for did, title, _cc in discs:
        try:
            u = f"{base}/api/posts?filter%5Bdiscussion%5D={did}&page%5Blimit%5D=100"
            r = s.get(u, headers=AH, timeout=25)
            if r.status_code != 200:
                continue
            posts = []
            for p in r.json().get("data", []):
                ch = p.get("attributes", {}).get("contentHtml")
                txt = html_to_text(ch)
                if txt and len(txt) >= 2:
                    posts.append(txt)
            if not posts:
                continue
            url = f"{base}/d/{did}"
            md = (f"# {title}\n\n> 来源：{url}\n"
                  f"> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛\n\n")
            md += "\n\n---\n\n".join(f"**#{i+1}楼**\n\n{p}" for i, p in enumerate(posts))
            save(f"{OUT}/moguwuyu/{did}-{safe(title)}.md", md)
            saved += 1
            if saved % 20 == 0:
                print(f"[flarum] 已保存 {saved}", flush=True)
        except Exception as e:
            print("[flarum] disc err", did, e, flush=True)
        time.sleep(DELAY)
    print(f"[flarum] 完成，共保存 {saved} 个帖子", flush=True)
    return saved


# ============== 枫叶物语（Discuz） ==============
def crawl_discuz(base="https://www.fengyewuyu.com", max_threads=120):
    s = requests.Session(); s.headers.update(UA)

    def get(u):
        r = s.get(u, timeout=25)
        r.encoding = r.apparent_encoding or "utf-8"
        return r

    tids, fids = set(), set()
    # 1) 从入口页发现板块(fid)与帖子(tid)
    for sd in [f"{base}/forum.php?gid=1", f"{base}/forum.php"]:
        try:
            soup = BeautifulSoup(get(sd).text, "html.parser")
            for a in soup.find_all("a", href=True):
                h = urljoin(base, a["href"])
                m = re.search(r"thread-(\d+)-", h)
                if m:
                    tids.add(m.group(1))
                m2 = re.search(r"fid=(\d+)", h)
                if m2:
                    fids.add(m2.group(1))
        except Exception as e:
            print("[discuz] seed err", sd, e, flush=True)
        time.sleep(DELAY)
    # 2) 遍历各板块前几页收集更多帖子
    for fid in list(fids)[:20]:
        for pg in range(1, 4):
            try:
                soup = BeautifulSoup(
                    get(f"{base}/forum.php?mod=forumdisplay&fid={fid}&page={pg}").text,
                    "html.parser")
                before = len(tids)
                for a in soup.find_all("a", href=True):
                    m = re.search(r"thread-(\d+)-", urljoin(base, a["href"]))
                    if m:
                        tids.add(m.group(1))
                if len(tids) == before:  # 该页无新帖，停止翻页
                    break
                time.sleep(DELAY)
            except Exception:
                break
        if len(tids) >= max_threads * 3:
            break
    tids = list(tids)[:max_threads]
    print(f"[discuz] 帖子数: {len(tids)}", flush=True)

    saved = 0
    for tid in tids:
        try:
            soup = BeautifulSoup(get(f"{base}/thread-{tid}-1-1.html").text, "html.parser")
            el = soup.select_one("#thread_subject") or soup.select_one("h1")
            title = el.get_text(strip=True) if el else f"帖子{tid}"
            posts = [p.get_text("\n", strip=True) for p in soup.select("td.t_f")]
            posts = [re.sub(r"\n{3,}", "\n\n", p) for p in posts if len(p) >= 5]
            if not posts:
                continue
            url = f"{base}/thread-{tid}-1-1.html"
            md = (f"# {title}\n\n> 来源：{url}\n"
                  f"> 站点：枫叶物语冒险岛单机论坛(fengyewuyu.com) · Discuz\n\n")
            md += "\n\n---\n\n".join(f"**#{i+1}楼**\n\n{p}" for i, p in enumerate(posts))
            save(f"{OUT}/fengye/{tid}-{safe(title)}.md", md)
            saved += 1
            if saved % 20 == 0:
                print(f"[discuz] 已保存 {saved}", flush=True)
        except Exception as e:
            print("[discuz] thread err", tid, e, flush=True)
        time.sleep(DELAY)
    print(f"[discuz] 完成，共保存 {saved} 个帖子", flush=True)
    return saved


if __name__ == "__main__":
    print("=" * 60, flush=True)
    a = crawl_flarum()
    b = crawl_discuz()
    print("=" * 60, flush=True)
    print(f"总计保存: {a + b} 个 markdown", flush=True)
