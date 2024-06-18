package edu.tongji.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from user where name= #{name};")
    User select1(String name);//自定义函数，通过SQL语句进行查询。本函数仅做示例，可能没有用gi
    User selectById(int id);
    @Select("SELECT contact from user where user_id=#{userId};")
    String getContact(String userId);
    @Update("UPDATE user SET password=#{new_password} WHERE user_id=#{user_id} and password =#{old_password}")
    int changePassword(@Param("userId") int user_id, @Param("new_password") String new_password, @Param("old_password") String old_password);
    @Select("select name from user where user_id= #{id};")
    String getUserName(int id);
    @Select("SELECT MAX(user_id) FROM user;")
    Integer getMaxUserId();
    static Integer UserIdLock = 1;
    @Update("UPDATE user SET name=#{name},contact=#{contact} WHERE user_id=#{adminId};")
    Boolean updateAdmin(String adminId,String name, String contact);
}
