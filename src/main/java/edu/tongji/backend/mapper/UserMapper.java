package edu.tongji.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Update("UPDATE user SET password=#{new_password} WHERE user_id=#{user_id} and password =#{old_password}")
    int changePassword(@Param("userId") int user_id, @Param("new_password") String new_password, @Param("old_password") String old_password);
}
