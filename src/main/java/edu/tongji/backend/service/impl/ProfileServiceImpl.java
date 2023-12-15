package edu.tongji.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.tongji.backend.entity.Profile;
import edu.tongji.backend.mapper.ProfileMapper;
import edu.tongji.backend.service.IProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl extends ServiceImpl<ProfileMapper, Profile> implements IProfileService {
    @Autowired
    ProfileMapper profileMapper;
}
