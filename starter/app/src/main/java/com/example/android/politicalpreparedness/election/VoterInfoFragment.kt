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


    private var electionId: Int = 0
    lateinit var division: Division



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

        val factory = VoterInfoViewModelFactory(app, database)

        viewModel = ViewModelProvider(this, factory).get(VoterInfoViewModel::class.java)

        binding.viewModel = viewModel

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
}