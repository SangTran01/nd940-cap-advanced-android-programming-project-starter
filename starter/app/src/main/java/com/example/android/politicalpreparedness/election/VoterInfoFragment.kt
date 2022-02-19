package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.snackbar.Snackbar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Division
import kotlinx.coroutines.launch


class VoterInfoFragment : Fragment() {

    lateinit var binding: FragmentVoterInfoBinding
    lateinit var viewModel: VoterInfoViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var electionId: Int = 0
    lateinit var division: Division

//    companion object {
//        private val TAG = this::class.java.simpleName
//        private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
//        private const val LOCATION_PERMISSION_INDEX = 0
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val app = requireNotNull(this.activity).application
        val database = ElectionDatabase.getInstance(requireActivity())

        division = VoterInfoFragmentArgs.fromBundle(requireArguments()).argDivision
        electionId = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElectionId

        binding = FragmentVoterInfoBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

//        checkPermissionsAndStartGettingLocation()


        val factory = VoterInfoViewModelFactory(app, database)

        viewModel = ViewModelProvider(this, factory).get(VoterInfoViewModel::class.java)

        binding.viewModel = viewModel


        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks
        binding.followButton.setOnClickListener {
            viewModel.voterInfo.value?.let {
                if (viewModel.savedElection.value == null) {
                    viewModel.saveCurrentElection(it.election)
                } else {
                    viewModel.deleteCurrentElection(it.election)
                }
            }
        }

        viewModel.voterInfo.observe(viewLifecycleOwner, {
            it?.let { response ->
                binding.progressBar.visibility = View.GONE
                binding.electionName.title = response.election.name
                binding.electionName.setTitleTextColor(app.resources.getColor(R.color.white))
                binding.electionDate.text = response.election.electionDay.toString()
                binding.stateHeader.text = "Election Information"
                binding.stateLocations.text = "Voting Locations"
                binding.stateBallot.text = "Ballot Information"

                binding.stateLocations.setOnClickListener(View.OnClickListener {
                    val votingUrl =
                        response.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
                    if (votingUrl != null) {
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(votingUrl))
                        startActivity(browserIntent)
                    } else {
                        Toast.makeText(requireActivity(), "Voting Location Url Unavailable", Toast.LENGTH_SHORT).show();
                    }
                })

                binding.stateBallot.setOnClickListener(View.OnClickListener {
                    val ballotUrl =
                        response.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
                    if (ballotUrl != null) {
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(ballotUrl))
                        startActivity(browserIntent)
                    } else {
                        Toast.makeText(requireActivity(), "Ballot Location Url Unavailable", Toast.LENGTH_SHORT).show();
                    }
                })
            }

        })

        viewModel.showError.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                Snackbar.make(
                    requireView(),
                    it, Snackbar.LENGTH_LONG
                ).show()
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (division.state.isNotEmpty() && division.country.isNotEmpty()) {
            val address = "${division.state}, ${division.country}"

            viewModel.fetchVoterInfo(address, electionId)

            lifecycleScope.launch {
                viewModel.fetchSavedElection(electionId)
            }
        } else {
            Snackbar.make(
                requireView(),
                "State or Country code missing", Snackbar.LENGTH_LONG
            ).show()

            binding.electionName.title = "Error or Test Election"
            binding.electionName.setTitleTextColor(requireActivity().resources.getColor(R.color.white))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onClear()
    }

//    /**
//     * Starts the permission check and Geofence process only if the Geofence associated with the
//     * current hint isn't yet active.
//     */
//    @TargetApi(29)
//    private fun checkPermissionsAndStartGettingLocation() {
//        if (foregroundLocationPermissionApproved()) {
//            checkDeviceLocationSettingsAndStartGetLocation()
//        } else {
//            requestForegroundPermissions()
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun checkDeviceLocationSettingsAndStartGetLocation(
//        resolve: Boolean = true
//    ) {
//        val locationRequest = LocationRequest.create().apply {
//            priority = LocationRequest.PRIORITY_LOW_POWER
//        }
//        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
//        val settingsClient = LocationServices.getSettingsClient(requireActivity())
//        val locationSettingsResponseTask =
//            settingsClient.checkLocationSettings(builder.build())
//        locationSettingsResponseTask.addOnFailureListener { exception ->
//            if (exception is ResolvableApiException && resolve) {
//                try {
//                    val intentSenderRequest =
//                        IntentSenderRequest.Builder(exception.resolution).build()
//                    requestLocationLauncher.launch(intentSenderRequest)
//                } catch (sendEx: IntentSender.SendIntentException) {
//                    Log.d(TAG, "Error getting location settings resolution: " + sendEx.message)
//                }
//            } else {
//                Snackbar.make(
//                    requireView(),
//                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
//                ).setAction(android.R.string.ok) {
//                    checkDeviceLocationSettingsAndStartGetLocation()
//                }.show()
//            }
//        }
//        locationSettingsResponseTask.addOnCompleteListener {
//            if (it.isSuccessful) {
//                Log.v(TAG, "Device location enabled")
//                //get lat and long
//                fusedLocationClient.lastLocation
//                    .addOnSuccessListener { location: Location? ->
//                        location?.let {
//                            try {
//                                val addresses: List<Address>
//                                val geocoder = Geocoder(requireActivity(), Locale.getDefault())
//
//                                addresses = geocoder.getFromLocation(
//                                    location.latitude,
//                                    location.longitude,
//                                    1
//                                ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//
//
//                                val address: String =
//                                    addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//
//                                //pass address, electionId to viewmodel
//
//                                getVoterInfo(address, electionId)
//
//                            } catch (ex: Exception) {
//                                Snackbar.make(
//                                    this.requireView(),
//                                    "Failed to get location",
//                                    Snackbar.LENGTH_INDEFINITE
//                                ).show()
//                            }
//                        }
//                    }
//            }
//        }
//    }

    private fun getVoterInfo(address: String, electionId: Int) {
        viewModel.fetchVoterInfo(address, electionId)
    }


//    private val requestLocationLauncher = registerForActivityResult(
//        ActivityResultContracts.StartIntentSenderForResult()
//    ) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            checkDeviceLocationSettingsAndStartGetLocation()
//        } else {
//            Snackbar.make(
//                this.requireView(),
//                R.string.permission_denied_explanation,
//                Snackbar.LENGTH_INDEFINITE
//            )
//                .setAction(getString(R.string.Settings)) {
//                    startActivity(Intent().apply {
//                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    })
//                }.show()
//        }
//    }
//
//    /*
//     *  Determines whether the app has the appropriate permissions across Android 10+ and all other
//     *  Android versions.
//     */
//    @TargetApi(29)
//    private fun foregroundLocationPermissionApproved(): Boolean {
//        val foregroundApprove = ContextCompat.checkSelfPermission(
//            requireActivity(),
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED
//
////        val backgroundPermissionApproved =
////            if (runningQorLater) {
////                PackageManager.PERMISSION_GRANTED ==
////                        ActivityCompat.checkSelfPermission(
////                            requireActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
////                        )
////            } else {
////                true
////            }
//
//        return foregroundApprove
//    }
//
//    /*
//     *  Requests ACCESS_FINE_LOCATION and (on Android 10+ (Q) ACCESS_BACKGROUND_LOCATION.
//     */
//
//    @TargetApi(29)
//    private fun requestForegroundPermissions() {
//        if (foregroundLocationPermissionApproved())
//            return
//        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
//        val resultCode = REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
//        Log.d(TAG, "Request foreground only location permission")
//        requestPermissions(permissionsArray, resultCode)
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        if (
//            grantResults.isEmpty() ||
//            grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED
//        ) {
//            //use snackbar to inform user of need for permission
//            Snackbar.make(
//                this.requireView(),
//                R.string.permission_denied_explanation,
//                Snackbar.LENGTH_INDEFINITE
//            )
//                .setAction(getString(R.string.Settings)) {
//                    startActivity(Intent().apply {
//                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    })
//                }.show()
//        } else {
//            checkDeviceLocationSettingsAndStartGetLocation()
//        }
//    }
}