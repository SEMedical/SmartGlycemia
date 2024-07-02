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





import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import edu.tongji.backend.entity.Glycemia;

import javax.annotation.PostConstruct;
import java.util.List;

import static edu.tongji.backend.util.RedisConstants.CACHE_LATEST_GLYCEMIA_KEY;

public class BloomFilterUtil {
    public static final Long exercise_running_bf_total= 1000000L;
    public static final BloomFilter<CharSequence> exercise_running_bf =
            BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8),exercise_running_bf_total);

}

