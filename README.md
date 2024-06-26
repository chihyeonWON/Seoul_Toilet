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

## 24.06.25 공공 데이터 API 연동
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
## AsyncTask -> Coroutine
현재 AsyncTask는 deprecated 되어 많은 안드로이드 개발자들이 AsyncTask 대신 코루틴을 선호하며, 공식적으로도 코루틴이 권장되고 있습니다.
따라서 24년 현재 프로젝트의 AsyncTask를 코루틴으로 마이그레이션하는 작업을 수행하였습니다. 코루틴으로 비동기 작업을 처리하면 얻는 이점들로는 다음과 같습니다.
- 유지보수성: AsyncTask는 코드가 복잡해지고, 여러 콜백과 내부 클래스를 관리해야 하기 때문에 유지보수가 어렵습니다. 반면, 코루틴은 코드를 더 간결하고 읽기 쉽게 만들어 유지보수를 용이하게 합니다.
- 생명주기 인식: AsyncTask는 액티비티나 프래그먼트의 생명주기를 인식하지 못해 메모리 누수를 일으킬 수 있습니다. 코루틴은 생명주기를 인식하는 스코프(LifecycleScope)와 함께 사용할 수 있어 안전하게 리소스를 관리할 수 있습니다.
- 오류 처리: AsyncTask는 오류 처리가 복잡하지만, 코루틴은 try/catch 블록을 사용하여 간단하게 오류를 처리할 수 있습니다.
- 취소 가능성: AsyncTask는 취소가 번거롭고 불완전할 수 있지만, 코루틴은 코루틴 스코프 내에서 쉽게 취소할 수 있으며, 취소 시 모든 관련 작업이 정리됩니다.
- 동시성: 코루틴은 동시에 여러 비동기 작업을 쉽게 처리할 수 있으며, async와 await 패턴을 사용하여 결과를 효율적으로 합칠 수 있습니다.
- 성능: 코루틴은 경량 스레드로, 많은 수의 동시 작업을 처리할 때 성능상의 이점을 제공합니다.
- 구조화된 동시성: 코루틴은 구조화된 동시성을 제공하여, 관련된 모든 작업이 함께 시작되고 종료될 수 있도록 합니다.
```kotlin
// 화장실 정보를 읽어와 JSONObject 로 반환하는 함수
    suspend fun readData(startIndex: Int, lastIndex: Int): JSONObject {
        val url = URL("http://openAPI.seoul.go.kr:8088/${API_KEY}/json/SearchPublicToiletPOIService/${startIndex}/${lastIndex}")
        val connection = url.openConnection()

        return withContext(Dispatchers.IO) {
            val data = connection.getInputStream().readBytes().toString(charset("UTF-8"))
            JSONObject(data)
        }
    }
```
```
네트워크/DB 입출력이 있는 작업들에 대해 적절한 Thread로 할당하는 역할을 하는 Dispatchers.IO로 withContext(문맥교환)하였습니다.
```
```kotlin
// 화장실 데이터를 읽어오는 코루틴
    fun CoroutineScope.loadToilets() {
        launch {
            // 구글맵 마커 초기화
            googleMap?.clear()
            // 화장실 정보 초기화
            toilets = JSONArray()

            val step = 1000
            var startIndex = 1
            var lastIndex = step
            var totalCount = 0

            do {
                // startIndex, lastIndex 로 데이터 조회
                val jsonObject = readData(startIndex, lastIndex)

                // totalCount를 가져온다.
                totalCount = jsonObject.getJSONObject("SearchPublicToiletPOIService")
                    .getInt("list_total_count")

                // 화장실 정보 데이터 집합을 가져온다.
                val rows = jsonObject.getJSONObject("SearchPublicToiletPOIService").getJSONArray("row")

                // 기존에 읽은 데이터와 병합
                toilets.merge(rows)

                // UI 업데이트를 위해 메인 스레드에서 실행
                withContext(Dispatchers.Main) {
                    updateUI(rows) // UI 업데이트 함수
                }

                // step 만큼 startIndex와 lastIndex를 증가
                startIndex += step
                lastIndex += step

            } while (lastIndex < totalCount)
        }
    }
```
```
readData 메서드를 서버에서 필요할 때 데이터를 요청하는 CoroutineScope로 감쌌습니다. 이 중 UI 업데이트(지도에 마커표시)하기 위해
Dispatchers.Main으로 문맥교환하였습니다.
```
```kotlin
 // UI 업데이트 함수
    fun updateUI(rows: JSONArray) {
        // 여기에 UI 업데이트 로직 구현
        rows?.let {
            for (i in 0 until it.length()) {
                // 마커 추가
                addMarkers(it.getJSONObject(i))
            }
        }
        // clusterManager의 클러스터링 실행
        clusterManager?.cluster()
    }
```
```
마커를 업데이트하는 로직을 let 영역함수에서 구현하였습니다.
```
```kotlin
// 앱이 활성화될때 서울시 화장실 데이터를 읽어옴
    fun startLoadingToilets() {
        // 코루틴 스코프 생성 및 실행
        CoroutineScope(Dispatchers.Main).loadToilets()
    }

    // 앱이 비활성화 될때 백그라운드 작업 취소
    fun stopLoadingToilets() {
        // 코루틴 취소
        CoroutineScope(Dispatchers.Main).cancel()
    }

    override fun onStart() {
        super.onStart()
        startLoadingToilets()
    }

    override fun onStop() {
        super.onStop()
        stopLoadingToilets()
    }
```
```
액티비티의 생명 주기에서 더 간단하게 코드를 처리할 수 있게 되었습니다.
또 onStop에서 AsyncTask는 취소(cancel())만 가능했다면 코루틴은 취소말고도 더 많은 작업을 수행할 수 있게 됩니다.
```

## 클러스터 작업
#### 발생한 문제 상황
![2024-06-25 18;56;43](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/b6bdafda-163e-4941-90e7-7228484f1714)
```
구글 맵을 축소화할 때 마커가 겹쳐서 알아보기 힘들어집니다.

구글 맵의 util 라이브러리를 사용하여 자동으로 마커가 여러 개 겹친 경우 원의 숫자를 보여주도록 하여 문제를 개선해야 할 필요성이 있습니다.
```
#### ClusterItem 인터페이스 구현
```kotlin
// 검색에서 아이템을 찾기위해 동등성 함수 오버라이드
    // GPS 상 위도, 경도, 제목, 설명 항목이 모두 같으면 같은 객체로 취급
    override fun equals(other: Any?): Boolean {
        if (other is MyItem) {
            return (other.position.latitude == position.latitude
                    && other.position.longitude == position.longitude
                    && other.title == _title
                    && other.snippet == _snippet)
        }
        return false
    }
```
```
동등성 함수를 오버라이드합니다. GPS 상 위도, 경도, 제목, 설명 항목이 모두 같으면 같은 객체로 취급합니다.
```
```kotlin
// equals() 를 오버라이드 한 경우 반드시 오버라이드 필요
    // 같은 객체는 같은 해시코드를 반환해야 함
    override fun hashCode(): Int {
        var hash = _position.latitude.hashCode() * 31
        hash = hash * 31 + _position.longitude.hashCode()
        hash = hash * 31 + title.hashCode()
        hash = hash * 31 + snippet.hashCode()
        return hash
    }
```
```
equals 를 오버라이딩한 경우 반드시 오버라이드해야하는 hashCode함수 입니다. hash 값을 생성하여 반환합니다.
```
#### ClusterRenderer 클래스 구현
```
앱의 마커의 아이콘을 변경하므로 마커를 렌더링 작업을 담당하는 클래스인 ClusterRenderer 클래스를 구현하였습니다.
```
```kotlin
init {
        // 전달받은 clusterManager 객체에 renderer를 자신으로 지정
        clusterManager?.renderer = this
    }
```
```
초기화 부분에서 ClusterManager 클래스의 렌더러를 자신으로 지정합니다.
```
```kotlin
// 클러스터 아이템이 렌더링 되기전 호출되는 함수
    override fun onBeforeClusterItemRendered(item: MyItem?, markerOptions: MarkerOptions?) {
        // 마커의 아이콘 지정
        markerOptions?.icon(item?.getIcon())
        markerOptions?.visible(true)
    }
```
```
마커의 아이콘을 지정하였습니다.
```
#### 구글 맵에 ClusterManager 연동
```kotlin
 // ClusterManger 객체 초기화
            clusterManager = ClusterManager(this, it)
            clusterRenderer = ClusterRenderer(this, it, clusterManager)

            // OnCameraIdleListener와 OnMarkerClickListener 를 clusterManager로 지정
            it.setOnCameraIdleListener(clusterManager)
            it.setOnMarkerClickListener(clusterManager)
```
```
initMap() 함수에서 clusterManager 객체를 초기화하였습니다.
```
```kotlin
fun addMarkers(toilet: JSONObject) {
            clusterManager?.addItem(
                MyItem(
                    LatLng(toilet.getDouble("Y_WGS84"), toilet.getDouble("X_WGS84")),
                    toilet.getString("FNAME"),
                    toilet.getString("ANAME"),
                    BitmapDescriptorFactory.fromBitmap(bitmap)
                )
            )
        }
```
```
마커를 추가하는 addMarkers에서 clusterManager를 이용해서 마커를 추가하도록 변경하였습니다.
```
```kotlin
// clusterManager의 클러스터링 실행
            clusterManager?.cluster()
```
```
데이터를 읽어 오는 지점마다 맵에 클러스터를 업데이트(클러스터링 진행)하기 위하여 onProgressUpdate() 함수를 수정하였습니다.
```
## 클러스터링 작업 수행 후
![2024-06-26 01;28;16](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/627e127d-f125-4082-9430-a898bf85cfe2)
```
클러스터링 작업을 수행하여 마커가 겹치는 경우 마커의 개수를 숫자로 보여주게 되어 마커가 겹쳐 알아보기 힘든 문제를 성공적으로 해결하였습니다.
```
## 24.06.25 카메라 각도 변경
![2024-06-26 02;04;15](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/866cf3d2-fbee-405c-afbf-a7f43768c2e8)
```
카메라 각도를 변경하여서 입체감이 느껴지도록 수정해보았습니다.
사용자가 현재 위치를 실감나게 느낄 수 있다는 장점이 있는 것 같습니다.

사용자가 카메라각도를 조절할 수 있도록 하는 기능을 넣거나 두 개의 화면을 제공하는 방법도
괜찮은 것 같습니다.
```
## 24.06.26 사용자 편의성을 위한 검색 기능 추가
```
내가 방문할 곳이나 방문한 곳에 화장실이 있는 지 검색하고 싶을 때가 있습니다.
사용자의 편의성을 위해서 검색 기능을 추가하여야 합니다.
```
#### 검색 UI 설계 
![image](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/4a0f8e2c-a865-4634-bb50-f5f84a0f5a83)
```
search_bar.xml 레이아웃 리소스 파일을 새로 추가하였습니다.
CardView와 ConstraintLayout 안에 자동완성 검색 기능을 위한 AutoCompleteTextView와 ImageView를 배치하였습니다.
```
#### MainActivity에 검색 바 UI 추가
![image](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/06ba4e42-ec22-4646-bb11-277b2f735ead)    
![image](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/6dcc3d7e-c69d-4078-8cc1-a31f4aac8b52)
![image](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/57048b69-1388-4906-b6b6-011dbe1f51d7)
```
MainActivity에 앞서 만든 검색 바 UI 를 include 태그를 사용하여 추가하였습니다.
```
#### 검색 기능 구현
```kotlin
 // 백그라운드 작업이 완료된 후 실행될 코드를 여기에 배치
            withContext(Dispatchers.Main) {
                // 자동완성 텍스트뷰(AutoCompleteTextView)에서 사용할 텍스트 리스트
                val textList = mutableListOf<String>()

                // 모든 화장실의 이름을 텍스트 리스트에 추가
                for (i in 0 until toilets.length()) {
                    val toilet = toilets.getJSONObject(i)
                    textList.add(toilet.getString("FNAME"))
                }

                // 자동완성 텍스트뷰에서 사용하는 어댑터 추가
                val adapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    textList
                )

                // 자동완성이 시작되는 글자수 지정
                autoCompleteTextView.threshold = 1
                // autoCompleteTextView의 어댑터를 상단에서 만든 어댑터로 지정
                autoCompleteTextView.setAdapter(adapter)
            }
```
```
자동완성텍스트뷰에 사용할 텍스트 리스트를 생성하고 리스트에 화장실 이름을 모두 넣고 어댑터를 추가하였습니다.
```
```kotlin
 fun addMarkers(toilet: JSONObject) {
        val item = MyItem(
            LatLng(toilet.getDouble("Y_WGS84"), toilet.getDouble("X_WGS84")),
            toilet.getString("FNAME"),
            toilet.getString("ANAME"),
            BitmapDescriptorFactory.fromBitmap(bitmap)
        )

        // 아이템맵에 toilet 객체를 키로 MyItem 객체 저장
        itemMap.put(toilet, item)

        clusterManager?.addItem(
            MyItem(
                LatLng(toilet.getDouble("Y_WGS84"), toilet.getDouble("X_WGS84")),
                toilet.getString("FNAME"),
                toilet.getString("ANAME"),
                BitmapDescriptorFactory.fromBitmap(bitmap)
            )
        )
```
```
마커를 추가하는 함수에 toilet 객체를 키로한 MyItem 객체를 itemMap 변수에 저장하였습니다.
```
```kotlin
 // JSONArray에서 원소의 속성으로 원소를 검색.
    // propertyName: 속성이름
    // value: 값
    fun JSONArray.findByChildProperty(propertName: String, value: String): JSONObject? {
        // JSONObject를 순회하면서 각 JSONObject의 프로퍼티의 값이 같은지 확인
        for (i in 0 until length()) {
            val obj = getJSONObject(i)
            if (value == obj.getString(propertName)) return obj
        }
        return null
    }
```
```
JSONObject를 순회하면서 속성이름 즉 자동완성텍스트뷰로 입력한 화장실 이름 텍스트를 기준으로 검색합니다.
```
```kotlin
// searchBar 의 검색 아이콘의 이벤트 리스너 설정
        imageView.setOnClickListener {
            // autoCompleteTextView 의 텍스트를 읽어 키워드로 가져옴
            val keyword = autoCompleteTextView.text.toString()
            // 키워드 값이 없으면 그대로 리턴
            if (TextUtils.isEmpty(keyword)) return@setOnClickListener
            // 검색 키워드에 해당하는 JSONObject 를 찾는다.
            toilets.findByChildProperty("FNAME", keyword)?.let {
                // itemMap 에서 JSONObject 를 키로 가진 MyItem 객체를 가져온다.
                val myItem = itemMap[it]
                // ClusterRenderer 에서 myItem 을 기반으로 마커를 검색한다.
                // myItem 은 위도,경도,제목,설명 속성이 같으면 같은 객체로 취급됨
                val marker = clusterRenderer?.getMarker(myItem)
                // 마커에 인포 윈도우를 보여준다
                marker?.showInfoWindow()
                // 마커의 위치로 맵의 카메라를 이동한다.
                googleMap?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(it.getDouble("Y_WGS84"), it.getDouble("X_WGS84")), DEFAULT_ZOOM_LEVEL
                    )
                )
                clusterManager?.cluster()
            }

            // 검색 텍스트뷰의 텍스트를 지운다.
            autoCompleteTextView.setText("")
```
```
onStart()에서 searchBar의 검색 아이콘을 클릭하면 다음 로직이 수행됩니다.
앞서 생성한 검색 함수의 매개변수로 화장실이름을 propertyName으로 넘겨주고 myItem 객체를 키로 기반하여 마커를 검색합니다.
만약 검색된 객체가 있다면 해당 객체의 위도와 경도로 카메라 경로를 이동시키고 마커의 인포 윈도우(title, snippet)를 보여줍니다.
```
## 검색 기능이 추가된 화장실 찾기 앱
![2024-06-26 15;32;06](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/5798df39-1534-4ef1-9f36-7e65af7262bd)
```
검색창에 성수를 입력하면 성~으로 시작하는 화장실 이름 리스트가 입력창 아래에 나타나게 되고 이 중에 내가 원하는 화장실 이름을 입력하고
검색 버튼을 누르면 해당 화장실로 카메라가 이동하며 해당 화장실의 정보(이름, snippet)가 나타나게 됩니다.

다음으로 이 앱에 Firebase 데이터베이스를 사용하여 더 사용자에게 좋은 기능을 제공할 수 있도록 수정합니다.
```
## 검색 리스트 텍스트를 클릭해도 검색되도록 수정
![2024-06-26 15;42;00](https://github.com/chihyeonwon/Seoul_Toilet/assets/58906858/26d298da-6d97-4134-9570-eb5e7d8b2a59)
```kotlin
 // AutoCompleteTextView의 항목 선택 이벤트 리스너 설정
        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            // 선택된 항목의 텍스트를 가져옴
            val keyword = autoCompleteTextView.adapter.getItem(position).toString()
            // 키워드 값이 없으면 그대로 리턴
            if (TextUtils.isEmpty(keyword)) return@setOnItemClickListener
            // 검색 키워드에 해당하는 JSONObject 를 찾는다.
            toilets.findByChildProperty("FNAME", keyword)?.let {
                // itemMap 에서 JSONObject 를 키로 가진 MyItem 객체를 가져온다.
                val myItem = itemMap[it]
                // ClusterRenderer 에서 myItem 을 기반으로 마커를 검색한다.
                // myItem 은 위도,경도,제목,설명 속성이 같으면 같은 객체로 취급됨
                val marker = clusterRenderer?.getMarker(myItem)
                // 마커에 인포 윈도우를 보여준다
                marker?.showInfoWindow()
                // 마커의 위치로 맵의 카메라를 이동한다.
                googleMap?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(it.getDouble("Y_WGS84"), it.getDouble("X_WGS84")), DEFAULT_ZOOM_LEVEL
                    )
                )
                clusterManager?.cluster()
            }

            // 검색 텍스트뷰의 텍스트를 지운다.
            autoCompleteTextView.setText("")
```
```
setOnItemClickListener 를 사용하여 선택된 아이템의 텍스트로 바로 검색할 수 있도록 하여 사용자의 편의성을 높혔습니다.
```
## 사용자 별점 기능, 한 줄 평가 기능 추가
