package com.flow.test.controller;

import com.flow.test.service.ExtensionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 메인 페이지 컨트롤러
 * MVC 패턴에 따른 View 렌더링 담당
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final ExtensionService extensionService;

    /**
     * 루트 경로 접속 시 메인 페이지 렌더링
     * @param model 뷰에 전달할 데이터 모델
     * @return main 템플릿 페이지
     */
    @GetMapping("/")
    public String main(Model model) {
        log.info("메인 페이지 접속");
        return "main";
    }

    /**
     * 확장자 관리 페이지 접속
     * @param model 뷰에 전달할 데이터 모델 (확장자 목록 포함)
     * @return flow_test 템플릿 페이지
     */
    @GetMapping("/flowtest")
    public String flowTest(Model model) {
        log.info("확장자 관리 페이지 접속");
        
        try {
            // 체크박스 타입 확장자 목록 조회
            var checkExtensions = extensionService.getCheckExtensions();
            // 라벨 타입 확장자 목록 조회
            var labelExtensions = extensionService.getLabelExtensions();
            
            // Model에 데이터 추가
            model.addAttribute("checkExtensions", checkExtensions);
            model.addAttribute("labelExtensions", labelExtensions);
            model.addAttribute("labelExtensionCount", labelExtensions.size());
            
            log.info("확장자 데이터 로드 완료 - 체크박스: {}개, 라벨: {}개", 
                    checkExtensions.size(), labelExtensions.size());
            
        } catch (Exception e) {
            log.error("확장자 데이터 로드 중 오류 발생", e);
            // 오류 발생 시에도 빈 목록으로 페이지 표시
            model.addAttribute("checkExtensions", java.util.Collections.emptyList());
            model.addAttribute("labelExtensions", java.util.Collections.emptyList());
            model.addAttribute("labelExtensionCount", 0);
        }
        
        return "flow_test";
    }
}
