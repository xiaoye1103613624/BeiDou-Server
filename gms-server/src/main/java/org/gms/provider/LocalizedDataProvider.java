package org.gms.provider;

public class LocalizedDataProvider implements DataProvider {
    private final DataProvider localized;
    private final DataProvider fallback;

    public LocalizedDataProvider(DataProvider localized, DataProvider fallback) {
        this.localized = localized;
        this.fallback = fallback;
    }

    @Override
    public Data getData(String path) {
        Data data = localized.getData(path);
        // 语言目录里没有该 XML 时，使用原始 WZ，避免为了少量翻译复制整包资源。
        return data != null ? data : fallback.getData(path);
    }

    @Override
    public DataDirectoryEntry getRoot() {
        // 使用本地化目录作为主文件树（包含全部20大陆数据），
        // getData会优先从localized取，缺失再回退fallback
        return localized.getRoot();
    }
}
