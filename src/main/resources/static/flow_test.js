// 파일 확장자 차단 설정 화면 js
document.addEventListener('DOMContentLoaded', function() {
    const extensionInput = document.getElementById('extensionInput');
    const addBtn = document.getElementById('addBtn');
    const extensionTags = document.getElementById('extensionTags');
    const extensionCount = document.getElementById('extensionCount');
    const fixedCheckboxes = document.querySelectorAll('.fixed-extensions input[type="checkbox"]');
    
    // API 주소
    const API_BASE_URL = '/api/extensions';
    
    // 페이지 로드시 서버에서 받아온 데이터
    const serverData = window.serverData || {
        checkExtensions: [],
        labelExtensions: [],
        labelExtensionCount: 0
    };
    
    // 기본 차단 확장자들
    const FIXED_EXTENSIONS = ['bat', 'cmd', 'com', 'cpl', 'exe', 'scr', 'js'];
    
    // 커스텀 확장자 200개까지만
    const MAX_EXTENSIONS = 200;
    
    // 초기 화면 세팅
    initializeWithServerData();
    
    // 서버 데이터 반영하기
    function initializeWithServerData() {
        console.log('서버 데이터로 초기화:', serverData);
        
        // 고정 확장자 체크박스 설정
        updateCheckExtensions(serverData.checkExtensions);
        
        // 커스텀 확장자 태그로 보여주기
        updateLabelExtensions(serverData.labelExtensions);
        
        // 개수 표시
        updateExtensionCount(serverData.labelExtensionCount);
    }
    
    // 입력할때마다 유효성 검사
    extensionInput.addEventListener('input', function() {
        // 한글이나 특수문자 입력하면 자동으로 지워버리기
        let value = this.value.replace(/[^a-zA-Z0-9]/g, '');
        if (this.value !== value) {
            this.value = value;
            showMessage('영어와 숫자만 입력 가능합니다.', 'error');
        }
        updateButtonState();
    });
    
    // 키보드 입력 시 처리
    extensionInput.addEventListener('keypress', function(e) {
        // 영어 숫자 아니면 입력 안됨
        const allowedChars = /[a-zA-Z0-9]/;
        if (!allowedChars.test(e.key) && e.key !== 'Enter' && e.key !== 'Backspace' && e.key !== 'Delete' && e.key !== 'Tab') {
            e.preventDefault();
            showMessage('영어와 숫자만 입력 가능합니다.', 'error');
            return;
        }
        
        // 엔터치면 추가버튼 클릭
        if (e.key === 'Enter') {
            e.preventDefault();
            addBtn.click();
        }
    });
    
    // +추가 버튼 눌렀을때
    addBtn.addEventListener('click', function() {
        const extension = extensionInput.value.trim().toLowerCase();
        
        if (extension) {
            // 기본 확장자면 추가 불가
            if (FIXED_EXTENSIONS.includes(extension)) {
                showMessage('고정 확장자는 추가할 수 없습니다.', 'error');
                extensionInput.value = '';
                return;
            }
            
            addExtension(extension);
            extensionInput.value = '';
        }
    });
    
    // 고정 확장자 체크박스 업데이트
    function updateCheckExtensions(extensions) {
        // 일단 다 해제
        fixedCheckboxes.forEach(checkbox => {
            checkbox.checked = false;
        });
        
        // DB에 있는 것들만 체크
        extensions.forEach(ext => {
            const checkbox = document.getElementById(ext.name);
            if (checkbox) {
                checkbox.checked = true;
            }
        });
    }
    
    // 커스텀 확장자 태그 표시
    function updateLabelExtensions(extensions) {
        extensionTags.innerHTML = '';
        
        extensions.forEach(extension => {
            const tag = document.createElement('span');
            tag.className = 'extension-tag';
            tag.innerHTML = `${extension.name}<span class="remove-btn">×</span>`;
            tag.setAttribute('data-extension', extension.name);
            
            // × 버튼 클릭 시에만 삭제 (이벤트 위임 사용)
            tag.addEventListener('click', function(event) {
                // × 버튼을 정확히 클릭했을 때만 삭제
                if (event.target.classList.contains('remove-btn')) {
                    removeExtension(extension.name);
                }
            });
            
            extensionTags.appendChild(tag);
        });
        
        // 추가 버튼 상태 갱신
        updateButtonState();
    }
    
    // 개수 업데이트
    function updateExtensionCount(count) {
        extensionCount.textContent = count;
    }
    
    // 추가 버튼 상태 처리
    function updateButtonState() {
        const extension = extensionInput.value.trim().toLowerCase();
        const addBtn = document.getElementById('addBtn');
        const currentCount = parseInt(document.getElementById('extensionCount').textContent);
        
        if (extension && FIXED_EXTENSIONS.includes(extension)) {
            // 기본 확장자 입력하면 경고
            addBtn.disabled = true;
            addBtn.textContent = '고정 확장자';
            addBtn.className = 'btn btn-warning';
            extensionInput.style.borderColor = '#ffc107';
        } else if (currentCount >= MAX_EXTENSIONS) {
            // 200개 넘으면 더 못넣음
            addBtn.disabled = true;
            addBtn.textContent = '최대 개수 도달';
            addBtn.className = 'btn btn-danger';
            extensionInput.style.borderColor = '#dc3545';
        } else {
            // 평소에는 그냥 이렇게
            addBtn.disabled = false;
            addBtn.textContent = '+추가';
            addBtn.className = 'btn btn-secondary';
            extensionInput.style.borderColor = '';
        }
    }
    
    // 확장자 추가 API 호출
    async function addExtension(extension) {
        try {
            // 200개 제한 체크
            const currentCount = parseInt(document.getElementById('extensionCount').textContent);
            if (currentCount >= MAX_EXTENSIONS) {
                showMessage(`최대 ${MAX_EXTENSIONS}개까지만 추가할 수 있습니다.`, 'error');
                return;
            }
            
            const response = await fetch(API_BASE_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    name: extension,
                    type: 'label', // 커스텀은 전부 label 타입
                    regId: 'user'
                })
            });
            
            const data = await response.json();
            
            if (data.success) {
                // 성공하면 새로고침해서 다시 불러오기
                window.location.reload();
                showMessage('확장자가 성공적으로 추가되었습니다.', 'success');
            } else {
                showMessage(data.message, 'error');
            }
        } catch (error) {
            console.error('확장자 추가 중 오류:', error);
            showMessage('확장자 추가 중 오류가 발생했습니다.', 'error');
        }
    }
    
    // 확장자 삭제 API 호출
    async function removeExtension(extension) {
        // 삭제 확인 다이얼로그
        if (!confirm(`'${extension}' 확장자를 삭제하시겠습니까?`)) {
            return; // 사용자가 취소하면 아무것도 하지 않음
        }
        
        try {
            const response = await fetch(`${API_BASE_URL}/${extension}`, {
                method: 'DELETE'
            });
            
            const data = await response.json();
            
            if (data.success) {
                // 성공하면 새로고침해서 다시 불러오기
                window.location.reload();
                showMessage('확장자가 성공적으로 삭제되었습니다.', 'success');
            } else {
                showMessage(data.message, 'error');
            }
        } catch (error) {
            console.error('확장자 삭제 중 오류:', error);
            showMessage('확장자 삭제 중 오류가 발생했습니다.', 'error');
        }
    }
    
    // 알림 메시지 보여주기
    function showMessage(message, type) {
        // 이전 메시지 있으면 지우고
        const existingMessage = document.querySelector('.alert-message');
        if (existingMessage) {
            existingMessage.remove();
        }
        
        // 알림창 만들기
        const messageDiv = document.createElement('div');
        messageDiv.className = `alert alert-${type === 'success' ? 'success' : 'danger'} alert-message`;
        messageDiv.style.position = 'fixed';
        messageDiv.style.top = '20px';
        messageDiv.style.right = '20px';
        messageDiv.style.zIndex = '9999';
        messageDiv.textContent = message;
        
        document.body.appendChild(messageDiv);
        
        // 3초 뒤에 자동으로 사라짐
        setTimeout(() => {
            if (messageDiv.parentNode) {
                messageDiv.parentNode.removeChild(messageDiv);
            }
        }, 3000);
    }
    
    // 고정 확장자 체크박스 클릭 처리
    fixedCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', async function() {
            const extension = this.value;
            const isChecked = this.checked;
            
            try {
                if (isChecked) {
                    // 체크하면 DB에 추가
                    const response = await fetch(API_BASE_URL, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify({
                            name: extension,
                            type: 'check',
                            regId: 'user'
                        })
                    });
                    
                    const data = await response.json();
                    if (!data.success) {
                        // 실패하면 원래대로
                        this.checked = false;
                        showMessage(data.message, 'error');
                    }
                } else {
                    // 체크 해제하면 DB에서 삭제
                    const response = await fetch(`${API_BASE_URL}/${extension}`, {
                        method: 'DELETE'
                    });
                    
                    const data = await response.json();
                    if (!data.success) {
                        // 실패하면 다시 체크해둘
                        this.checked = true;
                        showMessage(data.message, 'error');
                    }
                }
            } catch (error) {
                console.error('확장자 상태 변경 중 오류:', error);
                // 에러나면 원상복구
                this.checked = !isChecked;
                showMessage('확장자 상태 변경 중 오류가 발생했습니다.', 'error');
            }
        });
    });
});