// main.js - 메인 페이지 JavaScript 파일

console.log('main.js가 로드되었습니다.');

// 페이지 로드 완료 시 실행
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM이 완전히 로드되었습니다.');
    
    // 현재 시간 표시
    const now = new Date();
    const timeString = now.toLocaleString('ko-KR');
    
    const contentDiv = document.getElementById('content');
    if (contentDiv) {
        contentDiv.innerHTML += `<p>현재 시간: ${timeString}</p>`;
    }
});

// 메시지 표시 함수
function showMessage() {
    alert('안녕하세요! Spring Boot 애플리케이션입니다.');
}

// Flow Test 페이지로 이동하는 함수
function navigateToFlowTest() {
    window.location.href = '/flowtest';
}

// 유틸리티 함수들
const utils = {
    // 현재 시간 가져오기
    getCurrentTime: function() {
        return new Date().toLocaleString('ko-KR');
    },
    
    // 간단한 계산 함수
    calculate: function(a, b, operation) {
        switch(operation) {
            case 'add':
                return a + b;
            case 'subtract':
                return a - b;
            case 'multiply':
                return a * b;
            case 'divide':
                return b !== 0 ? a / b : '0으로 나눌 수 없습니다';
            default:
                return '지원하지 않는 연산입니다';
        }
    }
};

// 전역에서 사용할 수 있도록 window 객체에 추가
window.utils = utils;
