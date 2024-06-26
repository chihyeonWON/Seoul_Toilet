package com.wonchihyeon.seoultoilet

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.wonchihyeon.seoultoilet.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    // 런타임에서 권한이 필요한 퍼미션 목록
    val PERMISSIONS = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    // 퍼미션 승인 요청 시 사용하는 요청 코드
    val REQUEST_PERMISSION_CODE = 1

    // 기본 맵 줌 레벨
    val DEFAULT_ZOOM_LEVEL = 17f

    // 현재위치를 가져올 수 없는 경우 서울 시청의 위치로 지도를 보여주기 위해 서울시청의 위치를 변수로 선언
    // LatLng 클래스는 위도와 경도를 가지는 클래스
    val CITY_HALL = LatLng(37.56662952, 126.97794509999994)

    // 구글 맵 객체를 참조할 변수
    var googleMap: GoogleMap? = null

    private lateinit var binding: ActivityMainBinding

    // 서울 열린 데이터 광장에서 발급받은 API 키 입력
    val API_KEY = "7441494453686575343659486b4e62"

    // 서울시 화장실 정보 집합을 저장할 Array 변수, 검색을 위해 저장
    var toilets = JSONArray()

    // 화장실 이미지로 사용할 Bitmap
    val bitmap by lazy {
        val drawable = resources.getDrawable(R.drawable.restroom_sign) as BitmapDrawable
        Bitmap.createScaledBitmap(drawable.bitmap, 64, 64, false)
    }

    // ClusterManager 변수 선언
    var clusterManager: ClusterManager<MyItem>? = null

    // ClusterRenderer 변수 선언
    var clusterRenderer: ClusterRenderer? = null

    // JSONObject 를 키로 MyItem 객체를 저장할 맵
    val itemMap = mutableMapOf<JSONObject, MyItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        with(binding) {
            // 맵뷰에 onCreate 함수 호출
            mapView.onCreate(savedInstanceState)

            // 현재 위치 버튼을 클릭했을 때
            myLocationButton.setOnClickListener {
                onMyLocationButtonClick()
            }
        }

        // 앱이 실행될때 런타임에서 위치 서비스 관련 권한체크
        if (hasPermission()) {
            // 권한이 있는 경우 맵 초기화
            initMap()
        } else {
            // 권한 요청
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE)
        }
    }

    // 권한 요청의 결과로 맵 초기화
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 맵 초기화
        initMap()
    }

    // 앱에서 사용하는 권한이 있는지 체크하는 함수
    fun hasPermission(): Boolean {
        // 퍼미션 목록중 하나라도 권한이 없으면 false 반환
        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(
                    this, permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    // 맵 초기화하는 함수
    @SuppressLint("MissingPermission")
    fun initMap() {
        // 맵뷰에서 구글 맵을 불러오는 함수, 콜백함수에서 구글 맵 객체가 전달됨
        binding.mapView.getMapAsync {
            // ClusterManger 객체 초기화
            clusterManager = ClusterManager(this, it)
            clusterRenderer = ClusterRenderer(this, it, clusterManager)

            // OnCameraIdleListener와 OnMarkerClickListener 를 clusterManager로 지정
            it.setOnCameraIdleListener(clusterManager)
            it.setOnMarkerClickListener(clusterManager)

            // 구글맵 멤버 변수에 구글맵 객체 저장
            googleMap = it

            // 현재 위치로 이동 버튼 활성화
            it.uiSettings.isMyLocationButtonEnabled = false

            // 위치 권한이 있는 경우
            when {
                hasPermission() -> {
                    // 현재위치 표시 활성화
                    it.isMyLocationEnabled = true
                    // 현재위치로 카메라 이동
                    it.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            getMyLocation()!!, DEFAULT_ZOOM_LEVEL
                        )
                    )
                    // 카메라의 위치와 각도를 설정합니다.
                    val cameraPosition = CameraPosition.Builder()
                        .target(CITY_HALL)
                        .zoom(17f)
                        .bearing(0f) // 방향 설정
                        .tilt(90f) // 각도 설정
                        .build()
                    it.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                }

                else -> {
                    // 권한이 없으면 서울시청의 위치로 이동
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(CITY_HALL, DEFAULT_ZOOM_LEVEL))
                    // 카메라의 위치와 각도를 설정합니다.
                    val cameraPosition = CameraPosition.Builder()
                        .target(CITY_HALL)
                        .zoom(17f)
                        .bearing(0f) // 방향 설정
                        .tilt(90f) // 각도 설정
                        .build()
                    it.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getMyLocation(): LatLng? {
        val locationProvider: String = LocationManager.GPS_PROVIDER
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val lastKnownLocation: Location? = locationManager.getLastKnownLocation(locationProvider)
        // null 체크를 추가하여 안전하게 처리
        return if (lastKnownLocation != null) {
            LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
        } else {
            LatLng(37.56662952, 126.97794509999994)// 혹은 기본 위치로 대체
        }
    }

    // 현재 위치 버튼 클릭한 경우
    fun onMyLocationButtonClick() {
        when {
            hasPermission() -> googleMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    getMyLocation()!!, DEFAULT_ZOOM_LEVEL
                )
            )

            else -> Toast.makeText(applicationContext, "위치권한 설정에 동의해주세요.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // JSONArray 를 병합하기 위해 확장함수 사용
    fun JSONArray.merge(anotherArray: JSONArray) {
        for (i in 0 until anotherArray.length()) {
            this.put(anotherArray.get(i))
        }
    }

    // 화장실 정보를 읽어와 JSONObject 로 반환하는 함수
    suspend fun readData(startIndex: Int, lastIndex: Int): JSONObject {
        val url =
            URL("http://openAPI.seoul.go.kr:8088/${API_KEY}/json/SearchPublicToiletPOIService/${startIndex}/${lastIndex}")
        val connection = url.openConnection()

        return withContext(Dispatchers.IO) {
            val data = connection.getInputStream().readBytes().toString(charset("UTF-8"))
            JSONObject(data)
        }
    }

    // 화장실 데이터를 읽어오는 코루틴
    fun CoroutineScope.loadToilets() {

        // autoCompletTextView
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)

        launch {
            // 초기화 코드를 여기에 배치
            googleMap?.clear()
            toilets = JSONArray()
            itemMap.clear()

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
        }
    }


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
    }

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

        // imageView
        val imageView = findViewById<ImageView>(R.id.imageView)

        // autoCompletTextView
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)

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
        }

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
        }
    }

    override fun onStop() {
        super.onStop()
        stopLoadingToilets()
    }

    // 하단부터 맵뷰의 라이프사이클 함수 호출을 위한 코드들
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
}