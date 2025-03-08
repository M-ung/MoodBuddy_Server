# 📔 MoodBuddy - Server

## 🖥️ 프로젝트 소개
사용자가 쿼디와 함께 하루의 기록과 고민을 편리하게 작성할 수 있는 감정 일기 웹 서비스입니다.

<div style="display: flex; flex-wrap: wrap; gap: 10px;">
  <img src="https://github.com/user-attachments/assets/61b83563-2496-4d61-aa91-29e4392e656f" width="30%">
  <img src="https://github.com/user-attachments/assets/5c030d7a-882c-4ad2-af45-4086d8b97561" width="30%">
  <img src="https://github.com/user-attachments/assets/dc002fed-dc2c-4511-816c-e321090d119d" width="30%">
  <img src="https://github.com/user-attachments/assets/26d15186-8bcf-4c3a-91a8-b3cd98b69e89" width="30%">
</div>

<br>

## 📺 무드버디 실제 화면
<div style="display: flex; flex-wrap: wrap; gap: 10px;">
  <img src="https://github.com/user-attachments/assets/dc521e90-b4fd-4cd2-94e2-1e2d2a93dbbb" width="30%">
  <img src="https://github.com/user-attachments/assets/8db0dbf0-0c61-420b-9bb1-497f80b802dc" width="30%">
  <img src="https://github.com/user-attachments/assets/fe4a279c-d872-48eb-ab29-2de2627d7631" width="30%">
  <img src="https://github.com/user-attachments/assets/1d4a8bdc-5dc6-4015-861a-9f135cf07273" width="30%">
  <img src="https://github.com/user-attachments/assets/b91aeb51-91fe-4059-a2ca-01e257a7f166" width="30%">
  <img src="https://github.com/user-attachments/assets/3ec5da46-63de-4b67-99a3-aea10dd2477f" width="30%">
  <img src="https://github.com/user-attachments/assets/7e1354f8-dbb5-47f8-925a-f3e44304e068" width="30%">
  <img src="https://github.com/user-attachments/assets/73ad1b66-6ce5-4dc1-b5fd-f6b69f26f562" width="30%">
  <img src="https://github.com/user-attachments/assets/9e3b67a0-2eee-443f-ad79-c42af3acaad7" width="30%">
  <img src="https://github.com/user-attachments/assets/3d6159c0-b8f8-4f61-a8cf-dea0c27aa423" width="30%">
  <img src="https://github.com/user-attachments/assets/5ecc9021-4309-44a1-bf54-f585b7707b3d" width="30%">
</div>

<br>

## 🎥 시연 영상
![GIFMaker_me](https://github.com/user-attachments/assets/2a61f871-a1ca-402a-98a2-3643112e5b53)

<br>

## 🟢 ERD
![스크린샷 2025-01-10 오전 12 28 03](https://github.com/user-attachments/assets/7e43f023-2833-4a38-bf90-5dc60daec830)

<br>

## 🛠️ 아키텍처 구조
<img src="https://github.com/user-attachments/assets/13de24d9-5fd4-43c7-a5da-e324f6b4d867" alt="아키텍처 구조" width="50%">

<br>

## 🔥 TroubleShooting
- 정목(M-ung)
  - 📍 Service 계층이 무겁다면, Facade 패턴 어때?
    -  https://velog.io/@_mung/TroubleShooting-MoodBuddy-Service-%EA%B3%84%EC%B8%B5%EC%9D%B4-%EB%AC%B4%EA%B2%81%EB%8B%A4%EB%A9%B4-Facade-%ED%8C%A8%ED%84%B4-%EC%96%B4%EB%95%8C
  - 📍 사용자 피드백 🔊 : 일기 저장이 너무 느려요..😭😭
    -  https://velog.io/@_mung/TroubleShooting-MoodBuddy-%EC%82%AC%EC%9A%A9%EC%9E%90-%ED%94%BC%EB%93%9C%EB%B0%B1-%EC%9D%BC%EA%B8%B0-%EC%A0%80%EC%9E%A5%EC%9D%B4-%EB%84%88%EB%AC%B4-%EB%8A%90%EB%A0%A4%EC%9A%94
    
<br>
<br>

<details>
  <summary> 💊 1차 개발 (⬇️ 눌러주세요‼️) </summary>
  
  ## 🕰️ 개발 기간
  * 24.03.01 - 24.07.27
  
  <br>
  
  ## ⚙️ 개발 환경
  - `Java 17`
  - **IDE** : IntelliJ IDEA
  - **Framework** : Springboot(3.2.6)
  - **Database** : MySQL
  - **ORM** : Hibernate (Spring Data JPA 사용)
  
  <br>
  
  ## 🧑‍🤝‍🧑 멤버 구성
  <p>
      <a href="https://github.com/M-ung">
        <img src="https://avatars.githubusercontent.com/u/126846468?v=4" width="100">
      </a>
      <a href="https://github.com/dylee00">
        <img src="https://avatars.githubusercontent.com/u/135154209?v=4" width="100">
      </a>
      <a href="https://github.com/zzammin">
        <img src="https://avatars.githubusercontent.com/u/105933726?v=4" width="100"> 
      </a>
  </p>
  
  <br>
  
  ## 📝 규칙
  
  - **커밋 컨벤션**
      - Feat: 새로운 기능 추가
      - Fix: 버그 수정
      - Docs: 문서 수정
      - Style: 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우
      - Refactor: 코드 리팩토링
      - Test: 테스트 코드, 리팩토링 테스트 코드 추가
      - Chore: 빌드 업무 수정, 패키지 매니저 수정
  
  - **Branch 규칙**
      - 각자의 깃 닉네임을 딴 branch 명을 사용한다.
      - 예시
          - git checkout -b mung
  
  - **Commit message 규칙**
      - 종류: 메시지
      - 예시
          - feat: 커밋 내용 - #브랜치명
          - feat: 로그인 구현 - #mung
  
  - **DTO 규칙**
      - 엔티티명 + Res/Req + 역할 + DTO
      - 예시
          - UserResSaveDTO
          - PostReqSaveDTO

</details>


<details>
  <summary> 💊 2차 개발 (⬇️ 눌러주세요‼️) </summary>
  
  ## 🕰️ 개발 기간
  * 24.08.01 - 진행 중
  
  <br>
  
  ## ⚙️ 개발 환경
  - `Java 21`
  - **IDE** : IntelliJ IDEA
  - **Framework** : Springboot(3.3.7)
  - **Database** : MySQL
  - **ORM** : Hibernate (Spring Data JPA 사용)
  
  <br>
  
  ## 🧑‍🤝‍🧑 멤버 구성
  <p>
      <a href="https://github.com/M-ung">
        <img src="https://avatars.githubusercontent.com/u/126846468?v=4" width="100">
      </a>
      <a href="https://github.com/zzammin">
        <img src="https://avatars.githubusercontent.com/u/105933726?v=4" width="100"> 
      </a>
  </p>
  
  <br>
  
  ## 📝 규칙
  
  - **커밋 컨벤션**
      - Feat: 새로운 기능 추가
      - Fix: 버그 수정
      - Docs: 문서 수정
      - Style: 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우
      - Refactor: 코드 리팩토링
      - Test: 테스트 코드, 리팩토링 테스트 코드 추가
      - Chore: 빌드 업무 수정, 패키지 매니저 수정
  
  - **Branch 규칙**
      - 각자의 깃 타입과 이슈번호를 딴 branch 명을 사용한다.
      - 예시
          - git checkout -b 타입/#이슈번호
          - git checkout -b feature/#5
  
  - **Commit message 규칙**
      - "타입(앞글자를 대문자로): 커밋 메세지 - #이슈번호" 형식으로 작성한다.
      - 예시
          - Feat: 커밋 내용 - #이슈번호
          - Feat: 로그인 구현 - #5
  
  - **DTO 규칙**
      - 엔티티명 + Res/Req + 역할 + DTO
      - 예시
          - UserResSaveDTO
          - PostReqSaveDTO
  
</details>
