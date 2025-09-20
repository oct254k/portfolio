package com.flow.test.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 확장자 정보 엔티티
 * PostgreSQL ext 테이블과 매핑
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Extension {
    
    /**
     * 자동 증가 인덱스
     */
    private Long idx;
    
    /**
     * 확장자 타입 (check: 체크박스, label: 라벨형)
     */
    private String type;
    
    /**
     * 확장자명 (예: jpg, pdf, bat 등)
     */
    private String name;
    
    /**
     * 등록자 ID
     */
    private String regId;
    
    /**
     * 등록일시
     */
    private OffsetDateTime regDate;
    
    /**
     * 수정자 ID
     */
    private String modId;
    
    /**
     * 수정일시
     */
    private OffsetDateTime modDate;
    
    /**
     * 확장자 타입 상수
     */
    public static class Type {
        public static final String CHECK = "check";  // 체크박스 타입
        public static final String LABEL = "label";  // 라벨 타입
    }
    
    /**
     * 고정 확장자 목록 (추가/삭제 불가)
     */
    public static final String[] FIXED_EXTENSIONS = {
        "bat", "cmd", "com", "cpl", "exe", "scr", "js"
    };
}
