package com.jesper.seckill.mapper;

import com.jesper.seckill.bean.User;
import org.apache.ibatis.annotations.*;

/**
 * Created by jiangyunxiong on 2018/5/21.
 */
@Mapper
public interface UserMapper {

    @Select("select * from sk_user where id = #{id}")
    public User getById(@Param("id")long id);

    @Update("update sk_user set password = #{password} where id = #{id}")
    public void updatePassword(User toBeUpdate);

    @Update("update sk_user set nickname = #{nickname}, head = #{head} where id = #{id}")
    public void updateInfo(User user);

    @Update("update sk_user set last_login_date = #{lastLoginDate}, login_count = #{loginCount} where id = #{id}")
    public void updateLogin(User toBeUpdate);

    @Insert("insert into sk_user(login_count, nickname, register_date, salt, password, id)values("+
            "#{loginCount}, #{nickname}, #{registerDate}, #{salt}, #{password}, #{id} )")
    public boolean register(User regUser);
}
