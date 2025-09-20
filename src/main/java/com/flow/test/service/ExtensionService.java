package com.flow.test.service;

import com.flow.test.domain.Extension;
import com.flow.test.mapper.ExtensionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

// 확장자 관련 비즈니스 로직 처리
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExtensionService {
    
    private final ExtensionMapper extensionMapper;
    
    // 전체 확장자 목록 가져오기
    public List<Extension> getAllExtensions() {
        log.info("모든 확장자 조회 요청");
        return extensionMapper.selectAllExtensions();
    }
    
    // 고정 확장자들만 가져오기
    public List<Extension> getCheckExtensions() {
        log.info("체크박스 타입 확장자 조회 요청");
        return extensionMapper.selectExtensionsByType(Extension.Type.CHECK);
    }
    
    // 커스텀 확장자들만 가져오기
    public List<Extension> getLabelExtensions() {
        log.info("라벨 타입 확장자 조회 요청");
        return extensionMapper.selectExtensionsByType(Extension.Type.LABEL);
    }
    
    // 새 확장자 추가
    @Transactional
    public Extension addExtension(String name, String type, String regId) {
        log.info("확장자 등록 요청 - name: {}, type: {}, regId: {}", name, type, regId);
        
        // 이미 있는 확장자인지 체크
        if (extensionMapper.existsExtensionByName(name) > 0) {
            throw new IllegalArgumentException("이미 존재하는 확장자입니다: " + name);
        }
        
        // 커스텀 확장자 200개 제한
        if (Extension.Type.LABEL.equals(type)) {
            List<Extension> labelExtensions = extensionMapper.selectExtensionsByType(Extension.Type.LABEL);
            if (labelExtensions.size() >= 200) {
                throw new IllegalArgumentException("최대 200개까지만 추가할 수 있습니다.");
            }
        }
        
        // 객체 만들기
        Extension extension = Extension.builder()
                .name(name.toLowerCase()) // 소문자로 통일
                .type(type)
                .regId(regId)
                .regDate(OffsetDateTime.now())
                .build();
        
        // DB 저장
        extensionMapper.insertExtension(extension);
        
        log.info("확장자 등록 완료 - idx: {}, name: {}", extension.getIdx(), extension.getName());
        return extension;
    }
    
    // 확장자 삭제
    @Transactional
    public int removeExtension(String name) {
        log.info("확장자 삭제 요청 - name: {}", name);
        
        int deletedCount = extensionMapper.deleteExtensionByName(name);
        
        if (deletedCount > 0) {
            log.info("확장자 삭제 완료 - name: {}", name);
        } else {
            log.warn("삭제할 확장자를 찾을 수 없습니다 - name: {}", name);
        }
        
        return deletedCount;
    }
    
    // 확장자 존재 여부 체크
    public boolean existsExtension(String name) {
        return extensionMapper.existsExtensionByName(name) > 0;
    }
    
    // 입력값 유효성 검사 (영문, 숫자만 허용)
    public boolean isValidExtension(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        // 1~20자 사이, 영어+숫자만, 공백 안됨
        return name.matches("^[a-zA-Z0-9]{1,20}$");
    }
}
