package me.yuxiaoyao.virtuallocation

import android.annotation.SuppressLint
import android.content.*
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.*
import com.amap.api.maps.LocationSource.OnLocationChangedListener
import com.amap.api.maps.model.*
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.*
import me.yuxiaoyao.virtuallocation.amap.client.AMapWebClient
import me.yuxiaoyao.virtuallocation.cache.ListHistoryCache
import me.yuxiaoyao.virtuallocation.constants.CACHE_NAME
import me.yuxiaoyao.virtuallocation.constants.MOCK_HISTORY_LIST_KEY
import me.yuxiaoyao.virtuallocation.model.AddressPair
import me.yuxiaoyao.virtuallocation.model.SearchDataModel
import me.yuxiaoyao.virtuallocation.retrofit.getAMapRetrofitClient
import me.yuxiaoyao.virtuallocation.service.VirtualLocationService
import me.yuxiaoyao.virtuallocation.sheet.MockHistoryBottomSheet
import me.yuxiaoyao.virtuallocation.sheet.MockingClickBottomSheet
import me.yuxiaoyao.virtuallocation.sheet.PositionBottomSheet
import me.yuxiaoyao.virtuallocation.sheet.SearchBottomSheet
import me.yuxiaoyao.virtuallocation.util.MapUtil
import me.yuxiaoyao.virtuallocation.util.formatAMapLocation
import me.yuxiaoyao.virtuallocation.util.formatMapLocation
import me.yuxiaoyao.virtuallocation.util.locationToLatLnt


private const val TAG = "MainActivity"
private const val FINE_LOCATION_REQ_CODE = 0x1234
const val HAS_FINE_LOCATION = "HAS_FINE_LOCATION"


class MainActivity : AppCompatActivity(), LocationSource, AMap.OnMyLocationChangeListener,
    AMapLocationListener, CoroutineScope by MainScope(), PoiSearch.OnPoiSearchListener,
    SearchBottomSheet.ItemClickListener {

    /**
     * 是否有定位的权限
     */
    private var hasLocationPermission: Boolean = false

    private var mLocationClient: AMapLocationClient? = null
    private var mOnLocationChangedListener: OnLocationChangedListener? = null
    private lateinit var mLocationOption: AMapLocationClientOption

    private lateinit var mapView: MapView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var currentMockContainer: ViewGroup
    private lateinit var currentMockLocation: TextView
    private lateinit var searchText: TextView
    private lateinit var mockHistory: ImageView
    private lateinit var goToSetting: ImageView

    private var isDataLoading = false

    /**
     * 所有Marker列表
     */
    private val markers: MutableList<Marker> = arrayListOf()

    /**
     * 当前先中的Marker列表
     */
    private var markerInWindowIndex = -1

    /**
     * 当前模拟位置的信息
     */
    private var isMocking = false

    /**
     * 正在模拟中的位置
     */
    private var mockingInfo: AddressPair? = null

    /**
     * AMap http client
     */
    private val aMapWebClient = getAMapRetrofitClient(AMapWebClient::class.java)

    /**
     * 模拟位置服务
     */
    private var virtualLocationBinder: VirtualLocationService.VirtualLocationBinder? = null
    private var virtualServiceConnection: ServiceConnection? = null

    private var myLocation: Location? = null

    private var searchDialog: SearchBottomSheet? = null
    private var searchQueryText: String? = null
    private var searchQueryResult = arrayListOf<SearchDataModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hasLocationPermission = intent.getBooleanExtra(HAS_FINE_LOCATION, false)

        loadingProgressBar = findViewById(R.id.loadingBar)
        mapView = findViewById(R.id.mapView)

        currentMockContainer = findViewById(R.id.currentMockContainer)
        currentMockLocation = findViewById(R.id.currentMockLocation)
        searchText = findViewById(R.id.searchText)
        mockHistory = findViewById(R.id.mockHistory)
        goToSetting = findViewById(R.id.goToSetting)



        configView()
        configMapView()
        configListener()
        mapView.onCreate(savedInstanceState)

        startVirtualService()
    }

    private fun configView() {

    }

    private fun configListener() {
        currentMockLocation.setOnClickListener {
            val arrayListOf = arrayListOf("查看地图", "停止模拟")
            mockingInfo?.apply {
                val bs = MockingClickBottomSheet(
                    this@MainActivity,
                    mockingInfo!!.address,
                    arrayListOf,
                    object : MockingClickBottomSheet.OnItemClickListener {
                        override fun onItemClickListener(
                            dialog: BottomSheetDialog,
                            view: View,
                            index: Int
                        ) {
                            when (index) {
                                0 -> {
                                    clearAllMarker()
                                    mockingInfo?.apply {
                                        addMarker(
                                            LatLng(
                                                mockingInfo!!.latitude,
                                                mockingInfo!!.longitude,
                                                true
                                            ),
                                            mockingInfo!!.cityName,
                                            mockingInfo!!.address,
                                            isCenter = true, showInfoWindow = true
                                        )
                                    }
                                }
                                1 -> {
                                    stopMockLocationAction()
                                }
                            }
                            dialog.dismiss()
                        }
                    })
                bs.show()
            }
        }
        searchText.setOnClickListener {
            if (!isDataLoading) {
                if (searchDialog != null) {
                    searchDialog!!.dismiss()
                    searchDialog = null
                }
                searchDialog = SearchBottomSheet(
                    this,
                    "附近",
                    searchQueryText,
                    searchQueryResult,
                    this
                ) { text ->
                    searchPositionInLocation(
                        text,
                        myLocation!!.longitude,
                        myLocation!!.latitude
                    )
                }
                searchDialog!!.show()
            }
        }
        mockHistory.setOnClickListener {
            MockHistoryBottomSheet(this, object : MockHistoryBottomSheet.OnItemClickListener {
                override fun onItemClick(
                    dialog: MockHistoryBottomSheet,
                    position: Int,
                    ap: AddressPair
                ) {
                    clearAllMarker()
                    addMarker(
                        LatLng(ap.latitude, ap.longitude),
                        ap.cityName,
                        ap.address,
                        isCenter = true,
                        showInfoWindow = true
                    )
                }

                override fun onMockClick(dialog: MockHistoryBottomSheet, ap: AddressPair) {
                    dialog.dismiss()
                    clearAllMarker()
                    val marker = addMarker(
                        LatLng(ap.latitude, ap.longitude),
                        ap.cityName,
                        ap.address,
                        isCenter = true,
                        showInfoWindow = true
                    )
                    startMockLocationAction(marker, ap)
                }
            }).show()
        }
        goToSetting.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }


    private fun moveToCenter(latitude: Double, longitude: Double) {
        val nc = CameraUpdateFactory.newLatLng(
            LatLng(latitude, longitude)
        )
        mapView.map.animateCamera(nc)
    }

    private fun moveToCenter(pos: LatLng) {
        val nc = CameraUpdateFactory.newLatLng(
            LatLng(pos.latitude, pos.longitude)
        )
        mapView.map.animateCamera(nc)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause()
    }


    override fun onDestroy() {
        super.onDestroy()
        virtualServiceConnection?.apply {
            unbindService(virtualServiceConnection!!)
        }
        mapView.onDestroy()
        mLocationClient?.isStarted.apply {
            mLocationClient?.stopLocation()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configMapView() {
        val myLocationStyle = MyLocationStyle()
        val aMap = mapView.map

        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW)
        aMap.myLocationStyle = myLocationStyle
        val uiSettings = aMap.uiSettings
        // 有定位权限才配置
        if (hasLocationPermission) {
            // 设置默认位置和默认缩放级别
            aMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(23.129112, 113.264385),
                    16.0f
                )
            )
            aMap.setLocationSource(this)
            aMap.setOnMyLocationChangeListener(this)
            aMap.isMyLocationEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
        }

        uiSettings.isCompassEnabled = true
        uiSettings.isScrollGesturesEnabled = true
        uiSettings.isScaleControlsEnabled = true
        uiSettings.setAllGesturesEnabled(true)
        uiSettings.isZoomControlsEnabled = true
        uiSettings.zoomPosition = AMapOptions.ZOOM_POSITION_RIGHT_CENTER

        aMap.setOnPOIClickListener { poi: Poi ->
            if (!isDataLoading) {
                val poiId = poi.poiId
                clearAllMarker()
                val coordinate = poi.coordinate
                val currentMarker =
                    addMarker(coordinate, poi.name, formatAMapLocation(coordinate), true)
                loadPoiLocationData(coordinate, poiId, currentMarker)
            }
        }
        aMap.setOnMapLongClickListener { latLng: LatLng ->
            if (!isDataLoading) {
                clearAllMarker()
                val currentMarker = addMarker(
                    latLng,
                    "经纬度",
                    formatAMapLocation(latLng),
                    false, showInfoWindow = true
                )
                loadLatLngLocation(latLng, currentMarker)
            }
        }
        aMap.setOnInfoWindowClickListener { marker: Marker ->
            if (!isDataLoading) {
                // load by position
                loadLatLngLocation(marker.position, marker)
            }
        }
        aMap.addOnMarkerClickListener {
            it.showInfoWindow()
            return@addOnMarkerClickListener true
        }
    }

    private fun startVirtualService() {
        virtualServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                virtualLocationBinder = binder as VirtualLocationService.VirtualLocationBinder
                virtualLocationBinder!!.getService().callback =
                    object : VirtualLocationService.Callback {
                        override fun virtualLocation(status: Boolean, t: Throwable?) {
                            isMocking = status
                            if (!status) {
                                runOnUiThread {
                                    stopMockLocationAction()
                                }
                            }
                        }
                    }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }
        }

        virtualServiceConnection?.apply {
            val intent = Intent(this@MainActivity, VirtualLocationService::class.java)
            bindService(
                intent,
                virtualServiceConnection!!,
                Context.BIND_AUTO_CREATE
            )
        }
    }


    private fun loadPoiLocationData(location: LatLng, poiId: String, marker: Marker) {
        isDataLoading = true
        loadingProgressBar.visibility = View.VISIBLE
        val loadDataExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            isDataLoading = false
            loadingProgressBar.visibility = View.GONE
            throwable.printStackTrace()
            Toast.makeText(this, "加载失败:${throwable.message}", Toast.LENGTH_SHORT).show()
        }

        launch(loadDataExceptionHandler) {
            val data = withContext(Dispatchers.IO) {
                aMapWebClient.getByPoiId(poiId)
            }
            // 协程加载完成 UI 操作
            isDataLoading = false
            loadingProgressBar.visibility = View.GONE
            val pois = data.pois
            if (pois.isNullOrEmpty()) {
                Toast.makeText(
                    this@MainActivity,
                    R.string.load_location_data_error,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val item = pois[0]
                val lngLat = locationToLatLnt(item.location)

                val cityName: String
                val address: String
                if (item.cityName.isNullOrEmpty()) {
                    cityName = item.name
                    address = item.address + item.name
                } else {
                    cityName = item.cityName
                    address = "${item.cityName}${item.adName}${item.address}${item.name}"
                }
                val ap = AddressPair(address, item.cityCode, cityName, lngLat[0], lngLat[1])
                showClickBottomSheet(marker, ap)
            }
        }
    }

    private fun showClickBottomSheet(marker: Marker, ap: AddressPair) {
        PositionBottomSheet(
            this,
            ap,
            object : PositionBottomSheet.ClickListener {
                override fun onAddressClickListener(
                    dialog: PositionBottomSheet,
                    view: View
                ) {
                    moveToCenter(
                        dialog.currentLocation.latitude,
                        dialog.currentLocation.longitude
                    )
                }

                override fun onMockLocationClickListener(
                    dialog: PositionBottomSheet,
                    view: View
                ) {
                    startMockLocationAction(marker, ap)
                    dialog.dismiss()
                }

                override fun onMyLocationSearchListener(dialog: PositionBottomSheet, view: View) {
                    dialog.dismiss()
                    if (!isDataLoading) {
                        isDataLoading = true
                        if (searchDialog != null) {
                            searchDialog!!.dismiss()
                            searchDialog = null
                        }
                        searchDialog = SearchBottomSheet(
                            this@MainActivity,
                            ap.address,
                            searchQueryText,
                            searchQueryResult,
                            this@MainActivity
                        ) { text ->
                            searchPositionInLocation(
                                text,
                                ap.longitude,
                                ap.latitude
                            )
                        }
                        searchDialog!!.show()
                    }
                }
            })
            .show()
    }

    private fun loadLatLngLocation(latLng: LatLng, marker: Marker) {
        isDataLoading = true
        loadingProgressBar.visibility = View.VISIBLE
        val loadDataExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            isDataLoading = false
            loadingProgressBar.visibility = View.GONE
            throwable.printStackTrace()
            Toast.makeText(this, "加载失败:${throwable.message}", Toast.LENGTH_SHORT).show()
        }


        launch(loadDataExceptionHandler) {
            val data = withContext(Dispatchers.IO) {
                aMapWebClient.getLocationInfo(formatAMapLocation(latLng))
            }
            isDataLoading = false
            loadingProgressBar.visibility = View.GONE

            val reGeoCode = data.reGeoCode
            if (reGeoCode == null) {
                Toast.makeText(
                    this@MainActivity,
                    R.string.load_location_data_error,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val ap = AddressPair(
                    reGeoCode.formattedAddress,
                    reGeoCode.addressComponent.cityCode,
                    reGeoCode.addressComponent.city,
                    latLng.longitude,
                    latLng.latitude
                )
                showClickBottomSheet(marker, ap)
            }

        }
    }


    @SuppressLint("SetTextI18n")
    private fun startMockLocationAction(marker: Marker, ap: AddressPair) {

        // 高德坐标转换成GPS
        val gps = MapUtil.convertToGPS(ap.longitude, ap.latitude)
        Log.i(
            TAG,
            "高德坐标: ${ap.longitude},${ap.latitude} . GPS坐标: ${gps[0]},${gps[1]}"
        )

        virtualLocationBinder?.apply {
            val status = virtualLocationBinder!!.startVirtualLocation(gps[0], gps[1], ap.address)
            if (status) {
                // 启动成功
                mockingInfo = ap
                currentMockLocation.text = "正在模拟: ${ap.address}"
                currentMockContainer.visibility = View.VISIBLE

                // 添加到历史记录
                ListHistoryCache.with<AddressPair>(this@MainActivity, CACHE_NAME)
                    .add(MOCK_HISTORY_LIST_KEY, ap)
            } else {
                Toast.makeText(this@MainActivity, "模拟位置失败,请确定在开发者中选择当前应用", Toast.LENGTH_SHORT)
                    .show()
            }
        }


    }

    private fun stopMockLocationAction() {
        currentMockContainer.visibility = View.GONE
        virtualLocationBinder?.stopVirtualLocation()
        // mockingMarker = null
        mockingInfo = null
    }


    override fun activate(onLocationChangedListener: LocationSource.OnLocationChangedListener?) {
        this.mOnLocationChangedListener = onLocationChangedListener
        if (mLocationClient == null) {
            //初始化定位
            mLocationClient = AMapLocationClient(this)
            //初始化定位参数
            mLocationOption = AMapLocationClientOption()
            mLocationOption.isSensorEnable = true
            //            mLocationOption.setInterval(1000L);
            mLocationOption.isOnceLocation = true
            //设置定位回调监听
            mLocationClient!!.setLocationListener(this)
            //设置为高精度定位模式
            mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            //设置定位参数
            mLocationClient!!.setLocationOption(mLocationOption)
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient!!.startLocation() //启动定位
        }
    }

    override fun deactivate() {
        mOnLocationChangedListener = null
        if (mLocationClient != null) {
            mLocationClient!!.stopLocation()
            mLocationClient!!.onDestroy()
        }
        mLocationClient = null
    }

    override fun onMyLocationChange(location: Location?) {
        println(location?.longitude)
        println(location?.latitude)
        myLocation = location
    }


    override fun onLocationChanged(aMapLocation: AMapLocation?) {
        if (mOnLocationChangedListener != null && aMapLocation != null) {
            if (aMapLocation.errorCode == 0) {
                // 显示系统小蓝点
                mOnLocationChangedListener!!.onLocationChanged(aMapLocation)
            } else {
                val errText =
                    "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo()
                Log.e("AmapErr", errText)
            }
        }
    }

    private fun addMarker(
        latLng: LatLng,
        title: String,
        snippet: String?,
        isCenter: Boolean,
        showInfoWindow: Boolean = false
    ): Marker {

        val mb = MarkerOptions().position(latLng).title(title)
        snippet?.apply {
            mb.snippet(snippet)
        }
        val marker = mapView.map.addMarker(mb)
        if (showInfoWindow) {
            marker.showInfoWindow()
        }
        markers.add(marker)
        if (isCenter) {
            moveToCenter(latLng)
        }
        return marker
    }

    private fun clearAllMarker() {
        if (markers.isNotEmpty()) {
            val iterator: MutableIterator<Marker> = markers.iterator() as MutableIterator<Marker>
            while (iterator.hasNext()) {
                val m = iterator.next()
                m.destroy()
                iterator.remove()
            }
        }
        markerInWindowIndex = -1
    }


    /**
     * 在 longitude,latitude 附近搜索 text
     */
    private fun searchPositionInLocation(text: String, longitude: Double, latitude: Double) {
        isDataLoading = true
        searchQueryText = text
        val query = PoiSearch.Query(text, text)
        val poiSearch = PoiSearch(this, query)
        poiSearch.bound = PoiSearch.SearchBound(LatLonPoint(latitude, longitude), 3000)
        poiSearch.setOnPoiSearchListener(this)
        poiSearch.searchPOIAsyn()
        // callback onPoiSearched()
    }


    override fun onPoiSearched(result: PoiResult?, code: Int) {
        isDataLoading = false
        searchQueryResult.clear()
        if (searchDialog == null) {
            return
        }
        if (code != 1000) {
            // 高德地图API,1000 为成功
            Toast.makeText(this, "搜索失败.code=$code", Toast.LENGTH_SHORT).show()
            return
        }
        if (result == null) {
            Toast.makeText(this, "搜索结果为空", Toast.LENGTH_SHORT).show()
            return
        }
        result.pois.forEach { poi ->
            val address = "${poi.cityName}${poi.adName}${poi.snippet}"
            val latLonPoint = poi.latLonPoint
            val sm = SearchDataModel(
                poi.toString(),
                address,
                poi.cityCode,
                poi.cityName,
                latLonPoint.latitude,
                latLonPoint.longitude
            )
            searchQueryResult.add(sm)
        }

        searchQueryResult.forEachIndexed { index, it ->
            addMarker(
                LatLng(it.latitude, it.longitude), it.name,
                formatMapLocation(it.latitude, it.longitude), index == 0
            )
        }

        // set new Data
        searchDialog!!.setData(searchQueryResult)
        if (!searchDialog!!.isShowing) {
            searchDialog!!.show()
        }
    }

    override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {

    }

    override fun onSearchItemClick(position: Int) {
        val item = searchQueryResult[position]
        markers[position].showInfoWindow()
        moveToCenter(item.latitude, item.longitude)
    }
}