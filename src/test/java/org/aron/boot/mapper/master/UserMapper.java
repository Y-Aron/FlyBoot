//package org.aron.boot.mapper.master;
//
//
//import org.aron.boot.mapper.entity.User;
//import org.aron.boot.mapper.master.provider.UserMapperProvider;
//import org.fly.mybatis.annotation.Param;
//import org.fly.mybatis.annotation.provider.DeleteProvider;
//import org.fly.mybatis.annotation.provider.InsertProvider;
//import org.fly.mybatis.annotation.provider.SelectProvider;
//import org.fly.mybatis.annotation.provider.UpdateProvider;
//import org.fly.mybatis.annotation.sql.Delete;
//import org.fly.mybatis.annotation.sql.Insert;
//import org.fly.mybatis.annotation.sql.Select;
//import org.fly.mybatis.annotation.sql.Update;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * @author: Y-Aron
// * @create: 2019-01-12 18:16
// **/
////@Mapper(dataSource = "slave")
//public interface UserMapper {
//
//    @SelectProvider(type = UserMapperProvider.class, method = "testSelect")
//    List<User> selectProvider(User user);
//
////    @Mapper(dataSource = "slave")
//    @Select("select * from tb_user")
//    List<User> select();
//
//    @Select("select * from tb_user where username=#{u.username}")
//    List<User> select(@Param("u") User user);
//
////    @Mapper(dataSource = "master")
//    @Select("select * from tb_user")
//    User selectOne();
//
//    @Select("select * from tb_user where username=#{user.username}")
//    User selectOne(User user);
//
//    @Select("select username from tb_user where username=#{user.username}")
//    Map<String, Object> selectMap(User user);
//
//    @Select("select username, password from tb_user where username=#{user.username}")
//    List<Map<String, Object>> selectListMap(User user);
//
//    @Insert("insert into tb_user(id, username, password) values(#{u.id}, #{u.username}, #{u.password})")
//    int insertOne(@Param("u") User user);
//
//    @Insert(table = "tb_user", fields = {"id", "username", "password"})
//    int insertOnePlus(@Param("u") User user, long id);
//
//    @InsertProvider(type = UserMapperProvider.class, method = "testInsert")
//    int insertProvider(User user);
//
//    @UpdateProvider(type = UserMapperProvider.class, method = "testUpdate")
//    int updateProvider(long id, User user);
//
//    @DeleteProvider(type = UserMapperProvider.class, method = "testDelete")
//    int deleteProvider(long id);
//
//    @Update("update tb_user set username=#{user.username} where id=#{id}")
//    int updateOne(long id, User user);
//
//    // UPDATE tb_user set password=password where id=123123123123123 or username=test
//    @Update(table = "tb_user", and = {"id"}, or = {"id"}, fields = {"username", "password"})
//    int updateOnePlus(long[] id, User user);
//
//    @Delete("delete from tb_user where id=#{id}")
//    int deleteOne(long id);
//
//    @Delete(table = "tb_user", and = {"id"}, or = {"user.username"})
//    int deleteOnePlus(long[] id, User user);
//}
