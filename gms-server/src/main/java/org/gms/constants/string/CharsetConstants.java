/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gms.constants.string;

/*
 * Thanks to GabrielSin (EllinMS) - gabrielsin@playellin.net
 * Ellin
 * MapleStory Server
 * CharsetConstants
 */

import lombok.Getter;
import org.gms.manager.ServerManager;
import org.gms.property.ServiceProperty;

import java.nio.charset.Charset;
import java.util.Locale;

/**
 * 字符集常量类
 * <p>提供多语言字符集和Locale的获取方法</p>
 */
public class CharsetConstants {
    /** 服务端语言（保证只加载一次） */
    private static final Language SERVICE_LANGUAGE = loadServiceLanguage();

    /**
     * 根据语言ID获取字符集
     * @param language 语言ID
     * @return 字符集
     */
    public static Charset getCharset(int language) {
        return Charset.forName(Language.fromLang(language).getCharset());
    }

    /**
     * 根据语言ID获取Locale
     * @param language 语言ID
     * @return Locale
     */
    public static Locale getLanguageLocale(int language) {
        return Locale.forLanguageTag(Language.fromLang(language).getLanguageTag());
    }

    /**
     * 判断是否为中文语言
     * @return true=中文
     */
    public static boolean isZhCN() {
        return Language.LANGUAGE_CN == SERVICE_LANGUAGE;
    }

    /**
     * 加载服务端语言配置
     * @return 语言枚举值
     */
    private static Language loadServiceLanguage() {
        ServiceProperty serviceProperty = ServerManager.getApplicationContext().getBean(ServiceProperty.class);
        String language = serviceProperty.getLanguage();
        if (language.equals("zh-CN")) {
            return Language.LANGUAGE_CN;
        } else {
            return Language.LANGUAGE_US;
        }
    }

    /**
     * 语言枚举
     */
    @Getter
    private enum Language {
        /** 英语 */
        LANGUAGE_US(2, "US-ASCII", "en-US"),
        /** 中文 */
        LANGUAGE_CN(3, "GBK", "zh-CN"),
        /** 葡萄牙语(巴西) */
        LANGUAGE_PT_BR(-1, "ISO-8859-1", "en-US"),
        /** 泰语 */
        LANGUAGE_THAI(-1, "TIS620", "th-TH"),
        /** 韩语 */
        LANGUAGE_KOREAN(-1, "MS949", "ko-KR");

        /** 语言ID */
        private final int lang;
        /** 字符集 */
        private final String charset;
        /** 语言标签 */
        private final String languageTag;

        Language(int lang, String charset, String languageTag) {
            this.lang = lang;
            this.charset = charset;
            this.languageTag = languageTag;
        }

        /**
         * 根据语言ID获取语言枚举
         * @param lang 语言ID
         * @return 语言枚举值
         */
        public static Language fromLang(int lang) {
            for (Language value : values()) {
                if (value.getLang() == lang) {
                    return value;
                }
            }
            return SERVICE_LANGUAGE;
        }
    }
}