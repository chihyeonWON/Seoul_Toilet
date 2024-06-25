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
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.wonchihyeon.seoultoilet.databinding.ActivityMainBinding
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

    // 앱이 비활성화될때 백그라운드 작업도 취소하기 위한 변수 선언
    var task: ToiletReadTask? = null

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
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
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
                }

                else -> {
                    // 권한이 없으면 서울시청의 위치로 이동
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(CITY_HALL, DEFAULT_ZOOM_LEVEL))
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
    fun readData(startIndex: Int, lastIndex: Int): JSONObject {
        val url =
            URL("http://openAPI.seoul.go.kr:8088" + "/${API_KEY}/json/SearchPublicToiletPOIService/${startIndex}/${lastIndex}")
        val connection = url.openConnection()

        val data = connection.getInputStream().readBytes().toString(charset("UTF-8"))
        return JSONObject(data)
    }

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

        fun addMarkers(toilet: JSONObject) {
            googleMap?.addMarker(
                MarkerOptions()
                    .position(LatLng(toilet.getDouble("Y_WGS84"), toilet.getDouble("X_WGS84")))
                    .title(toilet.getString("FNAME"))
                    .snippet(toilet.getString("ANAME"))
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
            )
        }
    }

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