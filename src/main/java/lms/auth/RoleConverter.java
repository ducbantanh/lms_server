package lms.auth;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Lưu Role xuống DB dưới dạng số (cột TINYINT). */
@Converter
public class RoleConverter implements AttributeConverter<Role, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Role role) {
        return role == null ? null : role.code();
    }

    @Override
    public Role convertToEntityAttribute(Integer code) {
        return code == null ? null : Role.fromCode(code);
    }
}
