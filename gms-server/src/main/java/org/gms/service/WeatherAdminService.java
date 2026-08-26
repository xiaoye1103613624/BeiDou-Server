package org.gms.service;

import org.gms.exception.BizException;
import org.gms.model.dto.WeatherApplyDTO;
import org.gms.model.dto.WeatherStatusDTO;
import org.gms.net.server.Server;
import org.gms.net.server.world.World;
import org.gms.server.weather.WeatherPackets;
import org.gms.server.weather.WeatherProfile;
import org.gms.server.weather.WeatherService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 管理端天气切换：复用 {@link WeatherService} / {@link WeatherPackets}，不改 GM 指令类。
 */
@Service
public class WeatherAdminService {

    private static final List<WeatherStatusDTO.Option> TIME_OPTIONS = List.of(
            opt("keep", "不改时段", null),
            opt("day", "正午", "day"),
            opt("dusk", "黄昏", "dusk"),
            opt("night", "黑夜", "night"),
            opt("dawn", "黎明", "dawn"),
            opt("midnight", "午夜（裸天）", "midnight"),
            opt("clock", "指定时刻", null),
            opt("release", "仅解除时段冻结", null)
    );

    private static final List<WeatherStatusDTO.Option> SKY_OPTIONS = buildSkyOptions();

    public WeatherStatusDTO status() {
        byte skyId = WeatherService.currentSky();
        String skyName = WeatherService.skyName(skyId);
        boolean timeFrozen = WeatherService.isTimeOverridden();
        boolean skyForced = WeatherService.isOverridden();
        boolean bareSky = WeatherService.isBareSky();

        StringBuilder cmd = new StringBuilder("!weather");
        if (!timeFrozen && !skyForced) {
            cmd.append("（当前自动）");
        } else {
            if (bareSky) {
                cmd.append(" midnight");
            } else if (timeFrozen) {
                cmd.append(' ').append(WeatherService.clockString());
            }
            if (skyForced) {
                cmd.append(' ').append(skyName);
            }
        }

        return WeatherStatusDTO.builder()
                .clock(WeatherService.clockString())
                .wallClock(formatMinute(WeatherService.wallClockMinuteOfDay()))
                .nightLevel(WeatherService.nightLevel())
                .timeFrozen(timeFrozen)
                .skyForced(skyForced)
                .bareSky(bareSky)
                .skyId(skyId)
                .skyName(skyName)
                .skyNameZh(skyLabelZh(skyName))
                .equivalentCommand(cmd.toString().trim())
                .timeOptions(TIME_OPTIONS)
                .skyOptions(SKY_OPTIONS)
                .build();
    }

    public WeatherStatusDTO apply(WeatherApplyDTO req) {
        if (req == null) {
            throw BizException.illegalArgument("请求体为空");
        }

        if (req.isAuto()) {
            WeatherService.clearTimeOverride();
            WeatherService.clearSkyOverride();
            broadcastAll(false);
            return status();
        }

        String time = normalize(req.getTime());
        String sky = normalize(req.getSky());
        boolean changed = false;
        boolean bareSky = false;
        Integer minute = null;
        Byte skyId = null;

        if (time != null && !"keep".equals(time)) {
            switch (time) {
                case "day", "noon" -> minute = WeatherService.TIME_DAY;
                case "night" -> minute = WeatherService.TIME_NIGHT;
                case "midnight" -> {
                    minute = WeatherService.TIME_NIGHT;
                    bareSky = true;
                }
                case "dawn", "sunrise" -> minute = WeatherService.TIME_DAWN;
                case "dusk", "sunset" -> minute = WeatherService.TIME_DUSK;
                case "clock" -> {
                    minute = parseClock(req.getClock());
                    if (minute == null) {
                        throw BizException.illegalArgument("时刻格式无效，请用 HH:MM");
                    }
                }
                case "release" -> {
                    WeatherService.clearTimeOverride();
                    changed = true;
                }
                default -> throw BizException.illegalArgument("未知时段: " + time);
            }
        }

        if (sky != null && !"keep".equals(sky)) {
            if ("release".equals(sky)) {
                WeatherService.clearSkyOverride();
                changed = true;
            } else {
                WeatherProfile profile = WeatherProfile.byName(sky);
                if (profile == null) {
                    profile = aliasSky(sky);
                }
                if (profile == null) {
                    throw BizException.illegalArgument("未知天空: " + sky);
                }
                skyId = profile.id();
            }
        }

        if (minute == null && skyId == null && !changed) {
            throw BizException.illegalArgument("请选择时段和/或天空，或点击恢复自动");
        }

        if (minute != null) {
            WeatherService.setTime(minute);
            WeatherService.setBareSky(bareSky);
            changed = true;
        }
        if (skyId != null) {
            WeatherService.setSky(skyId, WeatherService.OVERRIDE_HOLD_MS);
            changed = true;
        }

        if (changed) {
            boolean snap = Boolean.TRUE.equals(req.getSnap());
            broadcastAll(snap);
        }
        return status();
    }

    private static void broadcastAll(boolean snap) {
        Server server = Server.getInstance();
        if (server == null || !server.isOnline()) {
            throw BizException.illegalArgument("游戏服务未在线，无法广播天气");
        }
        List<World> worlds = server.getWorlds();
        if (worlds == null) {
            return;
        }
        for (World world : worlds) {
            WeatherPackets.broadcast(world, snap);
        }
    }

    private static List<WeatherStatusDTO.Option> buildSkyOptions() {
        List<WeatherStatusDTO.Option> list = new ArrayList<>();
        list.add(opt("keep", "不改天空", null));
        for (WeatherProfile p : WeatherProfile.values()) {
            list.add(opt(p.profileName(), skyLabelZh(p.profileName()), p.profileName()));
        }
        list.add(opt("release", "仅解除天空强制", null));
        return List.copyOf(list);
    }

    private static WeatherStatusDTO.Option opt(String value, String label, String token) {
        return WeatherStatusDTO.Option.builder()
                .value(value)
                .label(label)
                .commandToken(token)
                .build();
    }

    private static String skyLabelZh(String name) {
        if (name == null) {
            return "";
        }
        return switch (name.toLowerCase(Locale.ROOT)) {
            case "clear" -> "晴朗";
            case "rain" -> "下雨";
            case "snow" -> "下雪";
            case "overcast" -> "阴天";
            case "storm" -> "暴雨";
            case "blizzard" -> "暴风雪";
            case "leaves" -> "枫叶";
            case "blossom" -> "樱花";
            case "sandstorm" -> "沙尘暴";
            default -> name;
        };
    }

    private static WeatherProfile aliasSky(String sky) {
        return switch (sky) {
            case "sunny" -> WeatherProfile.CLEAR;
            case "rainy" -> WeatherProfile.RAIN;
            case "snowy" -> WeatherProfile.SNOW;
            default -> null;
        };
    }

    private static String normalize(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim().toLowerCase(Locale.ROOT);
        return t.isEmpty() ? null : t;
    }

    private static String formatMinute(int m) {
        int mm = Math.floorMod(m, WeatherService.MINUTES_PER_DAY);
        return String.format("%02d:%02d", mm / 60, mm % 60);
    }

    /** 与 WeatherCommand.parseClock 同语义。 */
    private static Integer parseClock(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String s = raw.trim();
        try {
            int h;
            int m = 0;
            int colon = s.indexOf(':');
            if (colon >= 0) {
                h = Integer.parseInt(s.substring(0, colon));
                m = Integer.parseInt(s.substring(colon + 1));
            } else if (s.length() == 4) {
                h = Integer.parseInt(s.substring(0, 2));
                m = Integer.parseInt(s.substring(2));
            } else {
                h = Integer.parseInt(s);
            }
            if (h < 0 || h > 23 || m < 0 || m > 59) {
                return null;
            }
            return h * 60 + m;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
