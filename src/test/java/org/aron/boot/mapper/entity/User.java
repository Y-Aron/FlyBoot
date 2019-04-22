package org.aron.boot.mapper.entity;

import lombok.Data;

/**
 * @author: Y-Aron
 * @create: 2019-01-13 20:39
 **/
@Data
public class User {
    private long id;
    private String username;
    private String password;
    private String nickname;
}
