package com.example.android.politicalpreparedness.representative

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Office
import com.example.android.politicalpreparedness.network.models.Official
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListener
import com.example.android.politicalpreparedness.representative.model.Representative
import com.google.android.material.appbar.AppBarLayout
import java.util.Locale

class DetailFragment : Fragment() {

    companion object {
        //TODO: Add Constant for Location request
    }

    //TODO: Declare ViewModel
    lateinit var binding: FragmentRepresentativeBinding
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Establish bindings

        //TODO: Define and assign Representative adapter

        //TODO: Populate Representative adapter

        //TODO: Establish button listeners for field and location search
        binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val adapter = RepresentativeListAdapter(RepresentativeListener {
            Toast.makeText(requireActivity(), "name: ${it.official.name}", Toast.LENGTH_SHORT).show();
        })


        binding.representativeRecyclerview.adapter = adapter

//        binding.representativeButton.setOnClickListener { navToRepresentatives() }
//        binding.upcomingButton.setOnClickListener { navToElections() }

        val list = listOf<Representative>(
            Representative(Official(name = "Official 1"), Office("Office 1", division = Division(country = "asdf", id = "1", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 2"), Office("Office 2", division = Division(country = "asdf", id = "2", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 3"), Office("Office 3", division = Division(country = "asdf", id = "3", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 4"), Office("Office 4", division = Division(country = "asdf", id = "4", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 5"), Office("Office 5", division = Division(country = "asdf", id = "5", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 6"), Office("Office 6", division = Division(country = "asdf", id = "6", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 1"), Office("Office 1", division = Division(country = "asdf", id = "1", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 2"), Office("Office 2", division = Division(country = "asdf", id = "2", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 3"), Office("Office 3", division = Division(country = "asdf", id = "3", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 4"), Office("Office 4", division = Division(country = "asdf", id = "4", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 5"), Office("Office 5", division = Division(country = "asdf", id = "5", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 6"), Office("Office 6", division = Division(country = "asdf", id = "6", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 1"), Office("Office 1", division = Division(country = "asdf", id = "1", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 2"), Office("Office 2", division = Division(country = "asdf", id = "2", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 3"), Office("Office 3", division = Division(country = "asdf", id = "3", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 4"), Office("Office 4", division = Division(country = "asdf", id = "4", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 5"), Office("Office 5", division = Division(country = "asdf", id = "5", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 6"), Office("Office 6", division = Division(country = "asdf", id = "6", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 1"), Office("Office 1", division = Division(country = "asdf", id = "1", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 2"), Office("Office 2", division = Division(country = "asdf", id = "2", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 3"), Office("Office 3", division = Division(country = "asdf", id = "3", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 4"), Office("Office 4", division = Division(country = "asdf", id = "4", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 5"), Office("Office 5", division = Division(country = "asdf", id = "5", state = "asdf"), officials = listOf(1,2,3))),
            Representative(Official(name = "Official 6"), Office("Office 6", division = Division(country = "asdf", id = "6", state = "asdf"), officials = listOf(1,2,3))),

        )
        adapter.submitList(list)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val appBarLayout: AppBarLayout = view.findViewById(R.id.appbar_layout)
        val motionLayout: MotionLayout = view.findViewById(R.id.motion_layout)

        val listener = AppBarLayout.OnOffsetChangedListener { unused, verticalOffset ->
            val seekPosition = -verticalOffset / appBarLayout.totalScrollRange.toFloat()
            motionLayout.progress = seekPosition
        }

        appBarLayout.addOnOffsetChangedListener(listener)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //TODO: Handle location permission result to get location on permission granted
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            //TODO: Request Location permissions
            false
        }
    }

    private fun isPermissionGranted() : Boolean {
        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
        return true
    }

    private fun getLocation() {
        //TODO: Get location from LocationServices
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

}