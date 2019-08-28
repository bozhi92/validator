package cn.bruk.validator;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
/**
 * @author fengbozhi
 * @date 2019-08-07
 */
public class ValidatorUtil {
    private static final Logger log = Logger.getLogger(ValidatorUtil.class);
    private static final List<Class<?>> NUMBER_TYPES = new ArrayList<>();
    private static final List<Class<?>> INTEGER_TYPES = new ArrayList<>();
    private static final List<Class<?>> FLOAT_TYPES = new ArrayList<>();
    static {
        INTEGER_TYPES.add(int.class);
        INTEGER_TYPES.add(long.class);
        INTEGER_TYPES.add(Integer.class);
        INTEGER_TYPES.add(Long.class);
        FLOAT_TYPES.add(float.class);
        FLOAT_TYPES.add(double.class);
        FLOAT_TYPES.add(Float.class);
        FLOAT_TYPES.add(Double.class);
        NUMBER_TYPES.addAll(INTEGER_TYPES);
        NUMBER_TYPES.addAll(FLOAT_TYPES);
    }
    public static Errors validator(Object query) {
        Errors errors = new Errors();
        Field[] fields = query.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                Validator[] annotations = field.getAnnotationsByType(Validator.class);
                if (annotations != null && annotations.length > 0) {
                    for (Validator v : annotations) {
                        if (v.type().getValue() == 1 && StringUtils.isBlank(v.value())) {
                            //throw new ValidatorException("validator paramter value cat not be null");
                            log.warn("validator paramter value should not null");
                            continue;
                        }
                        if (v.type().getValue() == 2 && v.collections().length == 0) {
                            //throw new ValidatorException("validator paramter value cat not be null");
                            log.warn("validator paramter value should not null");
                            continue;
                        }
                        Object target = null;
                        try {
                            field.setAccessible(true);
                            target = field.get(query);
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        switch (v.type()) {
                            case NotNull:
                                notNullProcess(target, field, errors);
                                break;
                            case NotEmpty:
                                notEmptyProcess(target, field, errors);
                                break;
                            case NotBlank:
                                notBlankProcess(target, field, errors);
                                break;
                            case Length:
                                if (target != null) {
                                    lengthProcess(v, target, field, errors);
                                }
                                break;
                            case MaxLength:
                                if (target != null) {
                                    maxLengthProcess(v, target, field, errors);
                                }
                                break;
                            case MinLength:
                                if (target != null) {
                                    minLengthProcess(v, target, field, errors);
                                }
                                break;
                            case MaxSize:
                                if (target != null) {
                                    maxSizeProcess(v, target, field, errors);
                                }
                                break;
                            case MinSize:
                                if (target != null) {
                                    minSizeProcess(v, target, field, errors);
                                }
                                break;
                            case DateFormat:
                                if (target != null) {
                                    dateFormatProcess(v, target, field, errors);
                                }
                                break;
                            case ValueIn:
                                if (target != null) {
                                    valueInProcess(v, target, field, errors);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
        return errors;
    }
    
    private static void notNullProcess(Object target, Field field, Errors errors) {
        if (target == null) {
            errors.putField(field.getName(), String.format(ValidatorEnum.NotNull.getMessage(), field.getName()));
        }
    }
    
    private static void notEmptyProcess(Object target, Field field, Errors errors) {
        Class<?> t = field.getType();
        if (target == null) {
            errors.putField(field.getName(), String.format(ValidatorEnum.NotEmpty.getMessage(), field.getName()));
            return;
        }
        if (t.isArray()) {
            Object[] arr = (Object[])target;
            if (arr.length == 0) {
                errors.putField(field.getName(), String.format(ValidatorEnum.NotEmpty.getMessage(), field.getName()));
                return;
            }
        }
        if (t.equals(List.class) || t.equals(Map.class) || t.equals(Set.class)) {
            Collection<?> collection = (Collection<?>)target;
            if (collection.isEmpty()) {
                errors.putField(field.getName(), String.format(ValidatorEnum.NotEmpty.getMessage(), field.getName()));
            }
        }
    }
    
    private static void notBlankProcess(Object target, Field field, Errors errors) {
        Class<?> t = field.getType();
        if (target == null) {
            errors.putField(field.getName(), String.format(ValidatorEnum.NotBlank.getMessage(), field.getName()));
            return;
        }
        if (t.equals(String.class)) {
            String str = String.valueOf(target);
            if (str.trim().isEmpty()) {
                errors.putField(field.getName(), String.format(ValidatorEnum.NotBlank.getMessage(), field.getName()));
            }
        }
    }
    
    private static void lengthProcess(Validator v, Object target, Field field, Errors errors) {
        Class<?> t = field.getType();
        int length = Integer.valueOf(v.value());
        if (NUMBER_TYPES.contains(t)) {
            int p = String.valueOf(target).replace(".", "").length();
            if (p != length) {
                errors.putField(field.getName(), String.format(ValidatorEnum.Length.getMessage(), field.getName(), length));
            } 
            return;
        } else {
            int p = String.valueOf(target).length();
            if (p != length) {
                errors.putField(field.getName(), String.format(ValidatorEnum.Length.getMessage(), field.getName(), length));
            } 
            return;
        }
    }   
    
    private static void maxLengthProcess(Validator v, Object target, Field field, Errors errors) {
        Class<?> t = field.getType();
        int length = Integer.valueOf(v.value());
        String str = String.valueOf(target);
        if(FLOAT_TYPES.contains(t)) {
            String[] arr = str.split("\\.");
            boolean d = true;
            if (arr.length == 2) {
                String a = arr[1];
                String[] b = a.split("");
                for (int i=0;i<a.length();i++) {
                    if (!b[i].equals("0")) { 
                        d = false;
                    } 
                }
            } 
            if (d) {
                str = str.substring(0, str.indexOf("."));
            }
        }
        int p = str.replace(".", "").length();
        if (p > length) {
            errors.putField(field.getName(), String.format(ValidatorEnum.MaxLength.getMessage(), field.getName(), length));
        } 
        return;
        
    } 
    
    private static void minLengthProcess(Validator v, Object target, Field field, Errors errors) {
        Class<?> t = field.getType();
        int length = Integer.valueOf(v.value());
        String str = String.valueOf(target);
        if(FLOAT_TYPES.contains(t)) {
            String[] arr = str.split("\\.");
            boolean d = true;
            if (arr.length == 2) {
                String a = arr[1];
                String[] b = a.split("");
                for (int i=0;i<a.length();i++) {
                    if (!b[i].equals("0")) { 
                        d = false;
                    } 
                }
            } 
            if (d) {
                str = str.substring(0, str.indexOf("."));
            }
        }
        int p = str.replace(".", "").length();
        if (p < length) {
            errors.putField(field.getName(), String.format(ValidatorEnum.MinLength.getMessage(), field.getName(), length));
        } 
        return;
    } 
    
    private static void minSizeProcess(Validator v, Object target, Field field, Errors errors) {
        Class<?> t = field.getType();
        if (INTEGER_TYPES.contains(t)) {
            int size = Integer.valueOf(v.value());   
            long val = (long)target;
            if (val < size) {
                errors.putField(field.getName(), String.format(ValidatorEnum.MinSize.getMessage(), field.getName(), size));
            } 
            return;
        }
        if (FLOAT_TYPES.contains(t)) {
            double size = Double.valueOf(v.value());   
            double val = (double)target;
            if (val < size) {
                errors.putField(field.getName(), String.format(ValidatorEnum.MinSize.getMessage(), field.getName(), size));
            } 
            return;
        }
    } 
    
    private static void maxSizeProcess(Validator v, Object target, Field field, Errors errors) {
        Class<?> t = field.getType();
        if (INTEGER_TYPES.contains(t)) {
            int size = Integer.valueOf(v.value());   
            long val = (long)target;
            if (val > size) {
                errors.putField(field.getName(), String.format(ValidatorEnum.MaxSize.getMessage(), field.getName(), size));
            } 
            return;
        }
        if (FLOAT_TYPES.contains(t)) {
            double size = Double.valueOf(v.value());   
            double val = (double)target;
            if (val > size) {
                errors.putField(field.getName(), String.format(ValidatorEnum.MaxSize.getMessage(), field.getName(), size));
            } 
            return;
        }
    }
    
    private static void dateFormatProcess(Validator v, Object target, Field field, Errors errors) {
        String format = v.value();
        String val = String.valueOf(target);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            sdf.parse(val);
        } catch (ParseException e) {
            errors.putField(field.getName(), String.format(ValidatorEnum.DateFormat.getMessage(), field.getName(), format));
            return;
        }
    }

    private static void valueInProcess(Validator v, Object target, Field field, Errors errors) {
        String[] collections = v.collections();
        String val = String.valueOf(target);
        Class<?> t = field.getType();
        boolean exist = false;
        if (FLOAT_TYPES.contains(t)) {
            for (String item : collections) {
                if (Double.valueOf(val) == Double.valueOf(item)) {
                    exist = true;
                    break;
                }
            }
        } else if (INTEGER_TYPES.contains(t)) {
            for (String item : collections) {
                if (Integer.valueOf(val) == Integer.valueOf(item)) {
                    exist = true;
                    break;
                }
            }
        } else if (t.equals(String.class)){
            for (String item : collections) {
                if (val.equals(item)) {
                    exist = true;
                    break;
                }
            }
        } else {
            return;
        }
        
        if (!exist) {
            errors.putField(field.getName(), String.format(ValidatorEnum.ValueIn.getMessage(), field.getName(), String.join(",", collections)));
            return; 
        }
    }   
}