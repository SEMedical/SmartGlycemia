package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.entity.Complication;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.mapper.ComplicationMapper;
import edu.tongji.backend.mapper.UserMapper;
import edu.tongji.backend.service.IComplicationService;
import edu.tongji.backend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComplicationServiceImpl extends ServiceImpl<ComplicationMapper, Complication> implements IComplicationService {
    @Autowired
    ComplicationMapper complicationMapper;
}
