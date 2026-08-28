package org.gms.server.cashshop;

import org.gms.server.ItemInformationProvider;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * 上架前资源校验：服务端 ItemInformationProvider 必过；客户端 Data（若已配置）按 itemId 粗查节点。
 */
public final class CashShopAssetCheck {
    private CashShopAssetCheck() {
    }

    public record Result(boolean serverOk, boolean clientOk, boolean clientSkipped, List<String> messages) {
        public boolean canEnableStrict() {
            return serverOk && (clientSkipped || clientOk);
        }
    }

    public static Result check(int itemId) {
        final List<String> msgs = new ArrayList<>();
        final boolean existsOnServer = ItemInformationProvider.getInstance().itemExists(itemId);
        if (!existsOnServer) {
            msgs.add("服务端 WZ/ItemInfo 中不存在 itemId=" + itemId);
        } else {
            msgs.add("服务端存在");
        }

        final Optional<Path> clientRoot = ClientDataPath.resolve();
        if (clientRoot.isEmpty()) {
            msgs.add("未配置客户端 Data，跳过客户端校验");
            return new Result(existsOnServer, true, true, msgs);
        }

        final Path root = clientRoot.get();
        final boolean clientOk = clientAssetLikelyExists(root, itemId);
        if (clientOk) {
            msgs.add("客户端 Data 疑似存在: " + root);
        } else {
            msgs.add("客户端 Data 未找到对应资源: " + root + " itemId=" + itemId);
        }
        return new Result(existsOnServer, clientOk, false, msgs);
    }

    /**
     * 粗粒度：按常见散目录布局探测（Character Cap / Item Cash 等）。
     * 不保证 100%（加密 img / 打包 wz 需另扩），但能拦住明显缺素材。
     */
    static boolean clientAssetLikelyExists(Path dataRoot, int itemId) {
        final String id8 = String.format(Locale.ROOT, "%08d", itemId);
        final int type = itemId / 10000;
        final List<Path> candidates = new ArrayList<>();
        if (type == 100) { // Cap
            candidates.add(dataRoot.resolve("Character/Cap/" + id8 + ".img"));
            candidates.add(dataRoot.resolve("Character/Cap.img")); // 打包时无法逐 ID，仅作弱信号
        } else if (type >= 101 && type <= 199) {
            candidates.add(dataRoot.resolve("Character/" + categoryFolder(type) + "/" + id8 + ".img"));
        } else if (itemId >= 5000000) {
            final String prefix4 = id8.substring(0, 4);
            candidates.add(dataRoot.resolve("Item/Cash/" + prefix4 + ".img"));
            candidates.add(dataRoot.resolve("Item/Cash/" + id8 + ".img"));
            candidates.add(dataRoot.resolve("Item/Consume/" + prefix4 + ".img"));
            candidates.add(dataRoot.resolve("Item/Etc/" + prefix4 + ".img"));
            candidates.add(dataRoot.resolve("Item/Pet/" + id8 + ".img"));
        } else {
            final String prefix4 = id8.substring(0, 4);
            candidates.add(dataRoot.resolve("Item/Cash/" + prefix4 + ".img"));
            candidates.add(dataRoot.resolve("Item/Consume/" + prefix4 + ".img"));
            candidates.add(dataRoot.resolve("Item/Etc/" + prefix4 + ".img"));
            candidates.add(dataRoot.resolve("Item/Install/" + prefix4 + ".img"));
        }
        for (Path p : candidates) {
            if (Files.exists(p)) {
                return true;
            }
        }
        return false;
    }

    static String categoryFolder(int type) {
        return categoryFolderPublic(type);
    }

    /** Character 子目录名（供图标/同步扫描复用）。 */
    public static String categoryFolderPublic(int type) {
        return switch (type) {
            case 101, 102, 103 -> "Accessory";
            case 104 -> "Coat";
            case 105 -> "Longcoat";
            case 106 -> "Pants";
            case 107 -> "Shoes";
            case 108 -> "Glove";
            case 109 -> "Shield";
            case 110 -> "Cape";
            case 111, 112, 113, 114, 115 -> "Accessory";
            case 130, 131, 132, 133, 134, 137, 138, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149 -> "Weapon";
            default -> type == 100 ? "Cap" : "Cap";
        };
    }
}
