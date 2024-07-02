package edu.tongji.backend.util;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 Victor Hu
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




import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class RedidIdWorker {
    private static final Long BEGIN_TIMESTAMP=1701388800L;
    private static final Integer COUNT_BITS=32;

    public RedidIdWorker(RedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private RedisTemplate stringRedisTemplate;
    public Long nextId(LocalDateTime timeSecond,String keyPrefix){
        //Generate timestamp
        Long nowSecond=timeSecond.toEpochSecond(ZoneOffset.UTC);
        Long timestamp=nowSecond-BEGIN_TIMESTAMP;
        //Generate Serial
        String date = timeSecond.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));//Prefix statistics!!!IMPORTANT
        Long count = stringRedisTemplate.opsForValue().increment("icr:" + keyPrefix + ":" + date);//2^64
        //Concat
        return timestamp<<COUNT_BITS|count;
    }
    public Long nextId(String keyPrefix){
        //Generate timestamp
        LocalDateTime now=LocalDateTime.now();
        //Concat
        return nextId(now,keyPrefix);
    }
    public static void main(String[] args){
        LocalDateTime time=LocalDateTime.of(2023,12,1,0,0,0,0);
        Long second=time.toEpochSecond(ZoneOffset.UTC);
        System.out.println(second);
    }
}
