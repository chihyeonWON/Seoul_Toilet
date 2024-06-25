package com.wonchihyeon.seoultoilet

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

// ClusterItem 을 구현한 MyItem 클래스
class MyItem(
    val _position: LatLng, val _title: String, val _snippet: String, val _icon: BitmapDescriptor
) : ClusterItem {
    override fun getPosition(): LatLng {
        return _position
    }

    override fun getTitle(): String {
        return _title
    }

    override fun getSnippet(): String {
        return _snippet
    }

    // 마커의 아이콘을 변경하기 위함
    fun getIcon(): BitmapDescriptor {
        return _icon
    }

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

    // equals() 를 오버라이드 한 경우 반드시 오버라이드 필요
    // 같은 객체는 같은 해시코드를 반환해야 함
    override fun hashCode(): Int {
        var hash = _position.latitude.hashCode() * 31
        hash = hash * 31 + _position.longitude.hashCode()
        hash = hash * 31 + title.hashCode()
        hash = hash * 31 + snippet.hashCode()
        return hash
    }
}