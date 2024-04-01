package edu.tongji.backend.util;

public class RedisConstants {
    public static final String LOGIN_CODE_KEY="login:code:";
    public static final String LOGIN_TOKEN_KEY="login:token:";
    public static final Integer LOGIN_TOKEN_TTL=30;//Unit:Minute
    public static final Integer LOGIN_CODE_TIMEOUT=1;
}
