package it.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;         // 账号，主键
    private Integer password;   // 密码
    private String username;   // 昵称
}
