package edu.tongji.backend.util;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 All contributors of the project
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */





public class RedisConstants {
    public static final String LOGIN_CODE_KEY="login:code:";
    public static final String LOGIN_TOKEN_KEY="login:token:";
    public static final String SHARED_SESSION_KEY="login:shared:session:";
    public static final Integer LOGIN_TOKEN_TTL=30;//Unit:Minute
    public static final String LOGIN_LIMIT="login:limit:";
    public static final Integer LOGIN_LIMIT_TIMEOUT=12;
    public static final String ADMIN_PERM_CODE="admin:permission:code:";
    public static Integer ADMIN_PERM_CODE_TIMEOUT=7;
    public static final Integer LOGIN_CODE_TIMEOUT=1;
    public static final Long CACHE_NULL_TTL=2L;
    public static final String CACHE_GLYCEMIA_KEY="cache:glycemia:";
    public static final String CACHE_LATEST_GLYCEMIA_KEY ="latest:glycemia_id:";
    public static final Long LATEST_GLYCEMIA_TTL=2L;
    public static final String EXERCISE_RUNNING_KEY="exercise:running";
    public static final Long CACHE_GLYCEMIA_TTL=7L;
    public static final String CACHE_DAILY_GLYCEMIA_KEY="cache:daily:glycemia:";
    public static final Long CACHE_DAILY_GLYCEMIA_TTL= 1L;
    public static final String CACHE_HISTORY_GLYCEMIA_KEY="cache:history:glycemia:";
    public static final Long CACHE_HISTORY_GLYCEMIA_TTL=7L;
    public static final String HOSPITAL_GEO_KEY="geo:hospital:";
    public static final String USER_SIGN_KEY="sign:";
    public static final String RUNNING_GEO_KEY="geo:running:";
    public static final String CACHE_USER_LAST_EXERCISE_KEY="cache:user:last:exercise:";
    public static final String CACHE_EXERCISE_KEY="cache:exercise:";
    public static final String CACHE_RUNNING_KEY="cache:running:";
    public static final Long CACHE_RUNNING_TTL=30L;
    public static final Long RUNNING_GEO_TTL=30L;
    public static final String USER_PROFILE_KEY="user:profile:";
    public static final Long USER_PROFILE_TTL=30L;
    public static final String EMPHERAL_TOKEN_HEADER="empheral_token_header:";
    public static final String SUBSRIBE_DOCTOR_KEY="subscription:toDoctor:key:";
    public static final String FOLLOWER_KEY="message:fromPatient:key:";
    public static final Long FOLLOWER_KEY_TTL=7L;
    public static final String FOLLOWERS_NUM_KEY="follower:num:";
    public static final String FOLLOWEES_NUM_KEY="followee:num:";
    public static final String FOLLOWER_LIST_KEY="follower:list:";
    public static final String VERBOSE_DOCTOR_INFO="verbose:doctor:info:";
    public static final Long VERBOSE_DOCTOR_INFO_TTL=7L;
    public static final String PATIENT_LIST_PREFIX="patient:list:";
    public static final Long PATIENT_LIST_TTL=1L;//Hour
    public static final String PATIENT_SET="patient:set:";
    public static final Long PATIENT_SET_TTL=1L;//Day
}
