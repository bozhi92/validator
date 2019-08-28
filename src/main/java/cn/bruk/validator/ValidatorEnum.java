package cn.bruk.validator;
/** 
 * @author fengbozhi
 * @date 2019-08-07
 */
public enum ValidatorEnum {
    NotNull(0, "[%s]不能为空"),
    NotEmpty(0, "[%s]不能为空"),
    NotBlank(0, "[%s]不能为空"),
    Length(1, "[%s]长度必须为%s"),
    MinLength(1, "[%s]长度不能小于%s"),
    MaxLength(1, "[%s]长度不能大于%s"),
    MaxSize(1, "[%s]值不能大于%s"),
    MinSize(1, "[%s]值不能小于%s"),
    DateFormat(1, "[%s]日期格式必须为%s"),
    Regex(1, "[%s]格式错误"),
    ValueIn(2, "[%s]只能为(%s)"); 
    
    private int  value;
    private String message;
    
    public int getValue() {
        return this.value;
    }
    public String getMessage() {
        return this.message;
    }
    private ValidatorEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }
    
}
