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
