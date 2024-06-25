```
목표
Legacy Code를 최신 코틀린 버전의 코드로 수정한다.
위치 관련 라이브러리의 사용법을 익히고 api를 사용한 마커, 위치 클러스터링의 작업을 수행한다.
```
![image](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/aceb15b3-77ac-4a6a-b5bd-ada2851ec06e)
```
Android Studio 코알라 버전으로 개발하였습니다.
```
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
## 에뮬레이터의 현재 위치 설정
![image](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/a5dfd4cd-3326-46cb-bddd-9842a86554f2)
```
현재 GPS 상의 위치를 서울 시청으로 Set Location 위치 고정하고 개발을 진행하였습니다.
```
## 위치 권한 거부 시
![2024-06-24 11;04;36](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/45cea2a4-bdec-413b-91ca-84b1dbe2bb7c)
```
서울 시청의 위치를 줌레벨 17f 로 보여준다.
```
## 위치 권한 수락 시 
![2024-06-24 14;05;23](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/09add43e-c809-48df-bad4-c7047c1623b9)
```
위치 권한 수락 시 현재 위치를 서울 시청으로 설정하였습니다.
```
## OpenAPI Key 발급
![image](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/b9c14154-dca7-4d88-84e6-471bba7e781b)

## Thread 와 AsyncTask
```
메인 스레드(UI 스레드)로 돌아가야하는 UI 작업 실행시 UI 쓰레드로 전환하는 작업이 필요한 데 이때 실수를 방지하기 위한 수단으로
AsyncTask를 사용하였습니다.

백그라운드에서 API 서버에서 데이터를 받아오는 작업을 실행하기 위한 doInBackground와 같은 메서드를 오버라이드 하여 구현한다.
```
## 서버에서 넘어오는 JSON 형식의 공공데이터
![image](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/7f59168a-de55-4487-a9dd-665606dc2c9d)

## 공공 데이터 API 연동
![2024-06-25 18;19;17](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/47e68631-6bc1-4572-b4b0-31ffc106ad90)

## API 연동 코드 리뷰

#### 백그라운드 작업 취소를 위한 변수 선언 
```kotlin
// 앱이 비활성화될때 백그라운드 작업도 취소하기 위한 변수 선언
var task: ToiletReadTask? = null

    // 앱이 활성화될때 서울시 화장실 데이터를 읽어옴
    override fun onStart() {
        super.onStart()
        task?.cancel(true)
        task = ToiletReadTask()
        task?.execute()
    }

    // 앱이 비활성화 될때 백그라운드 작업 취소
    override fun onStop() {
        super.onStop()
        task?.cancel(true)
        task = null
    }
```
```
액티비티의 라이프 사이클에서 onStop() 함수에서 백그라운드 태스크를 제어하기 위한 설계를 진행하였습니다.
백그라운드 태스크를 실행하고 취소하는 코드에서 사용하기 위해서 task 변수를 선언하여 사용하였습니다.
```

#### 서울시 화장실 공공 데이터를 전부 저장할 변수
```kotlin
// 서울시 화장실 정보 집합을 저장할 Array 변수, 검색을 위해 저장
    var toilets = JSONArray()
```
```
서버에서 넘어오는 json 데이터를 JSON 객체 Array로 선언하여 저장하였습니다. 향후에 검색 기능을 개발하기 위해서 필요합니다.
```

#### 비트맵 리사이징
```kotlin
    // 화장실 이미지로 사용할 Bitmap
    val bitmap by lazy {
        val drawable = resources.getDrawable(R.drawable.restroom_sign) as BitmapDrawable
        Bitmap.createScaledBitmap(drawable.bitmap, 64, 64, false)
    }
```
```
구글 맵에 이미지 사이즈를 맞추기 위해 비트맵을 초기화하는 코드에 리사이징하는 코드를 추가하였습니다.
```

####  JSONArray 객체의 병합을 위한 확장 함수
```kotlin
 // JSONArray 를 병합하기 위해 확장함수 사용
    fun JSONArray.merge(anotherArray: JSONArray) {
        for (i in 0 until anotherArray.length()) {
            this.put(anotherArray.get(i))
        }
    }
```
```
JSONArray는 json 데이터의 집합을 구조화하여 접근할 수 있는 클래스이고 안드로이드 SDK에 포함되어 있기에 사용하였습니다.
JSONArray 클래스에 병합하기 위한 기능을 추가(확장)하기 위해서 확장함수를 사용하였습니다.
```

#### Open API에서 데이터를 읽어오는 readData 함수
```kotlin
  // 화장실 정보를 읽어와 JSONObject 로 반환하는 함수
    fun readData(startIndex: Int, lastIndex: Int): JSONObject {
        val url =
            URL("http://openAPI.seoul.go.kr:8088" + "/${API_KEY}/json/SearchPublicToiletPOIService/${startIndex}/${lastIndex}")
        val connection = url.openConnection()

        val data = connection.getInputStream().readBytes().toString(charset("UTF-8"))
        return JSONObject(data)
    }
```
```
서버에서 데이터를 받아오는 것은 URL의 startIndex와 lastIndex를 변경해가면서 수행합니다. 매번 이 값들을 수정하는 것은 불필요하고
반복적인 작업이기 때문에 이를 함수로 분리하여 재사용성을 높였습니다.
```

#### AsyncTask 
```kotlin
// 화장실 데이터를 읽어오는 AsyncTask
    inner class ToiletReadTask : AsyncTask<Void, JSONArray, String>() {

        // 데이터를 읽기 전에 기존 데이터 초기화
        override fun onPreExecute() {
            // 구글맵 마커 초기화
            googleMap?.clear()
            // 화장실 정보 초기화
            toilets = JSONArray()
        }

        override fun doInBackground(vararg params: Void?): String {
            // 서울시 데이터는 최대 1000 개씩 가져올 수 있기 때문에
            // step 만큼 startIndex와 lastIndex 값을 변경하며 여러번 호출
            val step = 1000
            var startIndex = 1
            var lastIndex = step
            var totalCount = 0

            do {
                // 백그라운드 작업이 취소된 경우 루프를 빠져나간다.
                if (isCancelled) break

                // totalCount가 0이 아닌경우 최초 실행이 아니므로 step 만큼 startIndex와 lastIndex를 증가
                if (totalCount != 0) {
                    startIndex += step
                    lastIndex += step
                }

                // startIndex, lastIndex 로 데이터 조회
                val jsonObject = readData(startIndex, lastIndex)

                // totalCount를 가져온다.
                totalCount = jsonObject.getJSONObject("SearchPublicToiletPOIService")
                    .getInt("list_total_count")

                // 화장실 정보 데이터 집합을 가져온다.
                val rows =
                    jsonObject.getJSONObject("SearchPublicToiletPOIService").getJSONArray("row")

                // 기존에 읽은 데이터와 병합
                toilets.merge(rows)

                // UI 업데이트를 위해 progress 발행
                publishProgress(rows)

            } while (lastIndex < totalCount) // lastIndex가 총 개수보다 적으면 반환한다.

            return "complete"
        }

        // 데이터를 읽어올때마다 중간중간 실행
        override fun onProgressUpdate(vararg values: JSONArray?) {
            // vararg는 JSONArray 파라미터를 가변적으로 전달하도록 하는 키워드

            //인덱스 0의 데이터를 사용
            val array = values[0]
            array?.let {
                for(i in 0 until array.length()) {
                    // 마커 추가
                    addMarkers(array.getJSONObject(i))
                }
            }
        }
```
```
네트워크 작업을 백그라운드에서 수행하고 작업이 진행될 때마다 UI 업데이트를 편리하게 사용하기 위해 AsyncTask 클래스를 사용하였습니다.

UI 업데이트 시 onPostExecute() 대신 onProgressUpdate() 를 사용하였습니다.
서버에서 넘어오는 데이터는 4000개 이상으로 매우 많기 때문에 1000개 씩 끊어서 읽어오고 중간중간 UI를 업데이트를 해야 했습니다.

가변 파라미터 vararg 키워드를 사용해서 호출하는 쪽에서 파라미터의 개수를 늘릴 수 있도록 하였습니다.
```

#### 마커 표시
![2024-06-25 18;33;00](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/394b8d2c-7342-4ee4-8036-d9a6a217ebc0)
```kotlin
 fun addMarkers(toilet: JSONObject) {
            googleMap?.addMarker(
                MarkerOptions()
                    .position(LatLng(toilet.getDouble("Y_WGS84"), toilet.getDouble("X_WGS84")))
                    .title(toilet.getString("FNAME"))
                    .snippet(toilet.getString("ANAME"))
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
            )
        }
```
```
마커를 추가하는 함수 부분입니다. JSON 데이터의 위도와 경도 항목을 읽어서 마커 객체를 만들고 구글 맵 객체에 addMarker() 함수를 이용해 추가했습니다.

선택 옵션인 snippet, icon은 마커를 클릭했을 때 풍선 도움말로 표시하는 정보입니다.
여기서는 json 데이터의 FNAME 과 ANAME을 줘서 구현했습니다.
```

## 클러스터 작업
#### 발생한 문제 상황
![2024-06-25 18;56;43](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/b6bdafda-163e-4941-90e7-7228484f1714)
```
구글 맵을 축소화할 때 마커가 겹쳐서 알아보기 힘들어집니다.

구글 맵의 util 라이브러리를 사용하여 자동으로 마커가 여러 개 겹친 경우 원의 숫자를 보여주도록 하여 문제를 개선해야 할 필요성이 있습니다.
```


