package edu.tongji.backend.util;

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

