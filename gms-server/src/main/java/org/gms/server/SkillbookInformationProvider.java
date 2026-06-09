/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gms.server;

import org.gms.client.Character;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.provider.Data;
import org.gms.provider.DataProvider;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.DataTool;
import org.gms.provider.wz.WZFiles;
import org.gms.util.DatabaseConnection;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 技能书信息提供者
 * 扫描怪物掉落数据，提供技能书的获取途径信息（任务、反应器、掉落等）
 * 仅用于一个脚本中向玩家展示技能书来源
 *
 * @author RonanLana
 */
public class SkillbookInformationProvider {
    private static final Logger log = LoggerFactory.getLogger(SkillbookInformationProvider.class);
    /** 技能书来源缓存 */
    private static volatile Map<Integer, SkillBookEntry> foundSkillbooks = new HashMap<>();

    /**
     * 技能书获取途径枚举
     */
    public enum SkillBookEntry {
        /** 不可获取 */
        UNAVAILABLE,
        /** 任务获取 */
        QUEST,
        /** 任务书获取 */
        QUEST_BOOK,
        /** 任务奖励 */
        QUEST_REWARD,
        /** 反应器掉落 */
        REACTOR,
        /** 脚本获取 */
        SCRIPT
    }

    /** 技能书范围：起始 */ 
    private static final int SKILLBOOK_MIN_ITEMID = 2280000;
    /** 技能书范围：结束（不含） */
    private static final int SKILLBOOK_MAX_ITEMID = 2300000;  // exclusively

    /**
     * 加载所有技能书来源信息
     */
    public static void loadAllSkillbookInformation() {
        Map<Integer, SkillBookEntry> loadedSkillbooks = new HashMap<>();
        loadedSkillbooks.putAll(fetchSkillbooksFromQuests());
        loadedSkillbooks.putAll(fetchSkillbooksFromReactors());
        loadedSkillbooks.putAll(fetchSkillbooksFromScripts());
        SkillbookInformationProvider.foundSkillbooks = loadedSkillbooks;
    }

    /**
     * 判断是否为4转技能
     *
     * @param itemid 物品/技能ID
     * @return 是4转技能返回true
     */
    private static boolean is4thJobSkill(int itemid) {
        return itemid / 10000 % 10 == 2;
    }

    /**
     * 判断物品ID是否为技能书
     *
     * @param itemid 物品ID
     * @return 是技能书返回true
     */
    private static boolean isSkillBook(int itemid) {
        return itemid >= SKILLBOOK_MIN_ITEMID && itemid < SKILLBOOK_MAX_ITEMID;
    }

    private static boolean isQuestBook(int itemid) {
        return itemid >= 4001107 && itemid <= 4001114 || itemid >= 4161015 && itemid <= 4161023;
    }

    /**
     * 递归查找任务关联的任务书物品ID
     *
     * @param checkData 任务检查数据（Check.img）
     * @param quest     任务ID
     * @return 任务书物品ID，未找到返回-1
     */
    private static int fetchQuestbook(Data checkData, String quest) {
        Data questStartData = checkData.getChildByPath(quest).getChildByPath("0");

        Data startReqItemData = questStartData.getChildByPath("item");
        if (startReqItemData != null) {
            for (Data itemData : startReqItemData.getChildren()) {
                int itemId = DataTool.getInt("id", itemData, 0);
                if (isQuestBook(itemId)) {
                    return itemId;
                }
            }
        }

        Data startReqQuestData = questStartData.getChildByPath("quest");
        if (startReqQuestData != null) {
            Set<Integer> reqQuests = new HashSet<>();

            for (Data questStatusData : startReqQuestData.getChildren()) {
                int reqQuest = DataTool.getInt("id", questStatusData, 0);
                if (reqQuest > 0) {
                    reqQuests.add(reqQuest);
                }
            }

            for (Integer reqQuest : reqQuests) {
                int book = fetchQuestbook(checkData, Integer.toString(reqQuest));
                if (book > -1) {
                    return book;
                }
            }
        }

        return -1;
    }

    /**
     * 扫描任务奖励中的技能书
     *
     * @return 技能书ID -> 获取途径
     */
    private static Map<Integer, SkillBookEntry> fetchSkillbooksFromQuests() {
        DataProvider questDataProvider = DataProviderFactory.getDataProvider(WZFiles.QUEST);
        Data actData = questDataProvider.getData("Act.img");
        Data checkData = questDataProvider.getData("Check.img");

        final Map<Integer, SkillBookEntry> loadedSkillbooks = new HashMap<>();
        for (Data questData : actData.getChildren()) {
            for (Data questStatusData : questData.getChildren()) {
                for (Data questNodeData : questStatusData.getChildren()) {
                    String actNodeName = questNodeData.getName();
                    if (actNodeName.contentEquals("item")) {
                        for (Data questItemData : questNodeData.getChildren()) {
                            int itemId = DataTool.getInt("id", questItemData, 0);
                            int itemCount = DataTool.getInt("count", questItemData, 0);

                            if (isSkillBook(itemId) && itemCount > 0) {
                                int questbook = fetchQuestbook(checkData, questData.getName());
                                if (questbook < 0) {
                                    loadedSkillbooks.put(itemId, SkillBookEntry.QUEST);
                                } else {
                                    loadedSkillbooks.put(itemId, SkillBookEntry.QUEST_BOOK);
                                }
                            }
                        }
                    } else if (actNodeName.contentEquals("skill")) {
                        for (Data questSkillData : questNodeData.getChildren()) {
                            int skillId = DataTool.getInt("id", questSkillData, 0);
                            if (is4thJobSkill(skillId)) {
                                // negative itemids are skill rewards

                                int questbook = fetchQuestbook(checkData, questData.getName());
                                if (questbook < 0) {
                                    loadedSkillbooks.put(-skillId, SkillBookEntry.QUEST_REWARD);
                                } else {
                                    loadedSkillbooks.put(-skillId, SkillBookEntry.QUEST_BOOK);
                                }
                            }
                        }
                    }
                }
            }
        }

        return loadedSkillbooks;
    }

    /**
     * 扫描反应器掉落中的技能书
     *
     * @return 技能书ID -> 获取途径
     */
    private static Map<Integer, SkillBookEntry> fetchSkillbooksFromReactors() {
        Map<Integer, SkillBookEntry> loadedSkillbooks = new HashMap<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT itemid FROM reactordrops WHERE itemid >= ? AND itemid < ?;")) {
            ps.setInt(1, SKILLBOOK_MIN_ITEMID);
            ps.setInt(2, SKILLBOOK_MAX_ITEMID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.isBeforeFirst()) {
                    while (rs.next()) {
                        loadedSkillbooks.put(rs.getInt("itemid"), SkillBookEntry.REACTOR);
                    }
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return loadedSkillbooks;
    }

    private static void listFiles(String directoryName, ArrayList<Path> files) {
        Path directory = Path.of(directoryName);

        // get all the files from a directory
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path path : stream) {

                if (Files.isRegularFile(path)) {
                    files.add(path);
                } else if (Files.isDirectory(path)) {
                    listFiles(path.toAbsolutePath().toString(), files);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

	/**
     * 递归列出目录中所有文件
     *
     * @param directory 目录路径
     * @return 文件路径列表
     */
    private static List<Path> listFilesFromDirectoryRecursively(String directory) {
		ArrayList<Path> files = new ArrayList<>();
		listFiles(directory, files);

		return files;
	}

    private static Set<Integer> findMatchingSkillbookIdsOnFile(String fileContent) {
        Set<Integer> skillbookIds = new HashSet<>(4);

        Matcher searchM = Pattern.compile("22(8|9)[0-9]{4}").matcher(fileContent);
        int idx = 0;
        while (searchM.find(idx)) {
            idx = searchM.end();
            skillbookIds.add(Integer.valueOf(fileContent.substring(searchM.start(), idx)));
        }

        return skillbookIds;
    }

    /**
     * 读取文件内容为字符串
     *
     * @param file     文件路径
     * @param encoding 编码
     * @return 文件内容
     * @throws IOException 读取异常
     */
    private static String readFileToString(Path file, String encoding) throws IOException {
        Scanner scanner = new Scanner(file, encoding);
        String text = "";
        try (scanner) {
           
           text = scanner.useDelimiter("\\A").next();
            
        } catch (NoSuchElementException e) {
        }

        return text;
    }

    /**
     * 在脚本文件中搜索技能书引用
     *
     * @param file 脚本文件路径
     * @return 技能书ID -> SCRIPT
     */
    private static Map<Integer, SkillBookEntry> fileSearchMatchingData(Path file) {
        Map<Integer, SkillBookEntry> scriptFileSkillbooks = new HashMap<>();

        try {
            String fileContent = readFileToString(file, "UTF-8");

            Set<Integer> skillbookIds = findMatchingSkillbookIdsOnFile(fileContent);
            for (Integer skillbookId : skillbookIds) {
                scriptFileSkillbooks.put(skillbookId, SkillBookEntry.SCRIPT);
            }
        } catch (IOException ioe) {
            log.error("Failed to read file:{}", file.getFileName(), ioe);
        }

        return scriptFileSkillbooks;
    }

    /**
     * 扫描所有脚本文件中的技能书引用
     *
     * @return 技能书ID -> 获取途径
     */
    private static Map<Integer, SkillBookEntry> fetchSkillbooksFromScripts() {
        Map<Integer, SkillBookEntry> scriptSkillbooks = new HashMap<>();

        for (Path file : listFilesFromDirectoryRecursively("./scripts")) {
            if (file.getFileName().endsWith(".js")) {
                scriptSkillbooks.putAll(fileSearchMatchingData(file));
            }
        }

        return scriptSkillbooks;
    }

    /**
     * 获取技能书的获取途径
     *
     * @param itemId 技能书物品ID
     * @return 获取途径枚举
     */
    public static SkillBookEntry getSkillbookAvailability(int itemId) {
        SkillBookEntry sbe = foundSkillbooks.get(itemId);
        return sbe != null ? sbe : SkillBookEntry.UNAVAILABLE;
    }

    /**
     * 获取角色可学习的技能列表（通过四转任务直接获得）
     *
     * @param chr 角色
     * @return 可学习技能ID列表（负值表示技能奖励）
     */
    public static List<Integer> getTeachableSkills(Character chr) {
        List<Integer> list = new ArrayList<>();

        for (Integer book : foundSkillbooks.keySet()) {
            if (book >= 0) {
                continue;
            }

            int skillid = -book;
            if (skillid / 10000 == chr.getJob().getId()) {
                if (chr.getMasterLevel(skillid) == 0) {
                    list.add(-skillid);
                }
            }
        }

        return list;
    }

}