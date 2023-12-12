package edu.tongji.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.tongji.backend.entity.user;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface UserMapper extends BaseMapper<user> {
    @Select("select * from user where name= #{name};")
    user select1(String name);//自定义函数，通过SQL语句进行查询。本函数仅做示例，可能没有用
    user selectbyId(String name);
}
