package com.wonchihyeon.seoultoilet

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.wonchihyeon.seoultoilet.databinding.ActivityMainBinding

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        with(binding) {
            // 맵뷰에 onCreate 함수 호출
            mapView.onCreate(savedInstanceState)

            // 현재 위치 버튼을 클릭했을 때
            myLocationButton.setOnClickListener{
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
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
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
                    this,
                    permission
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
                    getMyLocation()!!,
                    DEFAULT_ZOOM_LEVEL
                )
            ) else -> Toast.makeText(applicationContext, "위치권한 설정에 동의해주세요.", Toast.LENGTH_SHORT).show()
        }
    }
}