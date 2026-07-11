package com.campus.trade.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

public class SnowflakeIdUtil {

    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(1, 1);

    public static Long nextId() {
        return SNOWFLAKE.nextId();
    }

    public static String nextIdStr() {
        return SNOWFLAKE.nextIdStr();
    }
}
