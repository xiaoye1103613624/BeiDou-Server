#!/usr/bin/env python3
"""快速扫描估算各源目录的可索引文件数"""
import os

SKIP_DIRS = {'.git','.idea','.claude','__pycache__','node_modules','target','logs','.m2','repository','vendor','dist','build','venv','.venv','$RECYCLE.BIN','System Volume Information'}
SKIP_PATHS = ['docker','docker-data','ollama','crawlab','neo4j','pagefile.sys','xy_vector_db','.cache']

SOURCES = [
    ('E:/资料/xiaoye/mxd学习', {'.md','.txt','.js','.java'}),
    ('E:/资料', {'.md','.txt','.pdf','.docx','.js','.java','.sql','.yml','.yaml','.xml','.json','.py','.properties','.html'}),
    ('E:/mxd_soft', {'.md','.txt','.js','.java','.sql','.cfg','.ini','.xml','.properties'}),
    ('E:/教程', {'.md','.txt','.pdf','.docx','.js','.java','.py','.sql','.html'}),
    ('E:/萧曳工作文档', {'.md','.txt','.pdf','.docx','.xlsx','.pptx','.js','.java','.sql'}),
    ('E:/pro', {'.md','.txt','.js','.java','.sql','.yml','.yaml','.xml','.json','.sh','.bat','.vue','.ts'}),
    ('F:/BaiduNetdiskDownload', {'.md','.txt','.pdf','.docx','.js','.java','.py','.sql','.html','.xml','.json','.yml'}),
    ('D:/software', {'.md','.txt','.cfg','.ini','.conf','.xml','.json','.yml','.properties'}),
    ('D:/game', {'.md','.txt','.cfg','.ini','.xml','.json'}),
]

total_files = 0
total_size = 0
by_ext = {}

for src_dir, exts in SOURCES:
    if not os.path.exists(src_dir):
        print(f'SKIP: {src_dir}')
        continue
    count = 0
    size = 0
    for dirpath, dirnames, filenames in os.walk(src_dir):
        dirnames[:] = [d for d in dirnames if d not in SKIP_DIRS and not d.startswith('.')]
        depth = dirpath.count(os.sep) - src_dir.count(os.sep)
        if depth > 5:
            dirnames[:] = []
        for fname in filenames:
            ext = os.path.splitext(fname)[1].lower()
            if ext not in exts:
                continue
            fp = os.path.join(dirpath, fname)
            try:
                sz = os.path.getsize(fp)
                if sz > 10*1024*1024 or sz < 10:
                    continue
                skip = False
                for sp in SKIP_PATHS:
                    if ('/'+sp+'/' in fp or fp.endswith('/'+sp) or
                        '\\'+sp+'\\' in fp or fp.endswith('\\'+sp)):
                        skip = True
                        break
                if skip:
                    continue
                count += 1
                size += sz
                by_ext[ext] = by_ext.get(ext, 0) + 1
            except:
                pass
    label = os.path.basename(src_dir)
    print(f'{label:<35s} | {count:>8,d} files | {size/1024/1024:>8.1f} MB')
    total_files += count
    total_size += size

print('-' * 60)
print(f'{"TOTAL":<35s} | {total_files:>8,d} files | {total_size/1024/1024:>8.1f} MB')
est_chunks = total_files * 8
est_hours = est_chunks / 8 / 3600
print(f'\nEstimated chunks: ~{est_chunks:,d}')
print(f'Estimated time:   ~{est_hours:.1f} hours (@8 chunks/sec CPU)')
print(f'Estimated DB size: ~{est_chunks * 768 * 4 / 1024 / 1024:.0f} MB')
print(f'\nTop extensions:')
for ext, cnt in sorted(by_ext.items(), key=lambda x:-x[1])[:15]:
    print(f'  {ext}: {cnt:,d}')
