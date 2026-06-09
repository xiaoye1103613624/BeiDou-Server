package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.client.command.CommandsExecutor;

import org.gms.dao.entity.CommandInfoDO;
import org.gms.dao.mapper.CommandInfoMapper;
import org.gms.model.dto.CommandReqDTO;
import org.gms.net.server.PlayerStorage;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.world.World;
import org.gms.scripting.portal.PortalScriptManager;
import org.gms.server.maps.MapleMap;
import org.gms.util.I18nUtil;
import org.gms.util.Pair;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 指令服务类
 * 管理游戏GM指令的注册、更新、权限控制和热加载
 */
@Slf4j
@Service
@AllArgsConstructor
public class CommandService {

    /**
     * 指令信息数据访问对象
     */
    private final CommandInfoMapper commandInfoMapper;

    /**
     * 加载所有指令到内存
     * 从数据库读取指令配置，按等级分组注册到指令执行器
     *
     * @param registeredCommands 已注册指令映射（指令语法 -> 指令实例）
     * @param commandsNameDesc   指令名称描述列表，按GM等级分组
     */
    public void loadCommands(final HashMap<String, Command> registeredCommands,
                             final List<Pair<List<String>, List<String>>> commandsNameDesc) {
        registeredCommands.clear();
        commandsNameDesc.clear();

        List<CommandInfoDO> commandInfoList = commandInfoMapper.selectAll();
        if (commandInfoList == null || commandInfoList.isEmpty()) {
            log.warn(I18nUtil.getLogMessage("CommandService.loadCommands.warn1"));
            return;
        }
        // 根据 GM 等级对指令分组
        Map<Integer, List<CommandInfoDO>> levelMap = commandInfoList.stream()
                .collect(Collectors.groupingBy(CommandInfoDO::getLevel));
        for (int i = 0; i <= 6; i++) {
            registerCommands(registeredCommands, commandsNameDesc, i, levelMap.get(i));
        }
        log.info(I18nUtil.getLogMessage("CommandService.loadCommands.info1"), registeredCommands.size());
    }

    /**
     * 更新已注册指令
     * 根据数据库配置变化，更新内存中的指令注册状态
     *
     * @param commandInfoDO 指令信息实体
     */
    private void updateRegisteredCommands(CommandInfoDO commandInfoDO) {
        CommandsExecutor commandsExecutor = CommandsExecutor.getInstance();
        HashMap<String, Command> registeredCommands = commandsExecutor.getRegisteredCommands();
        List<Pair<List<String>, List<String>>> commandsNameDesc = commandsExecutor.getCommandsNameDesc();
        String syntax = commandInfoDO.getSyntax().toLowerCase();
        Command command = registeredCommands.get(syntax);
        // 新指令注册：未注册 + 状态开启 → 实例化并添加到对应等级分组末尾
        if (command == null) {
            if (commandInfoDO.isEnabled()) {
                command = getCommandInstance(commandInfoDO);
                RequireUtil.requireNotNull(command, I18nUtil.getExceptionMessage("UNKNOWN_PARAMETER_VALUE", "clazz", commandInfoDO.getClazz()));
                registeredCommands.put(syntax, command);
                // 按新等级获取 nameDesc 分组节点，追加到末尾
                Pair<List<String>, List<String>> nameDescPair = commandsNameDesc.get(commandInfoDO.getLevel());
                nameDescPair.getLeft().add(syntax);
                nameDescPair.getRight().add(command.getDescription());
            }
            return;
        }
        // 已注册指令更新：先按旧等级找到并移除 nameDesc 条目，再按新等级重新注册
        Pair<List<String>, List<String>> oldPair = commandsNameDesc.get(command.getRank());
        int index = oldPair.getLeft().indexOf(syntax);
        if (index > -1) {
            oldPair.getLeft().remove(index);
            oldPair.getRight().remove(index);
        }

        if (commandInfoDO.isEnabled()) {
            // 启用状态：新等级加入 nameDesc + 更新 Rank
            Pair<List<String>, List<String>> newPair = commandsNameDesc.get(commandInfoDO.getLevel());
            newPair.getLeft().add(syntax);
            newPair.getRight().add(command.getDescription());

            command.setRank(commandInfoDO.getLevel());
        } else {
            // 禁用状态：从注册表中移除
            registeredCommands.remove(syntax);
        }
    }

    /**
     * 注册指令到内存
     * 将指定等级的指令批量注册到指令执行器
     *
     * @param registeredCommands 已注册指令映射
     * @param commandsNameDesc   指令名称描述列表
     * @param level              GM等级
     * @param commandInfoList    指令信息列表
     */
    private void registerCommands(final HashMap<String, Command> registeredCommands,
                                  final List<Pair<List<String>, List<String>>> commandsNameDesc,
                                  int level,
                                  List<CommandInfoDO> commandInfoList) {
        if (commandInfoList == null) {
            log.warn(I18nUtil.getLogMessage("CommandService.loadCommands.warn2"), level);
            commandInfoList = new ArrayList<>();
        }

        Pair<List<String>, List<String>> levelCommandsCursor = new Pair<>(new ArrayList<>(), new ArrayList<>());
        for (CommandInfoDO item : commandInfoList) {
            // 已禁用的指令不注册
            if (!item.isEnabled()) {
                continue;
            }
            Command command = getCommandInstance(item);
            if (command == null) {
                log.warn(I18nUtil.getLogMessage("CommandService.loadCommands.warn3"), item.getSyntax());
                continue;
            }

            String commandName = item.getSyntax().toLowerCase();
            if (registeredCommands.containsKey(commandName)) {
                log.warn(I18nUtil.getLogMessage("CommandsExecutor.addCommand.warn1", item.getSyntax()));
                continue;
            }

            try {
                levelCommandsCursor.getRight().add(command.getDescription());
                levelCommandsCursor.getLeft().add(commandName);
                registeredCommands.put(commandName, command);
            } catch (Exception e) {
                log.warn(I18nUtil.getLogMessage("CommandsExecutor.addCommand.warn2"), e);
            }
        }

        commandsNameDesc.add(levelCommandsCursor);
    }

    /**
     * 通过反射创建指令实例
     * 根据类路径反射实例化指令对象
     *
     * @param commandInfoDO 指令信息实体
     * @return 指令实例，反射失败返回null
     */
    private Command getCommandInstance(CommandInfoDO commandInfoDO) {
        try {
            Class<?> aClass = Class.forName("org.gms.client.command.commands.gm" + commandInfoDO.getDefaultLevel()
                    + "." + commandInfoDO.getClazz());
            Command command = (Command) aClass.getDeclaredConstructor().newInstance();
            command.setRank(commandInfoDO.getLevel());
            return command;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 分页查询指令列表
     * 支持按等级、默认等级、语法和启用状态筛选
     *
     * @param request 查询请求参数
     * @return 分页的指令列表
     */
    public Page<CommandReqDTO> getCommandListFromDB(CommandReqDTO request) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (request.getLevel() != null) queryWrapper.in("level", request.getLevelList());
        if (request.getDefaultLevel() != null) queryWrapper.in("default_level", request.getDefaultLevelList());
        if (!RequireUtil.isEmpty(request.getSyntax())) queryWrapper.like("syntax", request.getSyntax());

        if (request.getEnabled() != null) queryWrapper.eq("enabled", request.getEnabled());
        Page<CommandInfoDO> commandInfoDOPage = commandInfoMapper.paginateWithRelations(request.getPageNo(), request.getPageSize(), queryWrapper);
        return new Page<>(
                commandInfoDOPage.getRecords().stream()
                        .map(record -> {
                            CommandReqDTO build = CommandReqDTO.builder()
                                    .id(record.getId())
                                    .level(record.getLevel())
                                    .syntax(record.getSyntax())
                                    .defaultLevel(record.getDefaultLevel())
                                    .clazz(record.getClazz())
                                    .enabled(record.isEnabled())
                                    .description(getDescriptionByCommandInfoDO(record))
                                    .build();
                            build.setPageNo(null);
                            build.setPageSize(null);
                            return build;
                        })
                        .toList(),
                commandInfoDOPage.getPageNumber(),
                commandInfoDOPage.getPageSize(),
                commandInfoDOPage.getTotalRow()
        );
    }

    /**
     * 根据指令信息获取指令描述
     * 反射实例化指令对象后获取其描述信息
     *
     * @param CommandDO 指令信息实体
     * @return 指令描述文本
     */
    public String getDescriptionByCommandInfoDO(CommandInfoDO CommandDO) {
        Command command = getCommandInstance(CommandDO);
        if (command == null) {
            return I18nUtil.getLogMessage("CommandsExecutor.addCommand.warn1", CommandDO.getSyntax());
        }
        return command.getDescription();
    }

    /**
     * 更新指令配置
     * 只允许修改开关和等级，语法和类名不允许修改以避免与描述文本冲突
     *
     * @param request 指令更新请求参数
     * @return 更新后的指令信息
     */
    @Transactional
    public CommandInfoDO updateCommand(CommandReqDTO request) {

        RequireUtil.requireNotNull(request.getEnabled(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "enabled"));
        RequireUtil.requireNotNull(request.getId(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));

        // 只能改开关和等级，其他的不能改
        // Syntax改了，指令和提示会冲突，比如提示：输入：!level <等级>就是错的了
        // 因为level已经被改成其他的了
        // DefaultLevel和Clazz也不能改
        commandInfoMapper.update(CommandInfoDO.builder()
                .id(request.getId())
                .level(request.getLevel())
                .enabled(request.getEnabled())
                .build());
        CommandInfoDO commandInfoDO = commandInfoMapper.selectOneById(request.getId());
        updateRegisteredCommands(commandInfoDO);
        return commandInfoDO;
    }


    /**
     * 热加载事件脚本
     * 遍历所有频道的EventScriptManager重新加载事件脚本
     */
    public void reloadEventsByGMCommand() {
        // 遍历所有频道执行 ReloadEventsCommand 中的 execute 逻辑
        for (Channel ch : Server.getInstance().getAllChannels()) {
            ch.reloadEventScriptManager();
        }
        log.info(I18nUtil.getMessage("ReloadEventsCommand.message2"));

    }

    /**
     * 热加载传送脚本
     * 重建PortalScriptManager实例重新加载所有传送脚本
     */
    public void reloadPortalsByGMCommand() {
        PortalScriptManager.getInstance().reloadPortalScripts();
        log.info(I18nUtil.getMessage("ReloadPortalsCommand.message2"));
    }


    /**
     * 热加载地图
     * 遍历所有世界的所有频道，重置每张地图并重新传送所有玩家
     */
    public void reloadMapsByGMCommand() {
        Server.getInstance().getWorlds().forEach(world -> {
            world.getChannels().forEach(channel -> {
                Map<Integer, MapleMap> maps = channel.getMapFactory().getMaps();
                maps.forEach((mapid, map) -> {
                    List<Character> allPlayers = map.getAllPlayers();
                    MapleMap newMap = channel.getMapFactory().resetMap(mapid);
                    String message = I18nUtil.getMessage("ReloadMapCommand.message2");
                    allPlayers.forEach(chr -> {
                        int callerid = chr.getId();
                        chr.saveLocationOnWarp();
                        chr.changeMap(newMap);
                        if (chr.getId() != callerid) {
                            chr.dropMessage(message);
                        }
                    });
                });
            });
        });
        log.info(I18nUtil.getMessage("ReloadMapCommand.message1"));
        }

    }