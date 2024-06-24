## ADB(Android Debug Bridge)b 환경 설정
1. Android Sdk Location 확인
![image](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/414395d7-cdcc-470b-879f-d81d5b969644)

2. 내PC - 속성 - 고급시스템설정 - 환경변수 - Path에 붙여넣고 경로\platform-tools
![image](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/0f75f84b-1bca-4e18-bf8d-e5c21868c822)

3. cmd에서 adb명령어로 설정 확인
![image](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/8b0ff8e2-41ff-4a0c-a8da-b27b0509102a)

## SHA-1 지문키 발급
```Windows
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
```
![image](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/e7609680-0fb4-494a-8480-eb29c239f05e)
```
다음 명령어를 그대로 치면 SHA-1 지문 인증서 키를 발급 받을 수 있다.
```
## Google Maps API 키 제한
![image](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/7bbde9de-50a9-4abe-a8be-6f5056fb8db4)
```
Android 앱의 사용량을 제한(패키지 이름, SHA-1 서명 인증서 지문 추가)
```

## MapView와 FloatingButton 추가
![image](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/70eedfbb-b894-4694-9747-c04507a4b8bb)

## MapView에 OnCreate 함수 호출
![2024-06-24 10;39;10](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/ab2ad852-84b4-46fd-8b16-d111b5c4d1bd)
```
정확한 위치 권한을 얻고 (coarse(네트워크), fine(gps위성)) manifest파일에 api키를 등록한 후
액티비티에서 바로 지도를 crate한 모습이다.
```
