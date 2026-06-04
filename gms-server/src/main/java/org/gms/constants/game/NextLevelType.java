package org.gms.constants.game;

import lombok.Getter;

/**
 * NPC对话下一级类型枚举
 * <p>定义NPC对话流程中下一步的处理方式</p>
 */
@Getter
public enum NextLevelType {
    /** 发送下一级 */
    SEND_NEXT("sendNextLevel"),
    /** 发送最后一级 */
    SEND_LAST("sendLastLevel"),
    /** 发送最后然后下一级 */
    SEND_LAST_NEXT("sendLastNextLevel"),
    /** 发送确认 */
    SEND_OK("sendOkLevel"),
    /** 发送选择 */
    SEND_SELECT("sendSelectLevel"),
    /** 发送下一级选择 */
    SEND_NEXT_SELECT("sendNextSelectLevel"),
    /** 获取数字输入 */
    GET_INPUT_NUMBER("getInputNumberLevel"),
    /** 获取文本输入 */
    GET_INPUT_TEXT("getInputTextLevel"),
    /** 发送接受/拒绝选项 */
    SEND_ACCEPT_DECLINE("sendAcceptDeclineLevel"),
    /** 发送是/否选项 */
    SEND_YES_NO("sendYesNoLevel"),
    ;

    /** 类型标识 */
    private final String type;

    NextLevelType(String type) {
        this.type = type;
    }
}