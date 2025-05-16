package org.gms.constants.game;

import lombok.Getter;

/**
 * 对话框按钮 枚举
 */
@Getter
public enum NextLevelType {
    /**
     * 下一步
     */
    SEND_NEXT("sendNextLevel"),
    /**
     * 最后一级,只有上一步
     */
    SEND_LAST("sendLastLevel"),
    /**
     * 最后一级 有上一步和下一步
     */
    SEND_LAST_NEXT("sendLastNextLevel"),
    /**
     * 带有是
     */
    SEND_OK("sendOkLevel"),
    /**
     * 多选
     */
    SEND_SELECT("sendSelectLevel"),
    /**
     * 多选且有下一步
     */
    SEND_NEXT_SELECT("sendNextSelectLevel"),
    /**
     * 数字输入框
     */
    GET_INPUT_NUMBER("getInputNumberLevel"),
    /**
     * 带有输入框
     */
    GET_INPUT_TEXT("getInputTextLevel"),
    /**
     * 带有接受和拒绝
     */
    SEND_ACCEPT_DECLINE("sendAcceptDeclineLevel"),
    /**
     * 是和否
     */
    SEND_YES_NO("sendYesNoLevel"),
    ;

    private final String type;

    NextLevelType(String type) {
        this.type = type;
    }
}
