package org.aron.boot.mapper.master.provider;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aron.boot.mapper.entity.User;

/**
 * @author: Y-Aron
 * @create: 2019-01-20 14:21
 **/
@Slf4j
public class UserMapperProvider {

    public String testSelect(User user) {
        log.info("----------test select----------");
        StringBuilder sb = new StringBuilder();
        sb.append("select * from tb_user where ");
        if (user.getId() > 0) {
            sb.append("id=#{user.id} and ");
        }
        if (StringUtils.isNotBlank(user.getUsername())) {
            sb.append("username=#{user.username}");
        }
        if (StringUtils.isNotBlank(user.getPassword())) {
            sb.append("password=#{user.password}");
        }
        return sb.toString();
    }

//    public String testInsert(@Param("u") User user) {
//        return "insert into tb_user(id, username, password) values(#{u.id}, #{u.username}, #{u.password})";
//    }

    public String testUpdate(long id, User user) {
        return "update tb_user set username=#{user.username} where id=#{id}";
    }

    public String testDelete(long id) {
        return "delete from tb_user where id=#{id}";
    }
}
