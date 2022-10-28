package com.riezki.storyapp.ui.maps

import android.content.ContentValues.TAG
import android.content.Context
import android.content.res.Resources
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.riezki.storyapp.R
import com.riezki.storyapp.databinding.FragmentMapsStoryBinding
import com.riezki.storyapp.utils.Resource
import com.riezki.storyapp.utils.ViewModelFactory
import java.io.IOException
import java.util.*

class MapsStoryFragment : Fragment() {

    private var _binding: FragmentMapsStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap

    private val viewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true

        getMapsList(requireContext())
        mMap.setOnPoiClickListener { pointInterest ->
            val poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(pointInterest.latLng)
                    .title(pointInterest.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            )
            poiMarker?.showInfoWindow()
        }

        setMapStyle()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //(activity as AppCompatActivity).supportActionBar?.hide()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun getAddressName(lat: Double, lon: Double): String? {
        var addressName: String? = null
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                addressName = list[0].getAddressLine(0)
                Log.d(TAG, "getAddressName: $addressName")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }

    private val boundsBuilder = LatLngBounds.builder()

    private fun getMapsList(context: Context) {
        viewModel.userToken.observe(viewLifecycleOwner) { token ->
            viewModel.getListMap(context, "Bearer $token").observe(viewLifecycleOwner) { items ->
                if (items != null) {
                    when (items) {
                        is Resource.Loading -> {
                            showLoading(true)
                        }

                        is Resource.Success -> {
                            showLoading(false)
                            items.data?.forEach { story ->
                                val latLng = story.lat?.let { lat ->
                                    story.lon?.let { lon ->
                                        LatLng(lat, lon)
                                    }
                                }

                                val addressName = story.lat?.let { lat ->
                                    story.lon?.let { lon ->
                                        getAddressName(lat, lon)
                                    }
                                }

                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(latLng!!)
                                        .title(story.name)
                                        .snippet(addressName)
                                )

                                boundsBuilder.include(latLng)
                            }

                            val bounds: LatLngBounds = boundsBuilder.build()
                            mMap.animateCamera(
                                CameraUpdateFactory.newLatLngBounds(
                                    bounds,
                                    resources.displayMetrics.widthPixels,
                                    resources.displayMetrics.heightPixels,
                                    300
                                )
                            )
                        }

                        is Resource.Error -> {
                            showLoading(false)
                            Toast.makeText(context, "Gagal menampilkan maps", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) binding.progressBar.visibility = View.VISIBLE else View.GONE
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_maps))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

}