#!/usr/bin/env python3
"""
MCP Server - Personal Knowledge Base Vector Search
Supports multi-collection search with category filtering.

Collections:
  - maplestory_kb: MapleStory server development knowledge
  - personal_kb:   Personal knowledge base (all sources)
"""

import os, sys, asyncio

VECTOR_DB_PATH = "d:/xy_vector_db"
EMBEDDING_MODEL = "paraphrase-multilingual-MiniLM-L12-v2"
MAX_RESULTS = 10

# Available collections
COLLECTIONS = {
    "maplestory_kb": "MapleStory server development knowledge base",
    "personal_kb": "Personal knowledge base (all indexed sources)",
}

# Category labels for personal_kb
CATEGORIES = {
    "code/server": "Server source code",
    "code/script": "Game scripts (JS)",
    "code/beidou": "BeiDou project",
    "code/frontend": "Frontend code",
    "code/other": "Other code",
    "doc/manual": "Manuals & guides",
    "doc/tutorial": "Tutorials & courses",
    "doc/reference": "Reference docs",
    "doc/work": "Work documents",
    "doc/maplestory": "MapleStory specific",
    "doc/downloads": "Downloaded materials",
    "db/schema": "Database schemas",
    "db/data": "Database data",
    "reference": "Reference materials",
    "reference/maplestory-tools": "MapleStory tools",
    "reference/software": "Software references",
    "reference/game": "Game references",
    "other": "Other",
}

# Lazy-loaded
_model = None
_clients = {}

def get_model():
    global _model
    if _model is None:
        from sentence_transformers import SentenceTransformer
        _model = SentenceTransformer(EMBEDDING_MODEL)
    return _model

def get_collection(name):
    if name not in _clients:
        import chromadb
        client = chromadb.PersistentClient(path=VECTOR_DB_PATH)
        _clients[name] = client.get_collection(name=name)
    return _clients[name]


def search(query, collection_name="personal_kb", n_results=MAX_RESULTS, category=None):
    """Search with optional category filter."""
    if not query or not query.strip():
        return "Please provide a search query."

    try:
        col = get_collection(collection_name)
        model = get_model()
        query_embedding = model.encode([query]).tolist()

        # Build where filter for category
        where_filter = None
        if category and category != "all":
            where_filter = {"category": category}

        results = col.query(
            query_embeddings=query_embedding,
            n_results=n_results,
            where=where_filter,
        )

        documents = results.get("documents", [[]])[0]
        metadatas = results.get("metadatas", [[]])[0]
        distances = results.get("distances", [[]])[0]

        if not documents:
            filter_info = f" (category: {category})" if category else ""
            return f'No results found for: {query}{filter_info}'

        coll_label = COLLECTIONS.get(collection_name, collection_name)
        lines = [f'## Search: {query}', f'Collection: {coll_label}', '']

        for i, (doc, meta, dist) in enumerate(zip(documents, metadatas, distances)):
            similarity = max(0, 1.0 - dist) if dist else 1.0
            source = meta.get("source", "unknown")
            cat = meta.get("category", "unknown")
            # Truncate source to last 80 chars
            short_source = source if len(source) < 80 else "..." + source[-77:]
            lines.append(f'### Result {i+1} [{cat}] {short_source} ({similarity:.1%})')
            lines.append('```')
            lines.append(doc[:1200])
            lines.append('```')
            lines.append('')

        return '\n'.join(lines)

    except Exception as e:
        return f'Search error: {str(e)}'


def stats():
    """Return all knowledge base stats."""
    import chromadb
    client = chromadb.PersistentClient(path=VECTOR_DB_PATH)
    lines = ['## Knowledge Base Stats', '']

    for name, desc in COLLECTIONS.items():
        try:
            col = client.get_collection(name=name)
            cnt = col.count()
            lines.append(f'- **{name}**: {cnt:,} chunks — {desc}')
        except:
            lines.append(f'- **{name}**: not built yet')

    lines.append('')
    lines.append(f'Storage: {VECTOR_DB_PATH}')
    return '\n'.join(lines)


# ========================== MCP Protocol ==========================
try:
    from mcp.server import Server, NotificationOptions
    from mcp.server.models import InitializationCapabilities
    import mcp.server.stdio
    import mcp.types as types
    MCP_OK = True
except ImportError:
    MCP_OK = False

if MCP_OK:
    server = Server("personal-kb")

    @server.list_tools()
    async def handle_list_tools() -> list[types.Tool]:
        return [
            types.Tool(
                name="search_knowledge_base",
                description="Search your personal knowledge base (all indexed documents, code, tutorials, references). Returns relevant snippets with source file and category. Use for any question about your projects, documents, or references.",
                inputSchema={
                    "type": "object",
                    "properties": {
                        "query": {
                            "type": "string",
                            "description": "Search query in Chinese or English"
                        },
                        "n_results": {
                            "type": "integer",
                            "default": MAX_RESULTS,
                            "description": "Results to return (default %d, max 20)" % MAX_RESULTS
                        },
                        "collection": {
                            "type": "string",
                            "enum": list(COLLECTIONS.keys()) + ["all"],
                            "default": "personal_kb",
                            "description": "Collection: personal_kb (all sources), maplestory_kb (MapleStory only)"
                        },
                        "category": {
                            "type": "string",
                            "description": "Filter by category (e.g. code/server, doc/tutorial, doc/maplestory). Omit to search all."
                        },
                    },
                    "required": ["query"],
                },
            ),
            types.Tool(
                name="get_kb_stats",
                description="Get statistics about all knowledge base collections.",
                inputSchema={"type": "object", "properties": {}},
            ),
        ]

    @server.call_tool()
    async def handle_call_tool(name: str, arguments: dict) -> list[types.TextContent]:
        if name == "search_knowledge_base":
            q = arguments.get("query", "")
            n = min(arguments.get("n_results", MAX_RESULTS), 20)
            coll = arguments.get("collection", "personal_kb")
            cat = arguments.get("category")
            result = search(q, coll, n, cat)
        elif name == "get_kb_stats":
            result = stats()
        else:
            result = "Unknown tool: " + name
        return [types.TextContent(type="text", text=result)]

    async def run_mcp():
        async with mcp.server.stdio.stdio_server() as (read, write):
            await server.run(
                read, write,
                InitializationCapabilities(sampling=None, experimental=None)
            )


# ========================== CLI Mode ==========================
if __name__ == "__main__":
    sys.stdout.reconfigure(encoding='utf-8', errors='replace')

    if len(sys.argv) < 2:
        print("Usage: python mcp_vector_search.py <mcp|search|stats> [query]")
        sys.exit(0)

    cmd = sys.argv[1]
    if cmd == "mcp":
        if MCP_OK:
            asyncio.run(run_mcp())
        else:
            print("MCP SDK not installed. Run: pip install mcp")
            sys.exit(1)
    elif cmd == "search":
        q = " ".join(sys.argv[2:]) if len(sys.argv) > 2 else ""
        if q:
            print(search(q))
        else:
            print(stats())
    elif cmd == "stats":
        print(stats())
    else:
        print("Unknown command: " + cmd)
