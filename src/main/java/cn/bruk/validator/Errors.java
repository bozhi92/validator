package cn.bruk.validator;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
/**
 * @author fengbozhi
 * @date 2019-08-07
 */
@Data
public class Errors {
    private boolean error = false;
    
    private Map<String, String> fields;
    
    public void putField(String fieldName, String errorMessage) {
        if (fields == null) {
            fields = new HashMap<>();
        }
        fields.put(fieldName, errorMessage);
        this.error = true;
    }
    
//    public ResultWrap parseResultWrap() {
//        return ResultUtil.error(ResultCodeEnum.PARAMS_ERROR, String.join(",", fields.values()));
//    }
}
