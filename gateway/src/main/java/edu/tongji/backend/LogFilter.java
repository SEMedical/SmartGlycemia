package edu.tongji.backend;

/*-
 * #%L
 * Tangxiaozhi
 * %%
 * Copyright (C) 2024 Victor Hu,UltraTempest10
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





import cn.hutool.core.date.DateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Slf4j
@Order(-1)
@Component
public class LogFilter implements GlobalFilter {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String ip = request.getRemoteAddress().getAddress().getHostAddress();
        RequestPath path = request.getPath();
        stringRedisTemplate.opsForHyperLogLog().add("ip:addr:"+LocalDate.now().toString(),ip);
        int dayOfMonth = LocalDate.now().getDayOfMonth();
        int monthNum= LocalDate.now().getMonthValue();
        int yearNum= LocalDate.now().getYear();
        if(dayOfMonth==1){
            Long size = stringRedisTemplate.opsForHyperLogLog().size("ip:addr:" + yearNum + "-" + monthNum);
            if(size==null) {
                for (int i = 1; i <= 30; i++) {
                    stringRedisTemplate.opsForHyperLogLog().union("ip:addr:" + yearNum + "-" + monthNum,
                            "ip:addr:" + LocalDate.now().minusDays(i).toString());
                }
            }
        }
        log.info(ip+" had visited "+path+" at "+ LocalDateTime.now().toString());
        return chain.filter(exchange);
    }
}
