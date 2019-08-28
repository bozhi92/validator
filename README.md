# validator
按自己习惯写的一个基于注解的校验工具

使用示例
```java
//在需要校验的字段上使用注解
public class UserLoginParam {
    @Validator(type=ValidatorEnum.NotBlank)
    private String username;
    @Validator(type=ValidatorEnum.NotBlank)
    @Validator(type=ValidatorEnum.Length, value="32")
    private String password;
}
```

```java
//使用ValidatorUtil.validator(object)
public String login(@RequestBody UserLoginParam param) {
    Errors errors = ValidatorUtil.validator(param);
    if (errors.isError()) {
        return errors.getFields().toString();
    }
    String token = userService.login(param.getUsername(), param.getPassword());
    log.info("User login successful:{}", param.getUsername());
    if (token != null) {
        return token;
    }
    return null;
}
```
