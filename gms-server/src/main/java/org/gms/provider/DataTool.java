/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

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
package org.gms.provider;

import org.gms.provider.wz.DataType;

import java.awt.*;

/**
 * 数据工具类
 * 提供从WZ数据节点中安全读取各种类型数据（String、Double、Int、Point等）的工具方法
 */
public class DataTool {
    /**
     * 从Data节点获取字符串
     *
     * @param data 数据节点
     * @return 字符串值
     */
    public static String getString(Data data) {
        return ((String) data.getData());
    }

    /**
     * 从Data节点获取字符串，带默认值
     *
     * @param data 数据节点
     * @param def  默认值
     * @return 字符串值或默认值
     */
    public static String getString(Data data, String def) {
        if (data == null || data.getData() == null) {
            return def;
        } else {
            return ((String) data.getData());
        }
    }

    /**
     * 通过路径从Data节点获取字符串
     *
     * @param path 子节点路径
     * @param data 数据节点
     * @return 字符串值
     */
    public static String getString(String path, Data data) {
        return getString(data.getChildByPath(path));
    }

    /**
     * 通过路径从Data节点获取字符串，带默认值
     *
     * @param path 子节点路径
     * @param data 数据节点
     * @param def  默认值
     * @return 字符串值或默认值
     */
    public static String getString(String path, Data data, String def) {
        return getString(data.getChildByPath(path), def);
    }

    /**
     * 从Data节点获取Double值
     *
     * @param data 数据节点
     * @return Double值
     */
    public static double getDouble(Data data) {
        return (Double) data.getData();
    }

    /**
     * 从Data节点获取Float值
     *
     * @param data 数据节点
     * @return Float值
     */
    public static float getFloat(Data data) {
        return (Float) data.getData();
    }

    public static int getInt(Data data) {
        if (data == null || data.getData() == null) {
            return 0;// DEF?
        }
        return (Integer) data.getData();
    }

    public static int getInt(String path, Data data) {
        return getInt(data.getChildByPath(path));
    }

    /**
     * 从Data节点获取Int值，支持类型转换
     *
     * @param data 数据节点
     * @return Int值
     */
    public static int getIntConvert(Data data) {
        if (data.getType() == DataType.STRING) {
            return Integer.parseInt(getString(data));
        } else {
            return getInt(data);
        }
    }

    public static int getIntConvert(Data data, int def) {
        if (data == null) {
            return def;
        }
        if (data.getType() == DataType.STRING) {
            String dd = getString(data);
            if (dd.endsWith("%")) {
                dd = dd.substring(0, dd.length() - 1);
            }
            try {
                return Integer.parseInt(dd);
            } catch (NumberFormatException nfe) {
                return def;
            }
        } else {
            return getInt(data, def);
        }
    }

    public static int getIntConvert(String path, Data data) {
        Data d = data.getChildByPath(path);
        if (d.getType() == DataType.STRING) {
            return Integer.parseInt(getString(d));
        } else {
            return getInt(d);
        }
    }

    public static int getInt(Data data, int def) {
        if (data == null || data.getData() == null) {
            return def;
        } else if (data.getType() == DataType.STRING) {
            return Integer.parseInt(getString(data));
        } else {
            Object numData = data.getData();
            if (numData instanceof Integer) {
                return (Integer) numData;
            } else {
                return (Short) numData;
            }
        }
    }

    /**
     * 通过路径从Data节点获取Int值，带默认值
     *
     * @param path 子节点路径
     * @param data 数据节点
     * @param def  默认值
     * @return Int值或默认值
     */
    public static int getInt(String path, Data data, int def) {
        return getInt(data.getChildByPath(path), def);
    }

    /**
     * 通过路径从Data节点获取Int值，带默认值，支持类型转换
     *
     * @param path 子节点路径
     * @param data 数据节点
     * @param def  默认值
     * @return Int值或默认值
     */
    public static int getIntConvert(String path, Data data, int def) {
        Data d = data.getChildByPath(path);
        if (d == null) {
            return def;
        }
        if (d.getType() == DataType.STRING) {
            try {
                return Integer.parseInt(getString(d));
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                return def;
            }
        } else {
            return getInt(d, def);
        }
    }

    public static Integer getInteger(String path, Data data) {
        Data child = data.getChildByPath(path);
        if (child == null || child.getData() == null) {
            return null;
        } else if (child.getType() == DataType.STRING) {
            return Integer.parseInt(getString(child));
        } else {
            Object numData = child.getData();
            return ((Number) numData).intValue();
        }
    }

    public static int getInteger(String path, Data data, int def) {
        Integer val = getInteger(path, data);
        return val == null ? def : val;
    }

    public static Short getShort(String path, Data data) {
        Data child = data.getChildByPath(path);
        if (child == null || child.getData() == null) {
            return null;
        } else if (child.getType() == DataType.STRING) {
            return Short.parseShort(getString(child));
        } else {
            Object numData = child.getData();
            return ((Number) numData).shortValue();
        }
    }

    public static short getShort(String path, Data data, short def) {
        Short val = getShort(path, data);
        return val == null ? def : val;
    }

    public static Long getLong(String path, Data data) {
        Data child = data.getChildByPath(path);
        if (child == null || child.getData() == null) {
            return null;
        } else if (child.getType() == DataType.STRING) {
            return Long.parseLong(getString(child));
        } else {
            Object numData = child.getData();
            return ((Number) numData).longValue();
        }
    }

    public static long getLong(String path, Data data, long def) {
        Long val = getLong(path, data);
        return val == null ? def : val;
    }

    /**
     * 从Data节点获取Point坐标
     *
     * @param data 数据节点
     * @return Point对象
     */
    public static Point getPoint(Data data) {
        return ((Point) data.getData());
    }

    /**
     * 通过路径从Data节点获取Point坐标
     *
     * @param path 子节点路径
     * @param data 数据节点
     * @return Point对象
     */
    public static Point getPoint(String path, Data data) {
        return getPoint(data.getChildByPath(path));
    }

    /**
     * 通过路径从Data节点获取Point坐标，带默认值
     *
     * @param path 子节点路径
     * @param data 数据节点
     * @param def  默认值
     * @return Point对象或默认值
     */
    public static Point getPoint(String path, Data data, Point def) {
        final Data pointData = data.getChildByPath(path);
        if (pointData == null) {
            return def;
        }
        return getPoint(pointData);
    }

    public static String getFullDataPath(Data data) {
        String path = "";
        DataEntity myData = data;
        while (myData != null) {
            path = myData.getName() + "/" + path;
            myData = myData.getParent();
        }
        return path.substring(0, path.length() - 1);
    }

    public static String getAttributeValue(Data data,String name) {
        return data.getAttributeValue(name);
    }
    public static String getAttributeValue(Data data,String name,String def) {
        String val = getAttributeValue(data,name);
        return val == null ? def : val;
    }
    public static int getAttributeValueInt(Data data,String name,int def) {
        String val = getAttributeValue(data,name);
        return val == null ? def : Integer.parseInt(val);
    }
}