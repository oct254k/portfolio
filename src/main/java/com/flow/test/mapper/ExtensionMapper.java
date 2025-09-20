package com.flow.test.mapper;

import com.flow.test.domain.Extension;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 확장자 정보 MyBatis 매퍼 인터페이스
 */
@Mapper
public interface ExtensionMapper {
    
    /**
     * 모든 확장자 조회
     * @return 확장자 목록
     */
    List<Extension> selectAllExtensions();
    
    /**
     * 타입별 확장자 조회
     * @param type 확장자 타입 (check, label)
     * @return 해당 타입의 확장자 목록
     */
    List<Extension> selectExtensionsByType(@Param("type") String type);
    
    /**
     * 확장자명으로 조회
     * @param name 확장자명
     * @return 확장자 정보
     */
    Extension selectExtensionByName(@Param("name") String name);
    
    /**
     * 확장자 등록
     * @param extension 확장자 정보
     * @return 등록된 행 수
     */
    int insertExtension(Extension extension);
    
    /**
     * 확장자 수정
     * @param extension 확장자 정보
     * @return 수정된 행 수
     */
    int updateExtension(Extension extension);
    
    /**
     * 확장자 삭제
     * @param idx 확장자 인덱스
     * @return 삭제된 행 수
     */
    int deleteExtension(@Param("idx") Long idx);
    
    /**
     * 확장자명으로 삭제
     * @param name 확장자명
     * @return 삭제된 행 수
     */
    int deleteExtensionByName(@Param("name") String name);
    
    /**
     * 확장자 존재 여부 확인
     * @param name 확장자명
     * @return 존재 여부 (1: 존재, 0: 미존재)
     */
    int existsExtensionByName(@Param("name") String name);
}
