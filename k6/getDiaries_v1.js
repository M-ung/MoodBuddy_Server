import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        { duration: '2m', target: 500 },  // 3분 동안 500명으로 증가
        { duration: '2m', target: 1000 }, // 3분 동안 1000명으로 증가
        { duration: '2m', target: 1000 }, // 4분 동안 1000명 유지
        { duration: '2m', target: 500 },  // 3분 동안 500명으로 감소
        { duration: '2m', target: 0 },    // 3분 동안 0명으로 감소
    ],
    thresholds: {
        'http_req_duration': ['p(95)<100'], // 95%가 100ms 미만으로 응답
        'http_req_failed': ['rate<0.01'],   // 실패율이 1% 미만
    },
};

export default function () {
    let userId = Math.floor(Math.random() * 1000) + 1;
    let loginPayload = JSON.stringify({ userId: userId.toString() });
    let loginHeaders = { 'Content-Type': 'application/json' };
    let loginRes = http.post('http://localhost:8080/api/v1/member/test/login', loginPayload, {
        headers: loginHeaders,
        timeout: '60s',
    });

    check(loginRes, {
        'login success': (r) => r.status === 200,
    });

    let accessToken = JSON.parse(loginRes.body).accessToken;

    let totalDiaries = 50; 
    let pageSize = 20;  
    let totalPages = Math.ceil(totalDiaries / pageSize);  

    for (let page = 0; page < totalPages; page++) {
        let diaryHeaders = {
            'Authorization': `Bearer ${accessToken}`,
        };

        let diaryRes = http.get(`http://localhost:8080/api/v1/member/diary/findAllPageable?page=${page}&size=${pageSize}`, { headers: diaryHeaders });

        check(diaryRes, {
            '✅ 일기 조회 테스트 성공': (r) => r.status === 200,
        });

        sleep(1);  
    }
}