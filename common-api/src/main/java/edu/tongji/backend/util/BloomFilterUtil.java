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
    public static final Long latest_glycemia_bf_total= 1000000L;
    public static final Long glycemia_bf_total= 1000000L;
    public static final Long daily_glycemia_bf_total= 1000000L;
    public static final Long history_glycemia_bf_total= 1000000L;
    public static final Long exercise_running_bf_total= 1000000L;
    public static final BloomFilter<CharSequence> exercise_running_bf =
            BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8),exercise_running_bf_total);
    public static final BloomFilter<CharSequence> daily_glycemia_bf =
            BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), daily_glycemia_bf_total);
    public static final BloomFilter<CharSequence> glycemia_bf =
            BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), glycemia_bf_total);
    public static final BloomFilter<CharSequence> latest_glycemia_bf =
            BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), latest_glycemia_bf_total);
    public static final BloomFilter<CharSequence> history_glycemia_bf =
            BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), history_glycemia_bf_total);

}

