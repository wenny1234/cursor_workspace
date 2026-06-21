package com.shop.backend.repository.csv;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeConverter extends AbstractBeanField<LocalDateTime, String> {
    
    private static final DateTimeFormatter[] FORMATTERS = {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    };
    
    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        String trimmedValue = value.trim();
        
        // 尝试所有格式
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDateTime.parse(trimmedValue, formatter);
            } catch (DateTimeParseException e) {
                // 继续尝试下一个格式
            }
        }
        
        throw new CsvDataTypeMismatchException(value, LocalDateTime.class, 
                "无法解析日期时间字符串: " + value);
    }
    
    @Override
    protected String convertToWrite(Object value) throws CsvDataTypeMismatchException {
        if (value == null) {
            return "";
        }
        
        if (!(value instanceof LocalDateTime)) {
            throw new CsvDataTypeMismatchException("值不是LocalDateTime类型: " + value.getClass());
        }
        
        LocalDateTime dateTime = (LocalDateTime) value;
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}