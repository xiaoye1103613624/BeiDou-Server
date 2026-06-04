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
     * 【业务服务】CommandService：GM指令服务类，负责GM指令的加载、注册和管理。
     * 
     * <p>提供指令的加载、查询、更新功能，支持指令的启用/禁用和权限等级调整。</p>
     */
    @Slf4j
    @Service
    @AllArgsConstructor
    public class CommandService {

        /** 指令信息数据访问接口 */
        private final CommandInfoMapper commandInfoMapper;

        /**
         * 加载所有GM指令到内存。
         * 
         * <p>从数据库读取所有指令配置，按权限等级分组后注册到指令执行器。</p>
         * 
         * @param registeredCommands 已注册指令映射表（key为指令名，value为指令对象）
         * @param commandsNameDesc 指令名称和描述列表（按等级分组）
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
            
            // 根据权限等级对指令分组
            Map<Integer, List<CommandInfoDO>> levelMap = commandInfoList.stream()
                    .collect(Collectors.groupingBy(CommandInfoDO::getLevel));
            
            // 按等级0-6依次注册指令
            for (int i = 0; i <= 6; i++) {
                registerCommands(registeredCommands, commandsNameDesc, i, levelMap.get(i));
            }
            
            log.info(I18nUtil.getLogMessage("CommandService.loadCommands.info1"), registeredCommands.size());
        }


    /**
         * 更新已注册的指令。
         * 
         * <p>根据指令配置的变更，动态更新内存中的指令注册：
         * <ul>
         *   <li>新指令启用：注册到内存</li>
         *   <li>已注册指令禁用：从内存移除</li>
         *   <li>权限等级变更：更新指令的权限等级</li>
         * </ul></p>
         * 
         * @param commandInfoDO 指令配置信息
         */
        private void updateRegisteredCommands(CommandInfoDO commandInfoDO) {
            CommandsExecutor commandsExecutor = CommandsExecutor.getInstance();
            HashMap<String, Command> registeredCommands = commandsExecutor.getRegisteredCommands();
            List<Pair<List<String>, List<String>>> commandsNameDesc = commandsExecutor.getCommandsNameDesc();
            String syntax = commandInfoDO.getSyntax().toLowerCase();
            Command command = registeredCommands.get(syntax);
            
            // 如果原先未注册
            if (command == null) {
                // 如果更新的状态是开启，则添加注册；如果新状态是关闭，则不必理会
                if (commandInfoDO.isEnabled()) {
                    command = getCommandInstance(commandInfoDO);
                    RequireUtil.requireNotNull(command, I18nUtil.getExceptionMessage("UNKNOWN_PARAMETER_VALUE", "clazz", commandInfoDO.getClazz()));
                    registeredCommands.put(syntax, command);
                    // 添加到对应等级的指令列表
                    Pair<List<String>, List<String>> nameDescPair = commandsNameDesc.get(commandInfoDO.getLevel());
                    nameDescPair.getLeft().add(syntax);
                    nameDescPair.getRight().add(command.getDescription());
                }
                return;
            }
            
            // 原先已注册，先从旧等级列表中移除
            Pair<List<String>, List<String>> oldPair = commandsNameDesc.get(command.getRank());
            int index = oldPair.getLeft().indexOf(syntax);
            if (index > -1) {
                oldPair.getLeft().remove(index);
                oldPair.getRight().remove(index);
            }

            // 如果新状态是开启，更新到新等级；如果关闭，则移除注册
            if (commandInfoDO.isEnabled()) {
                // 添加到新等级的指令列表
                Pair<List<String>, List<String>> newPair = commandsNameDesc.get(commandInfoDO.getLevel());
                newPair.getLeft().add(syntax);
                newPair.getRight().add(command.getDescription());
                // 更新指令的权限等级
                command.setRank(commandInfoDO.getLevel());
            } else {
                // 从注册映射中移除
                registeredCommands.remove(syntax);
            }
        }

     /**
         * 注册指定等级的指令。
         * 
         * <p>遍历指定等级的指令配置，创建指令实例并注册到内存中。</p>
         * 
         * @param registeredCommands 已注册指令映射表
         * @param commandsNameDesc 指令名称和描述列表
         * @param level 权限等级
         * @param commandInfoList 指令配置列表
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
                // 未开启的指令不加载
                if (!item.isEnabled()) {
                    continue;
                }
                
                // 创建指令实例
                Command command = getCommandInstance(item);
                if (command == null) {
                    log.warn(I18nUtil.getLogMessage("CommandService.loadCommands.warn3"), item.getSyntax());
                    continue;
                }

                String commandName = item.getSyntax().toLowerCase();
                // 检查指令是否已注册
                if (registeredCommands.containsKey(commandName)) {
                    log.warn(I18nUtil.getLogMessage("CommandsExecutor.addCommand.warn1", item.getSyntax()));
                    continue;
                }

                try {
                    // 添加到等级指令列表和注册映射
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
         * 根据指令配置创建指令实例。
         * 
         * <p>通过反射动态加载指令类，路径格式为：org.gms.client.command.commands.gm{等级}.{类名}</p>
         * 
         * @param commandInfoDO 指令配置信息
         * @return 指令实例，加载失败返回null
         */
        private Command getCommandInstance(CommandInfoDO commandInfoDO) {
            try {
                Class<?> aClass = Class.forName("org.gms.client.command.commands.gm" 
                        + commandInfoDO.getDefaultLevel() + "." + commandInfoDO.getClazz());
                Command command = (Command) aClass.getDeclaredConstructor().newInstance();
                command.setRank(commandInfoDO.getLevel());
                return command;
            } catch (Exception e) {
                return null;
            }
        }

    /**
         * 从数据库分页查询指令列表。
         * 
         * <p>支持按权限等级、默认等级、指令名和启用状态筛选。</p>
         * 
         * @param request 查询条件
         * @return 分页后的指令列表
         */
        public Page<CommandReqDTO> getCommandListFromDB(CommandReqDTO request) {
            QueryWrapper queryWrapper = new QueryWrapper();
            // 按权限等级筛选
            if (request.getLevel() != null) queryWrapper.in("level", request.getLevelList());
            // 按默认等级筛选
            if (request.getDefaultLevel() != null) queryWrapper.in("default_level", request.getDefaultLevelList());
            // 按指令名模糊搜索
            if (!RequireUtil.isEmpty(request.getSyntax())) queryWrapper.like("syntax", request.getSyntax());
            // 按启用状态筛选
            if (request.getEnabled() != null) queryWrapper.eq("enabled", request.getEnabled());

            Page<CommandInfoDO> commandInfoDOPage = commandInfoMapper.paginateWithRelations(
                    request.getPageNo(), request.getPageSize(), queryWrapper);
            
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
         * 根据指令配置获取指令描述。
         * 
         * @param CommandDO 指令配置信息
         * @return 指令描述
         */
        public String getDescriptionByCommandInfoDO(CommandInfoDO CommandDO) {
            Command command = getCommandInstance(CommandDO);
            if (command == null) {
                return I18nUtil.getLogMessage("CommandsExecutor.addCommand.warn1", CommandDO.getSyntax());
            }
            return command.getDescription();
        }

        /**
         * 更新指令配置。
         * 
         * <p>仅允许修改启用状态和权限等级，其他字段（如指令名、类名）不可修改。</p>
         * 
         * @param request 更新请求
         * @return 更新后的指令配置
         */
        @Transactional
        public CommandInfoDO updateCommand(CommandReqDTO request) {
            RequireUtil.requireNotNull(request.getEnabled(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "enabled"));
            RequireUtil.requireNotNull(request.getId(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));

            // 仅更新启用状态和权限等级
            commandInfoMapper.update(CommandInfoDO.builder()
                    .id(request.getId())
                    .level(request.getLevel())
                    .enabled(request.getEnabled())
                    .build());
            
            // 查询更新后的配置并同步到内存
            CommandInfoDO commandInfoDO = commandInfoMapper.selectOneById(request.getId());
            updateRegisteredCommands(commandInfoDO);
            return commandInfoDO;
        }

        /**
         * 重新加载事件脚本。
         * 
         * <p>通知所有频道重新加载事件脚本管理器。</p>
         */
        public void reloadEventsByGMCommand() {
            for (Channel ch : Server.getInstance().getAllChannels()) {
                ch.reloadEventScriptManager();
            }
            log.info(I18nUtil.getMessage("ReloadEventsCommand.message2"));
        }

        /**
         * 重新加载传送门脚本。
         * 
         * <p>重新加载所有传送门脚本。</p>
         */
        public void reloadPortalsByGMCommand() {
            PortalScriptManager.getInstance().reloadPortalScripts();
            log.info(I18nUtil.getMessage("ReloadPortalsCommand.message2"));
        }

        /**
         * 重新加载地图。
         * 
         * <p>重置所有地图，将玩家转移到新加载的地图中。</p>
         */
        public void reloadMapsByGMCommand() {
            Server.getInstance().getWorlds().forEach(world -> {
                world.getChannels().forEach(channel -> {
                    Map<Integer, MapleMap> maps = channel.getMapFactory().getMaps();
                    maps.forEach((mapid, map) -> {
                        // 保存当前地图的所有玩家
                        List<Character> allPlayers = map.getAllPlayers();
                        // 重置地图
                        MapleMap newMap = channel.getMapFactory().resetMap(mapid);
                        String message = I18nUtil.getMessage("ReloadMapCommand.message2");
                        // 将玩家转移到新地图
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